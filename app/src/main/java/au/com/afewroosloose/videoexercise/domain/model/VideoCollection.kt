package au.com.afewroosloose.videoexercise.domain.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class VideoCollection(val manifest: Map<String, String>)