package com.example.exercisetracker.bottomNavFragments.history.historyMain

import androidx.lifecycle.ViewModel
import com.example.exercisetracker.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val workoutRepository: WorkoutRepository) : ViewModel() {

    fun workoutByStartTime() = workoutRepository.retrieveByStartTime()
    fun workoutByTotalTime() = workoutRepository.retrieveByTime()
    fun workoutByTotalDistance() = workoutRepository.retrieveByKM()
    fun workoutByAverageSpeed() = workoutRepository.retrieveByAvgSpeed()
    fun workoutFilter(filterBy : String) = workoutRepository.retrieveByMode(filterBy)

}