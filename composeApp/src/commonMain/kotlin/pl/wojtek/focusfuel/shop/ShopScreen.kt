package pl.wojtek.focusfuel.shop


import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import org.jetbrains.compose.ui.tooling.preview.Preview
import pl.wojtek.focusfuel.util.parcelize.CommonParcelize
import software.amazon.lastmile.kotlin.inject.anvil.AppScope

@CommonParcelize
object ShopScreen : Screen

@CircuitInject(ShopScreen::class, AppScope::class)
class ShopUI : Ui<ShopState> {
    @Composable
    override fun Content(state: ShopState, modifier: Modifier) {
        ShopUI(modifier, state)
    }
}

@Composable
fun ShopUI(
    modifier: Modifier = Modifier,
    state: ShopState,
) {

}

@Preview
@Composable
fun ShopScreenPreview() {
    ShopUI()
}
