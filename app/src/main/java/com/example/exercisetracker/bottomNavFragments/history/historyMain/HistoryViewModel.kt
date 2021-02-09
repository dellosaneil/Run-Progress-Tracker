package com.example.exercisetracker.bottomNavFragments.history.historyMain

import androidx.lifecycle.*
import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(private val workoutRepository: WorkoutRepository) :
    ViewModel() {

    private val mWorkoutList = MutableLiveData<List<WorkoutData>>()
    fun workoutList(): LiveData<List<WorkoutData>> = mWorkoutList

    init{
        viewModelScope.launch(IO){
            val temp = workoutByStartTime()
            withContext(Main) {
                mWorkoutList.value = temp
            }
        }
    }


    fun sortWorkoutList(sortBy: Int) {
        var temp : List<WorkoutData>? = null
        viewModelScope.launch(IO){
            when (sortBy) {
                0 -> temp = workoutByStartTime()
                1 -> temp = workoutByTotalTime()
                2 -> temp = workoutByTotalDistance()
                3 -> temp = workoutByAverageSpeed()
            }
            withContext(Main){
                mWorkoutList.value = temp
            }

        }
    }

    fun filterWorkoutList(filterBy: Int) {
        var temp : List<WorkoutData>? = null
        viewModelScope.launch(IO){
            when (filterBy) {
                0 -> temp = workoutFilter("Cycling")
                1 -> temp = workoutFilter("Walking")
                2 -> temp = workoutFilter("Jogging")
                3 -> temp = workoutByStartTime()
            }
            withContext(Main){
                mWorkoutList.value = temp
            }
        }
    }


    private fun workoutByStartTime() = workoutRepository.retrieveByStartTime()
    private fun workoutByTotalTime() = workoutRepository.retrieveByTime()
    private fun workoutByTotalDistance() = workoutRepository.retrieveByKM()
    private fun workoutByAverageSpeed() = workoutRepository.retrieveByAvgSpeed()
    private fun workoutFilter(filterBy: String) = workoutRepository.retrieveByMode(filterBy)

}