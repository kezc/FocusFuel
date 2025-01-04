package pl.wojtek.focusfuel.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun rememberSnackbarHostState() = remember { SnackbarHostState() }

@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
    windowInsets: WindowInsets = WindowInsets.systemBars,
) {
    SnackbarHost(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(windowInsets)
            .imePadding()
            .padding(16.dp)
            .then(modifier),
        hostState = hostState
    ) { snackbarData ->
//        val isError = snackbarData.visuals is ErrorSnackbarVisuals
//        val snackbarTheme = if (isError) SnackbarTheme.error() else SnackbarTheme.success()
        Snackbar(snackbarData = snackbarData)
    }
}
