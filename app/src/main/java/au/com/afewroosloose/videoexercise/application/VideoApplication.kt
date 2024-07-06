package au.com.afewroosloose.videoexercise.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VideoApplication: Application() {

    override fun onCreate() {
        super.onCreate()
    }
}