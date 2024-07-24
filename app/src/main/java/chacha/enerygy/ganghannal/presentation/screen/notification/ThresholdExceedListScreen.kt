package chacha.energy.ganghannal.presentation.screen.notification

import android.util.Log
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
import chacha.energy.ganghannal.presentation.component.BpmIcon
import chacha.energy.ganghannal.presentation.component.NotificationItem
import chacha.energy.ganghannal.presentation.component.PageIndecator
import chacha.energy.ganghannal.presentation.theme.AppColor
import chacha.energy.ganghannal.presentation.viewmodel.AdminViewModel
import chacha.enerygy.ganghannal.presentation.viewmodel.NotificationViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ThresholdExceedListScreen(notificationViewModel: NotificationViewModel, adminViewModel: AdminViewModel, pagerState: PagerState? = null) {

    Log.i("알림리스트 신고", "접근")

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
            Triple(100F, "홍*동(wl**)님의 심박수가 임계치를 초과했습니다.", "2024/07/07 13:13"),
            Triple(5F, "홍*동(wl**)님 심박수 임계치 미만", "2024/07/07 12:00"),
            Triple(89F, "홍*동(wl**)님 심박수가 임계치 미만입니다.", "2024/07/07 11:45"),
            Triple(158F, "홍*동(wl**)님 심박수 임계치 초과입니다.", "2024/07/07 10:30"),
            Triple(0F, "홍*동(wl**)님 심박수가 임계치 미만입니다.", "2024/07/07 09:20")
        )

        items(notificationViewModel.thresholdExceedList) { notification ->
            NotificationItem(
                bpm = notification.bpm,
                notification = notification.message,
                dateTime = notification.timestamp
            )
        }
    }
}



