/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package chacha.energy.ganghannal.presentation

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
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
import chacha.energy.ganghannal.data.message.MessageService
import chacha.energy.ganghannal.presentation.constant.NavigationRoute
import chacha.energy.ganghannal.presentation.screen.main.MainScreen
import chacha.energy.ganghannal.presentation.screen.notification.PagerScreen
import chacha.energy.ganghannal.presentation.screen.report.ReportScreen
import chacha.energy.ganghannal.presentation.theme.AppColor
import chacha.energy.ganghannal.presentation.theme.GangHanNalTheme
import chacha.energy.ganghannal.presentation.viewmodel.AdminViewModel
import chacha.energy.ganghannal.presentation.viewmodel.MemberViewModel
import chacha.enerygy.ganghannal.cj.ApiService
import chacha.enerygy.ganghannal.cj.MyData
import chacha.enerygy.ganghannal.cj.MyResponse
import chacha.enerygy.ganghannal.cj.StepCounterService
import chacha.enerygy.ganghannal.data.message.dto.Hello
import chacha.enerygy.ganghannal.data.message.dto.MemberInfo
import chacha.enerygy.ganghannal.data.message.dto.NotificationItem
import chacha.enerygy.ganghannal.data.message.dto.Order
import chacha.enerygy.ganghannal.presentation.viewmodel.NotificationViewModel
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : ComponentActivity(), SensorEventListener {
    private val adminViewModel: AdminViewModel by viewModels()
    private val memberViewModel: MemberViewModel by viewModels()
    private val notificationViewModel: NotificationViewModel by viewModels()
    private val REQUEST_BODY_SENSORS_PERMISSION = 1
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
//    var permissionAgree = true

    private val PERMISSION_REQUEST_ACTIVITY_RECOGNITION = 100
    private lateinit var sensorManager: SensorManager
    private var stepSensor: Sensor? = null
    private var steps by mutableStateOf(0)

    private val handler = Handler(Looper.getMainLooper())
    private val stepUpdateInterval = 5000L // 5 seconds


    private var lastNotificationTime: Long = 0
    private val notificationCooldown: Long = 1000 * 60 * 5// 5분 (밀리초 단위)
    private lateinit var messageService: MessageService


    private val heartRateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val heartRate = intent?.getFloatExtra("heartRate", 0f) ?: 0f
            onHeartRateUpdate(heartRate)
        }
    }

    var onHeartRateUpdate: (Float) -> Unit = {}


    private fun startStepCounting() {
        TODO("Not yet implemented")
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // 권한이 허용된 경우 Foreground Service 시작
                startStepCounterService()
                Log.i("본선 걸음수", "서비스 시작")
            } else {
                // 권한이 거부된 경우 사용자에게 안내
                // 예를 들어, 권한이 필요함을 설명하는 메시지를 보여줄 수 있습니다.
                Log.i("본선 걸음수", "서비스 거부")
            }
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
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)
        Log.i("본선", "onCreate")

        // ACTIVITY_RECOGNITION 권한 요청
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
            != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
                PERMISSION_REQUEST_ACTIVITY_RECOGNITION
            )
        } else {
            // 권한이 이미 허용된 경우
            startStepCounter()
        }

        // SensorManager 초기화 및 걸음 센서 등록
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)



        Wearable.getMessageClient(this).addListener { event ->
            val dataString = String(event.data, Charsets.UTF_8)
            val gson = Gson()


            Log.i("메시지", "워치 메시지 받음: ${event.path} ${dataString}")

            if (event.path.equals(Order.HELLO.name)) {
                val dataObject = gson.fromJson(dataString, Hello::class.java)
                Log.i("메시지", "${Order.HELLO.name} 겟 $dataObject")
            } else if (event.path.equals(Order.MEMBER_INFO.name)) {
                val dataObject = gson.fromJson(dataString, MemberInfo::class.java)
                Log.i("메시지", "${Order.MEMBER_INFO.name} 겟 $dataObject")
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
            } else if (event.path.equals(Order.GET_ALERT_LIST.name)) {
                val listType = object : TypeToken<List<NotificationItem>>() {}.type
                val dataObject: List<NotificationItem> = gson.fromJson(dataString, listType)
                Log.i("메시지", "${Order.GET_ALERT_LIST.name} 겟 $dataObject")
                notificationViewModel.thresholdExceedList.clear()
                for (item in dataObject) {
                    notificationViewModel.thresholdExceedList.add(item)
                }
                if (dataObject.size.equals(0)) {
                    notificationViewModel.reportList.add(
                        NotificationItem(
                            0F,
                            "데이터가 존재하지 않습니다.",
                            ""
                        )
                    )
                }

            } else if (event.path.equals(Order.GET_REPORT_LIST.name)) {
                val listType = object : TypeToken<List<NotificationItem>>() {}.type
                val dataObject: List<NotificationItem> = gson.fromJson(dataString, listType)
                Log.i("메시지", "${Order.GET_REPORT_LIST.name} 겟 $dataObject")
                notificationViewModel.reportList.clear()
                for (item in dataObject) {
                    notificationViewModel.reportList.add(item)
                }

                if (dataObject.size.equals(0)) {
                    notificationViewModel.reportList.add(
                        NotificationItem(
                            0F,
                            "데이터가 존재하지 않습니다.",
                            ""
                        )
                    )
                }

            }
        }


//        // BODY_SENSORS 권한을 요청합니다
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.BODY_SENSORS),
//                REQUEST_BODY_SENSORS_PERMISSION
//            )
//
//        } else {
//            startHeartRateService()
//            memberViewModel.sensorPermissionAgree = true
//        }

//        // location 권한 부여
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        } else {
//            // 권한이 이미 부여되어 있는 경우
//            Log.i("위치", "권한 부여됨")
//        }


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

                    if (heartRate.toInt() != 0) {
                        memberViewModel.currentBpm = heartRate
                    }


                    // 모니터링 여부 반환
                    if (!isMonitoringStarted && heartRate.toInt() > 0) isMonitoringStarted = true
                    if (isMonitoringStarted && heartRate.toInt() == 0) isMonitoringStarted = false


//                    // ===== 심박수 저장 로직
//                    if (memberViewModel.haveMemberInfo) {
//                        // 만약 10초동안의 최빈값이 임계치를 초과했다면 즉시 저장 (저장 후 5분동안 쉬기)
//                        if (heartRate.toInt() != 0 && (heartRate < memberViewModel.minThreshold || heartRate > memberViewModel.maxThreshold)) {
//                            val exceedThreshold = true
//
//                            val currentTime = System.currentTimeMillis()
//
//                            // 이전 저장으로부터 5분 지났는지
//                            if (currentTime - lastNotificationTime > notificationCooldown) {
////                            Log.i("임계치 초과 심박수 저장 - 임계치 초과 저장", "${heartRate}")
//                                // 알림 전송 코드
//                                saveHeartRate(heartRate, exceedThreshold)
//                                lastNotificationTime = currentTime
//                            } else {
////                            Log.i("심박수 저장 - 임계치 초과", "아직 시간 안 지남")
//                            }
//                        }
//
//                        // 2분동안의 최빈값 저장 (0이 아닌 경우에만)
//                        heartRatesToSave.add(newHeartRate)
//                        val secounds = 120
//                        if (heartRatesToSave.size > secounds) {
//                            val mostFrequentHeartRateToSave =
//                                heartRates.groupingBy { it }.eachCount()
//                                    .maxByOrNull { it.value }?.key
//                                    ?: 0f
//
//                            var exceedThreshold = false
//                            if (mostFrequentHeartRateToSave.toInt() != 0 && (mostFrequentHeartRateToSave < memberViewModel.minThreshold || mostFrequentHeartRateToSave > memberViewModel.maxThreshold)) {
//                                exceedThreshold = true
//                            }
//                            saveHeartRate(heartRate, exceedThreshold)
//                            heartRatesToSave.clear()
//                        }
//                    }
                }
            }

            MainApp(
                heartRate,
                adminViewModel,
                memberViewModel,
                isMonitoringStarted,
                messageService,
                notificationViewModel
            )
        }
    }

    private fun createNotification(): android.app.Notification {
        val channelId = "step_counter_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Step Counter Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        return android.app.Notification.Builder(this, channelId)
            .setContentTitle("Step Counter Service")
            .setContentText("Counting steps...")
//            .setSmallIcon(R.drawable.ic_step_counter) // 적절한 아이콘을 사용하세요
            .build()
    }


    // 권한 요청 결과 처리
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACTIVITY_RECOGNITION) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // 권한 허용됨
                startStepCounter()
            } else {
                // 권한 거부됨
                Log.d("StepCounter", "ACTIVITY_RECOGNITION 권한이 거부되었습니다.")
            }
        }
    }

    private fun startStepCounterService() {
        Log.i("본선", "startStepCounterService 함수 시작")
        val intent = Intent(this, StepCounterService::class.java)
        ContextCompat.startForegroundService(this, intent)
        Log.i("본선", "startHeartRateService 종료")
    }

    private fun startStepCounter() {
        // SensorManager 초기화
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // 걸음 센서 가져오기
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        // 센서 리스너 등록
        if (stepSensor != null) {
            sensorManager.registerListener(
                this,
                stepSensor,
                SensorManager.SENSOR_DELAY_UI // 빠른 데이터 업데이트
            )
            Log.d("본선", "걸음 센서 등록 성공")

        } else {
            Log.d("본선", "걸음 센서를 사용할 수 없습니다.")
        }


        // 5초마다 걸음 수 기록
        startStepUpdateTask()
    }

    private fun startStepUpdateTask() {
        handler.post(object : Runnable {
            override fun run() {
                // 현재 걸음 수와 마지막 기록된 걸음 수의 차이를 계산하여 로그에 출력
                val stepsTaken = steps
                Log.d("본선 걸음수", "지난 5초 동안 걸음 수: $stepsTaken")
                sendPostRequest(stepsTaken)
                // 5초 후에 다시 실행
                handler.postDelayed(this, stepUpdateInterval)
            }
        })
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            steps = it.values[0].toInt() // 걸음 수 업데이트
            Log.i("본선 걸음수", steps.toString())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // 정확도 변경 시 처리 (필요시 구현)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this) // 리스너 해제
        Log.i("본선 걸음수", "리스너 디스트로이")
    }

//    /**
//     * 2분 동안의 심박 수 중 최빈값 서버에 저장
//     */
//    private fun saveHeartRate(heartRate: Float, exceedThreshold: Boolean) {
//        if (heartRate.toInt() <= 0) return
//        Log.i("심박수 저장", "${heartRate}: 임계치 초과여부 ${exceedThreshold}");
//        messageService.sendMessage(Order.POST_BPM.name, heartRate.toInt().toString())
//    }
//
//    private fun startHeartRateService() {
//        Log.i("심박수", "startHeartRateService 함수 시작")
//        val intent = Intent(this, HeartRateService::class.java)
//        ContextCompat.startForegroundService(this, intent)
//        Log.i("심박수", "startHeartRateService 종료")
//    }

//    private fun checkLocationPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(
//                this,
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
//                LOCATION_PERMISSION_REQUEST_CODE
//            )
//        } else {
//            // 권한이 이미 부여되어 있는 경우
//            getLocation()
//        }
//    }

//    @Deprecated("Deprecated in Java")
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (requestCode == REQUEST_BODY_SENSORS_PERMISSION) {
//            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
//                // 권한이 부여되었으므로 심박수 모니터링을 시작합니다
//                startHeartRateService()
//                memberViewModel.sensorPermissionAgree = true
//            } else {
//                // 권한이 거부되었을 때의 처리를 합니다
//                // 예를 들어, 사용자에게 권한이 필요하다는 메시지를 표시합니다
//                Log.i("권한", "권한 거부됨")
//                memberViewModel.sensorPermissionAgree = false
//            }
//        }
//    }
}

@Composable
fun MainApp(
    bpm: Float = 90.0F,
    adminViewModel: AdminViewModel,
    memberViewModel: MemberViewModel,
    isMonitoringStarted: Boolean,
    messageService: MessageService,
    notificationViewModel: NotificationViewModel
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
                    PagerScreen(adminViewModel, notificationViewModel, messageService)
                }
                composable(NavigationRoute.REPORT.name) {
                    ReportScreen(
                        memberViewModel,
                        messageService
                    )
                }
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

//            // 권한
//            if (!memberViewModel.sensorPermissionAgree) {
//                val context = LocalContext.current as MainActivity
//                Card(
//                    onClick = { /* 클릭 이벤트 처리 */ },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 20.dp, vertical = 4.dp),
//                    backgroundPainter = ColorPainter(MaterialTheme.colors.surface), // 배경 설정
//                    contentColor = MaterialTheme.colors.onSurface,
//                    shape = RoundedCornerShape(8.dp)
//                )
//                {
//                    Column(
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center,
//                        modifier = Modifier.fillMaxWidth()
//
//                    ) {
//                        Text(
//                            text = "심박수 측정을 위한",
//                            color = AppColor.textWhite.color,
//                            fontSize = MaterialTheme.typography.body2.fontSize,
//                            overflow = TextOverflow.Visible
//                        )
//                        Text(
//                            text = "센서 권한이 없습니다.",
//                            color = AppColor.textWhite.color,
//                            fontSize = MaterialTheme.typography.body2.fontSize,
//                            overflow = TextOverflow.Visible
//                        )
//                        Text(
//                            text = "설정에서 센서 권한을",
//                            color = AppColor.textWhite.color,
//                            fontSize = MaterialTheme.typography.body2.fontSize,
//                        )
//                        Text(
//                            text = "허용 바랍니다.",
//                            color = AppColor.textWhite.color,
//                            fontSize = MaterialTheme.typography.body2.fontSize,
//                        )
//                        CompactChip(onClick = {
//                            // 어플리케이션 종료
//                            context.finish()
//                        },
//                            colors = ChipDefaults.chipColors(AppColor.secondary.color),
//                            label = {
//                                Text(
//                                    text = "종료",
//                                    color = AppColor.textWhite.color,
//                                    fontSize = MaterialTheme.typography.title3.fontSize,
//                                    fontWeight = FontWeight.SemiBold
//                                )
//                            }
//                        )
//                    }
//                }
//            }
//
//            // 로그인
//            if (memberViewModel.sensorPermissionAgree && !memberViewModel.haveMemberInfo) {
//                val context = LocalContext.current as MainActivity
//                Card(
//                    onClick = { /* 클릭 이벤트 처리 */ },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 20.dp, vertical = 4.dp),
//                    backgroundPainter = ColorPainter(MaterialTheme.colors.surface), // 배경 설정
//                    contentColor = MaterialTheme.colors.onSurface,
//                    shape = RoundedCornerShape(8.dp),
//                )
//                {
//                    Column(
//                        modifier = Modifier.fillMaxSize(),
//                        horizontalAlignment = Alignment.CenterHorizontally,
//                        verticalArrangement = Arrangement.Center
//
//                    ) {
//                        Text(
//                            text = "폰에서 로그인을 해주세요.",
//                            color = AppColor.textWhite.color,
//                            fontSize = MaterialTheme.typography.body2.fontSize,
//
//                            )
//                        CompactChip(
//                            onClick = {
//                                messageService.sendMessage(Order.MEMBER_INFO.name, "멤버 인포 주세요")
//                            },
//                            colors = ChipDefaults.chipColors(AppColor.primary.color),
//                            label = {
//                                Text(
//                                    text = "로그인 완료",
//                                    color = AppColor.textWhite.color,
//                                    fontSize = MaterialTheme.typography.body2.fontSize,
//                                    fontWeight = FontWeight.SemiBold
//                                )
//                            },
//                            modifier = Modifier.fillMaxWidth()
//                        )
//                        CompactChip(
//                            onClick = {
//                                // 어플리케이션 종료
//                                context.finish()
//                            },
//                            colors = ChipDefaults.chipColors(AppColor.secondary.color),
//                            label = {
//                                Text(
//                                    text = "종료",
//                                    color = AppColor.textWhite.color,
//                                    fontSize = MaterialTheme.typography.body2.fontSize,
//                                    fontWeight = FontWeight.SemiBold
//                                )
//                            },
//                            modifier = Modifier.fillMaxWidth()
//                        )
//
//                    }
//                }
//            }
//
//            // 시계 바르게 착용 알림
//            if (memberViewModel.sensorPermissionAgree && memberViewModel.haveMemberInfo && !isMonitoringStarted) {
//                Card(
//                    onClick = { /* 클릭 이벤트 처리 */ },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(horizontal = 20.dp, vertical = 4.dp),
//                    backgroundPainter = ColorPainter(MaterialTheme.colors.surface), // 배경 설정
//                    contentColor = MaterialTheme.colors.onSurface,
//                    shape = RoundedCornerShape(8.dp)
//
//                )
//                {
//                    Column(
////                        horizontalAlignment = Alignment.CenterHorizontally,
//                        modifier = Modifier.fillMaxWidth()
//                    ) {
//                        Text(
//                            text = "심박수를 측정중입니다.",
//                            color = AppColor.textWhite.color,
//                            fontSize = MaterialTheme.typography.body2.fontSize,
//                        )
//                        Text(
//                            text = "10초 정도 소요됩니다.",
//                            color = AppColor.textWhite.color,
//                            fontSize = MaterialTheme.typography.body2.fontSize,
//                        )
//                        Text(
//                            text = "워치를 올바르게 착용해 주세요.",
//                            color = AppColor.textWhite.color,
//                            fontSize = MaterialTheme.typography.body2.fontSize,
//                        )
//                    }
//                }
//            }
        }
    }
}

