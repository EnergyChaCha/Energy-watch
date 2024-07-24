package chacha.enerygy.ganghannal.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import chacha.enerygy.ganghannal.data.message.dto.NotificationItem

class NotificationViewModel : ViewModel(){
    val thresholdExceedList = mutableStateListOf(
        NotificationItem(0F, "데이터가 존재하지 않습니다.", ""),
        NotificationItem(158F, "홍*동(wl**)님 심박수 임계치 초과입니다.", "2024/07/07 10:30"),
        NotificationItem(0F, "홍*동(wl**)님 심박수가 임계치 미만입니다.", "2024/07/07 09:20")
    )
}