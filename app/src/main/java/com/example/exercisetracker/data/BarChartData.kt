package com.example.exercisetracker.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BarChartData(
    val cyclingKilometers: Float,
    val cyclingTime: Float,

    val walkingKilometers: Float,
    val walkingTime: Float,

    val joggingKilometers: Float,
    val joggingTime: Float,

    val startDate : Long = 0L,
    val endDate : Long = System.currentTimeMillis()

) : Parcelable
