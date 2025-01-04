package pl.wojtek.focusfuel.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(AppScope::class)
@Inject
class AppInForegroundNotifier : DefaultLifecycleObserver {

    private val _isAppInForeground = MutableStateFlow(false)
    private val isAppInForeground: Flow<Boolean> = _isAppInForeground

    override fun onStart(owner: LifecycleOwner) {
        _isAppInForeground.tryEmit(true)
    }

    override fun onStop(owner: LifecycleOwner) {
        _isAppInForeground.tryEmit(false)
    }
}
