package pl.wojtek.focusfuel.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun rememberSnackbarHostState() = remember { SnackbarHostState() }

@Composable
fun AppSnackbarHost(
    hostState: SnackbarHostState,
    modifier: Modifier = Modifier,
) {
    SnackbarHost(
        modifier = Modifier
            .fillMaxWidth()
            .imePadding()
            .then(modifier),
        hostState = hostState
    ) { snackbarData ->
        Snackbar(snackbarData = snackbarData)
    }
}
