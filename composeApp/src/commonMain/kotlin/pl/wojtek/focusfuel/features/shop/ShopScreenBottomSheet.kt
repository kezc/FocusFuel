package pl.wojtek.focusfuel.features.shop

import androidx.compose.runtime.Composable
import com.slack.circuit.overlay.OverlayEffect
import com.slack.circuit.overlay.OverlayScope
import pl.wojtek.focusfuel.features.shop.ShopEvent.DeleteProduct
import pl.wojtek.focusfuel.features.shop.ShopEvent.HideProductBottomSheet
import pl.wojtek.focusfuel.features.shop.ShopEvent.NavigateToEditProduct
import pl.wojtek.focusfuel.util.circuit.Action
import pl.wojtek.focusfuel.util.circuit.showBottomSheet

@Composable
fun ProductBottomSheetHandler(state: ShopState) {
    if (state.showProductBottomSheet != null) {
        OverlayEffect(state) {
            val result = showShopBottomSheet()
            state.eventSink(result?.event ?: HideProductBottomSheet)
        }
    }
}

private suspend fun OverlayScope.showShopBottomSheet(): ShopAction? =
    showBottomSheet(
        listOf(
            Action("Edit", ShopAction.EDIT),
            Action("Delete", ShopAction.DELETE)
        )
    )

private enum class ShopAction(val event: ShopEvent) {
    EDIT(NavigateToEditProduct), DELETE(DeleteProduct)
}

