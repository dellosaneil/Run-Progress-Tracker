package com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.R
import com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing.WorkoutOnGoingService.Companion.currentState
import com.example.exercisetracker.databinding.FragmentWorkoutOngoingBinding
import com.example.exercisetracker.repository.WorkoutRepository
import com.example.exercisetracker.utility.Constants.Companion.EXTRA_SAVE
import com.example.exercisetracker.utility.Constants.Companion.LOCATION_CODE
import com.example.exercisetracker.utility.Constants.Companion.PAUSE
import com.example.exercisetracker.utility.Constants.Companion.RESUME
import com.example.exercisetracker.utility.Constants.Companion.START
import com.example.exercisetracker.utility.Constants.Companion.STOP
import com.example.exercisetracker.utility.Constants.Companion.WORKOUT_GOAL_BUNDLE
import com.example.exercisetracker.utility.FragmentLifecycleLog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class WorkoutOngoing : FragmentLifecycleLog(), View.OnClickListener {


    private var _binding: FragmentWorkoutOngoingBinding? = null
    private val binding get() = _binding!!
    private val workoutOnGoingViewModel: WorkoutOnGoingViewModel by viewModels()
    private val args: WorkoutOngoingArgs? by navArgs()

    @Inject
    lateinit var workoutRepository: WorkoutRepository

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
                    binding.workoutOnGoingStartOrPause.text =
                        resources.getString(R.string.workout_pauseRun)
                }
                PAUSE -> {
                    binding.workoutOnGoingStartOrPause.text =
                        resources.getString(R.string.workout_resumeRun)
                }
                else -> {
                    binding.workoutOnGoingStartOrPause.text =
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
        binding.workoutOnGoingStartOrPause.setOnClickListener(this)
        binding.workoutOnGoingStop.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.workoutOnGoingStartOrPause.setOnClickListener(null)
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
            R.id.workoutOnGoing_startOrPause -> {
                if (workoutOnGoingViewModel.firstStart().value == false) {
                    startWorkout()
                    binding.workoutOnGoingStartOrPause.text =
                        resources.getString(R.string.workout_pauseRun)
                } else {
                    resumeOrPauseWorkout()
                }
            }
            R.id.workoutOnGoing_stop -> stopRun()
        }
    }

    private fun alertDialogSaveWorkout() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.workoutDialog_title))
            .setMessage(getString(R.string.workoutDialog_content))
            .setPositiveButton(getString(R.string.save)) { _, _ ->
                stopServiceIntent(true)
            }
            .setNegativeButton(getString(R.string.discard)) { _, _ ->
                stopServiceIntent(false)
            }
            .setNeutralButton(getString(R.string.cancel), null)
            .setCancelable(false)
            .show()
    }

    private fun stopServiceIntent(extra: Boolean) {
        Intent(requireActivity(), WorkoutOnGoingService::class.java).also { service ->
            service.action = STOP
            service.putExtra(EXTRA_SAVE, extra)
            requireActivity().startService(service)
        }
        workoutOnGoingViewModel.runStopped()
        binding.workoutOnGoingStartOrPause.text =
            resources.getString(R.string.workout_startWorkout)
        binding.workoutOnGoingStop.visibility = View.GONE
    }

    private fun stopRun() {
        alertDialogSaveWorkout()
    }

    private fun resumeOrPauseWorkout() {
        workoutOnGoingViewModel.changeState()
        val isRunning = workoutOnGoingViewModel.isRunning().value
        if (isRunning == true) {
            resumeOrPauseIntent(RESUME)
        } else {
            resumeOrPauseIntent(PAUSE)
        }
    }

    private fun resumeOrPauseIntent(action : String){
        val viewText = if(action == RESUME) getString(R.string.workout_resumeRun) else getString(R.string.workout_pauseRun)
        Intent(requireActivity(), WorkoutOnGoingService::class.java).also { service ->
            service.action = action
            requireActivity().startService(service)
            binding.workoutOnGoingStartOrPause.text = viewText
        }
    }

    private fun startWorkout() {
        workoutOnGoingViewModel.startRun()
        workoutOnGoingViewModel.changeState()
        workoutOnGoingViewModel.startStopWatch()
        workoutOnGoingViewModel.stopWatchTimer().observe(viewLifecycleOwner) {
            binding.workoutOnGoingTimer.text = it
        }
        Intent(requireActivity(), WorkoutOnGoingService::class.java).also { service ->
            service.action = START
            service.putExtra(WORKOUT_GOAL_BUNDLE, args?.workoutgoal)
            requireActivity().startService(service)
        }
        binding.workoutOnGoingStop.visibility = View.VISIBLE
    }

}