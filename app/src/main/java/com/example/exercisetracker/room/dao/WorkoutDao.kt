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




/* **************************************************************** DATE RANGE QUERIES **************************************************************** */
    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode AND startTime BETWEEN :firstRange AND :secondRange ORDER BY startTime DESC")
    fun retrieveDateRangeWithMode(mode: String, firstRange: Long, secondRange : Long) : List<WorkoutData>

        // With SORT
    @Query("SELECT * FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange ORDER BY startTime DESC")
    fun retrieveRangeDateStartTime(firstRange: Long, secondRange : Long) : List<WorkoutData>
    @Query("SELECT * FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange ORDER BY totalTime DESC")
    fun retrieveRangeDateTotalTime(firstRange: Long, secondRange : Long) : List<WorkoutData>
    @Query("SELECT * FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange ORDER BY averageSpeed DESC")
    fun retrieveRangeDateAvgSpeed(firstRange: Long, secondRange : Long) : List<WorkoutData>
    @Query("SELECT * FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange ORDER BY totalKM DESC")
    fun retrieveRangeDateTotalKM(firstRange: Long, secondRange : Long) : List<WorkoutData>

        // With SORT and Mode
    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode AND startTime BETWEEN :firstRange AND :secondRange ORDER BY startTime DESC")
    fun retrieveRangeDateWithModeByStartTime(mode: String, firstRange: Long, secondRange : Long) : List<WorkoutData>
    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode AND startTime BETWEEN :firstRange AND :secondRange ORDER BY totalTime DESC")
    fun retrieveRangeDateWithModeByTotalTime(mode: String, firstRange: Long, secondRange : Long) : List<WorkoutData>
    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode AND startTime BETWEEN :firstRange AND :secondRange ORDER BY averageSpeed DESC")
    fun retrieveRangeDateWithModeByAvgSpeed(mode: String, firstRange: Long, secondRange : Long) : List<WorkoutData>
    @Query("SELECT * FROM workout_main WHERE modeOfExercise = :mode AND startTime BETWEEN :firstRange AND :secondRange ORDER BY totalKM DESC")
    fun retrieveRangeDateWithModeByTotalKM(mode: String, firstRange: Long, secondRange : Long) : List<WorkoutData>

    /* **********************************************END********************************************** */


    /* *****************************************STATISTICS**************************************** */

    @Query("SELECT SUM(totalTime) FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange")
    fun sumTotalTimeRangeDate(firstRange: Long, secondRange: Long) : Long
    @Query("SELECT SUM(totalTime) FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange AND modeOfExercise =  :mode")
    fun sumTotalTimeRangeDateWithFilter(firstRange: Long, secondRange: Long, mode : String) : Long


    @Query("SELECT AVG(totalTime) FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange")
    fun avgTotalTimeRangeDate(firstRange: Long, secondRange: Long) : Double
    @Query("SELECT AVG(totalTime) FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange AND modeOfExercise = :mode")
    fun avgTotalTimeRangeDateWithFilter(firstRange: Long, secondRange: Long, mode : String) : Double


    @Query("SELECT SUM(totalKM) FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange")
    fun sumTotalKMRangeDate(firstRange: Long, secondRange: Long) : Double
    @Query("SELECT SUM(totalKM) FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange AND modeOfExercise = :mode")
    fun sumTotalKMRangeDateWithFilter(firstRange: Long, secondRange: Long, mode : String) : Double


    @Query("SELECT AVG(totalKM) FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange")
    fun avgKMRangeDate(firstRange: Long, secondRange: Long) : Double
    @Query("SELECT AVG(totalKM) FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange AND modeOfExercise = :mode")
    fun avgKMRangeDateWithFilter(firstRange: Long, secondRange: Long, mode :String) : Double


    @Query("SELECT AVG(averageSpeed) FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange")
    fun avgSpeedRangeDate(firstRange: Long, secondRange: Long) : Double
    @Query("SELECT AVG(averageSpeed) FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange AND :mode")
    fun avgSpeedRangeDateWithFilter(firstRange: Long, secondRange: Long, mode : String) : Double

    @Query("SELECT COUNT(totalTime) FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange")
    fun sumTotalWorkoutCountRangeDate(firstRange: Long, secondRange: Long) : Int
    @Query("SELECT COUNT(totalTime) FROM workout_main WHERE startTime BETWEEN :firstRange AND :secondRange AND modeOfExercise = :mode")
    fun sumTotalWorkoutCountRangeDateWithFilter(firstRange: Long, secondRange: Long, mode : String) : Int

}