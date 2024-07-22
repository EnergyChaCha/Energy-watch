/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package chacha.enerygy.ganghannal.presentation

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Text
import chacha.enerygy.ganghannal.data.heartrate.HeartRateMonitor
import chacha.enerygy.ganghannal.data.heartrate.HeartRateService
import chacha.enerygy.ganghannal.presentation.constant.NavigationRoute
import chacha.enerygy.ganghannal.presentation.screen.main.MainScreen
import chacha.enerygy.ganghannal.presentation.screen.notification.PagerScreen
import chacha.enerygy.ganghannal.presentation.screen.report.ReportScreen
import chacha.enerygy.ganghannal.presentation.theme.AppColor
import chacha.enerygy.ganghannal.presentation.theme.GangHanNalTheme
import chacha.enerygy.ganghannal.presentation.viewmodel.AdminViewModel
import chacha.enerygy.ganghannal.presentation.viewmodel.MemberViewModel

class MainActivity : ComponentActivity() {
    private val adminViewModel: AdminViewModel by viewModels()
    private val memberViewModel: MemberViewModel by viewModels()
    private val REQUEST_BODY_SENSORS_PERMISSION = 1
//    private lateinit var heartRateMonitor: HeartRateMonitor

    private val heartRateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val heartRate = intent?.getFloatExtra("heartRate", 0f) ?: 0f
            onHeartRateUpdate(heartRate)
        }
    }

    var onHeartRateUpdate: (Float) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)


        // BODY_SENSORS 권한을 요청합니다
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BODY_SENSORS),
                REQUEST_BODY_SENSORS_PERMISSION
            )
        } else {
            startHeartRateService()
        }

        // 브로드캐스트 리시버 등록
        registerReceiver(heartRateReceiver, IntentFilter("HeartRateUpdate"))

        setContent {
            var heartRate by remember { mutableStateOf(0f) }
            val context = LocalContext.current as MainActivity

            // 심박수 업데이트를 처리하는 효과를 생성합니다
            LaunchedEffect(Unit) {
                context.onHeartRateUpdate = { newHeartRate ->
                    heartRate = newHeartRate
                    Log.i("심박수", heartRate.toString())
                }
            }

            MainApp(heartRate, adminViewModel, memberViewModel)
        }



    }

    private fun startHeartRateService() {
        Log.i("심박수", "startHeartRateService 함수 시작")
        val intent = Intent(this, HeartRateService::class.java)
        ContextCompat.startForegroundService(this, intent)
        Log.i("심박수", "startHeartRateService 종료")
    }


    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BODY_SENSORS_PERMISSION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // 권한이 부여되었으므로 심박수 모니터링을 시작합니다
//                startHeartRateMonitoring()
                startHeartRateService()
            } else {

                // 권한이 거부되었을 때의 처리를 합니다
                // 예를 들어, 사용자에게 권한이 필요하다는 메시지를 표시합니다
                Log.i("권한", "권한 거부됨")
            }
        }
    }

//    private fun startHeartRateMonitoring() {
//        // 심박수 모니터링을 시작하는 로직을 여기에 추가합니다
//        heartRateMonitor.startMonitoring()
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        heartRateMonitor.stopMonitoring()
//    }

}

@Composable
fun MainApp(bpm: Float = 90.0F, adminViewModel: AdminViewModel, memberViewModel: MemberViewModel) {
    GangHanNalTheme {
//        var heartRate by remember { mutableStateOf(0f) }
//        val context = LocalContext.current as MainActivity
//
//        // 심박수 업데이트를 처리하는 효과를 생성합니다
//        LaunchedEffect(context) {
//            context.onHeartRateUpdate = { newHeartRate ->
//                heartRate = newHeartRate
//            }
//        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.background.color),
            contentAlignment = Alignment.Center
        ) {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = NavigationRoute.MAIN.name) {
                composable(NavigationRoute.MAIN.name) { MainScreen(navController, bpm) }
                composable(NavigationRoute.NOTIFICATION.name) {
                    PagerScreen(adminViewModel)
                }
                composable(NavigationRoute.REPORT.name) { ReportScreen(memberViewModel) }
            }
        }
    }
}

