package pl.wojtek.focusfuel.features.shop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.features.history.PurchaseHistoryScreen
import pl.wojtek.focusfuel.repository.Product
import pl.wojtek.focusfuel.repository.ShopRepository
import pl.wojtek.focusfuel.util.circuit.FocusPresenter
import pl.wojtek.focusfuel.util.circuit.asyncEventSink
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

sealed interface ShopEvent : CircuitUiEvent {
    data object Close : ShopEvent
    data class Buy(val product: Product) : ShopEvent
    data object NavigateToPurchaseHistory : ShopEvent
}

data class ShopState(
    val products: List<Product>,
    val orderResult: ShopPresenter.OrderResult?,
    val availablePomodoros: Int,
    val eventSink: (ShopEvent) -> Unit,
) : CircuitUiState

@CircuitInject(ShopScreen::class, AppScope::class)
@Inject
class ShopPresenter(
    private val shopRepository: ShopRepository,
    @Assisted private val navigator: Navigator
) : FocusPresenter<ShopState>() {
    @Composable
    override fun presentState(): ShopState {
        val products by shopRepository.getProducts().collectAsStateWithLifecycle(emptyList())
        val balance by shopRepository.pomodoroBalance().collectAsStateWithLifecycle(0)
        var orderResult: OrderResult? by remember { mutableStateOf(null) }
        LaunchedEffect(orderResult) {
            if (orderResult != null) {
                delay(3000)
                orderResult = null
            }
        }

        return ShopState(
            products = products,
            orderResult = orderResult,
            availablePomodoros = balance,
            eventSink = asyncEventSink { event ->
                when (event) {
                    is ShopEvent.Buy -> launch {
                        val success = shopRepository.makePurchase(event.product)
                        orderResult = if (success) {
                            OrderResult.SUCCESS
                        } else {
                            OrderResult.INSUFFICIENT_POMODOROS
                        }
                    }

                    ShopEvent.Close -> navigator.pop()

                    ShopEvent.NavigateToPurchaseHistory -> {
                        // Logic to navigate to PurchaseHistoryScreen
                        navigator.goTo(PurchaseHistoryScreen)
                    }
                }
            }
        )
    }

    enum class OrderResult {
        SUCCESS,
        INSUFFICIENT_POMODOROS
    }
}
