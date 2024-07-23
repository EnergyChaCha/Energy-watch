package chacha.enerygy.ganghannal.data.message.dto

class Message<T>(val order: String, val data: T) {
    override fun toString(): String {
        return "RequestMessage(order='$order', data=$data)"
    }
}