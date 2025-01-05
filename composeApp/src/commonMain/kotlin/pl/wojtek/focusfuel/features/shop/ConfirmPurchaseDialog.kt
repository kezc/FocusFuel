package pl.wojtek.focusfuel.features.shop

import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.slack.circuit.overlay.OverlayEffect
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuitx.overlays.DialogResult
import com.slack.circuitx.overlays.alertDialogOverlay
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
            title = { Text("Are you sure?") },
            confirmButton = { onClick -> Button(onClick = onClick) { Text("Yes") } },
            dismissButton = { onClick -> Button(onClick = onClick) { Text("No") } },
        )
    )
}
