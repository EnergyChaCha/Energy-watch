package chacha.energy.ganghannal.presentation.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Sos
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.TimeText
import chacha.energy.ganghannal.presentation.component.BpmInfoHorizontal
import chacha.energy.ganghannal.presentation.component.CircleButton
import chacha.energy.ganghannal.presentation.constant.NavigationRoute
import chacha.energy.ganghannal.presentation.theme.AppColor


@Composable
fun MainScreen(navController: NavHostController, bpm: Float) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColor.background.color),
        contentAlignment = Alignment.Center
    ) {
        TimeText()
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            BpmInfoHorizontal(bpm)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Notification(navController)
                Spacer(modifier = Modifier.width(12.dp))
                Report(navController)
            }
        }
    }
}


@Composable
fun Notification(navController: NavHostController) {
    CircleButton(
        Icons.Default.Notifications,
        "알림",
        navController,
        NavigationRoute.NOTIFICATION.name
    )
}

@Composable
fun Report(navController: NavHostController) {
    CircleButton(Icons.Default.Sos, "신고", navController, NavigationRoute.REPORT.name)
}