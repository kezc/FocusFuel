package pl.wojtek.focusfuel.mainscreen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.touchlab.kermit.Logger
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.haze
import dev.chrisbanes.haze.hazeChild
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

val MainScaffoldContentPadding = staticCompositionLocalOf { PaddingValues(0.dp) }

@CircuitInject(screen = MainScreen::class, scope = AppScope::class)
class MainUI : Ui<MainState> {
    @Composable
    override fun Content(state: MainState, modifier: Modifier) {
        val hazeState = remember { HazeState() }
        Scaffold(
            modifier = modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                BottomNavigationBar(selectedTab = state.selectedTab, hazeState) { index ->
                    state.eventSink(TabClick(index))
                }
            },
        ) { paddingValues ->
            CompositionLocalProvider(MainScaffoldContentPadding provides paddingValues) {
                CircuitContent(
                    state.selectedTab.screen,
                    modifier = Modifier.haze(hazeState),
                    onNavEvent = { event -> state.eventSink(ChildNavEvent(event)) },
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    selectedTab: BottomNavTab,
    hazeState: HazeState,
    windowInsets: WindowInsets = NavigationBarDefaults.windowInsets,
    onSelectedTab: (BottomNavTab) -> Unit,
) {
    var tabPressed by remember { mutableStateOf(selectedTab) }
    Box(
        modifier = Modifier
            .padding(vertical = 12.dp, horizontal = 24.dp)
            .fillMaxWidth()
            .windowInsetsPadding(windowInsets)
            .height(64.dp)
            .clip(shape = CircleShape)
            .hazeChild(
                state = hazeState, style = HazeStyle(
                    backgroundColor = MaterialTheme.colorScheme.background,
                    tint = HazeTint(Color.White.copy(alpha = .2f)),
                    blurRadius = 32.dp,
                )
            )
            .border(
                width = Dp.Hairline,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.White.copy(alpha = .8f),
                        Color.White.copy(alpha = .2f),
                    ),
                ),
                shape = CircleShape
            )
    ) {
        BottomBarTabs(
            selectedTab = selectedTab,
            onTabSelected = onSelectedTab,
            onTabPressed = { tabPressed = it; Logger.d("DUPA") { it.toString() } }
        )
        val animatedSelectedTabIndex by animateFloatAsState(targetValue = NAV_ITEMS.indexOf(tabPressed).toFloat())
        BottomBarTabIndicator(animatedSelectedTabIndex)
    }
}

@Composable
private fun BottomBarTabIndicator(animatedSelectedTabIndex: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(CircleShape)
            .blur(50.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded)
            .drawBehind {
                val tabWidth = size.width / NAV_ITEMS.size
                drawCircle(
                    color = Color.Red.copy(alpha = .75f),
                    radius = size.height / 2,
                    center = Offset(
                        (tabWidth * animatedSelectedTabIndex) + tabWidth / 2,
                        size.height / 2
                    )
                )
            }
    )
}


@Composable
fun BottomBarTabs(
    selectedTab: BottomNavTab,
    onTabSelected: (BottomNavTab) -> Unit,
    onTabPressed: (BottomNavTab) -> Unit,
) {
    val selectedTabState by rememberUpdatedState(selectedTab)
    SideEffect {
        Logger.d("DUPA") { "BottomBarTabs selectedTab: $selectedTab" }
    }
    CompositionLocalProvider(
        LocalTextStyle provides LocalTextStyle.current.copy(
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            for (tab in NAV_ITEMS) {
                val text = stringResource(
                    when (tab) {
                        BottomNavTab.Pomodoro -> Res.string.bottom_bar_pomodoro
                        BottomNavTab.Shop -> Res.string.bottom_bar_shop
                        BottomNavTab.Purchases -> Res.string.bottom_bar_purchases
                    }
                )
                val icon = when (tab) {
                    BottomNavTab.Pomodoro -> vectorResource(Res.drawable.ic_tomato)
                    BottomNavTab.Shop -> Icons.Outlined.Payment
                    BottomNavTab.Purchases -> Icons.Outlined.History
                }
                val alpha by animateFloatAsState(
                    targetValue = if (selectedTab == tab) 1f else .40f,
                    label = "alpha"
                )
                val scale by animateFloatAsState(
                    targetValue = if (selectedTab == tab) 1f else .98f,
                    label = "scale"
                )
                Column(
                    modifier = Modifier
                        .scale(scale)
                        .alpha(alpha)
                        .fillMaxHeight()
                        .weight(1f)
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onPress = {
                                    onTabPressed(tab)
                                    if (!tryAwaitRelease()) {
                                        onTabPressed(selectedTabState)
                                    }
                                },
                                onTap = { onTabSelected(tab) }
                            )
                        },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Icon(imageVector = icon, contentDescription = text)
                    Text(text = text)
                }
            }
        }
    }
}
