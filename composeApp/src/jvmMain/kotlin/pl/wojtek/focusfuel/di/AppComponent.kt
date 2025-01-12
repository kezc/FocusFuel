package pl.wojtek.focusfuel.di

import androidx.room.Room
import androidx.room.RoomDatabase
import co.touchlab.kermit.Logger
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import pl.wojtek.focusfuel.database.AppDatabase
import pl.wojtek.focusfuel.notification.SoundNotificationSender
import pl.wojtek.focusfuel.notifications.NotificationSender
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import java.io.File

@Component
@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class AppComponent : AppComponentMerged {
    @Provides
    fun provideNotificationSender(soundNotificationSender: SoundNotificationSender): NotificationSender =
        soundNotificationSender

    @Provides
    fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFilePath = File(System.getProperty("java.io.tmpdir"), "focusfuel/app_database.db").absolutePath
        Logger.d("DB path: $dbFilePath")
        return Room.databaseBuilder<AppDatabase>(
            name = dbFilePath,
        )
    }
}
