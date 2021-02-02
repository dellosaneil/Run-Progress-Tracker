package com.example.exercisetracker.bottomNavFragments.workout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentHistoryBinding
import com.example.exercisetracker.databinding.FragmentWorkoutGoalBinding
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

class WorkoutGoal : Fragment() {

    private var _binding: FragmentWorkoutGoalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutGoalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.workoutGoalSetGoal.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.workoutGoal_workoutOngoing)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }






}






















