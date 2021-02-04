package com.example.exercisetracker.repository

import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.room.dao.WorkoutDao
import javax.inject.Inject

class WorkoutRepository @Inject constructor(private val workoutDao: WorkoutDao) {

    suspend fun insertWorkout(workout: WorkoutData) = workoutDao.insertWorkout(workout)
    suspend fun deleteFromDatabase(time: Long) = workoutDao.deleteFromDatabase(time)
    fun retrieveByKM() = workoutDao.retrieveDataByKM()
    fun retrieveByTime() = workoutDao.retrieveDataByTime()
    fun retrieveByAvgSpeed() = workoutDao.retrieveDataByAvgSpeed()
    fun retrieveByStartTime() = workoutDao.retrieveDataByStartTime()

}