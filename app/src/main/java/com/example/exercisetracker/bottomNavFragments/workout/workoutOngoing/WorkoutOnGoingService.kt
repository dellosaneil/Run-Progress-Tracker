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
import androidx.lifecycle.Observer
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

    companion object {
        var serviceRunning = false
        var currentState: String? = null
        var workoutGoal: WorkoutGoalData? = null
        private val mStopWatchRunningTime = MutableLiveData(0L)
        val stopWatchRunningTime: LiveData<Long> = mStopWatchRunningTime

        private val mStopWatchFragmentTime = MutableLiveData("00:00:00:00")
        val stopWatchFragmentTime: LiveData<String> = mStopWatchFragmentTime

        private val mStopWatchServiceTime = MutableLiveData("00:00:00")
        val stopWatchServiceTime: LiveData<String> = mStopWatchServiceTime

    }

    @Inject
    lateinit var workoutRepository: WorkoutRepository

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private val routeTaken = mutableListOf<LatLng>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback

    private var notificationManager: NotificationManager? = null
    private var notification: NotificationCompat.Builder? = null

    private var stopWatchTimeObserver: Observer<String>? = null
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    private fun runTimer(previousTime: Long) {
        stopWatchTimeObserver?.let { stopWatchServiceTime.observeForever(it) }
        val timeStarted = System.currentTimeMillis()
        serviceScope.launch {
            while (currentState != PAUSE && currentState != STOP && currentState != null) {
                withContext(Main) {
                    mStopWatchRunningTime.value =
                        System.currentTimeMillis() - timeStarted + previousTime
                    mStopWatchFragmentTime.value =
                        convertMilliSecondsToText(mStopWatchRunningTime.value!!, true)
                    mStopWatchServiceTime.value =
                        convertMilliSecondsToText(mStopWatchRunningTime.value!!, false)
                }
                delay(25)
            }
        }
    }

    private suspend fun convertMilliSecondsToText(time: Long, toFragment: Boolean): String {
        val timeInString = serviceScope.async {
            var timeMilli = time
            val hours = (timeMilli / 3_600_000).toInt()
            timeMilli -= hours * 3_600_000
            val minutes = (timeMilli / 60_000).toInt()
            timeMilli -= minutes * 60_000
            val seconds = (timeMilli / 1_000).toInt()
            timeMilli -= seconds * 1_000

            return@async if (toFragment) {
                val hourString = if (hours <= 9) "0$hours" else hours
                val minuteString = if (minutes <= 9) "0$minutes" else minutes
                val secondString = if (seconds <= 9) "0$seconds" else seconds
                val milliString = if (timeMilli <= 99) "0$timeMilli" else timeMilli
                "$hourString : $minuteString : $secondString : $milliString"
            } else {
                val hourString = if (hours <= 9) "0$hours" else hours
                val minuteString = if (minutes <= 9) "0$minutes" else minutes
                val secondString = if (seconds <= 9) "0$seconds" else seconds
                "$hourString : $minuteString : $secondString"
            }
        }
        return timeInString.await()
    }


    private fun saveToDatabase() {
        serviceScope.launch {
            workoutGoal?.let {
                val timeFinished = System.currentTimeMillis()
                val distance = computeDistance()

                val temp = stopWatchRunningTime.value?.let { workoutLength ->
                    val avgSpeed = computeAverageSpeed(distance, workoutLength)
                    WorkoutData(
                        it.modeOfExercise, it.startTime, timeFinished, routeTaken, distance,
                        workoutLength, avgSpeed
                    )
                }
                if (temp != null) {
                    workoutRepository.insertWorkout(temp)
                }
                stopForeground(true)
                stopSelf()
            }
        }
    }

    private fun computeAverageSpeed(distance: Float, workoutLength: Long): Double {
        return (distance / workoutLength.toFloat() / 3600).toDouble()
    }

    private fun computeDistance(): Float {
        val results = FloatArray(1)
        var distance = 0.0f
        for ((index, _) in routeTaken.withIndex()) {
            if (index == routeTaken.size - 1) {
                break
            }
            Location.distanceBetween(
                routeTaken[index].latitude,
                routeTaken[index].longitude,
                routeTaken[index + 1].latitude,
                routeTaken[index + 1].longitude,
                results
            )
            distance += results[0]
        }
        distance /= 1000.0f
        return distance
    }

    private fun createNotification() {
        notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager!!)
        }
        notification = NotificationCompat.Builder(this, ONGOING_NOTIFICATION_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentTitle(getString(R.string.notification_title))
            .setSmallIcon(R.drawable.ic_run_24)
            .setContentText(getString(R.string.notification_content))
            .setContentIntent(createPendingIntent())
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

    private val TAG = "WorkoutOnGoingService"


    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }


    private fun buildLocationRequest() {
        locationRequest = LocationRequest().apply {
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

    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate: ")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initializeStopwatchObserver()
        buildLocationRequest()
        buildLocationCallback()
    }

    private fun initializeStopwatchObserver() {
        stopWatchTimeObserver = Observer {
            if (notification == null) {
                createNotification()
            }
            Log.i(TAG, "initializeStopwatchObserver: $serviceRunning")
            if(serviceRunning){
                notification?.setContentText(it)
                notificationManager?.notify(NOTIFICATION_ID, notification?.build())
            }

        }

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.i(TAG, "onStartCommand: ${intent?.action}")
        when (intent?.action) {
            START -> startRunningForeground(intent)
            RESUME -> resumeRunningForeground()
            PAUSE -> pauseRunningForeground()
            STOP -> stopRunningForeground(intent)
        }
        return START_STICKY
    }


    private fun startRunningForeground(intent: Intent) {
        currentState = START
        stopWatchRunningTime.value?.let { runTimer(it) }
        workoutGoal = intent.getParcelableExtra(WORKOUT_GOAL_BUNDLE)
        serviceRunning = true
        createNotification()
        updateLocation()
        startForeground(NOTIFICATION_ID, notification?.build())
    }

    private fun resumeRunningForeground() {
        currentState = RESUME
        stopWatchRunningTime.value?.let { runTimer(it) }
        updateLocation()
    }


    private fun pauseRunningForeground() {
        currentState = PAUSE
        stopWatchTimeObserver?.let { stopWatchFragmentTime.removeObserver(it) }
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun stopRunningForeground(intent: Intent) {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        if (intent.getBooleanExtra(EXTRA_SAVE, false)) {
            saveToDatabase()
        } else {
            stopForeground(true)
            stopSelf()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        serviceRunning = false

        mStopWatchRunningTime.value = 0L
        mStopWatchServiceTime.value = "00:00:00"
        mStopWatchFragmentTime.value = "00:00:00:00"

        workoutGoal = null
        currentState = null
        fusedLocationClient.removeLocationUpdates(locationCallback)
        stopWatchTimeObserver?.let { stopWatchServiceTime.removeObserver(it) }
        stopWatchTimeObserver?.let { stopWatchFragmentTime.removeObserver(it) }

        stopWatchTimeObserver = null

        notification = null
        notificationManager = null

        serviceJob.cancel()
    }


}