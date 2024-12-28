package pl.wojtek.focusfuel.features.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.repository.ShopRepository
import pl.wojtek.focusfuel.util.circuit.FocusPresenter
import pl.wojtek.focusfuel.util.circuit.asyncEventSink
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import pl.wojtek.focusfuel.util.DateTimeHelper
import pl.wojtek.focusfuel.model.PurchaseItem

sealed interface PurchaseHistoryEvent : CircuitUiEvent {
    data object Close : PurchaseHistoryEvent
}

data class PurchaseHistoryState(
    val purchases: List<PurchaseItem> = emptyList(),
    val eventSink: (PurchaseHistoryEvent) -> Unit,
) : CircuitUiState

@CircuitInject(PurchaseHistoryScreen::class, AppScope::class)
@Inject
class PurchaseHistoryPresenter(
    private val shopRepository: ShopRepository,
    @Assisted private val navigator: Navigator
) : FocusPresenter<PurchaseHistoryState>() {
    private val dateTimeHelper = DateTimeHelper()

    @Composable
    override fun presentState(): PurchaseHistoryState {
        val purchases by shopRepository.getPurchases().collectAsStateWithLifecycle(emptyList())
        return PurchaseHistoryState(
            purchases = purchases.map { purchase ->
                PurchaseItem(
                    productName = purchase.productName,
                    formattedDate = dateTimeHelper.getFormattedDate(purchase.date),
                    price = purchase.costInPomodoros
                )
            },
            eventSink = asyncEventSink { event ->
                when (event) {
                    PurchaseHistoryEvent.Close -> navigator.pop()
                }
            }
        )
    }
} 
