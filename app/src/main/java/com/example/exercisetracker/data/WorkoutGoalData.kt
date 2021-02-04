package com.example.exercisetracker.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkoutGoalData(
    val startTime: Long = System.currentTimeMillis(),
    val modeOfExercise: String,
    val kmGoal: Double = 0.0,
    val minutesGoal : Double = 0.0
) : Parcelable