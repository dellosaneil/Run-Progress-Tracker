package com.example.exercisetracker.bottomNavFragments.history.historyMain

import androidx.lifecycle.*
import androidx.room.Query
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
            sortNumber = 0
            val temp = workoutByStartTime()
            withContext(Main) {
                mWorkoutList.value = temp
            }
        }
    }

    fun insertNewWorkout(workoutData : WorkoutData){
        viewModelScope.launch(IO){
            workoutRepository.insertWorkout(workoutData)
            sortWorkoutList(sortNumber)
        }
    }


    fun sortWorkoutList(sortBy: Int) {
        sortNumber = sortBy
        var tempWorkoutList: List<WorkoutData>? = null
        viewModelScope.launch(IO) {
            if (filterNumber == -1 || filterNumber == 3) {
                when (sortBy) {
                    0 -> tempWorkoutList = workoutByStartTime()
                    1 -> tempWorkoutList = workoutByTotalTime()
                    2 -> tempWorkoutList = workoutByTotalDistance()
                    3 -> tempWorkoutList = workoutByAverageSpeed()
                }
            } else {
                when (sortBy) {
                    0 -> tempWorkoutList = workoutFilterByStartTime(workoutArrayFilters[filterNumber])
                    1 -> tempWorkoutList = workoutFilterByTotalTime(workoutArrayFilters[filterNumber])
                    2 -> tempWorkoutList = workoutFilterByKM(workoutArrayFilters[filterNumber])
                    3 -> tempWorkoutList = workoutFilterByAvgSpeed(workoutArrayFilters[filterNumber])
                }
            }
            withContext(Main) {
                mWorkoutList.value = tempWorkoutList
            }
        }
    }

    fun filterWorkoutList(filterBy: Int) {
        filterNumber = filterBy
        var tempWorkoutList: List<WorkoutData>? = null
        viewModelScope.launch(IO) {
            if (filterNumber == 3) {
                sortWorkoutList(sortNumber)
            } else {
                when (filterNumber) {
                    0 -> tempWorkoutList = workoutFilterByStartTime(workoutArrayFilters[filterBy])
                    1 -> tempWorkoutList = workoutFilterByTotalTime(workoutArrayFilters[filterBy])
                    2 -> tempWorkoutList = workoutFilterByKM(workoutArrayFilters[filterBy])
                    3 -> tempWorkoutList = workoutFilterByAvgSpeed(workoutArrayFilters[filterBy])
                }
                withContext(Main) {
                    mWorkoutList.value = tempWorkoutList
                }
            }
        }
    }

    fun deleteWorkout(workoutData: WorkoutData) = viewModelScope.launch(IO) {
        workoutRepository.deleteFromDatabase(workoutData)
        sortWorkoutList(sortNumber)
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

}