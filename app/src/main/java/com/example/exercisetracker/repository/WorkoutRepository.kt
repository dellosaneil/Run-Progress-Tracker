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
    fun retrieveRangeDateWithModeByStartTime(mode: String, firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateWithModeByStartTime(mode, firstRange, secondRange)
    fun retrieveRangeDateWithModeByTotalTime(mode: String, firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateWithModeByTotalTime(mode, firstRange, secondRange)
    fun retrieveRangeDateWithModeByAvgSpeed(mode: String, firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateWithModeByAvgSpeed(mode, firstRange, secondRange)
    fun retrieveRangeDateWithModeByTotalKM(mode: String, firstRange: Long, secondRange: Long) = workoutDao.retrieveRangeDateWithModeByTotalKM(mode, firstRange, secondRange)

    fun sumTotalTimeRangeDate(firstRange: Long, secondRange: Long) = workoutDao.sumTotalTimeRangeDate(firstRange, secondRange)
    fun avgTotalTimeRangeDate(firstRange: Long, secondRange: Long) = workoutDao.avgTotalTimeRangeDate(firstRange, secondRange)
    fun sumTotalKMRangeDate(firstRange: Long, secondRange: Long) = workoutDao.sumTotalKMRangeDate(firstRange, secondRange)
    fun avgKMRangeDate(firstRange: Long, secondRange: Long) = workoutDao.avgKMRangeDate(firstRange, secondRange)
    fun avgSpeedRangeDate(firstRange: Long, secondRange: Long) = workoutDao.avgSpeedRangeDate(firstRange, secondRange)
    fun sumTotalWorkoutCountRangeDate(firstRange: Long, secondRange: Long) = workoutDao.sumTotalWorkoutCountRangeDate(firstRange, secondRange)

    fun sumTotalTimeRangeDateWithFilter(firstRange: Long, secondRange: Long, mode : String) = workoutDao.sumTotalTimeRangeDateWithFilter(firstRange, secondRange, mode)
    fun avgTotalTimeRangeDateWithFilter(firstRange: Long, secondRange: Long, mode : String) = workoutDao.avgTotalTimeRangeDateWithFilter(firstRange, secondRange, mode)
    fun sumTotalKMRangeDateWithFilter(firstRange: Long, secondRange: Long, mode : String) = workoutDao.sumTotalKMRangeDateWithFilter(firstRange, secondRange, mode)
    fun avgKMRangeDateWithFilter(firstRange: Long, secondRange: Long, mode : String) = workoutDao.avgKMRangeDateWithFilter(firstRange, secondRange, mode)
    fun avgSpeedRangeDateWithFilter(firstRange: Long, secondRange: Long, mode : String) = workoutDao.avgSpeedRangeDateWithFilter(firstRange, secondRange, mode)
    fun sumTotalWorkoutCountRangeDateWithFilter(firstRange: Long, secondRange: Long, mode : String) = workoutDao.sumTotalWorkoutCountRangeDateWithFilter(firstRange, secondRange, mode)


}