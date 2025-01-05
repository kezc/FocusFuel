package pl.wojtek.focusfuel.notifications

import com.mmk.kmpnotifier.notification.Notifier
import com.mmk.kmpnotifier.notification.NotifierManager
import me.tatarka.inject.annotations.Inject


@Inject
class SimpleNotificationSender(
    private val notifier: Notifier = NotifierManager.getLocalNotifier(),
) : NotificationSender {
    override fun sendNotification(title: String, body: String, id: Int) {
        notifier.notify {
            this.title = title
            this.body = body
            this.id = id
        }
    }
}
