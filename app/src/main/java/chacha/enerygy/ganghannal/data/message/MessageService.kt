package chacha.energy.ganghannal.data.message

import android.content.Context
import android.util.Log
import chacha.enerygy.ganghannal.data.message.dto.Hello
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MessageService(private val context: Context) {
    val messageClient by lazy { Wearable.getMessageClient(context) }
    val capabilityClient by lazy { Wearable.getCapabilityClient(context) }
    private val nodeClient = Wearable.getNodeClient(context)

    fun sendMessage(path: String, message: String){
//        val data = message
//        val gson = Gson()
//        val dataJson = gson.toJson(data)
//        val sendData = dataJson.toByteArray(Charsets.UTF_8)

        CoroutineScope(Dispatchers.Default).launch {
            try {
                val nodes = Tasks.await(nodeClient.connectedNodes)
//                nodes.forEach { node ->
//                    val result = Tasks.await(messageClient.sendMessage(node.id, path, message.toByteArray()))
//                    Log.i("메시지 보내기 1", message.toString())
//                }
                val node = nodes.get(0);
                val result = Tasks.await(messageClient.sendMessage(node.id, path, message.toByteArray(Charsets.UTF_8)))
                Log.i("메시지 보내기 1", message.toString())

            } catch (e: Exception) {
                Log.i("메시지 보내기 1 에러", e.message.toString())
            }
        }

    }
    fun sendMessageToApp(path: String, message: String) {
        val dataClient: DataClient = Wearable.getDataClient(context)

        val putDataMapRequest = PutDataMapRequest.create(path)
        putDataMapRequest.dataMap.putString("message", message)
        val request: PutDataRequest = putDataMapRequest.asPutDataRequest()

        val putDataTask = dataClient.putDataItem(request)

        putDataTask.addOnSuccessListener { dataItem: DataItem? ->
            Log.d("메시지 보내기", "Message sent: $message")
        }.addOnFailureListener { e: Exception? ->
            Log.e("메시지 보내기", "Failed to send message", e)
        }
    }

}