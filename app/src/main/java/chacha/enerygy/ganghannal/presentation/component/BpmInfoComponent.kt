package chacha.enerygy.ganghannal.presentation.component

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import chacha.enerygy.ganghannal.presentation.icons.MyIconPack
import chacha.enerygy.ganghannal.presentation.icons.myiconpack.EcgHeart
import chacha.enerygy.ganghannal.presentation.theme.AppColor


@Preview
@Composable
fun BpmInfoHorizontal(bpm: Float = 90F) {
    Row(
        verticalAlignment = Alignment.CenterVertically, // 중앙 선 맞추기
        horizontalArrangement = Arrangement.Center, // 중앙에 모여있게 하기
        modifier = Modifier
            .padding(bottom = 0.dp) // Add padding to separate rows
    ) {
        BpmIcon(1.2)
        Spacer(modifier = Modifier.width(4.dp))

        val annotatedString = buildAnnotatedString {
            append(bpmNumberText(bpm, 1.2))
            append(" ")
            append(bpmText(1.2))
        }
        BasicText(text = annotatedString)
    }
}

@Preview
@Composable
fun BpmInfoVertical(bpm: Float = 90F) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .padding(bottom = 0.dp) // Add padding to separate rows
    ) {
        BpmIcon(0.7)
        Spacer(modifier = Modifier.width(4.dp))
        BasicText(text = bpmNumberText(bpm, 0.7))
        BasicText(text = bpmText(0.7))
    }
}

@Composable
fun BpmIcon(size: Double) {

    val sizeResult = 20 * size
    Icon(
        imageVector = MyIconPack.EcgHeart,
        contentDescription = null,
        tint = AppColor.secondary.color,
        modifier = Modifier.size(sizeResult.dp) // Set the icon size
    )
}

@Composable
fun bpmNumberText(bpm: Float = 90F, size: Double): AnnotatedString {
    val zeros = getLeadingZeros(bpm)
    val annotatedString = buildAnnotatedString {
        append(
            AnnotatedString(
                text = "$zeros",
                spanStyle = SpanStyle(
                    color = AppColor.textGray.color,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.typography.title1.fontSize * size,
                )
            )
        )

        append(
            AnnotatedString(
                text = bpm.toInt().toString(),
                spanStyle = SpanStyle(
                    color = AppColor.textWhite.color,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = MaterialTheme.typography.title1.fontSize * size,
                )
            )
        )
    }
    return annotatedString
}

@Composable
fun bpmText(size: Double): AnnotatedString {
    val annotatedString = buildAnnotatedString {
        append(
            AnnotatedString(
                text = "BPM",
                spanStyle = SpanStyle(
                    color = AppColor.textGray.color,
                    fontSize = MaterialTheme.typography.body2.fontSize * size,
                )
            )
        )
    }
    return annotatedString
}

@SuppressLint("DefaultLocale")
fun formatBpmText(bpm: Int): String {
    return if (bpm < 100) {
        String.format("%3d", bpm)
    } else {
        bpm.toString()
    }
}

fun getLeadingZeros(number: Float): String {
    return when {
        number.toInt() in 100..999 -> ""
        number.toInt() in 10..99 -> "0"
        number.toInt() in 0..9 -> "00"
        else -> ""
    }
}