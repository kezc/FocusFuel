package pl.wojtek.focusfuel.features.addproduct

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import focusfuel.composeapp.generated.resources.Res
import focusfuel.composeapp.generated.resources.add_product_button_label
import focusfuel.composeapp.generated.resources.add_product_error_empty_name
import focusfuel.composeapp.generated.resources.add_product_error_invalid_price
import focusfuel.composeapp.generated.resources.add_product_name_label
import focusfuel.composeapp.generated.resources.add_product_price_label
import focusfuel.composeapp.generated.resources.add_product_title
import org.jetbrains.compose.resources.stringResource
import pl.wojtek.focusfuel.repository.Product
import pl.wojtek.focusfuel.ui.component.AppCloseIcon
import pl.wojtek.focusfuel.ui.component.AppOutlinedTextField
import pl.wojtek.focusfuel.ui.component.AppSnackbarHost
import pl.wojtek.focusfuel.ui.component.rememberSnackbarHostState
import pl.wojtek.focusfuel.ui.util.ShowSnackbarHandler
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

@CommonParcelize
data class AddProductScreen(
    val product: Product? = null
) : Screen

@CircuitInject(AddProductScreen::class, AppScope::class)
class AddProductUI : Ui<AddProductState> {
    @Composable
    override fun Content(state: AddProductState, modifier: Modifier) {
        AddProductUI(modifier, state)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProductUI(
    modifier: Modifier = Modifier,
    state: AddProductState,
) {
    val snackbarHostState = rememberSnackbarHostState()
    ShowSnackbarHandler(snackbarHostState, state.error?.message)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.add_product_title)) },
                navigationIcon = {
                    AppCloseIcon(onClick = { state.eventSink(AddProductEvent.Close) })
                }
            )
        },
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            NameTextField(state)
            Spacer(modifier = Modifier.height(2.dp))
            PriceTextField(state)
            Spacer(modifier = Modifier.height(12.dp))
            SubmitButton(state)
        }
    }
}

@Composable
private fun NameTextField(
    state: AddProductState,
) {
    AppOutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.name,
        onValueChange = { state.eventSink(AddProductEvent.SetName(it)) },
        label = { Text(stringResource(Res.string.add_product_name_label)) },
        error = state.nameError?.toText(),
    )
}

@Composable
private fun PriceTextField(
    state: AddProductState,
) {
    AppOutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = state.price,
        onValueChange = { state.eventSink(AddProductEvent.SetPrice(it)) },
        label = { Text(stringResource(Res.string.add_product_price_label)) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        error = state.priceError?.toText(),
    )
}

@Composable
private fun SubmitButton(
    state: AddProductState,
) {
    Button(
        modifier = Modifier.fillMaxWidth(),
        onClick = { state.eventSink(AddProductEvent.Add) }) {
        Text(stringResource(Res.string.add_product_button_label))
    }
}

@Composable
private fun AddProductError.toText() = when (this) {
    AddProductError.EMPTY_NAME -> stringResource(Res.string.add_product_error_empty_name)
    AddProductError.INVALID_PRICE -> stringResource(Res.string.add_product_error_invalid_price)
}
