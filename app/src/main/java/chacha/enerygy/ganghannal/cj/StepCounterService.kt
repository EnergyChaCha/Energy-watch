package chacha.enerygy.ganghannal.cj

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class StepCounterService : Service(), SensorEventListener {

    companion object {
        private const val CHANNEL_ID = "StepCounterServiceChannel"
        private const val NOTIFICATION_ID = 2
        private const val TAG = "본선 걸음수 서비스"
    }

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var handler: Handler? = null
    private var previousStepCount: Int = 0
    private val stepCountRunnable = object : Runnable {
        override fun run() {
            // Step count measurement logic
            Log.i(TAG, "Step count: ${getStepCount()}")
            sendPostRequest(getStepCount())
            handler?.postDelayed(this, 5000) // 5초마다 실행
        }
    }
    private lateinit var wakeLock: PowerManager.WakeLock
    override fun onCreate() {
        super.onCreate()

        // Wake Lock 설정
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "StepCounterService::WakeLock")
        wakeLock.acquire(10*60*1000L /*10 minutes*/)

        // Notification Channel 생성 (Android 8.0 이상에서 필요)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Step Counter Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }

        // Notification 생성
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Step Counter Service")
            .setContentText("Running...")
//            .setSmallIcon(R.drawable.ic_notification)
            .build()

        // Foreground Service로 실행
        startForeground(NOTIFICATION_ID, notification)

        // 센서 및 핸들러 초기화
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
        handler = Handler(Looper.getMainLooper())

        // 센서 리스너 등록
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
        }

        // 1초마다 걸음 수 측정
        handler?.post(stepCountRunnable)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY // 서비스가 강제 종료되었을 때 다시 시작하도록 설정
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null // 바인딩할 필요 없음
    }

    override fun onDestroy() {
        super.onDestroy()
        // 서비스 종료 시 리소스 정리
        sensorManager.unregisterListener(this)
        handler?.removeCallbacks(stepCountRunnable)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val steps = it.values[0].toInt()
            previousStepCount = event.values[0].toInt()
            Log.d(TAG, "걸음 수: $steps")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 정확도 변경 이벤트 처리 (필요 시)
    }

    private fun getStepCount(): Int {
        // 걸음 수를 얻는 로직을 구현
        return previousStepCount
    }

    fun sendPostRequest(data: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://server.ganghannal.life/api/cj/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val data = MyData(data)
        val call = service.postData(data)

        call.enqueue(object : Callback<Any> {

            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    val responseData = response.body()
                    Log.i("본선 요청","Response: 성공")
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                t.printStackTrace()
                Log.i("본선 요청","실패")
            }
        })
    }

}
