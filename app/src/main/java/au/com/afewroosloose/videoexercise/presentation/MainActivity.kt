package au.com.afewroosloose.videoexercise.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val error: String? by mainViewModel.errors.collectAsStateWithLifecycle(null)
            val videos: List<String> by mainViewModel.videos.collectAsStateWithLifecycle()
            val destination by mainViewModel.destination.collectAsStateWithLifecycle()

            val navController: NavHostController = rememberNavController()

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
                                videos
                            )
                        }
                        composable(ERROR_NAME) {
                            ErrorText(errorString = error)
                        }
                    }
                }
            }

            destination.getValue()?.let {
                navController.navigate(it.routeName)
            }
        }
    }
}