package ua.`in`.asilichenko.shortedges.di

import ua.`in`.asilichenko.shortedges.util.DispatcherProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.`in`.asilichenko.shortedges.data.FileServerApi
import ua.`in`.asilichenko.shortedges.data.PreferenceManager
import ua.`in`.asilichenko.shortedges.data.UdpClient
import ua.`in`.asilichenko.shortedges.data.UdpJavaClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideFileServerApi(): FileServerApi = Retrofit.Builder()
        .baseUrl(FileServerApi.BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FileServerApi::class.java)

    @Provides
    @Singleton
    fun provideUdpClient(): UdpClient {
        return UdpClient()
    }

    @Provides
    @Singleton
    fun provideUdpJavaClient(): UdpJavaClient {
        return UdpJavaClient()
    }

    @Singleton
    @Provides
    fun getPreferenceManager() : PreferenceManager {
        return PreferenceManager
    }

    @Singleton
    @Provides
    fun provideDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }

}