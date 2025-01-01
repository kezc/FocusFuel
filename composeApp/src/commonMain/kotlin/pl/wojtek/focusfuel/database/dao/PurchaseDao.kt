package pl.wojtek.focusfuel.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.wojtek.focusfuel.database.model.PurchaseEntity
import pl.wojtek.focusfuel.database.model.PurchaseWithProduct

@Dao
interface PurchaseDao {
    @Insert
    suspend fun insert(purchase: PurchaseEntity)

    @Query("SELECT * FROM PurchaseEntity")
    fun getAll(): Flow<List<PurchaseEntity>>

    @Query("""
        SELECT SUM(p.costInPomodoros) 
        FROM PurchaseEntity pu 
        JOIN ProductEntity p ON pu.productId = p.id
    """)
    fun getTotalSpendings(): Flow<Int?>

    @Query("""
        SELECT pu.id AS purchaseId, pu.productId, p.name AS productName, pu.date, p.costInPomodoros, pu.used
        FROM PurchaseEntity pu 
        JOIN ProductEntity p ON pu.productId = p.id
    """)
    fun getAllPurchasesWithProducts(): Flow<List<PurchaseWithProduct>>

    @Query("UPDATE PurchaseEntity SET used = :used WHERE id = :purchaseId")
    suspend fun updateUsedStatus(purchaseId: Int, used: Boolean)
}
