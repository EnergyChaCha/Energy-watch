package chacha.energy.ganghannal.presentation.screen.report

import android.Manifest
import android.content.pm.PackageManager
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import chacha.energy.ganghannal.data.message.MessageService
import chacha.energy.ganghannal.presentation.theme.AppColor
import chacha.energy.ganghannal.presentation.viewmodel.MemberViewModel
import chacha.enerygy.ganghannal.data.message.MessageUtil
import chacha.enerygy.ganghannal.data.message.dto.Order
import chacha.enerygy.ganghannal.data.message.dto.ReportDto
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@RequiresPermission(
    anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION],
)
@Composable
fun ReportScreen(memberViewModel: MemberViewModel, messageService: MessageService) {


    val usePreciseLocation = true;
    val permissions = listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )


    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val locationClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var locationInfo by remember {
        mutableStateOf("")
    }

    var reportProceed by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Sos,
            contentDescription = null,
            tint = AppColor.secondary.color,
            modifier = Modifier.size(28.dp) // Set the icon size
        )
        Text(
            text = "응급 신고",
            color = AppColor.textWhite.color,
            fontSize = MaterialTheme.typography.title3.fontSize,
            fontWeight = FontWeight.SemiBold
        )

        if (reportProceed == false) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "신고를 계속하시겠습니까?",
                color = AppColor.textWhite.color,
                fontSize = MaterialTheme.typography.title3.fontSize,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            CompactChip(onClick = {
                reportProceed = true
                Log.i("신고", "버튼 누름")
                    var longitude: Double = 0.0
                    var latitude: Double = 0.0
                    val reportdto = ReportDto(memberViewModel.currentBpm, longitude, latitude)
                    Log.i("신고 메시지", reportdto.toString())
                    val messageUtil = MessageUtil();
                    val stringData = messageUtil.makeString(reportdto)

                    messageService.sendMessage(
                        Order.POST_REPORT.name,
                        stringData
                    )

//                if (ActivityCompat.checkSelfPermission(
//                        context,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                        context,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    // 권한 없음
//                    var longitude: Double = 0.0
//                    var latitude: Double = 0.0
//                    val reportdto = ReportDto(memberViewModel.currentBpm, longitude, latitude)
//                    Log.i("신고 메시지", reportdto.toString())
//                    val messageUtil = MessageUtil();
//                    val stringData = messageUtil.makeString(reportdto)
//
//                    messageService.sendMessage(
//                        Order.POST_REPORT.name,
//                        stringData
//                    )
//
//                } else {
//                    scope.launch(Dispatchers.IO) {
//                        val priority = Priority.PRIORITY_HIGH_ACCURACY
//                        val result = locationClient.getCurrentLocation(
//                            priority,
//                            CancellationTokenSource().token,
//                        ).await()
//
//                        var longitude: Double = 0.0
//                        var latitude: Double = 0.0
//                        result?.let { fetchedLocation ->
//                            locationInfo =
//                                "Current location is \n" + "lat : ${fetchedLocation.latitude}\n" +
//                                        "long : ${fetchedLocation.longitude}\n" + "fetched at ${System.currentTimeMillis()}"
//                            longitude = fetchedLocation.longitude
//                            latitude = fetchedLocation.latitude
//                            Log.i("위치", locationInfo)
//
//                        }
//
//                        val reportdto = ReportDto(memberViewModel.currentBpm, longitude, latitude)
//                        Log.i("신고 메시지", reportdto.toString())
//                        val messageUtil = MessageUtil();
//                        val stringData = messageUtil.makeString(reportdto)
//
//                        messageService.sendMessage(
//                            Order.POST_REPORT.name,
//                            stringData
//                        )
//                    }
//                }

            },
                colors = ChipDefaults.chipColors(AppColor.secondary.color),
                label = {
                    Text(
                        text = "신고하기",
                        color = AppColor.textWhite.color,
                        fontSize = MaterialTheme.typography.title3.fontSize,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }

        if (reportProceed == true) {
            Spacer(modifier = Modifier.height(8.dp))
            Row() {
                Text(
                    text = "이름 : ",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = memberViewModel.name,
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                )
            }


            Row() {
                Text(
                    text = "아이디 : ",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = memberViewModel.loginId,
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                )
            }


            Row() {
                Text(
                    text = "생년월일 : ",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = memberViewModel.birthDate,
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "신고가 접수되었습니다.",
                color = AppColor.primary.color,
                fontSize = MaterialTheme.typography.title3.fontSize,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(28.dp))
        }


    }


}



