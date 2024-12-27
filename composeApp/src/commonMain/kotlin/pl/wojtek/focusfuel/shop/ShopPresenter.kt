package pl.wojtek.focusfuel.shop

import androidx.compose.runtime.Composable
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.counter.CounterScreen
import pl.wojtek.focusfuel.repository.ShopRepository
import pl.wojtek.focusfuel.util.circuit.asyncEventSink
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

sealed interface ShopEvent : CircuitUiEvent {
    data object Close : ShopEvent
}

data class ShopState(
    val eventSink: (ShopEvent) -> Unit
) : CircuitUiState

@CircuitInject(CounterScreen::class, AppScope::class)
@Inject
class ShopPresenter(
    private val shopRepository: ShopRepository,
    @Assisted private val navigator: Navigator
) : Presenter<ShopState> {
    @Composable
    override fun present(): ShopState {
        return ShopState(
            eventSink = asyncEventSink { event ->
                when (event) {
                    ShopEvent.Close -> navigator.pop()
                }
            }
        )
    }
}
