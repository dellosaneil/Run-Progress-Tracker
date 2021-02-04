package com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.R
import com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing.WorkoutOnGoingService.Companion.currentState
import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.data.WorkoutGoalData
import com.example.exercisetracker.databinding.FragmentWorkoutOngoingBinding
import com.example.exercisetracker.utility.Constants.Companion.LOCATION_CODE
import com.example.exercisetracker.utility.Constants.Companion.PAUSE
import com.example.exercisetracker.utility.Constants.Companion.RESUME
import com.example.exercisetracker.utility.Constants.Companion.START
import com.example.exercisetracker.utility.Constants.Companion.STOP
import com.example.exercisetracker.utility.Constants.Companion.WORKOUT_GOAL_BUNDLE
import com.example.exercisetracker.utility.FragmentLifecycleLog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class WorkoutOngoing : FragmentLifecycleLog(), View.OnClickListener {


    private var _binding: FragmentWorkoutOngoingBinding? = null
    private val binding get() = _binding!!
    private val workoutOnGoingViewModel: WorkoutOnGoingViewModel by viewModels()
    private val args: WorkoutOngoingArgs? by navArgs()

    private val TAG = "WorkoutOngoing"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutOngoingBinding.inflate(inflater, container, false)
        handleNotificationContinue()
        requestLocationPermission()
        setOnClickListeners()
        return binding.root
    }

    private fun handleNotificationContinue() {
        currentState?.let {
            workoutOnGoingViewModel.startRun()
            binding.workoutOnGoingStop.visibility = View.VISIBLE
            when (it) {
                START -> {
                    workoutOnGoingViewModel.changeState()
                    binding.workoutOngoingStartOrPause.text =
                        resources.getString(R.string.workout_pauseRun)
                }
                PAUSE -> {
                    binding.workoutOngoingStartOrPause.text =
                        resources.getString(R.string.workout_resumeRun)
                }
                else -> {
                    binding.workoutOngoingStartOrPause.text =
                        resources.getString(R.string.workout_pauseRun)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                isEnabled = false
                Navigation.findNavController(binding.root).navigate(R.id.workoutOnGoing_workout)

            }
        })
    }

    private fun setOnClickListeners() {
        binding.workoutOngoingStartOrPause.setOnClickListener(this)
        binding.workoutOnGoingStop.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.workoutOngoingStartOrPause.setOnClickListener(null)
        binding.workoutOnGoingStop.setOnClickListener(null)
        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(LOCATION_CODE)
    private fun requestLocationPermission() {
        val permissionLocation = Manifest.permission.ACCESS_FINE_LOCATION
        if (!EasyPermissions.hasPermissions(requireContext(), permissionLocation)) {
            EasyPermissions.requestPermissions(
                this,
                resources.getString(R.string.permission_rationale),
                LOCATION_CODE,
                permissionLocation
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val backgroundLocation = Manifest.permission.ACCESS_BACKGROUND_LOCATION
            if (!EasyPermissions.hasPermissions(requireContext(), backgroundLocation)) {
                EasyPermissions.requestPermissions(
                    this,
                    resources.getString(R.string.permission_rationale),
                    LOCATION_CODE,
                    backgroundLocation
                )
            }
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.workoutOngoing_startOrPause -> {
                if (workoutOnGoingViewModel.firstStart().value == false) {
                    startWorkout()
                    binding.workoutOngoingStartOrPause.text =
                        resources.getString(R.string.workout_pauseRun)
                } else {
                    resumeOrPauseWorkout()
                }
            }
            R.id.workoutOnGoing_stop -> stopRun()
        }
    }

    private fun stopRun() {
        Intent(requireActivity(), WorkoutOnGoingService::class.java).also { service ->
            service.action = STOP
            requireActivity().startService(service)
        }
        workoutOnGoingViewModel.runStopped()
        binding.workoutOngoingStartOrPause.text = resources.getString(R.string.workout_startWorkout)
        binding.workoutOnGoingStop.visibility = View.GONE


    }

    private fun resumeOrPauseWorkout() {
        workoutOnGoingViewModel.changeState()
        val isRunning = workoutOnGoingViewModel.isRunning().value
        if (isRunning == true) {
            Intent(requireActivity(), WorkoutOnGoingService::class.java).also { service ->
                service.action = RESUME
                requireActivity().startService(service)
                binding.workoutOngoingStartOrPause.text =
                    resources.getString(R.string.workout_pauseRun)
            }
        } else {
            Intent(requireActivity(), WorkoutOnGoingService::class.java).also { service ->
                service.action = PAUSE
                requireActivity().startService(service)
                binding.workoutOngoingStartOrPause.text =
                    resources.getString(R.string.workout_resumeRun)
            }
        }
    }

    private fun startWorkout() {
        workoutOnGoingViewModel.startRun()
        workoutOnGoingViewModel.changeState()
        Intent(requireActivity(), WorkoutOnGoingService::class.java).also { service ->
            service.action = START
            service.putExtra(WORKOUT_GOAL_BUNDLE, args?.workoutgoal)
            requireActivity().startService(service)
        }
        binding.workoutOnGoingStop.visibility = View.VISIBLE
    }

}