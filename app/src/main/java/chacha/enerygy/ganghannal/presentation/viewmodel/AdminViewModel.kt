package chacha.energy.ganghannal.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AdminViewModel : ViewModel() {
    // 관리자 여부를 관리하는 변수
    var isAdmin by mutableStateOf(true)
        private set

    // 관리자 여부를 업데이트하는 함수
    fun setAdminStatus(isAdmin: Boolean) {
        this.isAdmin = isAdmin
    }
}