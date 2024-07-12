/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package chacha.enerygy.ganghannal.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Sos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import chacha.enerygy.ganghannal.presentation.theme.GangHanNalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            MainApp(1)
        }
    }


}

@Composable
fun MainApp(bpm: Int) {
    GangHanNalTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.background),
            contentAlignment = Alignment.Center
        ) {
            TimeText()
            Layout(bpm = bpm)
        }
    }
}

@Composable
fun Layout(bpm: Int) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        BPMInfo(bpm)
        Spacer(modifier = Modifier.height(1.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Alert()
            Spacer(modifier = Modifier.width(12.dp))
            Report()

        }
    }
}

@Composable
fun BPMInfo(bpm: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically, // 중앙 선 맞추기
        horizontalArrangement = Arrangement.Center, // 중앙에 모여있게 하기
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp) // Add padding to separate rows
    ) {
        Icon(
            imageVector = Icons.Default.MonitorHeart,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(40.dp) // Set the icon size
        )

        Spacer(modifier = Modifier.width(4.dp))

        val formatBPM = formatBPM(bpm)
        Text(
            color = MaterialTheme.colors.primary,
            text = "$formatBPM BPM"
        )
    }
}

@SuppressLint("DefaultLocale")
fun formatBPM(bpm: Int): String {
    return if (bpm < 100) {
        String.format("%3d", bpm)
    } else {
        bpm.toString()
    }
}

@Composable
fun Alert() {
    ButtonUI(Icons.Default.Notifications, "알림")
}

@Composable
fun Report() {
    ButtonUI(Icons.Default.Sos, "신고")
}

@Composable
fun ButtonUI(icon: ImageVector, text: String) {
    Button(
        onClick = { /* TODO: Add button click functionality */ },
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.primary),
        modifier = Modifier.size(72.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(4.dp)) // Add space between icon and text
            Text(
                text = text,
                color = Color.White
            )
        }
    }
}


