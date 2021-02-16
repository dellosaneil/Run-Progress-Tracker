package com.example.exercisetracker.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LineChartData(
    val startTime: List<Long>,
    val totalKM: List<Double>,
    val totalTime: List<Long>,
    val averageSpeed: List<Double>
) : Parcelable
