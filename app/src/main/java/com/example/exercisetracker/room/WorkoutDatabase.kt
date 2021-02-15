package com.example.exercisetracker.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.room.dao.WorkoutDao


@Database(entities = [WorkoutData::class], version = 4, exportSchema = false)
@TypeConverters(MyConverters::class)
abstract class WorkoutDatabase : RoomDatabase(){
    abstract fun workoutDao() : WorkoutDao
}