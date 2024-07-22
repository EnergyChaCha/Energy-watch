package chacha.enerygy.ganghannal.data.heartrate

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class HeartRateService : Service() {
    private lateinit var heartRateMonitor: HeartRateMonitor

    override fun onCreate() {
        super.onCreate()

        Log.i("심박수", "서비스 onCreate")
        heartRateMonitor = HeartRateMonitor(this)
        heartRateMonitor.onHeartRateChanged = { heartRate ->
            val intent = Intent("HeartRateUpdate")
            intent.putExtra("heartRate", heartRate)
            sendBroadcast(intent)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "heart_rate_channel",
                "Heart Rate Monitor",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, "heart_rate_channel")
            .setContentTitle("Heart Rate Monitor")
            .setContentText("Monitoring heart rate in the background")
//            .setSmallIcon(R.drawable.ic_heart_rate)
            .build()

        startForeground(1, notification)
    }



    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i("심박수", "서비스 onStartCommand 호출됨")
        heartRateMonitor.startMonitoring()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        heartRateMonitor.stopMonitoring()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
