package chacha.energy.ganghannal.presentation.screen.push

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import chacha.energy.ganghannal.presentation.theme.AppColor
import com.google.android.gms.wearable.PutDataMapRequest


@Preview
@Composable
fun ReportPushScreen() {

    // TODO: 입력받은 데이터로 변경
    val putDataMapReq = PutDataMapRequest.create("/path/to/data")
    putDataMapReq.dataMap.putString("name", "홍길동")
    putDataMapReq.dataMap.putString("loginId", "wuedh234")
    putDataMapReq.dataMap.putString("workArea", "안산 HUB")
    putDataMapReq.dataMap.putString("department", "분류")
    putDataMapReq.dataMap.putInt("gender", 0)
    putDataMapReq.dataMap.putString(
        "status", "의식 없음"
    )
    putDataMapReq.dataMap.putString("birthDate", "1997-07-23")
    val dataMap = putDataMapReq.dataMap

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(bottom = 32.dp, top = 12.dp, start = 12.dp, end = 12.dp)
    ) {
        item {
            Icon(
                imageVector = Icons.Default.Sos,
                contentDescription = null,
                tint = AppColor.secondary.color,
                modifier = Modifier.size(28.dp) // Set the icon size
            )
            Text(
                text = "신고 알림",
                color = AppColor.textWhite.color,
                fontSize = MaterialTheme.typography.title3.fontSize,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row() {
                Text(
                    text = "이름 : ",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                dataMap.getString("name")?.let {
                    Text(
                        text = it,
                        color = AppColor.textWhite.color,
                        fontSize = MaterialTheme.typography.body2.fontSize,
                    )
                }
            }


            Row() {
                Text(
                    text = "아이디 : ",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                dataMap.getString("loginId")?.let {
                    Text(
                        text = it,
                        color = AppColor.textWhite.color,
                        fontSize = MaterialTheme.typography.body2.fontSize,
                    )
                }
            }


            Row() {
                Text(
                    text = "근무지 : ",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                dataMap.getString("workArea")?.let {
                    Text(
                        text = it,
                        color = AppColor.textWhite.color,
                        fontSize = MaterialTheme.typography.body2.fontSize,
                    )
                }
            }

            Row() {
                Text(
                    text = "직무 : ",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                dataMap.getString("department")?.let {
                    Text(
                        text = it,
                        color = AppColor.textWhite.color,
                        fontSize = MaterialTheme.typography.body2.fontSize,
                    )
                }
            }

            Row() {
                Text(
                    text = "성별 : ",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                dataMap.getInt("gender")?.let {
                    if (it.toInt() == 0) {
                        Text(
                            text = "여성",
                            color = AppColor.textWhite.color,
                            fontSize = MaterialTheme.typography.body2.fontSize,
                        )
                    }
                    if (it.toInt() == 1) {
                        Text(
                            text = "남성",
                            color = AppColor.textWhite.color,
                            fontSize = MaterialTheme.typography.body2.fontSize,
                        )
                    }

                }
            }

            Row() {
                Text(
                    text = "상태 : ",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                dataMap.getString("status")?.let {
                    Text(
                        text = it,
                        color = AppColor.textWhite.color,
                        fontSize = MaterialTheme.typography.body2.fontSize,
                    )
                }
            }

            Row() {
                Text(
                    text = "생년월일 : ",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                dataMap.getString("birthDate")?.let {
                    Text(
                        text = it,
                        color = AppColor.textWhite.color,
                        fontSize = MaterialTheme.typography.body2.fontSize,
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "신고가 접수되었습니다.",
                color = AppColor.textWhite.color,
                fontSize = MaterialTheme.typography.title3.fontSize,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(28.dp))

        }
    }
}
