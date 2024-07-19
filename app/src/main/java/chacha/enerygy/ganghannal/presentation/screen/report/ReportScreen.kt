package chacha.enerygy.ganghannal.presentation.screen.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import chacha.enerygy.ganghannal.presentation.theme.AppColor
import chacha.enerygy.ganghannal.presentation.viewmodel.AdminViewModel

@Composable
fun ReportScreen(adminViewModel: AdminViewModel) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "This is the Report Screen",
            color = MaterialTheme.colors.primary
        )

        val isAdmin by remember { adminViewModel::isAdmin }

        Button(
            onClick = { adminViewModel.setAdminStatus(!isAdmin) },
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                backgroundColor = AppColor.primary.color,
                contentColor = AppColor.textWhite.color
            ),
            modifier = Modifier.size(72.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (isAdmin) {
                    Text("관리자 화면")
                } else {
                    Text("일반 사용자 화면")
                }
            }
        }

    }
}
