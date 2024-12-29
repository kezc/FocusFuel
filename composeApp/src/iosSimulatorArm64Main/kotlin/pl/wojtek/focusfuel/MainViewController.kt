package pl.wojtek.focusfuel

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import androidx.room.Room
import androidx.room.RoomDatabase
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.ui.Ui
import kotlinx.cinterop.ExperimentalForeignApi
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import pl.wojtek.focusfuel.database.AppDatabase
import pl.wojtek.focusfuel.features.counter.CounterScreen
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

fun MainViewController() = ComposeUIViewController {
    val circuit = remember { AppComponent::class.create().circuit }
    App(
        circuit = circuit,
        backstack = rememberSaveableBackStack(CounterScreen),
        navigator = rememberCircuitNavigator(
            backStack = rememberSaveableBackStack(CounterScreen),
            onRootPop = { /* no-op */ }
        )
    )
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
        val dbFilePath = documentDirectory() + "/my_room.db"
        return Room.databaseBuilder<AppDatabase>(
            name = dbFilePath,
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    private fun documentDirectory(): String {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null,
        )
        return requireNotNull(documentDirectory?.path)
    }

}
