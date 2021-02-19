package com.example.exercisetracker.bottomNavFragments.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exercisetracker.data.BarChartData
import com.example.exercisetracker.data.LineChartData
import com.example.exercisetracker.data.PieChart
import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.repository.WorkoutRepository
import com.example.exercisetracker.utility.UtilityFunctions.convertMilliSecondsToText
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

    private val _firstMilliRange = MutableLiveData(0L)
    private val _secondMilliRange = MutableLiveData(0L)
    private val _filterItemChecked = MutableLiveData(3)


    fun firstMilliRange() : LiveData<Long> = _firstMilliRange
    fun secondMilliRange() : LiveData<Long> = _secondMilliRange
    fun filterItemChecked() : LiveData<Int> = _filterItemChecked

    fun setMilliRange(first : Long, second : Long) {
        _firstMilliRange.value = first
        _secondMilliRange.value = second
    }

    fun setItemChecked(index : Int) {
        _filterItemChecked.value = index
    }


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
            dateRangeRecord()
        }
    }

    /*Updates Record depending on the WorkoutFilter*/
    fun workoutRecordWithFilter(
        firstRange: Long = 0,
        secondRange: Long = System.currentTimeMillis(),
        mode: String
    ) {
        viewModelScope.launch(IO) {
            val tDistance: Double =
                repository.sumTotalKMRangeDateWithFilter(firstRange, secondRange, mode)
            val tTime: Long =
                repository.sumTotalTimeRangeDateWithFilter(firstRange, secondRange, mode)
            val aDistance: Double =
                repository.avgKMRangeDateWithFilter(firstRange, secondRange, mode)
            val aTime: Double =
                repository.avgTotalTimeRangeDateWithFilter(firstRange, secondRange, mode)
            val aSpeed: Double =
                repository.avgSpeedRangeDateWithFilter(firstRange, secondRange, mode)
            val tWorkout: Int =
                repository.sumTotalWorkoutCountRangeDateWithFilter(firstRange, secondRange, mode)
            withContext(Main) {
                _totalDistance.value = formatKM(tDistance)
                _totalTime.value = convertMilliSecondsToText(tTime, false, viewModelScope)
                _averageDistance.value = formatKM(aDistance)
                _averageTime.value = convertMilliSecondsToText(aTime.toLong(), false, viewModelScope)
                _averageSpeed.value = formatAverageSpeed(aSpeed)
                _totalWorkout.value = tWorkout

            }
        }
    }

    /*Returns List of Workout Data with DateRange and Workout Filter*/
    private fun workoutListWithFilter(firstRange: Long, secondRange: Long, mode: String) =
        repository.retrieveRangeDateWithModeByStartTime(mode, firstRange, secondRange)

    /*Returns List of Workout Data (ALL)*/
    private fun workoutList(firstRange: Long, secondRange: Long): List<WorkoutData> =
        repository.retrieveRangeDateStartTime(firstRange, secondRange)

    fun pieChartData(firstRange: Long, secondRange: Long, singleItems: Array<String>) : PieChart {
        val arrayData = FloatArray(3) {0.0f}
        val tempData = workoutList(firstRange, secondRange)
        for(data in tempData){
            when(data.modeOfExercise){
                singleItems[0] -> arrayData[0]++
                singleItems[1] -> arrayData[1]++
                singleItems[2] -> arrayData[2]++
            }
        }
        return PieChart(arrayData[0], arrayData[1], arrayData[2])
    }


    /*Returns a BarChartData*/
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
            arrayData[5],
            firstRange,
            secondRange
        )
    }

    /*Returns a LineChartData*/
    fun lineChartData(firstRange: Long, secondRange: Long, index: Int, singleItems: Array<String>): LineChartData {
        val startTimeArray = mutableListOf<Long>()
        val totalKMArray = mutableListOf<Float>()
        val totalTimeArray = mutableListOf<Long>()
        val averageSpeedArray = mutableListOf<Float>()
        val modeOfExercise = mutableListOf<String>()
        val tempData : List<WorkoutData> = if(index == 3){
            workoutList(firstRange, secondRange)
        }else{
            workoutListWithFilter(firstRange, secondRange, singleItems[index] )
        }

        for (data in tempData) {
            startTimeArray.add(data.startTime)
            totalKMArray.add(data.totalKM)
            totalTimeArray.add(data.totalTime)
            averageSpeedArray.add(data.averageSpeed.toFloat())
            modeOfExercise.add(data.modeOfExercise)
        }
        return LineChartData(
            startTimeArray,
            totalKMArray,
            totalTimeArray,
            averageSpeedArray,
            modeOfExercise
        )
    }

    /*Updates Record from Date Range*/
    fun dateRangeRecord(firstRange: Long = 0, secondRange: Long = System.currentTimeMillis()) {
        viewModelScope.launch(IO) {
            val tDistance = repository.sumTotalKMRangeDate(firstRange, secondRange)
            val tTime = repository.sumTotalTimeRangeDate(firstRange, secondRange)
            val aDistance = repository.avgKMRangeDate(firstRange, secondRange)
            val aTime = repository.avgTotalTimeRangeDate(firstRange, secondRange)
            val aSpeed = repository.avgSpeedRangeDate(firstRange, secondRange)
            val tWorkout = repository.sumTotalWorkoutCountRangeDate(firstRange, secondRange)
            withContext(Main) {
                _totalDistance.value = formatKM(tDistance)
                _totalTime.value = convertMilliSecondsToText(tTime, false, viewModelScope)
                _averageDistance.value = formatKM(aDistance)
                _averageTime.value = convertMilliSecondsToText(aTime.toLong(), false, viewModelScope)
                _averageSpeed.value = formatAverageSpeed(aSpeed)
                _totalWorkout.value = tWorkout
            }
        }
    }

    private fun formatAverageSpeed(speed: Double) = "${String.format("%.2f", speed)} km/h"

    private fun formatKM(km: Double): String = "${String.format("%.2f", km)} km"



}