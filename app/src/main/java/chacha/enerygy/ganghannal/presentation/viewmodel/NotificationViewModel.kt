package chacha.enerygy.ganghannal.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import chacha.enerygy.ganghannal.data.message.dto.NotificationItem

class NotificationViewModel : ViewModel(){
    val thresholdExceedList = mutableStateListOf(
        NotificationItem(0F, "데이터를 가져오는 중입니다.", "")
    )

    val reportList = mutableStateListOf(
        NotificationItem(0F, "데이터를 가져오는 중입니다.", "")
    )
}