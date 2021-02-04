package com.example.exercisetracker.repository

import androidx.lifecycle.LiveData
import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.room.dao.WorkoutDao
import javax.inject.Inject

class WorkoutRepository @Inject constructor(private val workoutDao: WorkoutDao) {

    suspend fun insertWorkout(workout: WorkoutData) = workoutDao.insertWorkout(workout)
    suspend fun deleteFromDatabase(time: Long) = workoutDao.deleteFromDatabase(time)
    fun retrieveByKM(): LiveData<List<WorkoutData>> = workoutDao.retrieveDataByKM()
    fun retrieveByTime(): LiveData<List<WorkoutData>> = workoutDao.retrieveDataByTime()
    fun retrieveByAvgSpeed(): LiveData<List<WorkoutData>> = workoutDao.retrieveDataByAvgSpeed()
    fun retrieveByStartTime(): LiveData<List<WorkoutData>> = workoutDao.retrieveDataByStartTime()

}