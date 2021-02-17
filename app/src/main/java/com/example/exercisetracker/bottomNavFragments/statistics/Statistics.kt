package com.example.exercisetracker.bottomNavFragments.statistics

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentStatisticsBinding
import com.example.exercisetracker.utility.FragmentLifecycleLog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

@AndroidEntryPoint
class Statistics : FragmentLifecycleLog(), View.OnClickListener,
    androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val statisticsViewModel: StatisticsViewModel by viewModels()
    private var first: Long? = null
    private var second: Long? = null
    private var filterItemChecked = 3
    private lateinit var singleItems: Array<String>


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        singleItems = resources.getStringArray(R.array.historyMenu_filter)
        populateGridLayout(view)
        setOnClickListeners()
        setRecords(view)
        toolBarInitialize()
    }


    private fun toolBarInitialize() {
        statisticsViewModel.totalWorkout().observe(viewLifecycleOwner) {
            binding.fragmentStatisticsToolBar.title = "Statistics (${it} workouts)"
        }
        binding.fragmentStatisticsToolBar.setOnMenuItemClickListener(this)
    }

    private fun setRecords(view: View) {
        val labelArray = resources.getStringArray(R.array.records)
        val liveDataFunctions = arrayOf(
            statisticsViewModel.totalDistance(),
            statisticsViewModel.totalTime(),
            statisticsViewModel.averageDistance(),
            statisticsViewModel.averageTime(),
            statisticsViewModel.averageSpeed()
        )
        val viewImageArray = arrayOf(
            binding.fragmentStatisticsTotalDistance.fragmentStatisticsWorkoutImage,
            binding.fragmentStatisticsTotalTime.fragmentStatisticsWorkoutImage,
            binding.fragmentStatisticsAverageDistance.fragmentStatisticsWorkoutImage,
            binding.fragmentStatisticsAverageTime.fragmentStatisticsWorkoutImage,
            binding.fragmentStatisticsAverageSpeed.fragmentStatisticsWorkoutImage
        )
        val viewNameArray = arrayOf(
            binding.fragmentStatisticsTotalDistance.fragmentStatisticsWorkoutName,
            binding.fragmentStatisticsTotalTime.fragmentStatisticsWorkoutName,
            binding.fragmentStatisticsAverageDistance.fragmentStatisticsWorkoutName,
            binding.fragmentStatisticsAverageTime.fragmentStatisticsWorkoutName,
            binding.fragmentStatisticsAverageSpeed.fragmentStatisticsWorkoutName
        )
        val viewValueArray = arrayOf(
            binding.fragmentStatisticsTotalDistance.fragmentStatisticsWorkoutValue,
            binding.fragmentStatisticsTotalTime.fragmentStatisticsWorkoutValue,
            binding.fragmentStatisticsAverageDistance.fragmentStatisticsWorkoutValue,
            binding.fragmentStatisticsAverageTime.fragmentStatisticsWorkoutValue,
            binding.fragmentStatisticsAverageSpeed.fragmentStatisticsWorkoutValue
        )
        val drawableArray = arrayOf(
            R.drawable.ic_road_24,
            R.drawable.ic_time_24,
            R.drawable.ic_road_24,
            R.drawable.ic_time_24,
            R.drawable.ic_speed_24
        )

        repeat(5) { index ->
            Glide.with(view)
                .load(drawableArray[index])
                .into(viewImageArray[index])
            viewNameArray[index].text = labelArray[index]
            liveDataFunctions[index].observe(viewLifecycleOwner) {
                viewValueArray[index].text = it
            }
        }
    }

    private fun setOnClickListeners() {
        binding.fragmentStatisticsBar.fragmentStatisticsGridCardView.setOnClickListener(this)
        binding.fragmentStatisticsLine.fragmentStatisticsGridCardView.setOnClickListener(this)
        binding.fragmentStatisticsPie.fragmentStatisticsGridCardView.setOnClickListener(this)
        binding.fragmentStatisticsCombined.fragmentStatisticsGridCardView.setOnClickListener(this)
    }

    @SuppressLint("NewApi")
    private fun populateGridLayout(view: View) {
        val cardViewArray = arrayOf(
            binding.fragmentStatisticsBar.fragmentStatisticsGridCardView,
            binding.fragmentStatisticsLine.fragmentStatisticsGridCardView,
            binding.fragmentStatisticsPie.fragmentStatisticsGridCardView,
            binding.fragmentStatisticsCombined.fragmentStatisticsGridCardView
        )
        val imageViewArray = arrayOf(
            binding.fragmentStatisticsBar.fragmentStatisticsGridChart,
            binding.fragmentStatisticsLine.fragmentStatisticsGridChart,
            binding.fragmentStatisticsPie.fragmentStatisticsGridChart,
            binding.fragmentStatisticsCombined.fragmentStatisticsGridChart
        )
        val textViewArray = arrayOf(
            binding.fragmentStatisticsBar.fragmentStatisticsGridName,
            binding.fragmentStatisticsLine.fragmentStatisticsGridName,
            binding.fragmentStatisticsPie.fragmentStatisticsGridName,
            binding.fragmentStatisticsCombined.fragmentStatisticsGridName
        )
        val colorArray = arrayOf(
            R.color.chart_bar_light,
            R.color.chart_line_light,
            R.color.chart_pie_light,
            R.color.chart_combined_light
        )
        val textArray = resources.getStringArray(R.array.charts)
        val imageArray = arrayOf(
            R.drawable.ic_bar_chart_68,
            R.drawable.ic_line_chart_68,
            R.drawable.ic_pie_chart_68,
            R.drawable.ic_combined_chart_68
        )
        repeat(4) {
            Glide.with(view)
                .load(imageArray[it])
                .into(imageViewArray[it])
            cardViewArray[it].setCardBackgroundColor(requireContext().getColor(colorArray[it]))
            textViewArray[it].text = textArray[it]
        }
    }

    private val TAG = "Statistics"
    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fragmentStatistics_bar -> {
                first?.let {
                    redirectBarChart(it, second!!)
                } ?: redirectBarChart()
            }
            R.id.fragmentStatistics_line -> {
                first?.let {
                    redirectLineChart(it, second!!)
                } ?: redirectLineChart()
            }
            R.id.fragmentStatistics_pie -> Log.i(TAG, "onClick: PIE")
            R.id.fragmentStatistics_combined -> Log.i(TAG, "onClick: COMBINED")
        }
    }

    private fun redirectBarChart(
        firstRange: Long = 0,
        secondRange: Long = System.currentTimeMillis()
    ) {
        val exercise = resources.getStringArray(R.array.mode_of_exercise)
        lifecycleScope.launch(IO) {
            val barChartData = statisticsViewModel.barChartData(firstRange, secondRange, exercise)
            withContext(Main) {
                val action = StatisticsDirections.statisticsStatisticsBarChart(barChartData)
                Navigation.findNavController(binding.root).navigate(action)
            }
        }
    }

    private fun redirectLineChart(
        firstRange: Long = 0,
        secondRange: Long = System.currentTimeMillis()
    ) {
        lifecycleScope.launch(IO) {
            val lineChartData = statisticsViewModel.lineChartData(firstRange, secondRange)
            withContext(Main) {
                val action = StatisticsDirections.statisticsStatisticsLineChart(lineChartData)
                Navigation.findNavController(binding.root).navigate(action)
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.fragmentStatisticsMenu_dateRange -> {
                chooseDateRange()
                true
            }
            R.id.fragmentStatisticsMenu_modeOfExercise -> {
                chooseModeOfExercise()
                true
            }

            else -> false
        }
    }

    private fun chooseModeOfExercise() {
        var checkedItem = filterItemChecked
        MaterialAlertDialogBuilder(binding.root.context)
            .setTitle(resources.getString(R.string.workoutHistory_menuFilterTitle))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.historyMenu_filter)) { dialog, _ ->
                filterItemChecked = checkedItem
                modeOfExerciseFilter(singleItems)
                dialog.dismiss()
            }
            .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                checkedItem = which
            }
            .setCancelable(false)
            .show()
    }

    private fun modeOfExerciseFilter(singleItems: Array<String>) {
        first?.let {
            if (filterItemChecked != 3) {
                statisticsViewModel.workoutRecordWithFilter(
                    it,
                    second!!,
                    singleItems[filterItemChecked]
                )
            } else {
                statisticsViewModel.dateRangeRecord(it, second!!)
            }
        } ?: if (filterItemChecked == 3) {
            statisticsViewModel.allWorkoutRecord()
        } else {
            statisticsViewModel.workoutRecordWithFilter(mode = singleItems[filterItemChecked])
        }
    }

    private fun chooseDateRange() {
        val materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().build()
        materialDatePicker.show(
            parentFragmentManager,
            getString(R.string.workoutHistory_menuDateRange)
        )
        materialDatePicker.addOnPositiveButtonClickListener {
            first = it.first?.minus(28_800_000)
            second = it.second?.plus(86_400_000)?.minus(28_800_000)
            if (filterItemChecked == 3) {
                statisticsViewModel.dateRangeRecord(first!!, second!!)
            } else {
                statisticsViewModel.workoutRecordWithFilter(
                    first!!,
                    second!!,
                    singleItems[filterItemChecked]
                )
            }
        }
        materialDatePicker.addOnNegativeButtonClickListener {
            if (filterItemChecked == 3) {
                statisticsViewModel.allWorkoutRecord()
            } else {
                statisticsViewModel.workoutRecordWithFilter(mode = singleItems[filterItemChecked])
            }
            first = null
            second = null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}