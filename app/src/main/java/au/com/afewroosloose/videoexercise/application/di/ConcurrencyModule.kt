package au.com.afewroosloose.videoexercise.application.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class ConcurrencyModule {
    @Provides
    @Named(IO_DISPATCHER)
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Named(MAIN_DISPATCHER)
    fun provideMainDispatcher(): CoroutineDispatcher = Dispatchers.Main

    companion object {
        const val IO_DISPATCHER = "ioDispatcher"
        const val MAIN_DISPATCHER = "mainDispatcher"
    }
}