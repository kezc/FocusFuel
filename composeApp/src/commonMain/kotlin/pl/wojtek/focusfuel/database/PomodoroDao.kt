package pl.wojtek.focusfuel.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PomodoroDao {
    @Insert
    suspend fun insert(item: PomodoroEntity)

    @Query("SELECT count(*) FROM PomodoroEntity")
    suspend fun count(): Int

    @Query("SELECT * FROM PomodoroEntity")
    fun getAllAsFlow(): Flow<List<PomodoroEntity>>
}
