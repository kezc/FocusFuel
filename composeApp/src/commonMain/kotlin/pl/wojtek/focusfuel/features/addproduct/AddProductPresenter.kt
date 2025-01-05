package pl.wojtek.focusfuel.features.addproduct

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import arrow.core.raise.either
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.features.addproduct.AddProductEvent.Add
import pl.wojtek.focusfuel.features.addproduct.AddProductEvent.Close
import pl.wojtek.focusfuel.features.addproduct.AddProductEvent.SetName
import pl.wojtek.focusfuel.features.addproduct.AddProductEvent.SetPrice
import pl.wojtek.focusfuel.repository.ShopRepository
import pl.wojtek.focusfuel.ui.rememberDisappearingState
import pl.wojtek.focusfuel.util.circuit.FocusPresenter
import pl.wojtek.focusfuel.util.circuit.asyncEventSink
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

sealed interface AddProductEvent : CircuitUiEvent {
    data class SetName(val name: String) : AddProductEvent
    data class SetPrice(val price: String) : AddProductEvent
    data object Add : AddProductEvent
    data object Close : AddProductEvent
}

data class AddProductState(
    val name: String = "",
    val price: String = "",
    val nameError: AddProductError? = null,
    val priceError: AddProductError? = null,
    val eventSink: (AddProductEvent) -> Unit,
    val error: Throwable?,
) : CircuitUiState

enum class AddProductError {
    EMPTY_NAME,
    INVALID_PRICE
}

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
        var nameError by remember { mutableStateOf<AddProductError?>(null) }
        var priceError by remember { mutableStateOf<AddProductError?>(null) }
        var error by rememberDisappearingState<Throwable>()

        return AddProductState(
            name = name,
            price = price,
            nameError = nameError,
            priceError = priceError,
            error = error,
            eventSink = asyncEventSink { event ->
                when (event) {
                    is Add -> {
                        nameError = getNameError(name)
                        priceError = getPriceError(price)

                        if (nameError == null && priceError == null) {
                            launch {
                                either {
                                    if (initialProduct != null) {
                                        shopRepository.hideProduct(initialProduct).bind()
                                    }
                                    shopRepository.addProduct(
                                        name = name,
                                        costInPomodoros = price.toIntOrNull() ?: 0
                                    ).bind()
                                }.onRight { navigator.pop() }
                                    .onLeft { error = it }
                            }
                        }
                    }

                    is SetName -> {
                        name = event.name
                        nameError = getNameError(name)
                    }

                    is SetPrice -> {
                        price = event.price
                        priceError = getPriceError(price)
                    }

                    is Close -> navigator.pop()
                }
            }
        )
    }

    private fun getNameError(name: String) = if (name.isBlank()) AddProductError.EMPTY_NAME else null

    private fun getPriceError(price: String): AddProductError? {
        val priceParsed = price.toIntOrNull()
        return if (
            price.isBlank()
            || priceParsed == null
            || priceParsed <= 0
        ) AddProductError.INVALID_PRICE else null
    }
}
