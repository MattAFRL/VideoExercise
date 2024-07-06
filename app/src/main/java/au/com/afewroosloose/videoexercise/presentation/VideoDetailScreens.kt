@file:OptIn(ExperimentalFoundationApi::class)

package au.com.afewroosloose.videoexercise.presentation

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerControlView.VISIBLE
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import au.com.afewroosloose.videoexercise.R
import au.com.afewroosloose.videoexercise.presentation.util.setOrientation
import kotlinx.coroutines.launch

@Composable
fun VideoDetailScreens(
    index: Int,
    navController: NavController,
    videos: List<String>
) {
    if (videos.isEmpty()) {
        Text("There are no videos currently")
        return
    }
    if (index !in videos.indices) {
        Text("The index is out of bounds")
        return
    }
    val context = LocalContext.current
    val exoPlayers =
        remember { // we cache the ExoPlayers up here so we can have more control over their lifecycle.
            videos.map {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(it))
                }
            }
        }
    DisposableEffect(LocalLifecycleOwner.current) {
        onDispose {
            exoPlayers.forEach {
                it.release() // just release them all if we go back
            }
        }
    }
    Box {
        val showPagerIndicator = remember { mutableStateOf(false) }
        val pagerState = rememberPagerState(initialPage = index, pageCount = { videos.size })
        HorizontalPager(state = pagerState) { currentPage ->
            FullScreenPlayerWithControls(
                navController = navController,
                exoPlayer = exoPlayers[currentPage],
                showPagerIndicator = showPagerIndicator
            )
        }
        // very quick and easy horizontal pager indicator, similar to the Google example. Ours is bigger though for better tappability.
        if (showPagerIndicator.value) {
            Row(
                Modifier
                    .wrapContentHeight()
                    .wrapContentWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 4.dp)
            ) {
                val scope = rememberCoroutineScope()
                repeat(pagerState.pageCount) { page ->
                    val color =
                        if (pagerState.currentPage == page) Color.DarkGray else Color.LightGray
                    Box(
                        modifier = Modifier
                            .padding(10.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(24.dp)
                            .clickable {
                                scope.launch {
                                    pagerState.animateScrollToPage(page)
                                }
                            }
                    )
                }
            }
        }
    }
    // force orientation to landscape
    setOrientation(orientation = SCREEN_ORIENTATION_LANDSCAPE, resetAfterViewRemoved = true)
}

@Composable
fun FullScreenPlayerWithControls(
    navController: NavController,
    showPagerIndicator: MutableState<Boolean>,
    exoPlayer: ExoPlayer
) {
    val showAdditionalControls = remember { mutableStateOf(false) }
    Box {
        LargeVideoPlayer(showAdditionalControls, showPagerIndicator, exoPlayer)
        if (showAdditionalControls.value) { // having this control not tied to the video player seemed weird so it lives inside the pager page
            IconButton(
                onClick = { navController.popBackStack() }, modifier = Modifier.align(
                    Alignment.TopEnd
                )
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_exit),
                    contentDescription = stringResource(R.string.exit),
                    tint = Color.White
                )
            }
        }
    }
}


@SuppressLint("UnsafeOptInUsageError")
@Composable
private fun LargeVideoPlayer(
    showAdditionalControls: MutableState<Boolean>,
    showPagerIndicator: MutableState<Boolean>,
    exoPlayer: ExoPlayer
) {
    AndroidView(
        factory = {
            PlayerView(it).apply {
                this.controllerAutoShow = true
                this.setControllerVisibilityListener(PlayerView.ControllerVisibilityListener { visibility ->
                    showAdditionalControls.value = visibility == VISIBLE
                    showPagerIndicator.value = visibility == VISIBLE
                })
                player = exoPlayer
            }
        },
        modifier = Modifier
            .fillMaxSize()
    )
}