package pl.wojtek.focusfuel.util.circuit

import androidx.compose.runtime.Composable
import co.touchlab.kermit.Logger
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.presenter.Presenter

abstract class FocusPresenter<UiState : CircuitUiState> : Presenter<UiState> {
    @Composable
    final override fun present(): UiState {
        return presentState().also { Logger.d { it.toString() } }
    }

    @Composable
    abstract fun presentState(): UiState
}
