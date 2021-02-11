package com.example.exercisetracker.data

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "workout_main")
data class WorkoutData(
    val modeOfExercise : String,
    val startTime : Long,
    val endTime : Long,
    val route : List<LatLng>,
    val totalKM : Float,
    val totalTime: Long,
    val averageSpeed: Double
): Parcelable{
    @IgnoredOnParcel
    @PrimaryKey(autoGenerate = true) var id : Int = 0
}
