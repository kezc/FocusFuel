package pl.wojtek.focusfuel.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.wojtek.focusfuel.database.model.ProductEntity

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
