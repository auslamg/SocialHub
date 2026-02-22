package com.example.socialhub.data.remote

import com.example.socialhub.data.remote.api.PostApi
import com.example.socialhub.data.remote.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import javax.inject.Named
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object RemoteModule {
    private const val JSONPLACEHOLDER_BASE_URL = "https://jsonplaceholder.typicode.com/"
    private const val DUMMYJSON_BASE_URL = "https://dummyjson.com/"

    @Provides
    @Singleton
    @Named("jsonplaceholder")
    fun provideJsonPlaceholderRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(JSONPLACEHOLDER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("dummyjson")
    fun provideDummyJsonRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(DUMMYJSON_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideUserApi(@Named("dummyjson") retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun providePostApi(@Named("dummyjson") retrofit: Retrofit): PostApi {
        return retrofit.create(PostApi::class.java)
    }
}
