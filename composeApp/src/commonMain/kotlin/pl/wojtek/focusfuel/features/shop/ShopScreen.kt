package pl.wojtek.focusfuel.features.shop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import focusfuel.composeapp.generated.resources.Res
import focusfuel.composeapp.generated.resources.ic_tomato
import focusfuel.composeapp.generated.resources.shop_insufficient_pomodoros
import focusfuel.composeapp.generated.resources.shop_purchase_success
import focusfuel.composeapp.generated.resources.shop_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.wojtek.focusfuel.features.shop.ShopEvent.Buy
import pl.wojtek.focusfuel.features.shop.ShopEvent.NavigateToAddProduct
import pl.wojtek.focusfuel.features.shop.ShopEvent.NavigateToPomodoro
import pl.wojtek.focusfuel.features.shop.ShopEvent.NavigateToPurchaseHistory
import pl.wojtek.focusfuel.features.shop.ShopEvent.ShowProductBottomSheet
import pl.wojtek.focusfuel.repository.Product
import pl.wojtek.focusfuel.ui.AppIconButton
import pl.wojtek.focusfuel.ui.ShowAnimatedText
import pl.wojtek.focusfuel.ui.unboundedClickable
import pl.wojtek.focusfuel.ui.withoutBottom
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

@CommonParcelize
object ShopScreen : Screen

@CircuitInject(ShopScreen::class, AppScope::class)
class ShopUI : Ui<ShopState> {
    @Composable
    override fun Content(state: ShopState, modifier: Modifier) {
        ProductBottomSheetHandler(state.showProductBottomSheet, state.eventSink)

        ShopUI(modifier, state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopUI(
    modifier: Modifier = Modifier,
    state: ShopState,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.shop_title)) },
                actions = {
                    PomodorosBalanceIcon(state)
                    HistoryIcon(state.eventSink)
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding.withoutBottom())
                .padding(horizontal = 16.dp)
        ) {
            ShowAnimatedText(state.orderResult?.toText(), {
                Text(
                    text = it,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textAlign = TextAlign.Center
                )
            }, 2000)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                state.products.forEach { product ->
                    item(key = product.id) { ProductCard(product, state.eventSink) }
                }
                item(key = "add_new") {
                    AddNewProductCard(state.eventSink)
                }
            }

        }
    }
}

@Composable
fun AddNewProductCard(eventSink: (ShopEvent) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { eventSink(NavigateToAddProduct) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 2.dp, bottom = 2.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = CenterVertically
        ) {
            AddProductIcon(eventSink)
            Text(
                text = "Add new product",
                style = MaterialTheme.typography.titleMedium,
            )
        }
    }
}

@Composable
private fun AddProductIcon(
    eventSink: (ShopEvent) -> Unit
) {
    AppIconButton(
        onClick = { eventSink(NavigateToAddProduct) },
        imageVector = Icons.Filled.Add,
        contentDescription = "Add Product"
    )
}

@Composable
private fun PomodorosBalanceIcon(
    state: ShopState,
) {
    Row(
        modifier = Modifier.unboundedClickable { state.eventSink(NavigateToPomodoro) },
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(painterResource(Res.drawable.ic_tomato), contentDescription = "Pomodoros")
        Text(state.availablePomodoros.toString())
    }
}

@Composable
private fun HistoryIcon(
    eventSink: (ShopEvent) -> Unit
) {
    AppIconButton(
        onClick = { eventSink(NavigateToPurchaseHistory) },
        imageVector = Icons.Filled.History,
        contentDescription = "Purchase History"
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ProductCard(
    product: Product,
    eventSink: (ShopEvent) -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { eventSink(Buy(product)) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 8.dp, start = 16.dp, end = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            FlowRow(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )
                Row {
                    Icon(
                        painter = painterResource(Res.drawable.ic_tomato),
                        contentDescription = "Pomodoros",
                        modifier = Modifier.padding(end = 2.dp)
                    )
                    Text(text = "${product.costInPomodoros}")
                }
            }
            MoreOptions(onClick = { eventSink(ShowProductBottomSheet(product)) })
        }
    }
}

@Composable
fun MoreOptions(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "More Options",
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ShopPresenter.OrderResult.toText() = when (this) {
    ShopPresenter.OrderResult.SUCCESS -> stringResource(Res.string.shop_purchase_success)
    ShopPresenter.OrderResult.INSUFFICIENT_POMODOROS -> stringResource(Res.string.shop_insufficient_pomodoros)
}

@Preview
@Composable
fun ShopScreenPreview() {
    ShopUI()
}
