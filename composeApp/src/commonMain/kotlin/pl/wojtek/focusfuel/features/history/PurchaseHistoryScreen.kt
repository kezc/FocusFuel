package pl.wojtek.focusfuel.features.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
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
                title = { Text("Purchase History") },
                navigationIcon = {
                    IconButton(onClick = { state.eventSink(PurchaseHistoryEvent.Close) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
