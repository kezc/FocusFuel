package pl.wojtek.focusfuel.ui.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.delay

@Composable
fun ShowAnimatedText(
    text: String?,
    durationMillis: Int = 300,
    textComposable: @Composable (String) -> Unit = { Text(text = it) },
) {
    var localText by remember {
        mutableStateOf<String?>(null)
    }
    AnimatedContent(text != null, localText, textComposable, durationMillis)
    LaunchedEffect(key1 = text, block = {
        if (text == null) {
            delay(durationMillis.toLong())
        }
        localText = text
    })
}

@Composable
private fun AnimatedContent(
    show: Boolean,
    localText: String?,
    textComposable: @Composable (String) -> Unit,
    durationMillis: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        AnimatedVisibility(
            visible = show,
            enter = expandVertically(animationSpec = tween(durationMillis))
                    + fadeIn(animationSpec = tween(durationMillis)),
            exit = shrinkVertically(animationSpec = tween(durationMillis))
                    + fadeOut(animationSpec = tween(durationMillis))
        ) {
            localText?.let {
                textComposable(it)
            }
        }
    }
}
