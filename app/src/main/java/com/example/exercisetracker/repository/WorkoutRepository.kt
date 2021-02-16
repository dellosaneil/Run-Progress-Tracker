package com.example.exercisetracker.repository

import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.room.dao.WorkoutDao
import javax.inject.Inject

class WorkoutRepository @Inject constructor(private val workoutDao: WorkoutDao) {

    suspend fun insertWorkout(workout: WorkoutData) = workoutDao.insertWorkout(workout)
    suspend fun deleteFromDatabase(workout: WorkoutData) = workoutDao.deleteFromDatabase(workout)
    fun retrieveByTotalKM() = workoutDao.retrieveDataByKM()
    fun retrieveByTotalTime() = workoutDao.retrieveDataByTime()
    fun retrieveByAvgSpeed() = workoutDao.retrieveDataByAvgSpeed()
    fun retrieveByStartTime() = workoutDao.retrieveDataByStartTime()
    fun retrieveModeByTotalKM(mode : String) = workoutDao.retrieveModeByKM(mode)
    fun retrieveModeByTotalTime(mode : String) = workoutDao.retrieveModeByTime(mode)
    fun retrieveModeByAvgSpeed(mode : String) = workoutDao.retrieveModeByAvgSpeed(mode)
    fun retrieveModeByStartTime(mode : String) = workoutDao.retrieveModeByStartTime(mode)

    fun retrieveRangeDateStartTime(firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateStartTime(firstRange, secondRange)
    fun retrieveRangeDateTotalTime(firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateTotalTime(firstRange, secondRange)
    fun retrieveRangeDateAvgSpeed(firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateAvgSpeed(firstRange, secondRange)
    fun retrieveRangeDateTotalKM(firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateTotalKM(firstRange, secondRange)
    fun retrieveRangeDateWithModeWithStartTime(mode: String, firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateWithModeWithStartTime(mode, firstRange, secondRange)
    fun retrieveRangeDateWithModeWithTotalTime(mode: String, firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateWithModeWithTotalTime(mode, firstRange, secondRange)
    fun retrieveRangeDateWithModeWithAvgSpeed(mode: String, firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateWithModeWithAvgSpeed(mode, firstRange, secondRange)
    fun retrieveRangeDateWithModeWithTotalKM(mode: String, firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateWithModeWithTotalKM(mode, firstRange, secondRange)

    fun sumTotalTimeAll() = workoutDao.sumTotalTimeAll()
    fun sumTotalTimeRangeDate(firstRange: Long, secondRange: Long) = workoutDao.sumTotalTimeRangeDate(firstRange, secondRange)
    fun avgTotalTimeAll() = workoutDao.avgTotalTimeAll()
    fun avgTotalTimeRangeDate(firstRange: Long, secondRange: Long) = workoutDao.avgTotalTimeRangeDate(firstRange, secondRange)
    fun sumTotalKMAll() = workoutDao.sumTotalKMAll()
    fun sumTotalKMRangeDate(firstRange: Long, secondRange: Long) = workoutDao.sumTotalKMRangeDate(firstRange, secondRange)
    fun avgKMAll() = workoutDao.avgKMAll()
    fun avgKMRangeDate(firstRange: Long, secondRange: Long) = workoutDao.avgKMRangeDate(firstRange, secondRange)
    fun avgSpeedAll() = workoutDao.avgSpeedAll()
    fun avgSpeedRangeDate(firstRange: Long, secondRange: Long) = workoutDao.avgSpeedRangeDate(firstRange, secondRange)
    fun sumTotalWorkoutCountAll() = workoutDao.sumTotalWorkoutCountAll()
    fun sumTotalWorkoutCountRangeDate(firstRange: Long, secondRange: Long) = workoutDao.sumTotalWorkoutCountRangeDate(firstRange, secondRange)

}