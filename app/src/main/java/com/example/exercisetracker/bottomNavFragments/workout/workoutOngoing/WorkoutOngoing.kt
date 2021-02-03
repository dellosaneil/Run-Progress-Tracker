package com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentWorkoutOngoingBinding
import com.example.exercisetracker.utility.Constants.Companion.LOCATION_CODE
import com.example.exercisetracker.utility.Constants.Companion.PAUSE
import com.example.exercisetracker.utility.Constants.Companion.RESUME
import com.example.exercisetracker.utility.Constants.Companion.START
import com.example.exercisetracker.utility.Constants.Companion.STOP
import com.example.exercisetracker.utility.FragmentLifecycleLog
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class WorkoutOngoing : FragmentLifecycleLog(), View.OnClickListener {


    private var _binding: FragmentWorkoutOngoingBinding? = null
    private val binding get() = _binding!!
    private val workoutOnGoingViewModel: WorkoutOnGoingViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutOngoingBinding.inflate(inflater, container, false)
        requestLocationPermission()
        setOnClickListeners()

        return binding.root
    }

    private fun setOnClickListeners() {
        binding.workoutOngoingStartOrPause.setOnClickListener(this)
        binding.workoutOnGoingStop.setOnClickListener(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
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
            EasyPermissions.requestPermissions(this, "TEST", LOCATION_CODE, permissionLocation)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val backgroundLocation = Manifest.permission.ACCESS_BACKGROUND_LOCATION
            if (!EasyPermissions.hasPermissions(requireContext(), backgroundLocation)) {
                EasyPermissions.requestPermissions(this, "TEST", LOCATION_CODE, backgroundLocation)
            }
        }
    }

    private fun resumeOrPauseWorkout() {
        workoutOnGoingViewModel.changeState()
        workoutOnGoingViewModel.isRunning().observe(viewLifecycleOwner) {
            if (it) {
                Intent(requireActivity(), WorkoutOnGoingService::class.java).also { service ->
                    service.action = RESUME
                    requireActivity().startService(service)
                }
            } else {
                Intent(requireActivity(), WorkoutOnGoingService::class.java).also { service ->
                    service.action = PAUSE
                    requireActivity().startService(service)
                }
            }
        }
    }

    private fun startWorkout() {
        workoutOnGoingViewModel.startRun()
        workoutOnGoingViewModel.changeState()
        Intent(requireActivity(), WorkoutOnGoingService::class.java).also { service ->
            service.action = START
            requireActivity().startService(service)
        }
        binding.workoutOnGoingStop.visibility = View.VISIBLE
    }


    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.workoutOngoing_startOrPause -> {
                if (workoutOnGoingViewModel.firstStart().value == false) {
                    startWorkout()
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
            requireActivity().stopService(service)
        }
    }
}