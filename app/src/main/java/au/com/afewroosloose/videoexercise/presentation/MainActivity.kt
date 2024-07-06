package au.com.afewroosloose.videoexercise.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import au.com.afewroosloose.videoexercise.presentation.Destination.Companion.DETAIL_NAME
import au.com.afewroosloose.videoexercise.presentation.Destination.Companion.ERROR_NAME
import au.com.afewroosloose.videoexercise.presentation.Destination.Companion.LIST_NAME
import au.com.afewroosloose.videoexercise.presentation.Destination.Companion.LOADING
import au.com.afewroosloose.videoexercise.presentation.theme.VideoExerciseTheme
import au.com.afewroosloose.videoexercise.presentation.util.Event
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val error: String? by mainViewModel.errors.collectAsState(initial = null)
            val videos: List<String> by mainViewModel.videos.collectAsState(initial = emptyList())
            val destination by mainViewModel.destination.collectAsState(initial = Event(null))

            val navController: NavHostController = rememberNavController()

            destination.getValue()?.let {
                navController.navigate(it.routeName)
            }

            LaunchedEffect("FetchVideos") {
                mainViewModel.fetchVideosForList()
            }

            VideoExerciseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = Destination.Loading.routeName
                    ) {
                        composable(LOADING) {
                            Loading()
                        }
                        composable(LIST_NAME) {
                            VideoList(listOfUrls = videos, onAction = mainViewModel::onAction)
                        }
                        composable(
                            "$DETAIL_NAME/{videoIndex}",
                            arguments = listOf(navArgument("videoIndex") { type = NavType.IntType })
                        ) { backStackEntry ->
                            VideoDetailScreens(
                                index = backStackEntry.arguments?.getInt(
                                    "videoIndex",
                                    0
                                ) ?: 0,
                                navController,
                                mainViewModel.getCurrentVideoList() // not really ideal, but I was running low on time
                            )
                        }
                        composable(ERROR_NAME) {
                            ErrorText(errorString = error)
                        }
                    }
                }
            }
        }
    }
}