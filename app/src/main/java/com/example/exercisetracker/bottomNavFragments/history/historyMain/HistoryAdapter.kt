package com.example.exercisetracker.bottomNavFragments.history.historyMain

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.exercisetracker.R
import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.databinding.ListItemHistoryBinding

class HistoryAdapter(private val historyListener: HistoryListener) :
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val TAG = "HistoryAdapter"
    private var workoutList = listOf<WorkoutData>()

    fun placeWorkoutData(newList: List<WorkoutData>) {
        val oldList = workoutList
        val diffResult = DiffUtil.calculateDiff(DiffCallbackHistoryAdapter(oldList, newList))
        workoutList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ListItemHistoryBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val workout = workoutList[position]
        holder.bind(workout)
    }

    override fun getItemCount() :Int {
        Log.i(TAG, "getItemCount: ${workoutList.size}")
        return workoutList.size
    }


    private class DiffCallbackHistoryAdapter(private val oldList: List<WorkoutData>, private val newList: List<WorkoutData>) : DiffUtil.Callback(){
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition].startTime == newList[newItemPosition].startTime

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) = oldList[oldItemPosition] == newList[newItemPosition]

    }


    inner class HistoryViewHolder(private val binding: ListItemHistoryBinding) :
        RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private val resources = binding.root.resources

        init {
            binding.historyRVContainer.setOnClickListener(this)
        }

        fun bind(workout: WorkoutData) {
            binding.historyRVDistance.text = resources.getString(
                R.string.workoutHistoryRV_distance,
                "${workout.totalKM} km"
            )
            binding.historyRVSpeed.text = resources.getString(R.string.workoutHistoryRV_speed, "${workout.averageSpeed} ")
            binding.historyRVTime.text = resources.getString(R.string.workoutHistoryRV_time, formatTime(workout.totalTime))

            Glide.with(binding.root.context)
                .asDrawable()
                .load(workoutType(workout.modeOfExercise))
                .into(binding.historyRVWorkout)
        }

        private fun formatTime(totalTime: Long): String {
            var timeMilli = totalTime
            val hours = (timeMilli / 3_600_000).toInt()
            timeMilli -= hours * 3_600_000
            val minutes = (timeMilli / 60_000).toInt()
            timeMilli -= minutes * 60_000
            val seconds = (timeMilli / 1_000).toInt()
            timeMilli -= seconds * 1_000

            val hourString = if (hours <= 9) "0$hours" else hours
            val minuteString = if (minutes <= 9) "0$minutes" else minutes
            val secondString = if (seconds <= 9) "0$seconds" else seconds
            return "$hourString : $minuteString : $secondString"
        }

        private fun workoutType(workout: String): Int {
            return when (workout) {
                "Bicycle" -> R.drawable.ic_bike_56
                "Walk" -> R.drawable.ic_walking_56
                else -> R.drawable.ic_jog_56
            }
        }
        override fun onClick(v: View?) {
            historyListener.onHistoryWorkoutClicked(adapterPosition)
        }
    }

    interface HistoryListener {
        fun onHistoryWorkoutClicked(index: Int)
    }

}