package com.example.exercisetracker.di

import com.example.exercisetracker.room.WorkoutDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
class ServiceModule {

    @ServiceScoped
    @Provides
    fun provideWorkoutDao(database : WorkoutDatabase) = database.workoutDao()

}