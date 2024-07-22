package chacha.energy.ganghannal.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MemberViewModel : ViewModel() {

    var name by mutableStateOf("홍길동")
        private set

    var loginId by mutableStateOf("qiua122")
        private set

    var workArea by mutableStateOf("근무지")
        private set

    var department by mutableStateOf("직무")
        private set

    var gender by mutableStateOf(0)
        private set

    var birthDate by mutableStateOf("1990-02-02")
        private set

    var minThreshold by mutableStateOf(40F)
        private set

    var maxThreshold by mutableStateOf(80F)
        private set


}