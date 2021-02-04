package com.example.exercisetracker.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkoutGoalData(
    val startTime: Long = System.currentTimeMillis(),
    val modeOfExercise: String,
    val kmGoal: Double? = null,
    val minutesGoal : Double? = null
) : Parcelable