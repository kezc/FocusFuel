package pl.wojtek.focusfuel.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(AppScope::class)
@Inject
class AppInForegroundNotifier : DefaultLifecycleObserver {

    private val _isAppInForeground = MutableStateFlow(false)
    val isAppInForegroundFlow: StateFlow<Boolean> = _isAppInForeground
    val isAppInForeground: Boolean
        get() = _isAppInForeground.value

    override fun onStart(owner: LifecycleOwner) {
        _isAppInForeground.tryEmit(true)
    }

    override fun onStop(owner: LifecycleOwner) {
        _isAppInForeground.tryEmit(false)
    }
}
