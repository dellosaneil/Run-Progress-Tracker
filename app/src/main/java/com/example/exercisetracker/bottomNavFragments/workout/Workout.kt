package com.example.exercisetracker.bottomNavFragments.workout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentStatisticsBinding
import com.example.exercisetracker.databinding.FragmentWorkoutBinding
import com.example.exercisetracker.utility.FragmentLifecycleLog

class Workout : FragmentLifecycleLog() {
    private var _binding: FragmentWorkoutBinding? = null
    private val binding get() = _binding!!


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
            Navigation.findNavController(view)
                .navigate(R.id.workout_workoutGoal)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}