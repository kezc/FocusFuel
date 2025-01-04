package pl.wojtek.focusfuel

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mmk.kmpnotifier.permission.permissionUtil
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.rememberCircuitNavigator
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import pl.wojtek.focusfuel.database.AppDatabase
import pl.wojtek.focusfuel.mainscreen.MainScreen
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val circuit = (applicationContext as FocusFuelApp).appComponent.circuit

        enableEdgeToEdge()
        val permissionUtil by permissionUtil()
        permissionUtil.askNotificationPermission()

        setContent {
            val backstack = rememberSaveableBackStack(MainScreen)
            val navigator = rememberCircuitNavigator(backStack = backstack)

            App(circuit, backstack, navigator)
        }
    }
}

@Component
@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class AppComponent(
    @get:Provides val context: Context
) : AppComponentMerged {
    abstract val pomodoroServiceManager: PomodoroServiceManager

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
