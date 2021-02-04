package com.example.exercisetracker.bottomNavFragments.workout

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.exercisetracker.R
import com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing.WorkoutOnGoingService.Companion.serviceRunning
import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.databinding.FragmentStatisticsBinding
import com.example.exercisetracker.databinding.FragmentWorkoutBinding
import com.example.exercisetracker.repository.WorkoutRepository
import com.example.exercisetracker.utility.FragmentLifecycleLog
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class Workout : FragmentLifecycleLog() {
    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var workoutRepository: WorkoutRepository
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.workoutButton.setOnClickListener {
            if(serviceRunning){
                Navigation.findNavController(it).navigate(R.id.workout_workoutOnGoing)
            }else{
                Navigation.findNavController(it)
                    .navigate(R.id.workout_workoutGoal)
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}