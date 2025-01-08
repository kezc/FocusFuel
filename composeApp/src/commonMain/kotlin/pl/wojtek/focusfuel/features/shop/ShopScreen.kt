package pl.wojtek.focusfuel.features.shop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import pl.wojtek.focusfuel.features.shop.ShopEvent.NavigateToAddProduct
import pl.wojtek.focusfuel.features.shop.ShopEvent.SelectProductToBuy
import pl.wojtek.focusfuel.features.shop.ShopEvent.ShowProductBottomSheet
import pl.wojtek.focusfuel.mainscreen.MainScaffoldContentPadding
import pl.wojtek.focusfuel.repository.Product
import pl.wojtek.focusfuel.ui.common.AppLoadingScreen
import pl.wojtek.focusfuel.ui.common.ProductName
import pl.wojtek.focusfuel.ui.component.AppIconButton
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
object ShopScreen : Screen

@CircuitInject(ShopScreen::class, AppScope::class)
class ShopUI : Ui<ShopState> {
    @Composable
    override fun Content(state: ShopState, modifier: Modifier) {
        ProductBottomSheetHandler(state.selectedProductToChange, state.eventSink)
        ConfirmPurchaseDialogHandler(state.selectedProductToBuy, state.eventSink)

        ShopUI(modifier, state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopUI(
    modifier: Modifier = Modifier,
    state: ShopState,
) {
    val snackbarHostState = rememberSnackbarHostState()
    ShowSnackbarHandler(snackbarHostState, state.orderResult?.toText())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.shop_title)) },
                actions = {
                    PomodorosBalanceIcon(state)
                    AddProductIcon(state.eventSink)
                }
            )
        },
        contentWindowInsets = PaddingValuesInsets(MainScaffoldContentPadding.current),
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        if (state.isLoading) {
            AppLoadingScreen(Modifier.padding(innerPadding))
            return@Scaffold
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(innerPadding.withoutBottom()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp) + innerPadding.onlyBottom()
        ) {
            state.products.forEach { product ->
                item(key = product.id, contentType = Product::class) { ProductCard(product, state.eventSink) }
            }
            item { AddNewProductCard(state.eventSink) }
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
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(painterResource(Res.drawable.ic_tomato), contentDescription = "Pomodoros")
        Text(state.availablePomodoros.toString())
    }
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
            .clickable { eventSink(SelectProductToBuy(product)) },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f)) {
                ProductName(
                    price = product.costInPomodoros,
                    productName = product.name
                )
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
