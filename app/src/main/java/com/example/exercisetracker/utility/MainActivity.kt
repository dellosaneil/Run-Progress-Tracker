package com.example.exercisetracker.utility

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.exercisetracker.R
import com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing.WorkoutOnGoingService.Companion.serviceRunning
import com.example.exercisetracker.databinding.ActivityMainBinding
import com.example.exercisetracker.utility.Constants.Companion.ACTION_BLOCK
import com.example.exercisetracker.utility.Constants.Companion.ACTION_NOTIFICATION_SERVICE
import dagger.hilt.android.AndroidEntryPoint

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

    /*connect bottom nav graph with Navigation Component
      listens where destination is then hide bottom nav bar accordingly*/
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

    private val TAG = "MainActivity"

    /*redirect to OnGoingWorkoutFragment when workout is OnGoing*/
    private fun navigateToWorkoutOngoing(intent: Intent?) {
        navController = findNavController(R.id.nav_host_fragment_container)
        if (intent?.action == ACTION_NOTIFICATION_SERVICE && serviceRunning) {
            navController.navigate(R.id.action_global_workout_service)
            intent.action = ACTION_BLOCK
        }
    }

}