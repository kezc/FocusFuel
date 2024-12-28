package pl.wojtek.focusfuel.features.shop

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import focusfuel.composeapp.generated.resources.shop_available_pomodoros
import focusfuel.composeapp.generated.resources.shop_insufficient_pomodoros
import focusfuel.composeapp.generated.resources.shop_purchase_success
import focusfuel.composeapp.generated.resources.shop_title
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

@CommonParcelize
object ShopScreen : Screen

@CircuitInject(ShopScreen::class, AppScope::class)
class ShopUI : Ui<ShopState> {
    @Composable
    override fun Content(state: ShopState, modifier: Modifier) {
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
                    IconButton(onClick = { state.eventSink(ShopEvent.Close) }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.purchase_history_back_button_description))
                    }
                },
                actions = {
                    IconButton(onClick = { state.eventSink(ShopEvent.NavigateToPurchaseHistory) }) {
                        Icon(Icons.Filled.History, contentDescription = stringResource(Res.string.purchase_history_back_button_description))
                    }
                    Text(stringResource(Res.string.shop_available_pomodoros, state.availablePomodoros))
                    IconButton(onClick = { state.eventSink(ShopEvent.NavigateToAddProduct) }) {
                        Icon(Icons.Filled.Add, contentDescription = "Add Product")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            state.products.forEach { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .clickable {
                            state.eventSink(ShopEvent.Buy(product))
                        },
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "${product.name} - ${product.costInPomodoros} pomodoros",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            state.orderResult?.let { message ->
                Text(
                    text = message.toText(),
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
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
