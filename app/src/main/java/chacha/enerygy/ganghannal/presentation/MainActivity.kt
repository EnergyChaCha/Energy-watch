/* While this template provides a good starting point for using Wear Compose, you can always
 * take a look at https://github.com/android/wear-os-samples/tree/main/ComposeStarter and
 * https://github.com/android/wear-os-samples/tree/main/ComposeAdvanced to find the most up to date
 * changes to the libraries and their usages.
 */

package chacha.enerygy.ganghannal.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import chacha.enerygy.ganghannal.presentation.constant.NavigationRoute
import chacha.enerygy.ganghannal.presentation.screen.main.MainScreen
import chacha.enerygy.ganghannal.presentation.screen.notification.PagerScreen
import chacha.enerygy.ganghannal.presentation.screen.report.ReportScreen
import chacha.enerygy.ganghannal.presentation.theme.AppColor
import chacha.enerygy.ganghannal.presentation.theme.GangHanNalTheme
import chacha.enerygy.ganghannal.presentation.viewmodel.AdminViewModel

class MainActivity : ComponentActivity() {
    private val adminViewModel: AdminViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)

        setTheme(android.R.style.Theme_DeviceDefault)

        setContent {
            MainApp(90, adminViewModel)
        }
    }
}

@Composable
fun MainApp(bpm: Int = 90, adminViewModel: AdminViewModel) {
    GangHanNalTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColor.background.color),
            contentAlignment = Alignment.Center
        ) {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = NavigationRoute.MAIN.name) {
                composable(NavigationRoute.MAIN.name) { MainScreen(navController, bpm) }
                composable(NavigationRoute.NOTIFICATION.name) {
                    PagerScreen(adminViewModel)
                }
                composable(NavigationRoute.REPORT.name) { ReportScreen(adminViewModel) }
            }
        }
    }
}

