package pl.wojtek.focusfuel.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.datetime.LocalDateTime
import androidx.room.ForeignKey
import kotlinx.coroutines.flow.Flow

@Entity(foreignKeys = [ForeignKey(entity = ProductEntity::class, parentColumns = ["id"], childColumns = ["productId"])])
data class PurchaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val date: LocalDateTime
)

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
        SELECT pu.id AS purchaseId, pu.productId, p.name AS productName, pu.date, p.costInPomodoros
        FROM PurchaseEntity pu 
        JOIN ProductEntity p ON pu.productId = p.id
    """)
    fun getAllPurchasesWithProducts(): Flow<List<PurchaseWithProduct>>
}
