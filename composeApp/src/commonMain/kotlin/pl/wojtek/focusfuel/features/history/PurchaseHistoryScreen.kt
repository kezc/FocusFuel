package pl.wojtek.focusfuel.features.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import focusfuel.composeapp.generated.resources.Res
import focusfuel.composeapp.generated.resources.ic_tomato
import focusfuel.composeapp.generated.resources.purchase_history_title
import focusfuel.composeapp.generated.resources.purchase_history_used
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.wojtek.focusfuel.features.history.PurchaseHistoryEvent.UpdateUsedStatus
import pl.wojtek.focusfuel.ui.withoutBottom
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
            TopAppBar(title = { Text(stringResource(Res.string.purchase_history_title)) })
        },
        modifier = modifier
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding.withoutBottom()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            state.purchases.forEach { purchase ->
                item(purchase.purchaseId) { PurchaseItem(purchase, state.eventSink) }
            }
        }
    }
}

@Composable
private fun PurchaseItem(
    purchase: PurchaseItem,
    eventSink: (PurchaseHistoryEvent) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(modifier = Modifier.width(IntrinsicSize.Max)) {
                    NameText(purchase)
                    Spacer(modifier = Modifier.width(4.dp))
                    PriceText(purchase)
                }
                DateText(purchase)
            }
            UsedCheckbox(purchase, eventSink)
        }
    }
}

@Composable
private fun RowScope.NameText(purchase: PurchaseItem) {
    Text(
        modifier = Modifier.Companion.weight(1f),
        text = purchase.productName,
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun PriceText(purchase: PurchaseItem) {
    Row {
        Icon(
            painter = painterResource(Res.drawable.ic_tomato),
            contentDescription = "Pomodoros",
            modifier = Modifier.padding(end = 2.dp)
        )
        Text(text = "${purchase.price}")
    }
}

@Composable
private fun DateText(purchase: PurchaseItem) {
    Text(
        text = purchase.formattedDate,
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(top = 4.dp)
    )
}

@Composable
private fun UsedCheckbox(
    purchase: PurchaseItem,
    eventSink: (PurchaseHistoryEvent) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
            Checkbox(
                checked = purchase.used,
                onCheckedChange = { isChecked ->
                    eventSink(UpdateUsedStatus(purchase.purchaseId, isChecked))
                }
            )
        }
        Text(
            text = stringResource(Res.string.purchase_history_used),
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Preview
@Composable
fun PurchaseHistoryScreenPreview() {
    PurchaseHistoryUI(state = PurchaseHistoryState(
        purchases = listOf(
            PurchaseItem(
                purchaseId = 1,
                productName = "Product 1",
                price = 10,
                formattedDate = "2021-01-01",
                used = false
            ),
            PurchaseItem(
                purchaseId = 2,
                productName = "Product Product Product Product Product 1",
                price = 10,
                formattedDate = "2021-01-01",
                used = true
            ),
        )
    ) {})
} 
