package chacha.energy.ganghannal.presentation.screen.push

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import chacha.energy.ganghannal.presentation.component.BpmIcon
import chacha.energy.ganghannal.presentation.theme.AppColor
import com.google.android.gms.wearable.PutDataMapRequest

@Preview
@Composable
fun ThresholdExceedPushScreen() {
    // TODO: 입력받은 데이터로 변경
    val putDataMapReq = PutDataMapRequest.create("/path/to/data")
    putDataMapReq.dataMap.putString("name", "홍길동")
    putDataMapReq.dataMap.putString("loginId", "wuedh234")
    putDataMapReq.dataMap.putInt("bpm", 120)
    val dataMap = putDataMapReq.dataMap

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(bottom = 32.dp, top = 12.dp, start = 8.dp, end = 8.dp)
    ) {

        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                BpmIcon(1.4)
                Text(
                    text = "심박수 알림",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.title3.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))

                val name = dataMap.getString("name")
                val loginId = dataMap.getString("loginId")
                val bpm = dataMap.getInt("bpm")

                Text(
                    text = "${name}(${loginId})님의 심박수가 임계치를 넘어섰습니다.",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    textAlign = TextAlign.Center

                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "현재 심박수는",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize
                )
                Text(
                    text = "${bpm}BPM ",
                    color = AppColor.secondary.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "입니다.",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize
                )

            }
        }

    }
}