package com.example.exercisetracker.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BarChartData(
    val kilometers: List<Double>,
    val time: List<Long>,
    val totalKM: Double,
    val totalTime: Long
) : Parcelable
