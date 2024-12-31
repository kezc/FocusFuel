package pl.wojtek.focusfuel.features.addproduct

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import pl.wojtek.focusfuel.repository.Product
import pl.wojtek.focusfuel.ui.AppCloseIcon
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
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Add Product") }, navigationIcon = {
                AppCloseIcon(onClick = { state.eventSink(AddProductEvent.Close) })
            })
        },
        modifier = modifier
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(16.dp)) {
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.name,
                onValueChange = { state.eventSink(AddProductEvent.SetName(it)) },
                label = { Text("Product Name") }
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.price,
                onValueChange = { state.eventSink(AddProductEvent.SetPrice(it)) },
                label = { Text("Product Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = { state.eventSink(AddProductEvent.Add) }) {
                Text("Add Product")
            }
        }
    }
} 
