package pl.wojtek.focusfuel.notification

import me.tatarka.inject.annotations.Inject
import pl.wojtek.focusfuel.notifications.NotificationSender
import pl.wojtek.focusfuel.notifications.SimpleNotificationSender
import pl.wojtek.focusfuel.util.AppInForegroundNotifier

@Inject
class BackgroundNotificationSender(
    private val simpleNotificationSender: SimpleNotificationSender,
    private val appInForegroundNotifier: AppInForegroundNotifier,
): NotificationSender {
    override fun sendNotification(title: String, body: String, id: Int) {
        if (!appInForegroundNotifier.isAppInForeground) {
            simpleNotificationSender.sendNotification(title, body, id)
        } else {
            simpleNotificationSender.remove(id)
        }
    }
}
