package chacha.enerygy.ganghannal.cj

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.util.Log

class StepCounterService : Service(), SensorEventListener {

    companion object {
        private const val CHANNEL_ID = "StepCounterServiceChannel"
        private const val NOTIFICATION_ID = 1
    }

    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private val TAG = "본선 걸음수"

    @SuppressLint("ForegroundServiceType")
    override fun onCreate() {
        super.onCreate()

        // SensorManager 초기화
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // 센서 리스너 등록
        stepSensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_FASTEST)
            Log.d(TAG, "걸음 센서 등록 성공")
        } ?: run {
            Log.d(TAG, "걸음 센서를 사용할 수 없습니다.")
        }

        // Notification 설정
        val notification = createNotification()
        startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION)
    }

    private fun createNotification(): Notification {
        val channelId = "step_counter_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Step Counter Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        return Notification.Builder(this, channelId)
            .setContentTitle("Step Counter Service")
            .setContentText("Counting steps...")
//            .setSmallIcon(R.drawable.ic_step_counter) // 적절한 아이콘을 사용하세요
            .build()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            val steps = it.values[0].toInt()
            Log.d(TAG, "걸음 수: $steps")
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 정확도 변경 처리
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
        Log.d(TAG, "걸음 센서 리스너 해제")
    }
}