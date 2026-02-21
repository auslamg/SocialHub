package com.example.socialhub.data.local.database

import android.content.Context
import androidx.room.Room
import com.example.socialhub.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SocialHubDatabase {
        // Single database instance for the app process.
        return Room.databaseBuilder(
            context,
            SocialHubDatabase::class.java,
            "socialhub.db"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideUserDao(database: SocialHubDatabase): UserDao {
        // DAOs are lightweight; provide directly from Room.
        return database.userDao()
    }
}
