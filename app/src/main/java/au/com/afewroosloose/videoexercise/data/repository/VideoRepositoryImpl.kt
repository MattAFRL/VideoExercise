package au.com.afewroosloose.videoexercise.data.repository

import au.com.afewroosloose.videoexercise.data.source.VideoSource
import au.com.afewroosloose.videoexercise.domain.model.VideoCollection
import au.com.afewroosloose.videoexercise.domain.repository.VideoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepositoryImpl @Inject constructor(private val videoSource: VideoSource) :
    VideoRepository {

    // just some lazy caching in case we need to keep fetching the value
    private var isCached: Boolean = false
    private lateinit var cachedValue: VideoCollection
    override suspend fun getVideoList(refresh: Boolean): Result<VideoCollection> {
        return try {
            if (!isCached || refresh) {
                cachedValue = videoSource.getVideos()
            }
            isCached = true
            Result.success(cachedValue)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}