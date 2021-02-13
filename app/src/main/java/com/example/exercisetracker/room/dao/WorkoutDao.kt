package com.example.exercisetracker.room.dao

import androidx.room.*
import com.example.exercisetracker.data.WorkoutData

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutData)

    @Delete
    suspend fun deleteFromDatabase(workout: WorkoutData)

    @Query("SELECT * FROM workout_main ORDER BY totalKM DESC")
    fun retrieveDataByKM(): List<WorkoutData>

    @Query("SELECT * FROM workout_main ORDER BY totalTime DESC")
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


    /* **************** DATE RANGE QUERIES **************** */

    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode AND startTime BETWEEN :firstRange AND :secondRange ORDER BY startTime DESC")
    fun retrieveDateRangeWithMode(mode: String, firstRange: Long, secondRange : Long) : List<WorkoutData>

    @Query("SELECT * FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange ORDER BY startTime DESC")
    fun retrieveRangeDate(firstRange: Long, secondRange : Long) : List<WorkoutData>



    @Query("SELECT * FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange ORDER BY startTime DESC")
    fun retrieveRangeDateStartTime(firstRange: Long, secondRange : Long) : List<WorkoutData>
    @Query("SELECT * FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange ORDER BY totalTime DESC")
    fun retrieveRangeDateTotalTime(firstRange: Long, secondRange : Long) : List<WorkoutData>
    @Query("SELECT * FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange ORDER BY averageSpeed DESC")
    fun retrieveRangeDateAvgSpeed(firstRange: Long, secondRange : Long) : List<WorkoutData>
    @Query("SELECT * FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange ORDER BY totalKM DESC")
    fun retrieveRangeDateTotalKM(firstRange: Long, secondRange : Long) : List<WorkoutData>


    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode AND startTime BETWEEN :firstRange AND :secondRange ORDER BY startTime DESC")
    fun retrieveRangeDateWithModeWithStartTime(mode: String, firstRange: Long, secondRange : Long) : List<WorkoutData>
    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode AND startTime BETWEEN :firstRange AND :secondRange ORDER BY totalTime DESC")
    fun retrieveRangeDateWithModeWithTotalTime(mode: String, firstRange: Long, secondRange : Long) : List<WorkoutData>
    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode AND startTime BETWEEN :firstRange AND :secondRange ORDER BY averageSpeed DESC")
    fun retrieveRangeDateWithModeWithAvgSpeed(mode: String, firstRange: Long, secondRange : Long) : List<WorkoutData>
    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode AND startTime BETWEEN :firstRange AND :secondRange ORDER BY totalKM DESC")
    fun retrieveRangeDateWithModeWithTotalKM(mode: String, firstRange: Long, secondRange : Long) : List<WorkoutData>




}