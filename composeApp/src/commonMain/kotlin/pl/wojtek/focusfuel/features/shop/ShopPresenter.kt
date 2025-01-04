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
import pl.wojtek.focusfuel.features.addproduct.AddProductScreen
import pl.wojtek.focusfuel.features.history.PurchaseHistoryScreen
import pl.wojtek.focusfuel.features.pomodoro.PomodoroScreen
import pl.wojtek.focusfuel.features.shop.ShopEvent.*
import pl.wojtek.focusfuel.repository.Product
import pl.wojtek.focusfuel.repository.ShopRepository
import pl.wojtek.focusfuel.util.circuit.FocusPresenter
import pl.wojtek.focusfuel.util.circuit.asyncEventSink
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

sealed interface ShopEvent : CircuitUiEvent {
    data class Buy(val product: Product) : ShopEvent
    data object NavigateToPurchaseHistory : ShopEvent
    data object NavigateToPomodoro : ShopEvent
    data object NavigateToAddProduct : ShopEvent
    data class ShowProductBottomSheet(val product: Product) : ShopEvent
    data object HideProductBottomSheet : ShopEvent
    data object DeleteProduct : ShopEvent
    data object NavigateToEditProduct : ShopEvent
}

data class ShopState(
    val products: List<Product>,
    val orderResult: ShopPresenter.OrderResult?,
    val availablePomodoros: Int,
    val eventSink: (ShopEvent) -> Unit,
    val showProductBottomSheet: Product?,
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
        var showProductBottomSheet by remember { mutableStateOf<Product?>(null) }

        return ShopState(
            products = products,
            orderResult = orderResult,
            availablePomodoros = balance,
            showProductBottomSheet = showProductBottomSheet,
            eventSink = asyncEventSink { event ->
                when (event) {
                    is Buy -> launch {
                        val success = shopRepository.makePurchase(event.product)
                        orderResult = getOrderResult(success)
                    }

                    NavigateToPurchaseHistory ->
                        navigator.goTo(PurchaseHistoryScreen)

                    NavigateToAddProduct ->
                        navigator.goTo(AddProductScreen())

                    is NavigateToEditProduct ->
                        navigator.goTo(AddProductScreen(showProductBottomSheet))

                    NavigateToPomodoro -> navigator.goTo(PomodoroScreen)

                    is ShowProductBottomSheet -> showProductBottomSheet = event.product

                    HideProductBottomSheet -> showProductBottomSheet = null

                    DeleteProduct -> launch {
                        shopRepository.hideProduct(showProductBottomSheet!!)
                        showProductBottomSheet = null
                    }
                }
            }
        )
    }

    private fun getOrderResult(success: Boolean) = if (success) {
        OrderResult.SUCCESS
    } else {
        OrderResult.INSUFFICIENT_POMODOROS
    }

    enum class OrderResult {
        SUCCESS,
        INSUFFICIENT_POMODOROS
    }
}
