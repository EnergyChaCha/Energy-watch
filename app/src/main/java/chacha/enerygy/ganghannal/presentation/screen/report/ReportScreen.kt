package chacha.energy.ganghannal.presentation.screen.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sos
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipColors
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import chacha.energy.ganghannal.presentation.theme.AppColor
import chacha.energy.ganghannal.presentation.viewmodel.AdminViewModel
import chacha.energy.ganghannal.presentation.viewmodel.MemberViewModel

@Composable
fun ReportScreen(memberViewModel: MemberViewModel) {
    var reportProceed by remember { mutableStateOf(false) }
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Sos,
            contentDescription = null,
            tint = AppColor.secondary.color,
            modifier = Modifier.size(28.dp) // Set the icon size
        )
        Text(
            text = "응급 신고",
            color = AppColor.textWhite.color,
            fontSize = MaterialTheme.typography.title3.fontSize,
            fontWeight = FontWeight.SemiBold
        )

        if (reportProceed == false) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "신고를 계속하시겠습니까?",
                color = AppColor.textWhite.color,
                fontSize = MaterialTheme.typography.title3.fontSize,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            CompactChip(onClick = { reportProceed = true },
                colors = ChipDefaults.chipColors(AppColor.secondary.color),
                label = {
                    Text(
                        text = "신고하기",
                        color = AppColor.textWhite.color,
                        fontSize = MaterialTheme.typography.title3.fontSize,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }

        if (reportProceed == true) {
            Spacer(modifier = Modifier.height(8.dp))
            Row(){
                Text(
                    text = "이름 : ",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = memberViewModel.name,
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                )
            }


            Row(){
                Text(
                    text = "아이디 : ",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = memberViewModel.loginId,
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                )
            }


            Row(){
                Text(
                    text = "생년월일 : ",
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = memberViewModel.birthDate,
                    color = AppColor.textWhite.color,
                    fontSize = MaterialTheme.typography.body2.fontSize,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "신고가 접수되었습니다.",
                color = AppColor.primary.color,
                fontSize = MaterialTheme.typography.title3.fontSize,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(28.dp))
        }




    }
}
