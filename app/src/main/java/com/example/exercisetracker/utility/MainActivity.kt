package com.example.exercisetracker.utility

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.ActivityMainBinding
import com.example.exercisetracker.utility.Constants.Companion.CHANNEL_ID


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = findNavController(R.id.nav_host_fragment_container)
        setUpNavController()
    }

    private fun setUpNavController() {
        binding.bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener{_, destination,_ ->
            run {
                when(destination.id){
                    R.id.history, R.id.workout, R.id.statistics -> binding.bottomNavigationView.visibility = View.VISIBLE
                    else -> binding.bottomNavigationView.visibility = View.GONE
                }
            }
        }
    }


}