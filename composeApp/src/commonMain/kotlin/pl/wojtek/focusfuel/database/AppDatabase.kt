package pl.wojtek.focusfuel.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.SQLiteConnection

@Database(entities = [PomodoroEntity::class], version = 1)
@TypeConverters(DateConverter::class)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pomodoroDao(): PomodoroDao
    abstract fun purchaseDao(): PurchaseDao
    abstract fun productDao(): ProductDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}


class AppDatabaseCallback : RoomDatabase.Callback() {
    override fun onCreate(connection: SQLiteConnection) {
        super.onCreate(connection)
        val statement = connection
            .prepare("INSERT INTO ProductEntity (id, name, costInPomodoros) VALUES ('1', 'Product 1', 5)")
        while (statement.step());
    }
}

