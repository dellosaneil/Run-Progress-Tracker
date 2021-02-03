package com.example.exercisetracker.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.room.dao.WorkoutDao


@Database(entities = [WorkoutData::class], version = 1)
abstract class WorkoutDatabase : RoomDatabase(){
    abstract fun workoutDao() : WorkoutDao
}