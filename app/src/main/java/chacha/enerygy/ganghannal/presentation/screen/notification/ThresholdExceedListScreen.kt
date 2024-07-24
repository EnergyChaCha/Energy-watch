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
import chacha.energy.ganghannal.data.message.MessageService
import chacha.energy.ganghannal.presentation.component.BpmIcon
import chacha.energy.ganghannal.presentation.component.NotificationItem
import chacha.energy.ganghannal.presentation.component.PageIndecator
import chacha.energy.ganghannal.presentation.theme.AppColor
import chacha.energy.ganghannal.presentation.viewmodel.AdminViewModel
import chacha.enerygy.ganghannal.data.message.dto.Order
import chacha.enerygy.ganghannal.presentation.viewmodel.NotificationViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ThresholdExceedListScreen(notificationViewModel: NotificationViewModel, adminViewModel: AdminViewModel, messageService: MessageService, pagerState: PagerState? = null ) {

    Log.i("알림리스트 임계치", "접근")
    messageService.sendMessage(Order.GET_ALERT_LIST.name, "ThresholdExceedListScreen 임계치 초과 리스트 주세요")

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

        items(notificationViewModel.thresholdExceedList) { notification ->
            NotificationItem(
                bpm = notification.bpm,
                notification = notification.message,
                dateTime = notification.timestamp
            )
        }
    }
}



