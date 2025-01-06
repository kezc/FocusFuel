package pl.wojtek.focusfuel.features.shop

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import arrow.core.getOrElse
import arrow.core.right
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.features.addproduct.AddProductScreen
import pl.wojtek.focusfuel.features.history.PurchaseHistoryScreen
import pl.wojtek.focusfuel.features.shop.ShopEvent.Buy
import pl.wojtek.focusfuel.features.shop.ShopEvent.DeleteProduct
import pl.wojtek.focusfuel.features.shop.ShopEvent.DismissConfirmation
import pl.wojtek.focusfuel.features.shop.ShopEvent.HideProductBottomSheet
import pl.wojtek.focusfuel.features.shop.ShopEvent.NavigateToAddProduct
import pl.wojtek.focusfuel.features.shop.ShopEvent.NavigateToEditProduct
import pl.wojtek.focusfuel.features.shop.ShopEvent.NavigateToPurchaseHistory
import pl.wojtek.focusfuel.features.shop.ShopEvent.SelectProductToBuy
import pl.wojtek.focusfuel.features.shop.ShopEvent.ShowProductBottomSheet
import pl.wojtek.focusfuel.repository.Product
import pl.wojtek.focusfuel.repository.ShopRepository
import pl.wojtek.focusfuel.ui.util.observeError
import pl.wojtek.focusfuel.ui.util.rememberDisappearingState
import pl.wojtek.focusfuel.util.circuit.FocusPresenter
import pl.wojtek.focusfuel.util.circuit.asyncEventSink
import pl.wojtek.focusfuel.util.either.EitherT
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

sealed interface ShopEvent : CircuitUiEvent {
    data class SelectProductToBuy(val product: Product) : ShopEvent
    data class Buy(val product: Product) : ShopEvent
    data object DismissConfirmation : ShopEvent
    data object NavigateToPurchaseHistory : ShopEvent
    data object NavigateToAddProduct : ShopEvent
    data class ShowProductBottomSheet(val product: Product) : ShopEvent
    data object HideProductBottomSheet : ShopEvent
    data object DeleteProduct : ShopEvent
    data object NavigateToEditProduct : ShopEvent
}

data class ShopState(
    val products: List<Product>,
    val orderResult: ShopPresenter.OrderResult?,
    val availablePomodoros: Int?,
    val eventSink: (ShopEvent) -> Unit,
    val selectedProductToChange: Product?,
    val selectedProductToBuy: Product?,
) : CircuitUiState

@CircuitInject(ShopScreen::class, AppScope::class)
@Inject
class ShopPresenter(
    private val shopRepository: ShopRepository,
    @Assisted private val navigator: Navigator
) : FocusPresenter<ShopState>() {
    @Composable
    override fun presentState(): ShopState {
        val error = rememberDisappearingState<Throwable>()
        val products by shopRepository.getProducts().observeError(error).collectAsStateWithLifecycle(emptyList<Product>().right())
        val balance by shopRepository.pomodoroBalance().observeError(error).collectAsStateWithLifecycle(null as EitherT<Int>?)
        var orderResult: OrderResult? by rememberDisappearingState<OrderResult?>()
        var selectedProductToChange by remember { mutableStateOf<Product?>(null) }
        var selectedProductToBuy by remember { mutableStateOf<Product?>(null) }

        return ShopState(
            products = products.getOrElse { emptyList() },
            orderResult = orderResult,
            availablePomodoros = balance?.getOrNull(),
            selectedProductToChange = selectedProductToChange,
            selectedProductToBuy = selectedProductToBuy,
            eventSink = asyncEventSink { event ->
                when (event) {
                    is SelectProductToBuy -> selectedProductToBuy = event.product

                    is Buy -> launch {
                        shopRepository.makePurchase(event.product)
                            .observeError(error)
                            .onRight { orderResult = getOrderResult(it) }
                        selectedProductToBuy = null
                    }

                    NavigateToPurchaseHistory ->
                        navigator.goTo(PurchaseHistoryScreen)

                    NavigateToAddProduct ->
                        navigator.goTo(AddProductScreen())

                    is NavigateToEditProduct ->
                        navigator.goTo(AddProductScreen(selectedProductToChange))

                    is ShowProductBottomSheet -> selectedProductToChange = event.product

                    HideProductBottomSheet -> selectedProductToChange = null

                    DeleteProduct -> launch {
                        shopRepository.hideProduct(selectedProductToChange!!)
                        selectedProductToChange = null
                    }

                    DismissConfirmation -> selectedProductToBuy = null
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
