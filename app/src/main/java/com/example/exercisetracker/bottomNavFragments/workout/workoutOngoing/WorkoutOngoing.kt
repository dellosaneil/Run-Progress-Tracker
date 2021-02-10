package com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.LocationManager
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
import com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing.WorkoutOnGoingService.Companion.kilometers
import com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing.WorkoutOnGoingService.Companion.stopWatchFragmentTime
import com.example.exercisetracker.databinding.FragmentWorkoutOngoingBinding
import com.example.exercisetracker.repository.WorkoutRepository
import com.example.exercisetracker.utility.Constants.Companion.BUNDLE
import com.example.exercisetracker.utility.Constants.Companion.EXTRA_SAVE
import com.example.exercisetracker.utility.Constants.Companion.LOCATION_CODE
import com.example.exercisetracker.utility.Constants.Companion.PAUSE
import com.example.exercisetracker.utility.Constants.Companion.RESUME
import com.example.exercisetracker.utility.Constants.Companion.START
import com.example.exercisetracker.utility.Constants.Companion.STOP
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutOngoingBinding.inflate(inflater, container, false)
        handleNotificationContinue()
        requestLocationPermission()
        setOnClickListeners()
        displayProgressIndicator()
        return binding.root
    }

    private fun checkLocation() : Boolean{
        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        var locationStatus = false
        try{
            locationStatus = locationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true
        }catch (e: Exception){}
        return locationStatus
    }


    private fun displayProgressIndicator() {
        stopWatchFragmentTime.observe(viewLifecycleOwner) {
            binding.workoutOnGoingTimer.text = it
        }
        kilometers.observe(viewLifecycleOwner){
            binding.workoutOnGoingKilometer.text = it
        }
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
        currentState = null
        Navigation.findNavController(binding.root).navigate(R.id.workoutOnGoing_workout)
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

    private fun resumeOrPauseIntent(action: String) {
        val viewText =
            if (action == RESUME) getString(R.string.workout_pauseRun) else getString(R.string.workout_resumeRun)
        Intent(requireActivity(), WorkoutOnGoingService::class.java).also { service ->
            service.action = action
            requireActivity().startService(service)
            binding.workoutOnGoingStartOrPause.text = viewText
        }
    }

    private fun startWorkout() {
        if(checkLocation()){
            workoutOnGoingViewModel.startRun()
            workoutOnGoingViewModel.changeState()
            Intent(requireActivity(), WorkoutOnGoingService::class.java).also { service ->
                service.action = START
                service.putExtra(BUNDLE, args?.workoutgoal)
                requireActivity().startService(service)
            }
            binding.workoutOnGoingStop.visibility = View.VISIBLE
        }else{
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(getString(R.string.workoutOnGoing_locationOffTitle))
                .setMessage(R.string.workoutOnGoing_locationOffMessage)
                .setPositiveButton(R.string.workoutOnGoing_locationOffSettings){_,_->
                    Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS).also{
                        startActivity(it)
                    }
                }
                .setNegativeButton(R.string.cancel){dialog, _ ->
                    dialog.dismiss()
                }
        }
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
                resources.getString(R.string.rationale_ask),
                LOCATION_CODE,
                permissionLocation
            )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val backgroundLocation = Manifest.permission.ACCESS_BACKGROUND_LOCATION
            if (!EasyPermissions.hasPermissions(requireContext(), backgroundLocation)) {
                EasyPermissions.requestPermissions(
                    this,
                    resources.getString(R.string.rationale_ask),
                    LOCATION_CODE,
                    backgroundLocation
                )
            }
        }
    }
}