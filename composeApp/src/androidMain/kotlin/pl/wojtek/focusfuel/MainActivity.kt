package pl.wojtek.focusfuel

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mmk.kmpnotifier.permission.permissionUtil
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.rememberCircuitNavigator
import pl.wojtek.focusfuel.mainscreen.MainScreen

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

