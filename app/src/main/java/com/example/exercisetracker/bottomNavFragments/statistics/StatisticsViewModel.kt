package com.example.exercisetracker.bottomNavFragments.statistics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.exercisetracker.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(private val repository : WorkoutRepository) : ViewModel(){


    private val _totalDistance = MutableLiveData(0.0)
    private val _totalTime = MutableLiveData(0L)
    private val _averageDistance = MutableLiveData(0.0)
    private val _averageTime = MutableLiveData(0.0)
    private val _averageSpeed = MutableLiveData(0.0)

    fun totalDistance() : LiveData<Double> = _totalDistance
    fun totalTime() : LiveData<Long> = _totalTime
    fun averageDistance() : LiveData<Double> = _averageDistance
    fun averageTime() : LiveData<Double> = _averageTime
    fun averageSpeed() : LiveData<Double> = _averageSpeed

    init{
        viewModelScope.launch(IO) {
            val tDistance = repository.sumTotalKMAll()
            val tTime = repository.sumTotalTimeAll()
            val aDistance = repository.avgKMAll()
            val aTime = repository.avgTotalTimeAll()
            val aSpeed = repository.avgSpeedAll()
            withContext(Main){
                _totalDistance.value = tDistance
                _totalTime.value = tTime
                _averageDistance.value = aDistance
                _averageTime.value = aTime
                _averageSpeed.value = aSpeed
            }
        }
    }
}