package pl.wojtek.focusfuel.notification

import focusfuel.composeapp.generated.resources.Res
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.ExperimentalResourceApi
import pl.wojtek.focusfuel.notifications.NotificationSender
import pl.wojtek.focusfuel.notifications.SimpleNotificationSender
import pl.wojtek.focusfuel.repository.AppSettings
import pl.wojtek.focusfuel.util.coroutines.DispatchersProvider
import java.io.ByteArrayInputStream
import javax.sound.sampled.AudioSystem

@Inject
class SoundNotificationSender(
    private val simpleNotificationSender: SimpleNotificationSender,
    private val coroutineScope: CoroutineScope,
    private val dispatchersProvider: DispatchersProvider,
    private val appSettings: AppSettings,
) : NotificationSender {
    override fun sendNotification(title: String, body: String, id: Int) {
        playSound()
        simpleNotificationSender.sendNotification(title, body, id)
    }

    @OptIn(ExperimentalResourceApi::class)
    private fun playSound() = coroutineScope.launch(dispatchersProvider.io) {
        if (!appSettings.isSoundEnabled()) return@launch

        try {
            val stream = ByteArrayInputStream(Res.readBytes("files/notification_sound.wav"))
            val inputStream = AudioSystem.getAudioInputStream(stream)
            AudioSystem.getClip().apply {
                open(inputStream)
                start()
            }
            inputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
