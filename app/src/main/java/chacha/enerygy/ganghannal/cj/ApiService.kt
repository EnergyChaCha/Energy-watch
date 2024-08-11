package chacha.enerygy.ganghannal.cj

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST


data class MyData(val key: Int)
data class MyResponse(val result: String)
interface ApiService {
    @POST("stepcount")
    fun postData(@Body data: MyData): Call<Any>
}