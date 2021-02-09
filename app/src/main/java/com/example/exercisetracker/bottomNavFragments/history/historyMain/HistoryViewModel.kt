package com.example.exercisetracker.bottomNavFragments.history.historyMain

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val workoutRepository: WorkoutRepository) : ViewModel() {

    private val TAG = "HistoryViewModel"
    fun workoutByStartTime() = workoutRepository.retrieveByStartTime()
    fun workoutByTotalTime() = workoutRepository.retrieveByTime()
    fun workoutByTotalDistance() = workoutRepository.retrieveByKM()
    fun workoutFilter(filterBy : String) = workoutRepository.retrieveByMode(filterBy)

}