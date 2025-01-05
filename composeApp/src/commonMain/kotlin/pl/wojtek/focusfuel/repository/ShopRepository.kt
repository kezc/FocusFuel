package pl.wojtek.focusfuel.repository

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.right
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.database.dao.ProductDao
import pl.wojtek.focusfuel.database.dao.PurchaseDao
import pl.wojtek.focusfuel.database.model.ProductEntity
import pl.wojtek.focusfuel.database.model.PurchaseEntity
import pl.wojtek.focusfuel.util.either.EitherT
import pl.wojtek.focusfuel.util.either.combineEither
import pl.wojtek.focusfuel.util.either.toEither
import pl.wojtek.focusfuel.util.parcelize.CommonParcelable
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn


interface ShopRepository {
    fun getProducts(): Flow<EitherT<List<Product>>>
    fun pomodoroBalance(): Flow<EitherT<Int>>
    suspend fun makePurchase(product: Product): EitherT<Boolean>
    fun getPurchases(): Flow<EitherT<List<Purchase>>>
    suspend fun addProduct(name: String, costInPomodoros: Int): EitherT<Unit>
    suspend fun hideProduct(product: Product): EitherT<Unit>
    suspend fun updatePurchaseUsedStatus(purchaseId: Int, used: Boolean): EitherT<Unit>
}

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class ShopRepositoryImpl(
    private val purchaseDao: PurchaseDao,
    private val pomodorosRepository: PomodorosRepository,
    private val productDao: ProductDao,
) : ShopRepository {

    override fun getProducts(): Flow<EitherT<List<Product>>> = productDao
        .getAll()
        .map { entities ->
            entities
                .filter { !it.hidden }
                .map { Product(it.id, it.name, it.costInPomodoros) }
        }
        .toEither()

    override suspend fun makePurchase(product: Product): EitherT<Boolean> = either {
        val totalPomodorosFinished = pomodorosRepository.getTotalPomodorosFinished().first().bind()
        val totalSpending = getTotalSpendings().first().bind()
        val totalPomodoros = totalPomodorosFinished - totalSpending
        if (totalPomodoros < product.costInPomodoros) return false.right()
        addNewPurchase(product).bind()
        true.right().bind()
    }

    private suspend fun addNewPurchase(product: Product) =
        Either.catch { purchaseDao.insert(PurchaseEntity(productId = product.id, date = currentLocalDateTime())) }

    override fun getPurchases(): Flow<EitherT<List<Purchase>>> = purchaseDao
        .getAllPurchasesWithProducts()
        .map { entities ->
            entities.map {
                Purchase(
                    purchaseId = it.purchaseId,
                    productId = it.productId,
                    productName = it.productName,
                    date = it.date,
                    costInPomodoros = it.costInPomodoros,
                    used = it.used
                )
            }
        }
        .toEither()

    override fun pomodoroBalance(): Flow<EitherT<Int>> =
        combineEither(
            pomodorosRepository.getTotalPomodorosFinished(),
            getTotalSpendings(),
        ) { pomodoros, spendings -> pomodoros - spendings }

    private fun getTotalSpendings(): Flow<EitherT<Int>> = purchaseDao
        .getTotalSpendings()
        .toEither()
        .map { either -> either.map { it ?: 0 } }

    override suspend fun addProduct(name: String, costInPomodoros: Int): EitherT<Unit> = Either.catch {
        productDao.insert(
            ProductEntity(
                name = name,
                costInPomodoros = costInPomodoros
            )
        )
    }

    override suspend fun hideProduct(product: Product): EitherT<Unit> = Either.catch {
        productDao.update(
            ProductEntity(
                id = product.id,
                name = product.name,
                costInPomodoros = product.costInPomodoros,
                hidden = true
            )
        )
    }

    override suspend fun updatePurchaseUsedStatus(purchaseId: Int, used: Boolean): EitherT<Unit> = Either.catch {
        purchaseDao.updateUsedStatus(purchaseId, used)
    }
}

@CommonParcelize
data class Product(
    val id: Long,
    val name: String,
    val costInPomodoros: Int
) : CommonParcelable

data class Purchase(
    val purchaseId: Int,
    val productId: Long,
    val productName: String,
    val date: LocalDateTime,
    val costInPomodoros: Int,
    val used: Boolean
)
