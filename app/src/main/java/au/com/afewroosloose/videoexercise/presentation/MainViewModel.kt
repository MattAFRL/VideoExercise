package au.com.afewroosloose.videoexercise.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import au.com.afewroosloose.videoexercise.application.di.ConcurrencyModule.Companion.IO_DISPATCHER
import au.com.afewroosloose.videoexercise.application.di.ConcurrencyModule.Companion.MAIN_DISPATCHER
import au.com.afewroosloose.videoexercise.domain.repository.VideoRepository
import au.com.afewroosloose.videoexercise.presentation.util.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
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

    private val _videos: MutableStateFlow<List<String>> = MutableStateFlow(emptyList())
    val videos: StateFlow<List<String>> = _videos
    private val _errors: MutableSharedFlow<String> = MutableSharedFlow()
    val errors: SharedFlow<String> = _errors
    private val _destination: MutableStateFlow<Event<Destination>> =
        MutableStateFlow(Event(Destination.Loading))
    val destination: StateFlow<Event<Destination>> = _destination

    private val hasFetchedVideos = AtomicBoolean(false)
    fun fetchVideosForList() {
        if (!hasFetchedVideos.getAndSet(true)) { // we'll assume one attempt since the specs don't mention how to retry
            viewModelScope.launch(ioDispatcher) {
                val result = videoRepository.getVideoList(false)
                withContext(mainDispatcher) {
                    if (!result.isSuccess) {
                        _errors.emit(result.exceptionOrNull()?.message ?: "Something went wrong")
                        _destination.emit(Event(Destination.Error))
                    } else {
                        try {
                            _videos.emit(result.getOrThrow().manifest.values.toList())
                            _destination.emit(Event(Destination.List))
                        } catch (ex: Exception) {
                            _errors.emit(ex.message ?: "Something went wrong")
                            _destination.emit(Event(Destination.Error))
                        }
                    }
                }
            }
        }
    }
    
    fun onAction(action: Action) {
        when (action) {
            is Action.FullScreen -> {
                viewModelScope.launch {
                    _destination.emit(Event(Destination.Detail(action.index)))
                }
            }
        }
    }

    sealed class Action {
        data class FullScreen(val index: Int) : Action()
    }
}