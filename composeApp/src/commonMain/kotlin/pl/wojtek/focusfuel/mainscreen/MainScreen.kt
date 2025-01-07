package pl.wojtek.focusfuel.mainscreen

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import focusfuel.composeapp.generated.resources.Res
import focusfuel.composeapp.generated.resources.bottom_bar_pomodoro
import focusfuel.composeapp.generated.resources.bottom_bar_purchases
import focusfuel.composeapp.generated.resources.bottom_bar_shop
import focusfuel.composeapp.generated.resources.ic_tomato
import kotlinx.collections.immutable.persistentListOf
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pl.wojtek.focusfuel.features.history.PurchaseHistoryScreen
import pl.wojtek.focusfuel.features.pomodoro.PomodoroScreen
import pl.wojtek.focusfuel.features.shop.ShopScreen
import pl.wojtek.focusfuel.mainscreen.MainEvent.ChildNavEvent
import pl.wojtek.focusfuel.mainscreen.MainEvent.TabClick
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

@CommonParcelize
data object MainScreen : Screen

val NAV_ITEMS = persistentListOf(BottomNavTab.Pomodoro, BottomNavTab.Shop, BottomNavTab.Purchases)

enum class BottomNavTab(val screen: Screen) {
    Pomodoro(PomodoroScreen),
    Shop(ShopScreen),
    Purchases(PurchaseHistoryScreen)
}

@CircuitInject(screen = MainScreen::class, scope = AppScope::class)
class MainUI : Ui<MainState> {
    @Composable
    override fun Content(state: MainState, modifier: Modifier) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                BottomNavigationBar(selectedTab = state.selectedTab) { index ->
                    state.eventSink(TabClick(index))
                }
            },
        ) { paddingValues ->
            CircuitContent(
                state.selectedTab.screen,
                modifier = Modifier.padding(paddingValues),
                onNavEvent = { event -> state.eventSink(ChildNavEvent(event)) },
            )
        }
    }
}

@Composable
private fun BottomNavigationBar(selectedTab: BottomNavTab, onSelectedTab: (BottomNavTab) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.primaryContainer) {
        NAV_ITEMS.forEach { item ->
            val text = stringResource(
                when (item) {
                    BottomNavTab.Pomodoro -> Res.string.bottom_bar_pomodoro
                    BottomNavTab.Shop -> Res.string.bottom_bar_shop
                    BottomNavTab.Purchases -> Res.string.bottom_bar_purchases
                }
            )
            val icon = when (item) {
                BottomNavTab.Pomodoro -> vectorResource(Res.drawable.ic_tomato)
                BottomNavTab.Shop -> Icons.Outlined.Payment
                BottomNavTab.Purchases -> Icons.Outlined.History
            }
            NavigationBarItem(
                icon = { Icon(imageVector = icon, contentDescription = text) },
                label = { Text(text = text) },
                alwaysShowLabel = true,
                selected = selectedTab == item,
                onClick = { onSelectedTab(item) },
                colors = NavigationBarItemDefaults.colors(
                    selectedTextColor = Color.Black,
                    selectedIconColor = Color.Black,
                    unselectedIconColor = Color.Black,
                    unselectedTextColor = Color.Black,
                ),
            )
        }
    }
}
