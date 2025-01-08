package pl.wojtek.focusfuel.ui.util

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.runtime.Stable
import androidx.compose.ui.unit.Density
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

operator fun PaddingValues.plus(that: PaddingValues): PaddingValues = object : PaddingValues {
    override fun calculateBottomPadding(): Dp =
        this@plus.calculateBottomPadding() + that.calculateBottomPadding()

    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        this@plus.calculateLeftPadding(layoutDirection) + that.calculateLeftPadding(layoutDirection)

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        this@plus.calculateRightPadding(layoutDirection) +
                that.calculateRightPadding(layoutDirection)

    override fun calculateTopPadding(): Dp =
        this@plus.calculateTopPadding() + that.calculateTopPadding()
}


operator fun PaddingValues.minus(that: PaddingValues): PaddingValues = object : PaddingValues {
    override fun calculateBottomPadding(): Dp =
        this@minus.calculateBottomPadding() - that.calculateBottomPadding()

    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp =
        this@minus.calculateLeftPadding(layoutDirection) - that.calculateLeftPadding(layoutDirection)

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp =
        this@minus.calculateRightPadding(layoutDirection) -
                that.calculateRightPadding(layoutDirection)

    override fun calculateTopPadding(): Dp =
        this@minus.calculateTopPadding() - that.calculateTopPadding()
}


fun PaddingValues.onlyBottom(): PaddingValues = object : PaddingValues {
    override fun calculateBottomPadding(): Dp = this@onlyBottom.calculateBottomPadding()

    override fun calculateLeftPadding(layoutDirection: LayoutDirection): Dp = 0.dp

    override fun calculateRightPadding(layoutDirection: LayoutDirection): Dp = 0.dp

    override fun calculateTopPadding(): Dp = 0.dp
}


@Stable
 data class PaddingValuesInsets(private val paddingValues: PaddingValues) : WindowInsets {
    override fun getLeft(density: Density, layoutDirection: LayoutDirection) = with(density) {
        paddingValues.calculateLeftPadding(layoutDirection).roundToPx()
    }

    override fun getTop(density: Density) = with(density) {
        paddingValues.calculateTopPadding().roundToPx()
    }

    override fun getRight(density: Density, layoutDirection: LayoutDirection) = with(density) {
        paddingValues.calculateRightPadding(layoutDirection).roundToPx()
    }

    override fun getBottom(density: Density) = with(density) {
        paddingValues.calculateBottomPadding().roundToPx()
    }
}
