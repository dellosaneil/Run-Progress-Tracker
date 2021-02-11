package com.example.exercisetracker.bottomNavFragments.workout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.exercisetracker.R
import com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing.WorkoutOnGoingService.Companion.currentState
import com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing.WorkoutOnGoingService.Companion.serviceRunning
import com.example.exercisetracker.databinding.FragmentWorkoutBinding
import com.example.exercisetracker.repository.WorkoutRepository
import com.example.exercisetracker.utility.FragmentLifecycleLog
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class Workout : FragmentLifecycleLog(), View.OnClickListener {
    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var workoutRepository: WorkoutRepository
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutBinding.inflate(inflater, container, false)
        if(currentState != null){
            binding.workoutButton.text = getString(R.string.workout_continueWorkout)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.workoutButton.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.workout_button -> redirectToNextFragment()
        }
    }

    /*Redirect to the appropriate fragment depending if service is already running.*/
    private fun redirectToNextFragment(){
        if (serviceRunning) {
            Navigation.findNavController(binding.root).navigate(R.id.workout_workoutOnGoing)
        } else {
            Navigation.findNavController(binding.root)
                .navigate(R.id.workout_workoutGoal)
        }
    }
}