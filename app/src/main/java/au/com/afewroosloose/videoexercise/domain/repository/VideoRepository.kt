package au.com.afewroosloose.videoexercise.domain.repository

import au.com.afewroosloose.videoexercise.domain.model.VideoCollection

interface VideoRepository {
    suspend fun getVideoList(refresh: Boolean): Result<VideoCollection>
}