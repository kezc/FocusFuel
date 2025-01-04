package pl.wojtek.focusfuel.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp

fun PaddingValues.withoutBottom(): PaddingValues = object : PaddingValues {
    override fun calculateBottomPadding(): Dp = 0.dp

    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        this@withoutBottom.calculateLeftPadding(layoutDirection)

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        this@withoutBottom.calculateRightPadding(layoutDirection)

    override fun calculateTopPadding(): Dp = this@withoutBottom.calculateTopPadding()
}
