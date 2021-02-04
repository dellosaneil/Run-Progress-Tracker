package com.example.exercisetracker.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WorkoutGoalData(
    val startTime: Long,
    val modeOfExercise: String,
    val kmGoal: Long,
    val minutesGoal: Long
) : Parcelable