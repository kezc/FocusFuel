package pl.wojtek.focusfuel.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters
import androidx.sqlite.SQLiteConnection
import pl.wojtek.focusfuel.database.converter.DateConverter
import pl.wojtek.focusfuel.database.dao.PomodoroDao
import pl.wojtek.focusfuel.database.dao.ProductDao
import pl.wojtek.focusfuel.database.dao.PurchaseDao
import pl.wojtek.focusfuel.database.model.PomodoroEntity
import pl.wojtek.focusfuel.database.model.ProductEntity
import pl.wojtek.focusfuel.database.model.PurchaseEntity

@Database(
    entities = [PomodoroEntity::class, ProductEntity::class, PurchaseEntity::class],
    version = 2
)
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
            .prepare("" +
                    "INSERT INTO ProductEntity (id, name, costInPomodoros, hidden, originalId) VALUES" +
                    "(1, 'Reward yourself with 1 hour of gaming', 3, 0, 1)," +
                    "(2, 'Treat yourself to a delicious bar of chocolate', 2, 0, 2)," +
                    "(3, 'Take a day off from studying or chores', 8, 0, 3)," +
                    "(4, 'Watch 1 episode of a TV show', 4, 0, 4)," +
                    "(5, 'Spend 30 minutes on a creative activity', 4, 0, 5)," +
                    "(6, 'Get a new book', 5, 0, 6)," +
                    "(7, 'Order your favorite snack or meal', 6, 0, 7)," +
                    "(8, 'Enjoy a relaxing walk in the park', 6, 0, 8)," +
                    "(9, 'Treat yourself to a movie or special event', 10, 0, 9);")
        while (statement.step());
    }
}

