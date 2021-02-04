package com.example.exercisetracker.room.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.exercisetracker.data.WorkoutData

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutData)

    @Query("DELETE FROM workout_main WHERE startTime = :time")
    suspend fun deleteFromDatabase(time: Long)

    @Query("SELECT * FROM workout_main ORDER BY totalKM")
    fun retrieveDataByKM(): LiveData<WorkoutData>

    @Query("SELECT * FROM workout_main ORDER BY  totalTime")
    fun retrieveDataByTime(): LiveData<WorkoutData>

    @Query("SELECT * FROM workout_main ORDER BY averageSpeed")
    fun retrieveDataByAvgSpeed(): LiveData<WorkoutData>

    @Query("SELECT * FROM workout_main ORDER BY startTime")
    fun retrieveDataByStartTime(): LiveData<WorkoutData>

}