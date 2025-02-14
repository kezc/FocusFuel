package pl.wojtek.focusfuel.features.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import arrow.core.getOrElse
import arrow.core.right
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.repository.Purchase
import pl.wojtek.focusfuel.repository.ShopRepository
import pl.wojtek.focusfuel.ui.util.observeError
import pl.wojtek.focusfuel.ui.util.rememberDisappearingState
import pl.wojtek.focusfuel.ui.util.rememberRetainedProgressCounter
import pl.wojtek.focusfuel.ui.util.watchProgress
import pl.wojtek.focusfuel.util.circuit.FocusPresenter
import pl.wojtek.focusfuel.util.circuit.asyncEventSink
import pl.wojtek.focusfuel.util.circuit.rememberRetainedCoroutineScope
import pl.wojtek.focusfuel.util.datetime.DateTimeFormatter
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

sealed interface PurchaseHistoryEvent : CircuitUiEvent {
    data class UpdateUsedStatus(val purchaseId: Int, val used: Boolean) : PurchaseHistoryEvent
}

data class PurchaseHistoryState(
    val purchases: List<PurchaseItem> = emptyList(),
    val eventSink: (PurchaseHistoryEvent) -> Unit,
    val error: Throwable?,
    val isLoading: Boolean,
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
        val error = rememberDisappearingState<Throwable>()
        val progressCounter = rememberRetainedProgressCounter(true)
        val retainedScope = rememberRetainedCoroutineScope()

        val purchases by rememberRetained {
            shopRepository.getPurchases()
                .watchProgress(progressCounter)
                .observeError(error)
                .stateIn(retainedScope, SharingStarted.Lazily, emptyList<Purchase>().right())
        }.collectAsRetainedState()

        return PurchaseHistoryState(
            error = error.value,
            isLoading = progressCounter.state.value,
            purchases = purchases.getOrElse { emptyList() }.toListItem(),
            eventSink = asyncEventSink { event ->
                when (event) {
                    is PurchaseHistoryEvent.UpdateUsedStatus -> launch {
                        shopRepository
                            .updatePurchaseUsedStatus(event.purchaseId, event.used)
                            .observeError(error)
                    }
                }
            }
        )
    }

    private fun List<Purchase>.toListItem() = map { purchase ->
        PurchaseItem(
            purchaseId = purchase.purchaseId,
            productName = purchase.productName,
            formattedDate = dateTimeFormatter.getFormattedDateTime(purchase.date),
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
