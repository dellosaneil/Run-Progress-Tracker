package com.example.exercisetracker.bottomNavFragments.workout

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.core.widget.doOnTextChanged
import androidx.navigation.Navigation
import com.example.exercisetracker.R
import com.example.exercisetracker.data.WorkoutGoalData
import com.example.exercisetracker.databinding.FragmentWorkoutGoalBinding

class WorkoutGoal : Fragment() {

    private var _binding: FragmentWorkoutGoalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutGoalBinding.inflate(inflater, container, false)
        setUpDropDownMenu()
        setGoalListener()
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

    private fun setGoalListener() {
        binding.workoutGoalGoalType.editText?.doOnTextChanged { text, _, _, _ ->
            binding.workoutGoalGoal.editText?.text?.clear()
            when (text.toString()) {
                "Distance" -> {
                    binding.workoutGoalGoal.visibility = View.VISIBLE
                    binding.workoutGoalGoal.suffixText =
                        resources.getString(R.string.workoutGoal_km)
                }
                "Time" -> {
                    binding.workoutGoalGoal.visibility = View.VISIBLE
                    binding.workoutGoalGoal.suffixText =
                        resources.getString(R.string.workoutGoal_minutes)
                }
                else -> {
                    binding.workoutGoalGoal.visibility = View.GONE
                }

            }
        }
    }

    private fun checkValues(): Boolean {
        var completeFields = true
        if (binding.workoutGoalTypeOfExercise.editText?.text?.isEmpty()!!) {
            completeFields = false
            binding.workoutGoalTypeOfExercise.error =
                resources.getString(R.string.workoutGoal_requiredField)
        } else {
            binding.workoutGoalTypeOfExercise.error = null
        }
        if (binding.workoutGoalGoalType.editText?.text?.isEmpty()!!) {
            completeFields = false
            binding.workoutGoalGoalType.error =
                resources.getString(R.string.workoutGoal_requiredField)
        } else {
            binding.workoutGoalGoalType.error = null
            if (binding.workoutGoalGoalType.editText?.text.toString() != "None") {
                if (binding.workoutGoalGoal.editText?.text?.isEmpty()!!) {
                    completeFields = false
                    binding.workoutGoalGoal.error =
                        resources.getString(R.string.workoutGoal_requiredField)
                } else {
                    binding.workoutGoalGoal.error = null
                }
            }
        }
        return completeFields
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        redirectToOnGoingWorkout(view)
    }

    private fun redirectToOnGoingWorkout(view : View){
        binding.workoutGoalSetGoal.setOnClickListener {
            if (checkValues()) {
                val workoutGoal: WorkoutGoalData
                val typeOfExercise = binding.workoutGoalTypeOfExercise.editText?.text?.toString()
                workoutGoal = if (binding.workoutGoalGoalType.editText?.text.toString() == "None") {
                    WorkoutGoalData(modeOfExercise = typeOfExercise!!)
                } else {
                    val goal = binding.workoutGoalGoal.editText?.text.toString().toDouble()
                    if (binding.workoutGoalGoalType.editText?.text?.toString() == "Time") {
                        WorkoutGoalData(modeOfExercise = typeOfExercise!!, minutesGoal = goal)
                    } else {
                        WorkoutGoalData(modeOfExercise = typeOfExercise!!, kmGoal = goal)
                    }
                }
                val action = WorkoutGoalDirections.workoutGoalWorkoutOngoing(workoutGoal)
                Navigation.findNavController(view).navigate(action)
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}






















