package pl.wojtek.focusfuel.notifications

interface NotificationSender {
    fun sendNotification(title: String, body: String, id: Int)
}
