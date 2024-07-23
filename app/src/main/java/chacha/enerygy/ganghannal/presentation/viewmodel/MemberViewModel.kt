package chacha.energy.ganghannal.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MemberViewModel : ViewModel() {

    var name by mutableStateOf("홍길동")

    var loginId by mutableStateOf("qiua122")
    var workArea by mutableStateOf("근무지")

    var department by mutableStateOf("직무")

    var gender by mutableStateOf("남성")

    var birthDate by mutableStateOf("1990-02-02")

    var minThreshold by mutableStateOf(40F)

    var maxThreshold by mutableStateOf(80F)


}