package pl.wojtek.focusfuel.database

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Entity
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val costInPomodoros: Int
)

@Dao
interface ProductDao {
    @Insert
    suspend fun insert(product: ProductEntity)

    @Query("SELECT * FROM ProductEntity")
    fun getAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM ProductEntity WHERE id = :productId")
    suspend fun getById(productId: String): ProductEntity?

    @Query("DELETE FROM ProductEntity WHERE id = :productId")
    suspend fun deleteById(productId: String)
}
