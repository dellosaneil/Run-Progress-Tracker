package com.example.exercisetracker.bottomNavFragments.workout.workoutOngoing

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_LOW
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.exercisetracker.R
import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.data.WorkoutGoalData
import com.example.exercisetracker.repository.WorkoutRepository
import com.example.exercisetracker.utility.Constants.Companion.ACTION_NOTIFICATION_SERVICE
import com.example.exercisetracker.utility.Constants.Companion.CHANNEL_ID
import com.example.exercisetracker.utility.Constants.Companion.EXTRA_SAVE
import com.example.exercisetracker.utility.Constants.Companion.NOTIFICATION_ID
import com.example.exercisetracker.utility.Constants.Companion.ONGOING_NOTIFICATION_ID
import com.example.exercisetracker.utility.Constants.Companion.PAUSE
import com.example.exercisetracker.utility.Constants.Companion.RESUME
import com.example.exercisetracker.utility.Constants.Companion.START
import com.example.exercisetracker.utility.Constants.Companion.STOP
import com.example.exercisetracker.utility.Constants.Companion.WORKOUT_GOAL_BUNDLE
import com.example.exercisetracker.utility.MainActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

@AndroidEntryPoint
class WorkoutOnGoingService : Service() {
    private val TAG = "WorkoutOnGoingService"

    companion object {
        var serviceRunning = false
        var currentState: String? = null
        var workoutGoal: WorkoutGoalData? = null
        private val mStopWatchTime = MutableLiveData(0L)
        val stopWatchTime : LiveData<Long> = mStopWatchTime
    }

    @Inject
    lateinit var workoutRepository: WorkoutRepository

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private val routeTaken = mutableListOf<LatLng>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startTimer(){
        val timeStarted = System.currentTimeMillis()
        serviceScope.launch {
            while(true){
                if(currentState == PAUSE || currentState == STOP){
                    break
                }
                withContext(Main) {
                    mStopWatchTime.value = System.currentTimeMillis() - timeStarted
                }
                delay(1000)
            }
        }
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START -> startRunningForeground(intent)
            RESUME -> resumeRunningForeground()
            PAUSE -> pauseRunningForeground()
            STOP -> stopRunningForeground(intent)
        }
        return START_STICKY
    }

    private fun stopRunningForeground(intent: Intent) {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        currentState = null
        serviceRunning = false
        if(intent.getBooleanExtra(EXTRA_SAVE, false)){
            saveToDatabase()
        }else{
            stopSelf()
        }
    }

    private fun saveToDatabase() {
        serviceScope.launch {
            workoutGoal?.let{
                val distance = computeDistance()
                val temp = WorkoutData(it.modeOfExercise, it.startTime, System.currentTimeMillis(), routeTaken, distance, 23, 23.1)
                workoutRepository.insertWorkout(temp)
                stopSelf()
            }
        }
    }

    private fun computeDistance(): Float {
        val results = FloatArray(1)
        var distance = 0.0f
        for((index,_) in routeTaken.withIndex()){
            if(index == routeTaken.size - 1){
                break
            }
            Location.distanceBetween(routeTaken[index].latitude,routeTaken[index].longitude, routeTaken[index + 1].latitude, routeTaken[index + 1].longitude, results)
            distance += results[0]
        }
        distance /= 1000.0f
        return distance
    }


    private fun pauseRunningForeground() {
        currentState = PAUSE
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }


    private fun resumeRunningForeground() {
        currentState = RESUME
        updateLocation()
    }

    private fun startRunningForeground(intent: Intent) {
        startTimer()
        workoutGoal = intent.getParcelableExtra(WORKOUT_GOAL_BUNDLE)
        currentState = START
        serviceRunning = true
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
        updateLocation()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createPendingIntent(): PendingIntent {
        val notificationIntent = Intent(this, MainActivity::class.java).also {
            it.action = ACTION_NOTIFICATION_SERVICE
        }
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
        serviceJob.cancel()
        serviceScope.cancel()
        Log.i(TAG, "onDestroy: ")
    }


    private fun buildLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 500
            fastestInterval = 250
        }
    }

    private fun buildLocationCallback() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    val latLng = LatLng(location.latitude, location.longitude)
                    routeTaken.add(latLng)
                }
            }
        }
    }
}