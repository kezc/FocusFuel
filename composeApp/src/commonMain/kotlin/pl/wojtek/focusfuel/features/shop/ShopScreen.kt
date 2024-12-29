package pl.wojtek.focusfuel.features.shop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import pl.wojtek.focusfuel.features.shop.ShopEvent.Close
import pl.wojtek.focusfuel.features.shop.ShopEvent.NavigateToAddProduct
import pl.wojtek.focusfuel.features.shop.ShopEvent.NavigateToPurchaseHistory
import pl.wojtek.focusfuel.features.shop.ShopEvent.ShowProductBottomSheet
import pl.wojtek.focusfuel.ui.AppCloseIcon
import pl.wojtek.focusfuel.ui.AppIconButton
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

@CommonParcelize
object ShopScreen : Screen

@CircuitInject(ShopScreen::class, AppScope::class)
class ShopUI : Ui<ShopState> {
    @Composable
    override fun Content(state: ShopState, modifier: Modifier) {
        ProductBottomSheetHandler(state)

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
                navigationIcon = {
                    AppCloseIcon(onClick = { state.eventSink(Close) })
                },
                actions = {
                    AppIconButton(
                        onClick = { state.eventSink(NavigateToPurchaseHistory) },
                        imageVector = Icons.Filled.History,
                        contentDescription = "Purchase History"
                    )
                    Row(verticalAlignment = CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(state.availablePomodoros.toString())
                        Icon(painterResource(Res.drawable.ic_tomato), contentDescription = "Pomodoros")
                    }
                    AppIconButton(
                        onClick = { state.eventSink(NavigateToAddProduct) },
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Product"
                    )
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            state.orderResult?.let { message ->
                Text(
                    text = message.toText(),
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            state.products.forEach { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            state.eventSink(Buy(product))
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Row {
                        Text(
                            text = "${product.name} - ${product.costInPomodoros} pomodoros",
                            modifier = Modifier.padding(16.dp)
                        )
                        MoreOptions(
                            onClick = { state.eventSink(ShowProductBottomSheet(product)) }
                        )
                    }
                }
            }
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
