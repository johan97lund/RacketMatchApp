// app/src/main/java/com/johan/racketmatchapp/di/DatabaseModule.kt
package com.johan.racketmatchapp.di

import android.content.Context
import androidx.room.Room
import com.johan.racketmatchapp.core.data.local.AppDatabase
import com.johan.racketmatchapp.core.data.local.daos.MatchDao
import com.johan.racketmatchapp.core.data.local.daos.PlayerDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext ctx: Context
    ): AppDatabase =
        Room.databaseBuilder(ctx, AppDatabase::class.java, "racket_db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun providePlayerDao(db: AppDatabase): PlayerDao =
        db.playerDao()

    @Provides
    fun provideMatchDao(db: AppDatabase): MatchDao =
        db.matchDao()
}
