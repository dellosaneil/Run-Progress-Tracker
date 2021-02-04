package com.example.exercisetracker.bottomNavFragments.workout

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.navigation.Navigation
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentWorkoutGoalBinding

class WorkoutGoal : Fragment() {

    private var _binding: FragmentWorkoutGoalBinding? = null
    private val binding get() = _binding!!


    private val TAG = "WorkoutGoal"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutGoalBinding.inflate(inflater, container, false)
        setUpDropDownMenu()
        testFunction()
        return binding.root
    }

    private fun setUpDropDownMenu() {
        val typeOfExercise = resources.getStringArray(R.array.mode_of_exercise)
        val adapter = ArrayAdapter(requireContext(), R.layout.drop_down, typeOfExercise)
        (binding.workoutGoalTypeOfExercise.editText as? AutoCompleteTextView)?.setAdapter(adapter)

        val setGoal = resources.getStringArray(R.array.goal_type)
        val goalAdapter = ArrayAdapter(requireContext(), R.layout.drop_down, setGoal)
        (binding.workoutGoalGoalType.editText as? AutoCompleteTextView)?.setAdapter(goalAdapter)
    }

    private fun testFunction(){
        binding.workoutGoalGoalType.editText?.doOnTextChanged { text, _, _, _ ->
            binding.workoutGoalGoal.editText?.text?.clear()
            when(text.toString()){
                "Distance" -> {
                    binding.workoutGoalGoal.visibility = View.VISIBLE
                    binding.workoutGoalGoal.suffixText = resources.getString(R.string.workoutGoal_km)
                }
                "Time" -> {
                    binding.workoutGoalGoal.visibility = View.VISIBLE
                    binding.workoutGoalGoal.suffixText = resources.getString(R.string.workoutGoal_minutes)
                }
                else -> {
                    binding.workoutGoalGoal.visibility = View.GONE
                }

            }
        }


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






















