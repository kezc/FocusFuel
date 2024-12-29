package pl.wojtek.focusfuel.util.circuit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import arrow.core.None
import arrow.core.Option
import arrow.core.some
import com.slack.circuit.overlay.OverlayScope
import com.slack.circuitx.overlays.BottomSheetOverlay


@OptIn(ExperimentalMaterial3Api::class)
suspend fun <T> OverlayScope.showBottomSheet(listOfActions: List<Action<T>>): T? =
    show(
        BottomSheetOverlay<List<Action<T>>, Option<T>>(
            model = listOfActions,
            onDismiss = { None },
            content = { actions, overlayNavigator ->
                ActionsSheet(
                    actions,
                    { overlayNavigator.finish(it.some()) }
                )
            }
        )
    ).getOrNull()

@Composable
fun <T> ActionsSheet(actions: List<Action<T>>, onActionClicked: (T) -> Unit) {
    Column(Modifier.navigationBarsPadding()) {
        actions.forEach { action ->
            Text(
                text = action.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onActionClicked(action.value) }
                    .padding(16.dp)
            )
        }
    }
}

data class Action<T>(val title: String, val value: T)
