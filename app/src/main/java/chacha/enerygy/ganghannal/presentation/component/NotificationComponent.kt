package chacha.enerygy.ganghannal.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Card
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text

@Composable
fun NotificationItem(bpm: Float, notification: String, dateTime: String) {
    Card(
        onClick = { /* 클릭 이벤트 처리 */ },
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        backgroundPainter = ColorPainter(MaterialTheme.colors.surface), // 배경 설정
        contentColor = MaterialTheme.colors.onSurface,
        shape = RoundedCornerShape(8.dp), // 둥근 모서리 설정
        contentPadding = PaddingValues(
            start = 8.dp,
            top = 8.dp,
            end = 8.dp,
            bottom = 8.dp
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            BpmInfoVertical(bpm)

            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .padding(0.dp),
                    contentAlignment = Alignment.TopEnd // 우측 상단 정렬 설정
                ) {
                    Text(
                        text = dateTime,
                        color = Color.Gray,
                        fontSize = MaterialTheme.typography.caption2.fontSize,
                        fontWeight = FontWeight.Light
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = notification,
                        color = MaterialTheme.colors.onSurface,
                        style = MaterialTheme.typography.body2,
                        modifier = Modifier.weight(1f) // 텍스트가 남은 공간을 차지하도록 설정
                    )
                }
            }
        }
    }
}