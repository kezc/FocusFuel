package pl.wojtek.focusfuel.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import pl.wojtek.focusfuel.database.model.PomodoroEntity

@Dao
interface PomodoroDao {
    @Insert
    suspend fun insert(item: PomodoroEntity)

    @Query("SELECT count(*) FROM PomodoroEntity")
    fun count(): Flow<Int>

    @Query("SELECT * FROM PomodoroEntity")
    fun getAllAsFlow(): Flow<List<PomodoroEntity>>
}
