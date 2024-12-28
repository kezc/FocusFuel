package pl.wojtek.focusfuel.features.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import focusfuel.composeapp.generated.resources.Res
import focusfuel.composeapp.generated.resources.purchase_history_back_button_description
import focusfuel.composeapp.generated.resources.purchase_history_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

@CommonParcelize
object PurchaseHistoryScreen : Screen

@CircuitInject(PurchaseHistoryScreen::class, AppScope::class)
class PurchaseHistoryUI : Ui<PurchaseHistoryState> {
    @Composable
    override fun Content(state: PurchaseHistoryState, modifier: Modifier) {
        PurchaseHistoryUI(modifier, state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseHistoryUI(
    modifier: Modifier = Modifier,
    state: PurchaseHistoryState,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.purchase_history_title)) },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(PurchaseHistoryEvent.Close) }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.purchase_history_back_button_description)
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            state.purchases.forEach { purchase ->
                Text(
                    text = "${purchase.productName} - ${purchase.formattedDate} - $${purchase.price}",
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview
@Composable
fun PurchaseHistoryScreenPreview() {
    PurchaseHistoryUI(state = PurchaseHistoryState() {})
} 
