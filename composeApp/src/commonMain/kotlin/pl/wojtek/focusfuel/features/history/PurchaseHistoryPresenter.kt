package pl.wojtek.focusfuel.features.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.repository.Purchase
import pl.wojtek.focusfuel.repository.ShopRepository
import pl.wojtek.focusfuel.util.circuit.FocusPresenter
import pl.wojtek.focusfuel.util.circuit.asyncEventSink
import pl.wojtek.focusfuel.util.datetime.DateTimeFormatter
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

sealed interface PurchaseHistoryEvent : CircuitUiEvent {
    data object Close : PurchaseHistoryEvent
    data class UpdateUsedStatus(val purchaseId: Int, val used: Boolean) : PurchaseHistoryEvent
}

data class PurchaseHistoryState(
    val purchases: List<PurchaseItem> = emptyList(),
    val eventSink: (PurchaseHistoryEvent) -> Unit,
) : CircuitUiState

@CircuitInject(PurchaseHistoryScreen::class, AppScope::class)
@Inject
class PurchaseHistoryPresenter(
    private val shopRepository: ShopRepository,
    private val dateTimeFormatter: DateTimeFormatter,
    @Assisted private val navigator: Navigator
) : FocusPresenter<PurchaseHistoryState>() {

    @Composable
    override fun presentState(): PurchaseHistoryState {
        val purchases by shopRepository.getPurchases().collectAsStateWithLifecycle(emptyList())
        return PurchaseHistoryState(
            purchases = purchases.toListItem(),
            eventSink = asyncEventSink { event ->
                when (event) {
                    PurchaseHistoryEvent.Close -> navigator.pop()
                    is PurchaseHistoryEvent.UpdateUsedStatus -> launch {
                        shopRepository.updatePurchaseUsedStatus(event.purchaseId, event.used)
                    }
                }
            }
        )
    }

    private fun List<Purchase>.toListItem() = map { purchase ->
        PurchaseItem(
            purchaseId = purchase.purchaseId,
            productName = purchase.productName,
            formattedDate = dateTimeFormatter.getFormattedDate(purchase.date),
            price = purchase.costInPomodoros,
            used = purchase.used
        )
    }
}

data class PurchaseItem(
    val purchaseId: Int,
    val productName: String,
    val formattedDate: String,
    val price: Int,
    val used: Boolean
)
