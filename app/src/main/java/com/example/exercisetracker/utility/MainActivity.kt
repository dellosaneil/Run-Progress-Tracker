package com.example.exercisetracker.utility

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.exercisetracker.R
import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.databinding.ActivityMainBinding
import com.example.exercisetracker.repository.WorkoutRepository
import com.example.exercisetracker.utility.Constants.Companion.ACTION_NOTIFICATION_SERVICE
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navigateToWorkoutOngoing(intent)
        setUpNavController()
    }

    private fun setUpNavController() {
        binding.bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.history, R.id.workout, R.id.statistics -> binding.bottomNavigationView.visibility =
                    View.VISIBLE
                else -> binding.bottomNavigationView.visibility = View.GONE
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        navigateToWorkoutOngoing(intent)
    }

    private fun navigateToWorkoutOngoing(intent: Intent?) {
        navController = findNavController(R.id.nav_host_fragment_container)
        if (intent?.action == ACTION_NOTIFICATION_SERVICE) {
            navController.navigate(R.id.action_global_workout_service)
        }
    }
}