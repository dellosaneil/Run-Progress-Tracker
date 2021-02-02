package com.example.exercisetracker.data


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*


@Parcelize
data class WorkoutLog(
    val modeOfExercise : String,
    val startTime : Date,
    val endTime : Date,
//    val route : List<>
    val totalKM : Double,
    val totalTime: Long,
    val averageSpeed: Double
): Parcelable
