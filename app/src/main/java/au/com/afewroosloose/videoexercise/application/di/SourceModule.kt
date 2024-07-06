package au.com.afewroosloose.videoexercise.application.di

import au.com.afewroosloose.videoexercise.BuildConfig
import au.com.afewroosloose.videoexercise.data.source.VideoSource
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SourceModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(moshi: Moshi): Retrofit {
        return Retrofit.Builder().baseUrl(BuildConfig.BASE_URL).addConverterFactory(
            MoshiConverterFactory.create(moshi)
        ).build()
    }

    @Provides
    @Singleton
    fun provideVideoSource(retrofit: Retrofit): VideoSource {
        return retrofit.create(VideoSource::class.java)
    }
}