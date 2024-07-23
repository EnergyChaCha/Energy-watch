package chacha.energy.ganghannal.data.message

import android.util.Log
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.WearableListenerService

class WearableListenerService : WearableListenerService() {
    companion object {
        private const val TAG = "WearableListenerService"
    }

    override fun onMessageReceived(messageEvent: MessageEvent) {
        Log.d(TAG, "메시지 received: ${messageEvent.path}")
        // 메시지를 처리하는 코드를 여기에 작성합니다.
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        for (event in dataEvents) {
            if (event.type == DataEvent.TYPE_CHANGED) {
                val item = event.dataItem
                if ("/path" == item.uri.path) {
                    val dataMap = DataMap.fromByteArray(
                        item.data!!
                    )
                    val message = dataMap.getString("message")
                    Log.d("메시지 받기", "Received message: $message")
                }
            }
        }
    }
}