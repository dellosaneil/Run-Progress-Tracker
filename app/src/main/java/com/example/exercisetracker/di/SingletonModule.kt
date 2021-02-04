package com.example.exercisetracker.di

import android.content.Context
import androidx.room.Room
import com.example.exercisetracker.room.WorkoutDatabase
import com.example.exercisetracker.utility.Constants.Companion.DATABASE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class SingletonModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(context, WorkoutDatabase::class.java, DATABASE_NAME).build()

}