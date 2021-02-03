package com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel



class WorkoutOnGoingViewModel : ViewModel() {

    private var _isRunning = MutableLiveData(false)
    private var _firstStart = MutableLiveData(false)

    fun isRunning() : LiveData<Boolean> = _isRunning


    fun changeState(){
        _isRunning.value = !(_isRunning.value as Boolean)
    }

    fun firstStart() = _firstStart

    fun startRun(){
        _firstStart.value = true
    }

}