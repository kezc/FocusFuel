package pl.wojtek.focusfuel.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.datetime.LocalDateTime

@Entity
data class PurchaseEntity(
    @PrimaryKey val productId: String,
    val date: LocalDateTime
)

@Dao
interface PurchaseDao {
    @Insert
    suspend fun insert(purchase: PurchaseEntity)

    @Query("SELECT * FROM PurchaseEntity")
    suspend fun getAll(): List<PurchaseEntity>
}
