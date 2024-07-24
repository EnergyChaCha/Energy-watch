package chacha.enerygy.ganghannal.data.message.dto

class NotificationItem {
    var bpm: Float = 0F
    var message: String = "데이터가 존재하지 않습니다."
    var timestamp: String = ""

    constructor(bpm: Float, message: String, timestamp: String) {
        this.bpm = bpm
        this.message = message
        this.timestamp = timestamp
    }
    override fun toString(): String {
        return "NotificationItem(bpm=$bpm, message='$message', timestamp='$timestamp')"
    }
}