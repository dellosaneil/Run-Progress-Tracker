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
    fun retrieveDataByKM(): LiveData<List<WorkoutData>>

    @Query("SELECT * FROM workout_main ORDER BY  totalTime DESC")
    fun retrieveDataByTime(): LiveData<List<WorkoutData>>

    @Query("SELECT * FROM workout_main ORDER BY averageSpeed DESC")
    fun retrieveDataByAvgSpeed(): LiveData<List<WorkoutData>>

    @Query("SELECT * FROM workout_main ORDER BY startTime DESC")
    fun retrieveDataByStartTime(): LiveData<List<WorkoutData>>

    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode")
    fun retrieveSpecificMode(mode : String) : LiveData<List<WorkoutData>>

}