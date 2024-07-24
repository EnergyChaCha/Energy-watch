package chacha.enerygy.ganghannal.data.message.dto

class NotificationList {
    var notifications = mutableListOf<NotificationItem>()

    constructor(notifications: MutableList<NotificationItem>) {
        this.notifications = notifications
    }

    override fun toString(): String {
        return "NotificationList(notifications=$notifications)"
    }
}