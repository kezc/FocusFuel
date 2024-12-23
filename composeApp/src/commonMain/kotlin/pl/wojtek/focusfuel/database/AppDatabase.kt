package pl.wojtek.focusfuel.database

import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

@Database(entities = [PomodoroEntity::class], version = 1)
@TypeConverters(DateConverter::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pomodoroDao(): PomodoroDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}

@Dao
interface PomodoroDao {
    @Insert
    suspend fun insert(item: PomodoroEntity)

    @Query("SELECT count(*) FROM PomodoroEntity")
    suspend fun count(): Int

    @Query("SELECT * FROM PomodoroEntity")
    fun getAllAsFlow(): Flow<List<PomodoroEntity>>
}

@Entity
data class PomodoroEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: LocalDateTime
)
