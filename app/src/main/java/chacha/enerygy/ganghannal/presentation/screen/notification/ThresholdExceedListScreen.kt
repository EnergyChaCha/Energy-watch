package chacha.enerygy.ganghannal.presentation.screen.notification

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import chacha.enerygy.ganghannal.presentation.component.BpmIcon
import chacha.enerygy.ganghannal.presentation.component.NotificationItem
import chacha.enerygy.ganghannal.presentation.component.PageIndecator
import chacha.enerygy.ganghannal.presentation.theme.AppColor
import chacha.enerygy.ganghannal.presentation.viewmodel.AdminViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ThresholdExceedListScreen(adminViewModel: AdminViewModel, pagerState: PagerState? = null) {
    val isAdmin by remember { adminViewModel::isAdmin }

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        state = rememberScalingLazyListState(),
        contentPadding = PaddingValues(bottom = 32.dp, top = 12.dp)
    ) {

        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                if (isAdmin && pagerState != null) {
                    PageIndecator(pagerState = pagerState)
                }

                BpmIcon(1.4)
                Text(
                    text = "심박수 알림",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.title3.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
            }

        }

        val notifications = listOf(
            Triple(100, "홍*동(wl**)님의 심박수가 임계치를 초과했습니다.", "2024/07/07 13:13"),
            Triple(5, "홍*동(wl**)님 심박수 임계치 미만", "2024/07/07 12:00"),
            Triple(89, "홍*동(wl**)님 심박수가 임계치 미만입니다.", "2024/07/07 11:45"),
            Triple(158, "홍*동(wl**)님 심박수 임계치 초과입니다.", "2024/07/07 10:30"),
            Triple(0, "홍*동(wl**)님 심박수가 임계치 미만입니다.", "2024/07/07 09:20")
        )

        items(notifications) { notification ->
            NotificationItem(
                bpm = notification.first,
                notification = notification.second,
                dateTime = notification.third
            )
        }
    }
}



