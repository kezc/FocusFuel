package pl.wojtek.focusfuel.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import pl.wojtek.focusfuel.AppComponentMerged
import pl.wojtek.focusfuel.PomodoroServiceManager
import pl.wojtek.focusfuel.database.AppDatabase
import pl.wojtek.focusfuel.notification.BackgroundNotificationSender
import pl.wojtek.focusfuel.notifications.NotificationSender
import pl.wojtek.focusfuel.util.AppInForegroundNotifier
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Component
@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class AppComponent(
    @get:Provides val context: Context
) : AppComponentMerged {
    abstract val pomodoroServiceManager: PomodoroServiceManager
    abstract val appInForegroundNotifier: AppInForegroundNotifier

    @Provides
    fun provideNotificationSender(backgroundNotificationSender: BackgroundNotificationSender) : NotificationSender =
        backgroundNotificationSender

    @Provides
    fun provideDatabaseBuilder(ctx: Context): RoomDatabase.Builder<AppDatabase> {
        val appContext = ctx.applicationContext
        val dbFile = appContext.getDatabasePath("my_room.db")
        return Room.databaseBuilder<AppDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}
