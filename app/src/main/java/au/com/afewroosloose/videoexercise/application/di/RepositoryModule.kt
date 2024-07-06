package au.com.afewroosloose.videoexercise.application.di

import au.com.afewroosloose.videoexercise.data.repository.VideoRepositoryImpl
import au.com.afewroosloose.videoexercise.domain.repository.VideoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun provideVideoRepository(impl: VideoRepositoryImpl): VideoRepository = impl
}