package com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.exercisetracker.utility.Constants.Companion.CHANNEL_ID
import com.example.exercisetracker.utility.Constants.Companion.NOTIFICATION_CONTENT
import com.example.exercisetracker.utility.Constants.Companion.NOTIFICATION_ID
import com.example.exercisetracker.utility.Constants.Companion.NOTIFICATION_TITLE
import com.example.exercisetracker.utility.Constants.Companion.ONGOING_NOTIFICATION_ID
import com.google.android.gms.location.*
import com.example.exercisetracker.R


class WorkoutOnGoingService : Service() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback


    private val TAG = "WorkoutOnGoingService"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateLocation()
        startRunningForeground()
        return START_STICKY
    }

    private fun startRunningForeground() {
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }
        val notification = NotificationCompat.Builder(this, ONGOING_NOTIFICATION_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentTitle(getString(R.string.notification_title))
            .setSmallIcon(R.drawable.ic_run_24)
            .setContentText(getString(R.string.notification_content))
            .setContentIntent(createPendingIntent())
            .build()

        startForeground(NOTIFICATION_ID, notification)

    }

    private fun createPendingIntent(): PendingIntent {
        val notificationIntent = Intent(this, WorkoutOngoing::class.java)
        return PendingIntent.getActivity(this, 0, notificationIntent, 0)
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channel = NotificationChannel(ONGOING_NOTIFICATION_ID, CHANNEL_ID, IMPORTANCE_LOW)
        notificationManager.createNotificationChannel(channel)
    }


    override fun onCreate() {
        super.onCreate()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        buildLocationRequest()
        buildLocationCallback()
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 500
            fastestInterval = 250
            smallestDisplacement = 1f
        }
    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    Log.i(TAG, "onLocationResult: ${location.latitude}")
                    Log.i(TAG, "onLocationResult: ${location.longitude}")
                }
            }
        }
    }


}