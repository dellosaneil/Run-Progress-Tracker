package com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.exercisetracker.databinding.FragmentWorkoutOngoingBinding
import com.example.exercisetracker.utility.Constants.Companion.LOCATION_CODE
import com.google.android.gms.location.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions


class WorkoutOngoing : Fragment() {

    private val TAG = "WorkoutOngoing"

    private var _binding: FragmentWorkoutOngoingBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWorkoutOngoingBinding.inflate(inflater, container, false)
        requestLocationPermission()
        


        return binding.root
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
}