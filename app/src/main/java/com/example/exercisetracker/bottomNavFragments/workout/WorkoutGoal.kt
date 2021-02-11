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
import com.example.exercisetracker.utility.Constants.Companion.DISTANCE
import com.example.exercisetracker.utility.Constants.Companion.NONE
import com.example.exercisetracker.utility.Constants.Companion.TIME

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

    /*place values in the dropDownMenu*/
    private fun setUpDropDownMenu() {
        val arrayGoals = arrayOf(resources.getStringArray(R.array.mode_of_exercise), resources.getStringArray(R.array.goal_type))
        val arrayViews = arrayOf(binding.workoutGoalTypeOfExercise, binding.workoutGoalGoalType)
        repeat(2){
            val adapter = ArrayAdapter(requireContext(), R.layout.drop_down, arrayGoals[it])
            (arrayViews[it].editText as? AutoCompleteTextView)?.setAdapter(adapter)
        }
    }

    /*Changes the Property of Goal according to the GoalType selected*/
    private fun setGoalListener() {
        binding.workoutGoalGoalType.editText?.doOnTextChanged { text, _, _, _ ->
            binding.workoutGoalGoal.editText?.text?.clear()
            when (text.toString()) {
                DISTANCE -> {
                    binding.workoutGoalGoal.visibility = View.VISIBLE
                    binding.workoutGoalGoal.suffixText =
                        resources.getString(R.string.workoutGoal_km)
                }
                TIME -> {
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

    /*Checks all of the EditText have values*/
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
            if (binding.workoutGoalGoalType.editText?.text.toString() != NONE) {
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


    /*When Button is clicked first check the values if it is complete then send it in a Bundle by SafeArgs to
     WorkoutOnGoing Fragment*/
    private fun redirectToOnGoingWorkout(view : View){
        binding.workoutGoalSetGoal.setOnClickListener {
            if (checkValues()) {
                val workoutGoal: WorkoutGoalData
                val typeOfExercise = binding.workoutGoalTypeOfExercise.editText?.text?.toString()
                workoutGoal = if (binding.workoutGoalGoalType.editText?.text.toString() == NONE) {
                    WorkoutGoalData(modeOfExercise = typeOfExercise!!)
                } else {
                    val goal = binding.workoutGoalGoal.editText?.text.toString().toDouble()
                    if (binding.workoutGoalGoalType.editText?.text?.toString() == TIME) {
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






















