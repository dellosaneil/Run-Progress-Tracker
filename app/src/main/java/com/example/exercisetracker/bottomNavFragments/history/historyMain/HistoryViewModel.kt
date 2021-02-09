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

    private var sortNumber = -1
    private var filterNumber = -1
    private val workoutArrayFilters = arrayOf("Cycling", "Walking", "Jogging")

    init {
        viewModelScope.launch(IO) {
            val temp = workoutByStartTime()
            withContext(Main) {
                mWorkoutList.value = temp
            }
        }
    }

    fun sortWorkoutList(sortBy: Int) {
        sortNumber = sortBy
        var temp: List<WorkoutData>? = null
        viewModelScope.launch(IO) {
            if (filterNumber == -1 || filterNumber == 3) {
                when (sortBy) {
                    0 -> temp = workoutByStartTime()
                    1 -> temp = workoutByTotalTime()
                    2 -> temp = workoutByTotalDistance()
                    3 -> temp = workoutByAverageSpeed()
                }
            } else {
                when (sortBy) {
                    0 -> temp = workoutFilterByStartTime(workoutArrayFilters[filterNumber])
                    1 -> temp = workoutFilterByTotalTime(workoutArrayFilters[filterNumber])
                    2 -> temp = workoutFilterByKM(workoutArrayFilters[filterNumber])
                    3 -> temp = workoutFilterByAvgSpeed(workoutArrayFilters[filterNumber])
                }
            }
            withContext(Main) {
                mWorkoutList.value = temp
            }
        }
    }

    fun filterWorkoutList(filterBy: Int) {
        filterNumber = filterBy
        var temp: List<WorkoutData>? = null
        viewModelScope.launch(IO) {
            if (sortNumber == -1) {
                when (filterBy) {
                    0, 1, 2 -> temp = workoutFilter(workoutArrayFilters[filterBy])
                    3 -> temp = workoutByStartTime()
                }
                withContext(Main) {
                    mWorkoutList.value = temp
                }
            } else {
                if (filterNumber == 3) {
                    sortWorkoutList(sortNumber)
                } else {
                    when (filterNumber) {
                        0 -> temp = workoutFilterByStartTime(workoutArrayFilters[filterBy])
                        1 -> temp = workoutFilterByTotalTime(workoutArrayFilters[filterBy])
                        2 -> temp = workoutFilterByKM(workoutArrayFilters[filterBy])
                        3 -> temp = workoutFilterByAvgSpeed(workoutArrayFilters[filterBy])
                    }
                    withContext(Main) {
                        mWorkoutList.value = temp
                    }
                }
            }

        }
    }

    private fun workoutFilterByKM(filterBy: String) = workoutRepository.retrieveModeByKM(filterBy)
    private fun workoutFilterByTotalTime(filterBy: String) =
        workoutRepository.retrieveModeByTime(filterBy)

    private fun workoutFilterByStartTime(filterBy: String) =
        workoutRepository.retrieveModeByStartTime(filterBy)

    private fun workoutFilterByAvgSpeed(filterBy: String) =
        workoutRepository.retrieveModeByAvgSpeed(filterBy)


    private fun workoutByStartTime() = workoutRepository.retrieveByStartTime()
    private fun workoutByTotalTime() = workoutRepository.retrieveByTime()
    private fun workoutByTotalDistance() = workoutRepository.retrieveByKM()
    private fun workoutByAverageSpeed() = workoutRepository.retrieveByAvgSpeed()
    private fun workoutFilter(filterBy: String) = workoutRepository.retrieveByMode(filterBy)

}