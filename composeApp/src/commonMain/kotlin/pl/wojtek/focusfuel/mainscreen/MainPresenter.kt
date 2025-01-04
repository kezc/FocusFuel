package pl.wojtek.focusfuel.mainscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.foundation.NavEvent
import com.slack.circuit.foundation.onNavEvent
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import kotlinx.collections.immutable.ImmutableList
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

data class MainState(
    val navItems: ImmutableList<BottomNavTab> = NAV_ITEMS,
    val selectedTab: BottomNavTab = BottomNavTab.Pomodoro,
    val eventSink: (MainEvent) -> Unit,
) : CircuitUiState

sealed interface MainEvent : CircuitUiEvent {
    data class TabClick(val tab: BottomNavTab) : MainEvent
    data class ChildNavEvent(val navEvent: NavEvent) : MainEvent
}

@CircuitInject(screen = MainScreen::class, scope = AppScope::class)
@Composable
fun MainPresenter(navigator: Navigator): MainState {
    var selectedTab by rememberSaveable { mutableStateOf(BottomNavTab.Pomodoro) }
    return MainState(selectedTab = selectedTab) { event ->
        when (event) {
            is MainEvent.TabClick -> selectedTab = event.tab
            is MainEvent.ChildNavEvent -> navigator.onNavEvent(event.navEvent)
        }
    }
}
