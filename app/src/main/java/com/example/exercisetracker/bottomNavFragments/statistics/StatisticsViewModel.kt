package com.example.exercisetracker.bottomNavFragments.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exercisetracker.data.BarChartData
import com.example.exercisetracker.data.LineChartData
import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val repository: WorkoutRepository) :
    ViewModel() {


    private val _totalDistance = MutableLiveData("0.00 km")
    private val _totalTime = MutableLiveData("00:00:00")
    private val _averageDistance = MutableLiveData("0.00 km")
    private val _averageTime = MutableLiveData("00:00:00")
    private val _averageSpeed = MutableLiveData("0.00 km/h")

    private val _totalWorkout = MutableLiveData(0)


    fun totalWorkout(): LiveData<Int> = _totalWorkout
    fun totalDistance(): LiveData<String> = _totalDistance
    fun totalTime(): LiveData<String> = _totalTime
    fun averageDistance(): LiveData<String> = _averageDistance
    fun averageTime(): LiveData<String> = _averageTime
    fun averageSpeed(): LiveData<String> = _averageSpeed

    init {
        allWorkoutRecord()
    }

    fun allWorkoutRecord() {
        viewModelScope.launch(IO) {
            val tDistance = repository.sumTotalKMAll()
            val tTime = repository.sumTotalTimeAll()
            val aDistance = repository.avgKMAll()
            val aTime = repository.avgTotalTimeAll()
            val aSpeed = repository.avgSpeedAll()
            val tWorkout = repository.sumTotalWorkoutCountAll()
            withContext(Main) {
                _totalDistance.value = formatKM(tDistance)
                _totalTime.value = millisecondToTime(tTime)
                _averageDistance.value = formatKM(aDistance)
                _averageTime.value = millisecondToTime(aTime.toLong())
                _averageSpeed.value = formatAverageSpeed(aSpeed)
                _totalWorkout.value = tWorkout
            }
        }
    }

    private fun workoutList(firstRange: Long, secondRange: Long): List<WorkoutData> =
        repository.retrieveRangeDateAvgSpeed(firstRange, secondRange)

    fun barChartData(firstRange: Long, secondRange: Long, exercise: Array<String>): BarChartData {
        val arrayData = FloatArray(6) { 0.0F }
        val tempData = workoutList(firstRange, secondRange)
        for (data in tempData) {
            when (data.modeOfExercise) {
                exercise[0] -> {
                    arrayData[0] += data.totalKM
                    arrayData[1] += data.totalTime.toFloat()
                }
                exercise[1] -> {
                    arrayData[2] += data.totalKM
                    arrayData[3] += data.totalTime.toFloat()
                }
                else -> {
                    arrayData[4] += data.totalKM
                    arrayData[5] += data.totalTime.toFloat()
                }
            }
        }
        return BarChartData(
            arrayData[0],
            arrayData[1],
            arrayData[2],
            arrayData[3],
            arrayData[4],
            arrayData[5]
        )
    }

    fun lineChartData(firstRange: Long, secondRange: Long): LineChartData {
        val startTimeArray = mutableListOf<Long>()
        val totalKMArray = mutableListOf<Float>()
        val totalTimeArray = mutableListOf<Long>()
        val averageSpeedArray = mutableListOf<Float>()
        val modeOfExercise = mutableListOf<String>()
        val tempData = workoutList(firstRange, secondRange)
        for (data in tempData) {
            startTimeArray.add(data.startTime)
            totalKMArray.add(data.totalKM)
            totalTimeArray.add(data.totalTime)
            averageSpeedArray.add(data.averageSpeed.toFloat())
            modeOfExercise.add(data.modeOfExercise)
        }
        return LineChartData(startTimeArray, totalKMArray, totalTimeArray, averageSpeedArray, modeOfExercise)
    }


    fun dateRangeRecord(firstRange: Long, secondRange: Long) {
        viewModelScope.launch(IO) {
            val tDistance = repository.sumTotalKMRangeDate(firstRange, secondRange)
            val tTime = repository.sumTotalTimeRangeDate(firstRange, secondRange)
            val aDistance = repository.avgKMRangeDate(firstRange, secondRange)
            val aTime = repository.avgTotalTimeRangeDate(firstRange, secondRange)
            val aSpeed = repository.avgSpeedRangeDate(firstRange, secondRange)
            val tWorkout = repository.sumTotalWorkoutCountRangeDate(firstRange, secondRange)
            withContext(Main) {
                _totalDistance.value = formatKM(tDistance)
                _totalTime.value = millisecondToTime(tTime)
                _averageDistance.value = formatKM(aDistance)
                _averageTime.value = millisecondToTime(aTime.toLong())
                _averageSpeed.value = formatAverageSpeed(aSpeed)
                _totalWorkout.value = tWorkout
            }
        }
    }


    private fun formatAverageSpeed(speed: Double) = "${String.format("%.2f", speed)} km/h"

    private fun formatKM(km: Double): String = "${String.format("%.2f", km)} km"

    private fun millisecondToTime(time: Long): String {
        var timeMilli = time
        val hours = (timeMilli / 3_600_000).toInt()
        timeMilli -= hours * 3_600_000
        val minutes = (timeMilli / 60_000).toInt()
        timeMilli -= minutes * 60_000
        val seconds = (timeMilli / 1_000).toInt()
        timeMilli -= seconds * 1_000

        val hourString = if (hours <= 9) "0$hours" else hours
        val minuteString = if (minutes <= 9) "0$minutes" else minutes
        val secondString = if (seconds <= 9) "0$seconds" else seconds
        return "$hourString : $minuteString : $secondString"
    }


}