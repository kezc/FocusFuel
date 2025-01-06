package pl.wojtek.focusfuel.ui.util

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import pl.wojtek.focusfuel.util.either.EitherT

@Composable
fun <T> rememberDisappearingState(delayMs: Long = 3000): MutableState<T?> {
    val state = remember { mutableStateOf<T?>(null) }
    LaunchedEffect(state.value) {
        if (state.value != null) {
            delay(delayMs)
            state.value = null
        }
    }
    return state
}

fun <T> Flow<EitherT<T>>.observeError(state: MutableState<Throwable?>): Flow<EitherT<T>> =
    onEach { either -> either.onLeft { state.value = it } }

fun <T> EitherT<T>.observeError(state: MutableState<Throwable?>): EitherT<T> =
    onLeft { state.value = it }

@Composable
fun ShowSnackbarHandler(snackbarHostState: SnackbarHostState, message: String?) {
    if (message != null) {
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
        }
    }
}
