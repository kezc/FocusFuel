package pl.wojtek.focusfuel.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import pl.wojtek.focusfuel.database.model.ProductEntity

@Dao
interface ProductDao {
    @Insert
    suspend fun insert(product: ProductEntity)

    @Update
    suspend fun update(product: ProductEntity)

    @Query("SELECT * FROM ProductEntity ORDER BY COALESCE(originalId, id)")
    fun getAll(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM ProductEntity WHERE id = :productId")
    suspend fun getById(productId: String): ProductEntity?

    @Query("DELETE FROM ProductEntity WHERE id = :productId")
    suspend fun deleteById(productId: String)
}
