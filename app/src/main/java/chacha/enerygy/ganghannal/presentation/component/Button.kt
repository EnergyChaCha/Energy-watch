package chacha.energy.ganghannal.presentation.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import chacha.energy.ganghannal.presentation.theme.AppColor

@Composable
fun CircleButton(
    icon: ImageVector,
    text: String,
    navController: NavHostController,
    targetScreen: String
) {
    Button(
        onClick = { navController.navigate(targetScreen) },
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
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AppColor.textWhite.color
            )
            Spacer(modifier = Modifier.width(4.dp)) // Add space between icon and text
            Text(
                text = text,
                color = AppColor.textWhite.color
            )
        }
    }
}