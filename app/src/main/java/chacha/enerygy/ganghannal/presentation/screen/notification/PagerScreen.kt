package chacha.energy.ganghannal.presentation.screen.notification


import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import chacha.energy.ganghannal.presentation.viewmodel.AdminViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PagerScreen(adminViewModel: AdminViewModel) {
    val isAdmin by remember { adminViewModel::isAdmin }

    if (isAdmin) {
        val pagerState = rememberPagerState(pageCount = {
            2
        })
        HorizontalPager(state = pagerState) { page ->
            if (page == 0) {
                ThresholdExceedListScreen(adminViewModel, pagerState)
            } else {
                ReportListScreen(pagerState)
            }
        }
    }
    if (!isAdmin) {
        ThresholdExceedListScreen(adminViewModel)
    }
}

