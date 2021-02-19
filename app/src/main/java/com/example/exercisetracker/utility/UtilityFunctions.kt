package com.example.exercisetracker.utility

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import java.text.SimpleDateFormat
import java.util.*

object UtilityFunctions {

    fun dateFormatter(milliseconds: Long): String {
        val simpleDateFormat = SimpleDateFormat("dd/MM/yy", Locale.ROOT)
        return simpleDateFormat.format(milliseconds)
    }

    /*converts milliseconds to human readable time*/
    suspend fun convertMilliSecondsToText(time: Long, toFragment: Boolean, scope : CoroutineScope): String {
        val timeInString = scope.async {
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

}