package pl.wojtek.focusfuel.features.history

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import focusfuel.composeapp.generated.resources.Res
import focusfuel.composeapp.generated.resources.ic_tomato
import focusfuel.composeapp.generated.resources.purchase_history_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.wojtek.focusfuel.ui.AppCloseIcon
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
                    AppCloseIcon(onClick = { state.eventSink(PurchaseHistoryEvent.Close) })
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            state.purchases.forEach { purchase ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Row(modifier = Modifier.width(IntrinsicSize.Max)) {
                            Text(
                                modifier = Modifier.weight(1f),
                                text = purchase.productName,
                                style = MaterialTheme.typography.titleMedium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row {
                                Icon(
                                    painter = painterResource(Res.drawable.ic_tomato),
                                    contentDescription = "Pomodoros",
                                    modifier = Modifier.padding(end = 2.dp)
                                )
                                Text(text = "${purchase.price}")
                            }
                        }
                        Text(
                            text = purchase.formattedDate,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun PurchaseHistoryScreenPreview() {
    PurchaseHistoryUI(state = PurchaseHistoryState(
        purchases = listOf(
            PurchaseItem(
                productName = "Product 1",
                price = 10,
                formattedDate = "2021-01-01"
            ),
            PurchaseItem(
                productName = "Product Product Product Product Product 1",
                price = 10,
                formattedDate = "2021-01-01"
            ),
        )
    ) {})
} 
