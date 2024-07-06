package au.com.afewroosloose.videoexercise.presentation

import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import au.com.afewroosloose.videoexercise.presentation.util.setOrientation

@Composable
fun VideoList(listOfUrls: List<String>, onAction: (MainViewModel.Action) -> Unit) {
    setOrientation(orientation = SCREEN_ORIENTATION_PORTRAIT, resetAfterViewRemoved = false)
    LazyColumn {
        itemsIndexed(listOfUrls) { index, item ->
            SmallVideoPlayer(url = item, index = index, onAction)
        }
    }
}

@Composable
fun SmallVideoPlayer(
    url: String,
    index: Int,
    onAction: (MainViewModel.Action) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
        }
    }
    Box(
        modifier = modifier
            .wrapContentSize()
            .padding(10.dp)
    ) {
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                }
            },
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        )
        Box(modifier = Modifier
            .matchParentSize()
            .clickable {
                onAction(MainViewModel.Action.FullScreen(index))
            })
    }
}