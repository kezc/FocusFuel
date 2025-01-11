package pl.wojtek.focusfuel.features.shop

import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.slack.circuit.overlay.OverlayEffect
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuitx.overlays.DialogResult
import com.slack.circuitx.overlays.alertDialogOverlay
import focusfuel.composeapp.generated.resources.Res
import focusfuel.composeapp.generated.resources.common_no
import focusfuel.composeapp.generated.resources.common_yes
import focusfuel.composeapp.generated.resources.confirm_purchase_title
import org.jetbrains.compose.resources.stringResource
import pl.wojtek.focusfuel.repository.Product

@Composable
fun ConfirmPurchaseDialogHandler(product: Product?, eventSink: (ShopEvent) -> Unit) {
    if (product != null) {
        OverlayEffect(product) {
            val result = showConfirmPurchaseDialog()
            eventSink(
                when (result) {
                    DialogResult.Confirm -> ShopEvent.Buy(product)
                    DialogResult.Cancel, DialogResult.Dismiss -> ShopEvent.DismissConfirmation
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
suspend fun OverlayHost.showConfirmPurchaseDialog(): DialogResult {
    return show(
        alertDialogOverlay(
            title = { Text(stringResource(Res.string.confirm_purchase_title)) },
            confirmButton = { onClick -> Button(onClick = onClick) { Text(stringResource(Res.string.common_yes)) } },
            dismissButton = { onClick -> Button(onClick = onClick) { Text(stringResource(Res.string.common_no)) } },
        )
    )
}
