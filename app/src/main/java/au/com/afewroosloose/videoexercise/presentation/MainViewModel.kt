package au.com.afewroosloose.videoexercise.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.com.afewroosloose.videoexercise.application.di.ConcurrencyModule.Companion.IO_DISPATCHER
import au.com.afewroosloose.videoexercise.application.di.ConcurrencyModule.Companion.MAIN_DISPATCHER
import au.com.afewroosloose.videoexercise.domain.repository.VideoRepository
import au.com.afewroosloose.videoexercise.presentation.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MainViewModel @Inject constructor(
    private val videoRepository: VideoRepository,
    @Named(IO_DISPATCHER) private val ioDispatcher: CoroutineDispatcher,
    @Named(MAIN_DISPATCHER) private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    val videos: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val errors: MutableSharedFlow<String> = MutableSharedFlow()
    val destination: MutableSharedFlow<Event<Destination>> = MutableSharedFlow()

    private val hasFetchedVideos = AtomicBoolean(false)
    fun fetchVideosForList() {
        if (!hasFetchedVideos.getAndSet(true)) { // we'll assume one attempt since the specs don't mention how to retry
            viewModelScope.launch(ioDispatcher) {
                val result = videoRepository.getVideoList(false)
                withContext(mainDispatcher) {
                    if (!result.isSuccess) {
                        errors.emit(result.exceptionOrNull()?.message ?: "Something went wrong")
                        destination.emit(Event(Destination.Error))
                    } else {
                        try {
                            videos.emit(result.getOrThrow().manifest.values.toList())
                            destination.emit(Event(Destination.List))
                        } catch (ex: Exception) {
                            errors.emit(ex.message ?: "Something went wrong")
                            destination.emit(Event(Destination.Error))
                        }
                    }
                }
            }
        }
    }

    fun getCurrentVideoList() = videos.value

    fun onAction(action: Action) {
        when (action) {
            is Action.FullScreen -> {
                viewModelScope.launch {
                    destination.emit(Event(Destination.Detail(action.index)))
                }
            }
        }
    }

    sealed class Action {
        data class FullScreen(val index: Int) : Action()
    }
}