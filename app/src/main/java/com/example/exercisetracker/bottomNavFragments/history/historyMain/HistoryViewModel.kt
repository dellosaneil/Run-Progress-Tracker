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
    private var startDate = 0L
    private var endDate = 0L



    init {
        viewModelScope.launch(IO) {
            sortNumber = 0
            val temp = workoutRepository.retrieveByStartTime()
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

    fun filterDateRangePicked(firstRange : Long, secondRange: Long){
        startDate = firstRange
        endDate = secondRange
        var tempWorkoutList : List<WorkoutData>? = null
        viewModelScope.launch(IO){
            if(filterNumber != -1 && filterNumber != 3){
                when(sortNumber){
                    0 -> tempWorkoutList = workoutRepository.retrieveRangeDateWithModeWithStartTime(workoutArrayFilters[filterNumber], firstRange, secondRange)
                    1 -> tempWorkoutList = workoutRepository.retrieveRangeDateWithModeWithTotalTime(workoutArrayFilters[filterNumber], firstRange, secondRange)
                    2 -> tempWorkoutList = workoutRepository.retrieveRangeDateWithModeWithTotalKM(workoutArrayFilters[filterNumber], firstRange, secondRange)
                    3 -> tempWorkoutList = workoutRepository.retrieveRangeDateWithModeWithAvgSpeed(workoutArrayFilters[filterNumber], firstRange, secondRange)
                }
            }else{
                when(sortNumber){
                    0 -> tempWorkoutList = workoutRepository.retrieveRangeDateStartTime(firstRange, secondRange)
                    1 -> tempWorkoutList = workoutRepository.retrieveRangeDateTotalTime(firstRange, secondRange)
                    2 -> tempWorkoutList = workoutRepository.retrieveRangeDateTotalKM(firstRange, secondRange)
                    3 -> tempWorkoutList = workoutRepository.retrieveRangeDateAvgSpeed(firstRange, secondRange)
                }
            }
            withContext(Main){
                mWorkoutList.value = tempWorkoutList
            }
        }
    }


    fun sortWorkoutList(sortBy: Int) {
        sortNumber = sortBy
        var tempWorkoutList: List<WorkoutData>? = null
        viewModelScope.launch(IO) {
            if(startDate != 0L){
                filterDateRangePicked(startDate, endDate)
            }else{
                if (filterNumber == -1 || filterNumber == 3) {
                    when (sortBy) {
                        0 -> tempWorkoutList =  workoutRepository.retrieveByStartTime()
                        1 -> tempWorkoutList =  workoutRepository.retrieveByTotalTime()
                        2 -> tempWorkoutList =  workoutRepository.retrieveByTotalKM()
                        3 -> tempWorkoutList =  workoutRepository.retrieveByAvgSpeed()
                    }
                } else {
                    when (sortBy) {
                        0 -> tempWorkoutList = workoutRepository.retrieveModeByStartTime(workoutArrayFilters[filterNumber])
                        1 -> tempWorkoutList = workoutRepository.retrieveModeByTotalTime(workoutArrayFilters[filterNumber])
                        2 -> tempWorkoutList = workoutRepository.retrieveModeByTotalKM(workoutArrayFilters[filterNumber])
                        3 -> tempWorkoutList = workoutRepository.retrieveModeByAvgSpeed(workoutArrayFilters[filterNumber])
                    }
                }
                withContext(Main) {
                    mWorkoutList.value = tempWorkoutList
                }
            }
        }
    }

    fun filterWorkoutList(filterBy: Int) {
        filterNumber = filterBy
        var tempWorkoutList: List<WorkoutData>? = null
        viewModelScope.launch(IO) {
            when {
                startDate != 0L -> {
                    filterDateRangePicked(startDate, endDate)
                }
                filterNumber == 3 -> {
                    sortWorkoutList(sortNumber)
                }
                else -> {
                    when (filterBy) {
                        0 -> tempWorkoutList = workoutRepository.retrieveModeByStartTime(workoutArrayFilters[filterBy])
                        1 -> tempWorkoutList = workoutRepository.retrieveModeByTotalTime(workoutArrayFilters[filterBy])
                        2 -> tempWorkoutList = workoutRepository.retrieveModeByTotalKM(workoutArrayFilters[filterBy])
                        3 -> tempWorkoutList = workoutRepository.retrieveModeByAvgSpeed(workoutArrayFilters[filterBy])
                    }
                    withContext(Main) {
                        mWorkoutList.value = tempWorkoutList
                    }
                }
            }
        }
    }

    fun deleteWorkout(workoutData: WorkoutData) = viewModelScope.launch(IO) {
        workoutRepository.deleteFromDatabase(workoutData)
        sortWorkoutList(sortNumber)
    }


}