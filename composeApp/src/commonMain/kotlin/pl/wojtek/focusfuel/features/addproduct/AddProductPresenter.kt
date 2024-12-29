package pl.wojtek.focusfuel.features.addproduct

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.repository.ShopRepository
import pl.wojtek.focusfuel.util.circuit.FocusPresenter
import pl.wojtek.focusfuel.util.circuit.asyncEventSink
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

sealed interface AddProductEvent : CircuitUiEvent {
    data class SetName(val name: String) : AddProductEvent
    data class SetPrice(val price: String) : AddProductEvent
    data object Add : AddProductEvent
}

data class AddProductState(
    val name: String = "",
    val price: String = "",
    val eventSink: (AddProductEvent) -> Unit,
) : CircuitUiState

@CircuitInject(AddProductScreen::class, AppScope::class)
@Inject
class AddProductPresenter(
    @Assisted private val addProductScreen: AddProductScreen,
    private val shopRepository: ShopRepository,
    @Assisted private val navigator: Navigator,
) : FocusPresenter<AddProductState>() {
    @Composable
    override fun presentState(): AddProductState {
        val initialProduct = addProductScreen.product
        var name by remember { mutableStateOf(initialProduct?.name ?: "") }
        var price by remember { mutableStateOf(initialProduct?.costInPomodoros?.toString() ?: "") }

        return AddProductState(
            name = name,
            price = price,
            eventSink = asyncEventSink { event ->
                when (event) {
                    is AddProductEvent.Add -> launch {
                        if (initialProduct != null) {
                            shopRepository.hideProduct(initialProduct)
                        }
                        shopRepository.addProduct(
                            name = name,
                            costInPomodoros = price.toIntOrNull() ?: 0
                        )
                        navigator.pop()
                    }

                    is AddProductEvent.SetName -> name = event.name
                    is AddProductEvent.SetPrice -> price = event.price
                }
            }
        )
    }
} 
