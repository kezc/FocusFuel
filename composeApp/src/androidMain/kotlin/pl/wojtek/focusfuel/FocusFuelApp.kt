package pl.wojtek.focusfuel

import android.app.Application
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration

class FocusFuelApp : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = AppComponent::class.create(this)
        appComponent.pomodoroServiceManager.init()

        NotifierManager.initialize(
            configuration = NotificationPlatformConfiguration.Android(
                notificationIconResId = R.drawable.ic_launcher_foreground,
                showPushNotification = false,
                notificationChannelData = NotificationPlatformConfiguration.Android.NotificationChannelData(
                    id = "PomodoroChannel",
                    name = getString(R.string.pomodoro_channel),
                )
            )
        )

        appComponent.pomodoroNotificationsManager.value.init()

    }
}
