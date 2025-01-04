package pl.wojtek.focusfuel.features.pomodoro

import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.NotifierManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class PomodoroNotificationsManager(
    private val notifier: Notifier = NotifierManager.getLocalNotifier(),
    private val coroutineScope: CoroutineScope,
    private val pomodoroTimer: PomodoroTimer
) {
    fun init() {
        pomodoroTimer.phaseFinished
            .filterNotNull()
            .map { sendNotification(it) }
            .launchIn(coroutineScope)
    }

    private fun sendNotification(finishedPhase: PomodoroPhase) {
        notifier.notify {
            id = 100
            title = "Title from KMPNotifier"
            body = "Body message from KMPNotifier"
        }
    }
}
