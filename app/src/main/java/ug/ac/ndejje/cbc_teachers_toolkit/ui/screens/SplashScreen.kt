package ug.ac.ndejje.cbc_teachers_toolkit.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import ug.ac.ndejje.cbc_teachers_toolkit.R

@Composable
fun SplashScreen(onSplashComplete: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2000) // 2 seconds delay
        onSplashComplete()
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = stringResource(id = R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.splash_subtitle),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
