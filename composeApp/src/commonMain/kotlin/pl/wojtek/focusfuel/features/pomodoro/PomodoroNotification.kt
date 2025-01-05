package pl.wojtek.focusfuel.features.pomodoro

import focusfuel.composeapp.generated.resources.Res
import focusfuel.composeapp.generated.resources.notification_long_break_title
import focusfuel.composeapp.generated.resources.notification_short_break_title
import focusfuel.composeapp.generated.resources.notification_work_title
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.getString
import pl.wojtek.focusfuel.notifications.NotificationSender

@Inject
class PomodoroNotificationsManager(
    private val notificationSender: NotificationSender,
    private val coroutineScope: CoroutineScope,
    private val pomodoroTimer: PomodoroTimer
) {
    fun init() {
        pomodoroTimer.phaseFinished
            .filterNotNull()
            .map { sendNotification(it) }
            .launchIn(coroutineScope)
    }

    private fun sendNotification(finishedPhase: PomodoroPhase) = coroutineScope.launch {
        notificationSender.sendNotification(
            title = getNotificationText(finishedPhase),
            body = "",
            id = 100
        )
    }

    private suspend fun getNotificationText(phase: PomodoroPhase): String {
        return when (phase) {
            PomodoroPhase.WORK -> getString(Res.string.notification_work_title)
            PomodoroPhase.SHORT_BREAK -> getString(Res.string.notification_short_break_title)
            PomodoroPhase.LONG_BREAK -> getString(Res.string.notification_long_break_title)
        }
    }
}
