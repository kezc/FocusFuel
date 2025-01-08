package pl.wojtek.focusfuel.features.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import focusfuel.composeapp.generated.resources.Res
import focusfuel.composeapp.generated.resources.purchase_history_no_purchases_available
import focusfuel.composeapp.generated.resources.purchase_history_title
import focusfuel.composeapp.generated.resources.purchase_history_used
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.wojtek.focusfuel.features.history.PurchaseHistoryEvent.UpdateUsedStatus
import pl.wojtek.focusfuel.mainscreen.MainScaffoldContentPadding
import pl.wojtek.focusfuel.ui.common.AppLoadingScreen
import pl.wojtek.focusfuel.ui.common.ProductName
import pl.wojtek.focusfuel.ui.component.AppSnackbarHost
import pl.wojtek.focusfuel.ui.component.rememberSnackbarHostState
import pl.wojtek.focusfuel.ui.util.PaddingValuesInsets
import pl.wojtek.focusfuel.ui.util.ShowSnackbarHandler
import pl.wojtek.focusfuel.ui.util.onlyBottom
import pl.wojtek.focusfuel.ui.util.plus
import pl.wojtek.focusfuel.ui.util.withoutBottom
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
    val snackbarHostState = rememberSnackbarHostState()
    ShowSnackbarHandler(snackbarHostState, state.error?.message)

    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(Res.string.purchase_history_title)) }) },
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
        modifier = modifier,
        contentWindowInsets = PaddingValuesInsets(MainScaffoldContentPadding.current),
    ) { innerPadding ->
        if (state.isLoading) {
            AppLoadingScreen(Modifier.padding(innerPadding))
            return@Scaffold
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding.withoutBottom())
        ) {
            if (state.purchases.isEmpty()) {
                EmptyListPlaceholder()
            } else {
                PurchasesList(state, innerPadding.onlyBottom())
            }
        }
    }
}

@Composable
private fun PurchasesList(state: PurchaseHistoryState, padding: PaddingValues) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(16.dp) + padding
    ) {
        state.purchases.forEach { purchase ->
            item(purchase.purchaseId) { PurchaseItem(purchase, state.eventSink) }
        }
    }
}

@Composable
private fun BoxScope.EmptyListPlaceholder() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.Companion.align(Alignment.Center)
    ) {
        Text(
            text = stringResource(Res.string.purchase_history_no_purchases_available),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
        Text(
            text = "🤓",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
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
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                ProductName(price = purchase.price, productName = purchase.productName)
                DateText(purchase)
            }
            UsedCheckbox(purchase, eventSink)
        }
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
    PurchaseHistoryUI(
        state = PurchaseHistoryState(
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
            ),
            {},
            null,
            false,
        )
    )
} 
