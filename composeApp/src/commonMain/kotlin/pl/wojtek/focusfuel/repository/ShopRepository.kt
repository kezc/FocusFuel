package pl.wojtek.focusfuel.repository

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.datetime.LocalDateTime
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.database.ProductDao
import pl.wojtek.focusfuel.database.PurchaseDao
import pl.wojtek.focusfuel.database.PurchaseEntity
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn


interface ShopRepository {
    fun getProducts(): Flow<List<Product>>
    fun pomodoroBalance(): Flow<Int>
    suspend fun makePurchase(product: Product): Boolean
    suspend fun getPurchases(): List<Purchase>
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
        .map { entities -> entities.map { Product(it.id, it.name, it.costInPomodoros) } }

    override fun pomodoroBalance(): Flow<Int> =
        getTotalSpendings()
            .map { pomodorosRepository.getTotalPomodorosFinished() - it }

    override suspend fun makePurchase(product: Product): Boolean {
        val totalPomodoros = pomodorosRepository.getTotalPomodorosFinished() - getTotalSpendings().first()
        return if (totalPomodoros >= product.costInPomodoros) {
            purchaseDao.insert(PurchaseEntity(productId = product.id, date = currentLocalDateTime()))
            true
        } else {
            false
        }
    }

    override suspend fun getPurchases(): List<Purchase> {
        return purchaseDao.getAll().map { Purchase(it.productId, it.date) }
    }


    private fun getTotalSpendings(): Flow<Int> {
        return purchaseDao.getTotalSpendings().map { it ?: 0 }.onEach { Logger.d("DUPA") {"Kurwa? $it®"} }
    }


}

data class Product(
    val id: String,
    val name: String,
    val costInPomodoros: Int
)

data class Purchase(
    val productId: String,
    val date: LocalDateTime
)
