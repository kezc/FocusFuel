package pl.wojtek.focusfuel.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.database.dao.ProductDao
import pl.wojtek.focusfuel.database.dao.PurchaseDao
import pl.wojtek.focusfuel.database.model.ProductEntity
import pl.wojtek.focusfuel.database.model.PurchaseEntity
import pl.wojtek.focusfuel.util.parcelize.CommonParcelable
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn


interface ShopRepository {
    fun getProducts(): Flow<List<Product>>
    fun pomodoroBalance(): Flow<Int>
    suspend fun makePurchase(product: Product): Boolean
    fun getPurchases(): Flow<List<Purchase>>
    suspend fun addProduct(name: String, costInPomodoros: Int)
    suspend fun hideProduct(product: Product)
    suspend fun updatePurchaseUsedStatus(purchaseId: Int, used: Boolean)
}

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class ShopRepositoryImpl(
    private val purchaseDao: PurchaseDao,
    private val pomodorosRepository: PomodorosRepository,
    private val productDao: ProductDao,
) : ShopRepository {

    override fun getProducts(): Flow<List<Product>> = productDao
        .getAll()
        .map { entities ->
            entities
                .filter { !it.hidden }
                .map { Product(it.id, it.name, it.costInPomodoros) }
        }

    override suspend fun makePurchase(product: Product): Boolean {
        val totalPomodoros = pomodorosRepository.getTotalPomodorosFinished().first() - getTotalSpendings().first()
        return if (totalPomodoros >= product.costInPomodoros) {
            purchaseDao.insert(PurchaseEntity(productId = product.id, date = currentLocalDateTime()))
            true
        } else {
            false
        }
    }

    override fun getPurchases(): Flow<List<Purchase>> = purchaseDao
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

    override fun pomodoroBalance(): Flow<Int> =
        combine(
            pomodorosRepository.getTotalPomodorosFinished(),
            getTotalSpendings(),
        ) { pomodoros, spendings -> pomodoros - spendings }

    private fun getTotalSpendings(): Flow<Int> = purchaseDao
        .getTotalSpendings().map { it ?: 0 }

    override suspend fun addProduct(name: String, costInPomodoros: Int) {
        productDao.insert(
            ProductEntity(
                name = name,
                costInPomodoros = costInPomodoros
            )
        )
    }

    override suspend fun hideProduct(product: Product) {
        productDao.update(
            ProductEntity(
                id = product.id,
                name = product.name,
                costInPomodoros = product.costInPomodoros,
                hidden = true
            )
        )
    }

    override suspend fun updatePurchaseUsedStatus(purchaseId: Int, used: Boolean) {
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
