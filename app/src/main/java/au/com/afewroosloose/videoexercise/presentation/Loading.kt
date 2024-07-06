package au.com.afewroosloose.videoexercise.presentation

import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun Loading() {
    CircularProgressIndicator(
        modifier = Modifier.wrapContentSize(),
        color = Color.Blue,
        trackColor = MaterialTheme.colorScheme.surfaceVariant
    )
}