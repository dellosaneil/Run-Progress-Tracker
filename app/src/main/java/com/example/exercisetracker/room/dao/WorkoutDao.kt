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

    @Query("SELECT * FROM workout_main ORDER BY totalKM DESC")
    fun retrieveDataByKM(): List<WorkoutData>

    @Query("SELECT * FROM workout_main ORDER BY  totalTime DESC")
    fun retrieveDataByTime(): List<WorkoutData>

    @Query("SELECT * FROM workout_main ORDER BY averageSpeed DESC")
    fun retrieveDataByAvgSpeed(): List<WorkoutData>

    @Query("SELECT * FROM workout_main ORDER BY startTime DESC")
    fun retrieveDataByStartTime(): List<WorkoutData>


    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode ORDER BY totalKM DESC")
    fun retrieveModeByKM(mode : String) : List<WorkoutData>
    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode ORDER BY totalTime DESC")
    fun retrieveModeByTime(mode : String) : List<WorkoutData>
    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode ORDER BY averageSpeed DESC")
    fun retrieveModeByAvgSpeed(mode : String) : List<WorkoutData>
    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode ORDER BY startTime DESC")
    fun retrieveModeByStartTime(mode : String) : List<WorkoutData>





    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode")
    fun retrieveSpecificMode(mode : String) : List<WorkoutData>

}