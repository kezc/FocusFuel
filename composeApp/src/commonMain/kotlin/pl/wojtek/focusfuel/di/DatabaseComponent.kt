package pl.wojtek.focusfuel.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import me.tatarka.inject.annotations.Provides
import pl.wojtek.focusfuel.database.AppDatabase
import pl.wojtek.focusfuel.database.AppDatabaseCallback
import pl.wojtek.focusfuel.database.PomodoroDao
import pl.wojtek.focusfuel.database.ProductDao
import pl.wojtek.focusfuel.database.PurchaseDao
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface DatabaseComponent {
    @SingleIn(AppScope::class)
    @Provides
    fun provideDatabase(
        builder: RoomDatabase.Builder<AppDatabase>
    ): AppDatabase {
        return builder
            .fallbackToDestructiveMigrationOnDowngrade(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .addCallback(AppDatabaseCallback())
            .build()
    }

    @SingleIn(AppScope::class)
    @Provides
    fun providePomodorosDao(appDatabase: AppDatabase): PomodoroDao {
        return appDatabase.pomodoroDao()
    }

    @SingleIn(AppScope::class)
    @Provides
    fun provideProductDao(appDatabase: AppDatabase): ProductDao {
        return appDatabase.productDao()
    }

    @SingleIn(AppScope::class)
    @Provides
    fun providePurchaseDao(appDatabase: AppDatabase): PurchaseDao {
        return appDatabase.purchaseDao()
    }
}
