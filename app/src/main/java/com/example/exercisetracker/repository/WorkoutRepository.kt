package com.example.exercisetracker.repository

import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.room.dao.WorkoutDao
import javax.inject.Inject

class WorkoutRepository @Inject constructor(private val workoutDao: WorkoutDao) {

    suspend fun insertWorkout(workout: WorkoutData) = workoutDao.insertWorkout(workout)
    suspend fun deleteFromDatabase(workout: WorkoutData) = workoutDao.deleteFromDatabase(workout)
    fun retrieveByKM() = workoutDao.retrieveDataByKM()
    fun retrieveByTime() = workoutDao.retrieveDataByTime()
    fun retrieveByAvgSpeed() = workoutDao.retrieveDataByAvgSpeed()
    fun retrieveByStartTime() = workoutDao.retrieveDataByStartTime()
    fun retrieveModeByKM(mode : String) = workoutDao.retrieveModeByKM(mode)
    fun retrieveModeByTime(mode : String) = workoutDao.retrieveModeByTime(mode)
    fun retrieveModeByAvgSpeed(mode : String) = workoutDao.retrieveModeByAvgSpeed(mode)
    fun retrieveModeByStartTime(mode : String) = workoutDao.retrieveModeByStartTime(mode)

    fun retrieveRangeDate(firstRange : Long, secondRange : Long) = workoutDao.retrieveRangeDate(firstRange,secondRange)
    fun retrieveRangeWithMode(mode: String, firstRange: Long, secondRange: Long) = workoutDao.retrieveDateRangeWithMode(mode, firstRange, secondRange)
    fun retrieveRangeDateStartTime(firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateStartTime(firstRange, secondRange)
    fun retrieveRangeDateTotalTime(firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateTotalTime(firstRange, secondRange)
    fun retrieveRangeDateAvgSpeed(firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateAvgSpeed(firstRange, secondRange)
    fun retrieveRangeDateTotalKM(firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateTotalKM(firstRange, secondRange)
    fun retrieveRangeDateWithModeWithStartTime(mode: String, firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateWithModeWithStartTime(mode, firstRange, secondRange)
    fun retrieveRangeDateWithModeWithTotalTime(mode: String, firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateWithModeWithTotalTime(mode, firstRange, secondRange)
    fun retrieveRangeDateWithModeWithAvgSpeed(mode: String, firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateWithModeWithAvgSpeed(mode, firstRange, secondRange)
    fun retrieveRangeDateWithModeWithTotalKM(mode: String, firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateWithModeWithTotalKM(mode, firstRange, secondRange)




}