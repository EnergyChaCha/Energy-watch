package chacha.enerygy.ganghannal.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

data class Notification(val bpm: Float, val message: String, val timestamp: String)

class NotificationViewModel : ViewModel(){
    val thresholdExceedList = mutableStateListOf(
        Notification(0F, "데이터가 존재하지 않습니다.", ""),
        Notification(158F, "홍*동(wl**)님 심박수 임계치 초과입니다.", "2024/07/07 10:30"),
        Notification(0F, "홍*동(wl**)님 심박수가 임계치 미만입니다.", "2024/07/07 09:20")
    )
}