package chacha.enerygy.ganghannal.data.heartrate
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class HeartRateService : Service() {

    private lateinit var heartRateMonitor: HeartRateMonitor

    override fun onCreate() {
        super.onCreate()
        heartRateMonitor = HeartRateMonitor(this)
        heartRateMonitor.onHeartRateChanged = { heartRate ->
            // 심박수 데이터를 처리합니다.
        }
        heartRateMonitor.startMonitoring()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "heart_rate_channel",
                "Heart Rate Monitor",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, "heart_rate_channel")
            .setContentTitle("Heart Rate Monitor")
            .setContentText("Monitoring your heart rate")
//            .setSmallIcon(R.drawable.ic_heart)
            .build()

        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        heartRateMonitor.stopMonitoring()
    }
}
