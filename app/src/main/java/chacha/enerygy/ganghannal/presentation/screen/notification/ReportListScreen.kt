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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import chacha.energy.ganghannal.data.message.MessageService
import chacha.energy.ganghannal.presentation.component.NotificationItem
import chacha.energy.ganghannal.presentation.component.PageIndecator
import chacha.energy.ganghannal.presentation.theme.AppColor
import chacha.enerygy.ganghannal.data.message.dto.Order
import chacha.enerygy.ganghannal.presentation.viewmodel.NotificationViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReportListScreen(notificationViewModel: NotificationViewModel, pagerState: PagerState, messageService: MessageService) {
    Log.i("알림리스트 신고", "접근")
    messageService.sendMessage(Order.GET_REPORT_LIST.name, "ReportListScreen 신고 초과 리스트 주세요")

    ScalingLazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 0.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        contentPadding = PaddingValues(bottom = 32.dp, top = 12.dp)
    ) {
        item {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                PageIndecator(pagerState = pagerState)

                val sizeResult = 20 * 1.4
                Icon(
                    imageVector = Icons.Default.Sos,
                    contentDescription = null,
                    tint = AppColor.secondary.color,
                    modifier = Modifier.size(sizeResult.dp) // Set the icon size
                )
                Text(
                    text = "신고 알림",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.title3.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
            }

        }


        items(notificationViewModel.reportList) { notification ->
            NotificationItem(
                bpm = notification.bpm,
                notification = notification.message,
                dateTime = notification.timestamp
            )
        }
    }
}



