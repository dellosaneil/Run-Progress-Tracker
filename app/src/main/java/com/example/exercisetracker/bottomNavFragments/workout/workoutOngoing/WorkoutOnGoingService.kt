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
import com.example.exercisetracker.utility.Constants.Companion.BACKGROUND
import com.example.exercisetracker.utility.Constants.Companion.CHANNEL_ID
import com.example.exercisetracker.utility.Constants.Companion.EXTRA_SAVE
import com.example.exercisetracker.utility.Constants.Companion.NOTIFICATION_ID
import com.example.exercisetracker.utility.Constants.Companion.ONGOING_NOTIFICATION_ID
import com.example.exercisetracker.utility.Constants.Companion.PAUSE
import com.example.exercisetracker.utility.Constants.Companion.RESUME
import com.example.exercisetracker.utility.Constants.Companion.START
import com.example.exercisetracker.utility.Constants.Companion.STOP
import com.example.exercisetracker.utility.Constants.Companion.BUNDLE
import com.example.exercisetracker.utility.Constants.Companion.IN_BACKGROUND
import com.example.exercisetracker.utility.MainActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import javax.inject.Inject

@AndroidEntryPoint
class WorkoutOnGoingService : Service() {


    /*static objects to update fragments outside of class*/
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

        private val mKilometers = MutableLiveData("0.00 km")
        val kilometers: LiveData<String> = mKilometers

        private val mGoalProgress = MutableLiveData(0)
        val goalProgress: LiveData<Int> = mGoalProgress
    }


    @Inject
    lateinit var workoutRepository: WorkoutRepository

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private val routeTaken = mutableListOf<LatLng>()

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var locationRequest: LocationRequest? = null
    private var locationCallback: LocationCallback? = null

    private var notificationManager: NotificationManager? = null
    private var notification: NotificationCompat.Builder? = null
    private var stopWatchTimeObserver: Observer<String>? = null

    private var isInBackground = false


    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    /*update notification every second for the Stopwatch*/
    private fun initializeStopwatchObserver() {
        stopWatchTimeObserver = Observer {
            if (notification == null) {
                createNotification()
            }
            if (serviceRunning && currentState != null) {
                notification?.setContentText(it)
                notificationManager?.notify(NOTIFICATION_ID, notification?.build())
            }
        }
    }

    /* calculate KM & timer*/
    private fun updateDistanceAndTime(previousTime: Long) {
        stopWatchTimeObserver?.let { stopWatchServiceTime.observeForever(it) }
        val timeStarted = System.currentTimeMillis()
        serviceScope.launch {
            while (currentState != PAUSE && currentState != STOP && currentState != null) {
                val updatedTime = System.currentTimeMillis() - timeStarted + previousTime
                withContext(Main) {
                    mStopWatchRunningTime.value = updatedTime
                    mStopWatchFragmentTime.value =
                        convertMilliSecondsToText(mStopWatchRunningTime.value!!, true)
                    mStopWatchServiceTime.value =
                        convertMilliSecondsToText(mStopWatchRunningTime.value!!, false)
                }
                if(isInBackground){
                    delay(1000)
                }else{
                    delay(25)
                }
            }
        }

        serviceScope.launch {
            while (currentState != PAUSE && currentState != STOP && currentState != null) {
                val distance = computeDistance()
                withContext(Main) {
                    mKilometers.value = "${String.format("%.2f", distance)} km"
                    mStopWatchRunningTime.value?.let { progressGoalIndicator(it, distance) }
                }
                delay(5000)
            }
        }
    }




    private fun progressGoalIndicator(updatedTime: Long, distance: Float) {
        if (workoutGoal?.kmGoal != null) {
            mGoalProgress.value = (distance / workoutGoal?.kmGoal!! * 100).toInt()
        } else if (workoutGoal?.minutesGoal != null) {
            mGoalProgress.value = (updatedTime / workoutGoal?.minutesGoal!! * 100).toInt()
        }
    }

    /*converts milliseconds to human readable time*/
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
                val tempWorkoutData = stopWatchRunningTime.value?.let { workoutLength ->
                    val avgSpeed = computeAverageSpeed(distance, workoutLength)
                    WorkoutData(
                        it.modeOfExercise, it.startTime, timeFinished, routeTaken, distance,
                        workoutLength, avgSpeed
                    )
                }
                if (tempWorkoutData != null) {
                    workoutRepository.insertWorkout(tempWorkoutData)
                }
                stopForeground(true)
                stopSelf()
            }
        }
    }


    /*Computes KM/H*/
    private fun computeAverageSpeed(distance: Float, workoutLength: Long): Double {
        return (distance / (workoutLength.toFloat() / 3_600_000.00))
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

        val iconDrawable = notificationDrawable()

        notification = NotificationCompat.Builder(this, ONGOING_NOTIFICATION_ID)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentTitle(getString(R.string.notification_title))
            .setSmallIcon(iconDrawable)
            .setContentText(getString(R.string.notification_defaultTime))
            .setContentIntent(createPendingIntent())
    }

    /*Changes the notification icon depending on the mode of exercise*/
    private fun notificationDrawable(): Int {
        val arrayExercise = resources.getStringArray(R.array.mode_of_exercise)
        workoutGoal?.modeOfExercise.let {
            return when (it) {
                arrayExercise[0] -> R.drawable.ic_bicycle_24
                arrayExercise[1] -> R.drawable.ic_walk_24
                else -> R.drawable.ic_run_24
            }
        }
    }

    /*returns Pending intent to redirect when Notification is clicked*/
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

    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    private fun buildLocationRequest() {
        locationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 60000
            fastestInterval = 60000
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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        initializeStopwatchObserver()
        buildLocationRequest()
        buildLocationCallback()
    }

    private val TAG = "WorkoutOnGoingService"
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            START -> startRunningForeground(intent)
            RESUME -> resumeRunningForeground()
            PAUSE -> pauseRunningForeground()
            STOP -> stopRunningForeground(intent)
            IN_BACKGROUND -> serviceInBackground(intent)
        }
        return START_STICKY
    }

    private fun serviceInBackground(intent: Intent?) {
        isInBackground = intent?.getBooleanExtra(BACKGROUND, false) == true
    }


    private fun startRunningForeground(intent: Intent) {
        currentState = START
        stopWatchRunningTime.value?.let { updateDistanceAndTime(it) }
        workoutGoal = intent.getParcelableExtra(BUNDLE)
        serviceRunning = true
        createNotification()
        updateLocation()
        startForeground(NOTIFICATION_ID, notification?.build())
    }



    private fun resumeRunningForeground() {
        currentState = RESUME
        stopWatchRunningTime.value?.let { updateDistanceAndTime(it) }
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
        mStopWatchServiceTime.value = getString(R.string.notification_defaultTime)
        mStopWatchFragmentTime.value = getString(R.string.workoutOnGoing_defaultTime)
        mKilometers.value = getString(R.string.workoutOnGoing_defaultKilometer)
        mGoalProgress.value = 0

        workoutGoal = null
        currentState = null
        fusedLocationClient.removeLocationUpdates(locationCallback)
        locationCallback = null
        locationRequest = null


        stopWatchTimeObserver?.let { stopWatchServiceTime.removeObserver(it) }
        stopWatchTimeObserver?.let { stopWatchFragmentTime.removeObserver(it) }

        stopWatchTimeObserver = null

        notification = null
        notificationManager = null

        serviceJob.cancel()
    }


}