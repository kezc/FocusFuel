package pl.wojtek.focusfuel

import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.mmk.kmpnotifier.extensions.composeDesktopResourcesPath
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.rememberCircuitNavigator
import pl.wojtek.focusfuel.di.AppComponent
import pl.wojtek.focusfuel.mainscreen.MainScreen
import java.io.File

fun main() = application {
    val windowState =
        rememberWindowState(
            width = 1200.dp,
            height = 800.dp,
            position = WindowPosition(Alignment.Center),
        )
    val appComponent = remember { AppComponent::class.create() }
    val backstack = rememberSaveableBackStack(MainScreen)
    NotifierManager.initialize(
        NotificationPlatformConfiguration.Desktop(
            showPushNotification = false,
            notificationIconPath = composeDesktopResourcesPath() + File.separator + "notification_icon.png",
        )
    )
    appComponent.pomodoroNotificationsManager.value.init()

    Window(
        title = "FocusFuel",
        state = windowState,
        onCloseRequest = ::exitApplication
    ) {
        App(
            circuit = appComponent.circuit,
            backstack = backstack,
            navigator = rememberCircuitNavigator(
                backStack = backstack,
                onRootPop = { /* no-op */ }
            )
        )
    }
}
