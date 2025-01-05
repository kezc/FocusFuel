package pl.wojtek.focusfuel.repository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.util.coroutines.DispatchersProvider

@Inject
class AppSettings(
    private val settings: Settings,
    private val dispatchersProvider: DispatchersProvider
) {
    companion object {
        private const val SOUND_ENABLED = "SOUND_ENABLED"
    }

    suspend fun isSoundEnabled() = withContext(dispatchersProvider.io) {
        settings.getBoolean(SOUND_ENABLED, true)
    }

    suspend fun setSoundEnabled(enabled: Boolean) = withContext(dispatchersProvider.io) {
        settings[SOUND_ENABLED] = enabled
    }
}
