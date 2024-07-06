package au.com.afewroosloose.videoexercise.data.source

import au.com.afewroosloose.videoexercise.domain.model.VideoCollection
import retrofit2.http.GET

interface VideoSource {
    @GET("/")
    suspend fun getVideos(): VideoCollection
}