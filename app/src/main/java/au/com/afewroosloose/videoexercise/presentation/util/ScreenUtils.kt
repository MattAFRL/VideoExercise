package au.com.afewroosloose.videoexercise.presentation.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun setOrientation(orientation: Int, resetAfterViewRemoved: Boolean) {
    val context = LocalContext.current
    DisposableEffect(key1 = "settingOrientation") {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {
            Log.d(
                "setOrientation",
                "We couldn't set the orientation due to being called outside an Activity context"
            )
        }
        val previousOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        return@DisposableEffect onDispose {
            if (resetAfterViewRemoved) {
                activity.requestedOrientation = previousOrientation
            }
        }
    }
}

private fun Context.findActivity(): Activity? {
    if (this is Activity) {
        return this
    } else if (this is ContextWrapper) {
        return baseContext.findActivity()
    } else {
        return null
    }
}