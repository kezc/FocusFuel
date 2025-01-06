package pl.wojtek.focusfuel.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import arrow.atomic.AtomicBoolean
import arrow.atomic.AtomicInt
import com.slack.circuit.retained.rememberRetained
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart

class ProgressCounter(initialValue: Boolean) {
    val progress = AtomicInt()
    private val _state = mutableStateOf(initialValue)
    val state: State<Boolean> = _state

    fun addProgress() {
        progress.incrementAndGet()
        _state.value = true
    }

    fun removeProgress() {
        _state.value = progress.decrementAndGet() != 0
    }
}

@Composable
fun rememberRetainedProgressCounter(initialValue: Boolean): ProgressCounter =
    rememberRetained { ProgressCounter(initialValue) }

inline fun <T> ProgressCounter.watchProgress(block: () -> T): T {
    addProgress()
    return try {
        block()
    } finally { removeProgress() }
}

fun <T> Flow<T>.watchProgress(progressCounter: ProgressCounter): Flow<T> {
    val wasFirstValue = AtomicBoolean(false)
    return this
        .onStart {
            progressCounter.addProgress()
            wasFirstValue.set(false)
        }
        .onEach {
            if (!wasFirstValue.get()) {
                progressCounter.removeProgress()
                wasFirstValue.set(true)
            }
        }
}
