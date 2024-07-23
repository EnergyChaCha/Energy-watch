package chacha.enerygy.ganghannal.data.message.dto

class MemberInfo {
    var name: String = ""
    var loginId: String = ""
    var workArea: String = ""
    var department: String = ""
    var gender: String = ""
    var birthDate: String = ""
    var minThreshold: Float = 49F
    var maxThreshold: Float = 170F
    var isAdmin: Boolean = false
    override fun toString(): String {
        return "MemberInfo(name='$name', loginId='$loginId', workArea='$workArea', department='$department', gender='$gender', birthDate='$birthDate', minThreshold=$minThreshold, maxThreshold=$maxThreshold, isAdmin=$isAdmin)"
    }
}