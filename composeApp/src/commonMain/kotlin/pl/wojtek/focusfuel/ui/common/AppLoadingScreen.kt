package pl.wojtek.focusfuel.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import focusfuel.composeapp.generated.resources.Res
import focusfuel.composeapp.generated.resources.loading_text
import org.jetbrains.compose.resources.stringResource

@Composable
fun AppLoadingScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(stringResource(Res.string.loading_text))
        CircularProgressIndicator()
    }
}
