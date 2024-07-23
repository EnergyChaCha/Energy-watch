/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package chacha.energy.ganghannal.presentation

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
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import chacha.energy.ganghannal.data.heartrate.HeartRateService
import chacha.energy.ganghannal.data.message.MessageService
import chacha.energy.ganghannal.presentation.constant.NavigationRoute
import chacha.energy.ganghannal.presentation.screen.main.MainScreen
import chacha.energy.ganghannal.presentation.screen.notification.PagerScreen
import chacha.energy.ganghannal.presentation.screen.report.ReportScreen
import chacha.energy.ganghannal.presentation.theme.AppColor
import chacha.energy.ganghannal.presentation.theme.GangHanNalTheme
import chacha.energy.ganghannal.presentation.viewmodel.AdminViewModel
import chacha.energy.ganghannal.presentation.viewmodel.MemberViewModel
import chacha.enerygy.ganghannal.data.message.dto.Hello
import chacha.enerygy.ganghannal.data.message.dto.MemberInfo
import chacha.enerygy.ganghannal.data.message.dto.Message
import chacha.enerygy.ganghannal.data.message.dto.Order
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson

class MainActivity : ComponentActivity() {
    private val adminViewModel: AdminViewModel by viewModels()
    private val memberViewModel: MemberViewModel by viewModels()
    private val REQUEST_BODY_SENSORS_PERMISSION = 1
    var permissionAgree = true

    private var lastNotificationTime: Long = 0
    private val notificationCooldown: Long = 1000 * 60 * 5// 5분 (밀리초 단위)
    private lateinit var  messageService: MessageService


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

        Wearable.getMessageClient(this).addListener { event ->
            val dataString = String(event.data, Charsets.UTF_8)
            val gson = Gson()


            Log.i("메시지", "워치 메시지 받음: ${event.path} ${dataString}")

            if (event.path.equals(Order.HELLO.name)) {
                val dataObject = gson.fromJson(dataString, Hello::class.java)
                Log.i("메시지", "${Order.HELLO.name} 겟 ${dataObject.toString()}")
            } else if(event.path.equals(Order.MEMBER_INFO.name)) {
                val dataObject = gson.fromJson(dataString, MemberInfo::class.java)
                Log.i("메시지", "${Order.MEMBER_INFO.name} 겟 ${dataObject.toString()}")
                memberViewModel.haveMemberInfo = true
                memberViewModel.name = dataObject.name
                memberViewModel.loginId = dataObject.loginId
                memberViewModel.workArea = dataObject.workArea
                memberViewModel.department = dataObject.department
                memberViewModel.gender = dataObject.gender
                memberViewModel.birthDate = dataObject.birthDate
                memberViewModel.minThreshold = dataObject.minThreshold
                memberViewModel.maxThreshold = dataObject.maxThreshold
                adminViewModel.isAdmin = dataObject.isAdmin
            }
        }


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
            permissionAgree = true
        }


        // 브로드캐스트 리시버 등록
        registerReceiver(heartRateReceiver, IntentFilter("HeartRateUpdate"))

        setContent {
            var heartRate by remember { mutableStateOf(0f) }
            val context = LocalContext.current as MainActivity
            var isMonitoringStarted by remember { mutableStateOf(false) }
            val heartRates = remember { mutableListOf<Float>() }
            val heartRatesToSave = remember { mutableListOf<Float>() }
            messageService = MessageService(context)

            // 심박수 업데이트를 처리하는 효과를 생성합니다
            LaunchedEffect(Unit) {
                context.onHeartRateUpdate = { newHeartRate ->
//                    heartRate = newHeartRate

                    // 심박수 데이터 수집 및 최빈값 계산
                    heartRates.add(newHeartRate)
                    if (heartRates.size > 10) {
                        heartRates.removeAt(0)
                    }

                    val mostFrequentHeartRate =
                        heartRates.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: 0f
                    heartRate = mostFrequentHeartRate

                    // 모니터링 여부 반환
                    if (!isMonitoringStarted && heartRate.toInt() > 0) isMonitoringStarted = true
                    if (isMonitoringStarted && heartRate.toInt() == 0) isMonitoringStarted = false


                    // ===== 심박수 저장 로직
                    if (memberViewModel.haveMemberInfo) {
                        // 만약 10초동안의 최빈값이 임계치를 초과했다면 즉시 저장 (저장 후 5분동안 쉬기)
                        if (heartRate.toInt() != 0 && (heartRate < memberViewModel.minThreshold || heartRate > memberViewModel.maxThreshold)) {
                            val exceedThreshold = true

                            val currentTime = System.currentTimeMillis()

                            // 이전 저장으로부터 5분 지났는지
                            if (currentTime - lastNotificationTime > notificationCooldown) {
//                            Log.i("임계치 초과 심박수 저장 - 임계치 초과 저장", "${heartRate}")
                                // 알림 전송 코드
                                saveHeartRate(heartRate, exceedThreshold)
                                lastNotificationTime = currentTime
                            } else {
//                            Log.i("심박수 저장 - 임계치 초과", "아직 시간 안 지남")
                            }
                        }

                        // 2분동안의 최빈값 저장 (0이 아닌 경우에만)
                        heartRatesToSave.add(newHeartRate)
                        val secounds = 120
                        if (heartRatesToSave.size > secounds) {
                            val mostFrequentHeartRateToSave =
                                heartRates.groupingBy { it }.eachCount()
                                    .maxByOrNull { it.value }?.key
                                    ?: 0f

                            var exceedThreshold = false
                            if (mostFrequentHeartRateToSave.toInt() != 0 && (mostFrequentHeartRateToSave < memberViewModel.minThreshold || mostFrequentHeartRateToSave > memberViewModel.maxThreshold)) {
                                exceedThreshold = true
                            }
                            saveHeartRate(heartRate, exceedThreshold)
                            heartRatesToSave.clear()
                        }
                    }
                }
            }

            MainApp(
                heartRate,
                adminViewModel,
                memberViewModel,
                isMonitoringStarted,
                permissionAgree,
                messageService
            )
        }
    }

    /**
     * 2분 동안의 심박 수 중 최빈값 서버에 저장
     */
    private fun saveHeartRate(heartRate: Float, exceedThreshold: Boolean) {
        if (heartRate.toInt() <= 0) return
        Log.i("심박수 저장", "${heartRate}: 임계치 초과여부 ${exceedThreshold}");
        messageService.sendMessage(Order.POST_BPM.name, heartRate.toInt().toString())
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
                permissionAgree = false
            }
        }
    }
}

@Composable
fun MainApp(
    bpm: Float = 90.0F,
    adminViewModel: AdminViewModel,
    memberViewModel: MemberViewModel,
    isMonitoringStarted: Boolean,
    permissionAgree: Boolean,
    messageService: MessageService
) {
    GangHanNalTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
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

//            if (!memberViewModel.haveMemberInfo){
//                Text(text = "폰에서 로그인을 해주세요.")
//                messageService.sendMessage(Order.MEMBER_INFO.name, "멤버 인포 주세요")
//            } else {
//                Text(text = "로그인 완료!")
//            }

//            Button(onClick = {
////                messageService.sendMessageToApp("/hello", "안녕 wearos 메시지야 sendMessageToApp")
////                Log.i("메시지", "전송 완료 sendMessageToApp")
//                messageService.sendMessage("/world", "안녕 나는 wearos에서 보낸 메시지야")
//                Log.i("메시지", "전송 완료")
//            }) {
//                Text(text = "클릭하면 메시지 보냄")
//            }

            // 권한
            if (!permissionAgree) {
                val context = LocalContext.current as MainActivity
                Card(
                    onClick = { /* 클릭 이벤트 처리 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    backgroundPainter = ColorPainter(MaterialTheme.colors.surface), // 배경 설정
                    contentColor = MaterialTheme.colors.onSurface,
                    shape = RoundedCornerShape(8.dp)
                )
                {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()

                    ) {
                        Text(
                            text = "심박수 측정을 위한",
                            color = AppColor.textWhite.color,
                            fontSize = MaterialTheme.typography.body2.fontSize,
                            overflow = TextOverflow.Visible
                        )
                        Text(
                            text = "센서 권한이 없습니다.",
                            color = AppColor.textWhite.color,
                            fontSize = MaterialTheme.typography.body2.fontSize,
                            overflow = TextOverflow.Visible
                        )
                        Text(
                            text = "설정에서 센서 권한을",
                            color = AppColor.textWhite.color,
                            fontSize = MaterialTheme.typography.body2.fontSize,
                        )
                        Text(
                            text = "허용 바랍니다.",
                            color = AppColor.textWhite.color,
                            fontSize = MaterialTheme.typography.body2.fontSize,
                        )
                        CompactChip(onClick = {
                            // 어플리케이션 종료
                            context.finish()
                        },
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

            // 로그인
            if (permissionAgree && !memberViewModel.haveMemberInfo) {
                val context = LocalContext.current as MainActivity
                Card(
                    onClick = { /* 클릭 이벤트 처리 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    backgroundPainter = ColorPainter(MaterialTheme.colors.surface), // 배경 설정
                    contentColor = MaterialTheme.colors.onSurface,
                    shape = RoundedCornerShape(8.dp),
                )
                {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center

                    ) {
                        Text(
                            text = "폰에서 로그인을 해주세요.",
                            color = AppColor.textWhite.color,
                            fontSize = MaterialTheme.typography.body2.fontSize,

                        )
                        CompactChip(onClick = {
                            messageService.sendMessage(Order.MEMBER_INFO.name, "멤버 인포 주세요")
                        },
                            colors = ChipDefaults.chipColors(AppColor.primary.color),
                            label = {
                                Text(
                                    text = "로그인 완료",
                                    color = AppColor.textWhite.color,
                                    fontSize = MaterialTheme.typography.body2.fontSize,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        CompactChip(onClick = {
                            // 어플리케이션 종료
                            context.finish()
                        },
                            colors = ChipDefaults.chipColors(AppColor.secondary.color),
                            label = {
                                Text(
                                    text = "종료",
                                    color = AppColor.textWhite.color,
                                    fontSize = MaterialTheme.typography.body2.fontSize,
                                    fontWeight = FontWeight.SemiBold
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                    }
                }
            }

            // 시계 바르게 착용 알림
            if (permissionAgree && memberViewModel.haveMemberInfo && !isMonitoringStarted) {
                Card(
                    onClick = { /* 클릭 이벤트 처리 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    backgroundPainter = ColorPainter(MaterialTheme.colors.surface), // 배경 설정
                    contentColor = MaterialTheme.colors.onSurface,
                    shape = RoundedCornerShape(8.dp)

                )
                {
                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "심박수를 측정중입니다.",
                            color = AppColor.textWhite.color,
                            fontSize = MaterialTheme.typography.body2.fontSize,
                        )
                        Text(
                            text = "10초 정도 소요됩니다.",
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

