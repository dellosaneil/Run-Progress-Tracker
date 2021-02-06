package com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel


class WorkoutOnGoingViewModel : ViewModel() {

    private val TAG = "WorkoutOnGoingViewModel"

    private val _isRunning = MutableLiveData(false)
    private val _firstStart = MutableLiveData(false)
    private  val _stopWatchTimer = MutableLiveData("00:00:00:00")

    private val stopwatchObserver : Observer<Long> = Observer {
        _stopWatchTimer.value = convertMilliSecondsToText(it)
    }

    fun stopWatchTimer() : LiveData<String> = _stopWatchTimer
    fun startStopWatch(){
        WorkoutOnGoingService.stopWatchTime.observeForever(stopwatchObserver)
    }

    private fun convertMilliSecondsToText(time : Long): String{
        var timeMilli = time
        val hours = (timeMilli / 3_600_000).toInt()
        timeMilli -= hours * 3_600_000
        val minutes = (timeMilli / 60_000).toInt()
        timeMilli -= minutes * 60_000
        val seconds = (timeMilli / 1_000).toInt()
        timeMilli -= seconds * 1_000

        val hourString = if(hours <= 9) "0$hours" else hours
        val minuteString = if(minutes <= 9) "0$minutes" else minutes
        val secondString = if(seconds <= 9) "0$seconds" else seconds
        val milliString = if(timeMilli <= 99) "0$timeMilli" else timeMilli
        return "$hourString : $minuteString : $secondString : $milliString"
    }



    fun isRunning() : LiveData<Boolean> = _isRunning

    fun changeState(){
        _isRunning.value = !(_isRunning.value as Boolean)
    }

    fun firstStart() :LiveData<Boolean> = _firstStart

    fun startRun(){
        _firstStart.value = true
    }

    fun runStopped() {
        _firstStart.value = false
    }


    override fun onCleared() {
        Log.i(TAG, "onCleared: ")
        WorkoutOnGoingService.stopWatchTime.removeObserver(stopwatchObserver)
        super.onCleared()
    }




}