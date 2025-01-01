package pl.wojtek.focusfuel

import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.room.Room
import androidx.room.RoomDatabase
import co.touchlab.kermit.Logger
import com.mmk.kmpnotifier.extensions.composeDesktopResourcesPath
import com.mmk.kmpnotifier.notification.NotifierManager
import com.mmk.kmpnotifier.notification.configuration.NotificationPlatformConfiguration
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import pl.wojtek.focusfuel.database.AppDatabase
import pl.wojtek.focusfuel.features.counter.CounterScreen
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import java.io.File

fun main() = application {
    val windowState =
        rememberWindowState(
            width = 1200.dp,
            height = 800.dp,
            position = WindowPosition(Alignment.Center),
        )
    val circuit = remember { AppComponent::class.create().circuit }
    val backstack = rememberSaveableBackStack(CounterScreen)
    NotifierManager.initialize(
        NotificationPlatformConfiguration.Desktop(
            showPushNotification = true,
            notificationIconPath = composeDesktopResourcesPath() + File.separator + "notification_icon.png"
        )
    )

    Window(
        title = "FocusFuel",
        state = windowState,
        onCloseRequest = ::exitApplication
    ) {
        App(
            circuit = circuit,
            backstack = backstack,
            navigator = rememberCircuitNavigator(
                backStack = backstack,
                onRootPop = { /* no-op */ }
            )
        )
    }
}

@Component
@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class AppComponent : AppComponentMerged {
    abstract val presenterFactories: Set<Presenter.Factory>
    abstract val uiFactories: Set<Ui.Factory>
    abstract val circuit: Circuit

    @SingleIn(AppScope::class)
    @Provides
    fun circuit(presenterFactories: Set<Presenter.Factory>, uiFactories: Set<Ui.Factory>): Circuit {
        return Circuit.Builder()
            .addPresenterFactories(presenterFactories)
            .addUiFactories(uiFactories)
            .build()
    }

    @Provides
    fun provideDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> {
        val dbFilePath = File(System.getProperty("java.io.tmpdir"), "focusfuel/my_mroom.db").absolutePath
        Logger.d("DB path: $dbFilePath")
        return Room.databaseBuilder<AppDatabase>(
            name = dbFilePath,
        )
    }
}
