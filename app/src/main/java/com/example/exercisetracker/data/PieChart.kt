package com.example.exercisetracker.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PieChart(
    val cycling: Float,
    val walking: Float,
    val jogging: Float
) : Parcelable
