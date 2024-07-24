package chacha.enerygy.ganghannal.data.message

import com.google.gson.Gson

class MessageUtil {
    fun makeString(target: Any) : String{
        val gson = Gson()
        val dataJson = gson.toJson(target)
        return dataJson
    }
}