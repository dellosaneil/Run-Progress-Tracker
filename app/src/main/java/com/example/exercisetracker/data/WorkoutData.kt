package com.example.exercisetracker.data


import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize
import java.util.*


@Parcelize
@Entity(tableName = "workout_main")
data class WorkoutData(
    val modeOfExercise : String,
    @PrimaryKey val startTime : Date,
    val endTime : Date,
    val route : List<LatLng>,
    val totalKM : Double,
    val totalTime: Long,
    val averageSpeed: Double
): Parcelable
