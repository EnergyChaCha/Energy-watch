/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package chacha.enerygy.ganghannal.presentation

import android.Manifest
import android.app.Activity
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
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
    var permissionAgree = false

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
            var isMonitoringStarted by remember { mutableStateOf(false) }
            val heartRates = remember { mutableListOf<Float>() }

            // 심박수 업데이트를 처리하는 효과를 생성합니다
            LaunchedEffect(Unit) {
                context.onHeartRateUpdate = { newHeartRate ->
//                    heartRate = newHeartRate

                    // 심박수 데이터 수집 및 최빈값 계산
                    heartRates.add(newHeartRate)
                    if (heartRates.size > 15) {
                        heartRates.removeAt(0)
                    }

                    val mostFrequentHeartRate = heartRates.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: 0f
                    heartRate = mostFrequentHeartRate

                    // 모니터링 여부 반환
                    if( !isMonitoringStarted && heartRate.toInt() > 0 ) isMonitoringStarted = true
                    if( isMonitoringStarted && heartRate.toInt() == 0 ) isMonitoringStarted = false
                }
            }

            MainApp(heartRate, adminViewModel, memberViewModel, isMonitoringStarted, permissionAgree)
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
                startHeartRateService()
                permissionAgree = true
            } else {
                // 권한이 거부되었을 때의 처리를 합니다
                // 예를 들어, 사용자에게 권한이 필요하다는 메시지를 표시합니다
                Log.i("권한", "권한 거부됨")
            }
        }
    }
}

@Composable
fun MainApp(bpm: Float = 90.0F, adminViewModel: AdminViewModel, memberViewModel: MemberViewModel, isMonitoringStarted: Boolean, permissionAgree: Boolean) {
    GangHanNalTheme {
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


            // 권한
            if (!permissionAgree) {
                val context = LocalContext.current as MainActivity
                Card(
                    onClick = { /* 클릭 이벤트 처리 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    backgroundPainter = ColorPainter(MaterialTheme.colors.surface), // 배경 설정
                    contentColor = MaterialTheme.colors.onSurface,
                    shape = RoundedCornerShape(8.dp)
                )
                {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text(
                            text = "권한을 허용하지 않으면 사용할 수 없습니다.",
                            color = AppColor.textWhite.color,
                            fontSize = MaterialTheme.typography.body2.fontSize,
                        )
                        Text(
                            text = "종료 버튼을 누르면 좋료합니다.",
                            color = AppColor.textWhite.color,
                            fontSize = MaterialTheme.typography.body2.fontSize,
                        )
                        CompactChip(onClick = {
                            // 어플리케이션 종료
                            context.finish() },
                            colors = ChipDefaults.chipColors(AppColor.secondary.color),
                            label = {
                                Text(
                                    text = "종료",
                                    color = AppColor.textWhite.color,
                                    fontSize = MaterialTheme.typography.title3.fontSize,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        )
                    }
                }
            }

            // 시계 바르게 착용 알림
            if (permissionAgree && !isMonitoringStarted) {
                Card(
                    onClick = { /* 클릭 이벤트 처리 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    backgroundPainter = ColorPainter(MaterialTheme.colors.surface), // 배경 설정
                    contentColor = MaterialTheme.colors.onSurface,
                    shape = RoundedCornerShape(8.dp)

                )
                {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ){
                        Text(
                            text = "심박수를 측정중입니다.",
                            color = AppColor.textWhite.color,
                            fontSize = MaterialTheme.typography.body2.fontSize,
                        )
                        Text(
                            text = "워치를 올바르게 착용해 주세요.",
                            color = AppColor.textWhite.color,
                            fontSize = MaterialTheme.typography.body2.fontSize,
                        )
                    }
                }
            }
        }
    }
}

