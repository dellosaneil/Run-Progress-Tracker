package com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel



class WorkoutOnGoingViewModel : ViewModel() {

    private val TAG = "WorkoutOnGoingViewModel"

    private var _isRunning = MutableLiveData(false)
    private var _firstStart = MutableLiveData(false)

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


}