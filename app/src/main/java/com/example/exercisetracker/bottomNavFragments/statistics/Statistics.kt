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

@AndroidEntryPoint
class Statistics : FragmentLifecycleLog(), View.OnClickListener,
    androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val statisticsViewModel: StatisticsViewModel by viewModels()
    private var first = 0L
    private var second = 0L
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
        initializeObservables()
    }

    /*Updates Global Variable, Save when recreated change*/
    private fun initializeObservables() {
        statisticsViewModel.firstMilliRange().observe(viewLifecycleOwner) {
            first = it
        }
        statisticsViewModel.secondMilliRange().observe(viewLifecycleOwner) {
            second = it
        }
        statisticsViewModel.filterItemChecked().observe(viewLifecycleOwner){
            filterItemChecked = it
        }
    }


    private fun toolBarInitialize() {
        statisticsViewModel.totalWorkout().observe(viewLifecycleOwner) {
            binding.fragmentStatisticsToolBar.title = "Statistics (${it} workouts)"
        }
        binding.fragmentStatisticsToolBar.setOnMenuItemClickListener(this)
    }

    /*Set the Material CardView Values*/
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

    /*Activates Checkbox Listeners*/
    private fun setOnClickListeners() {
        binding.fragmentStatisticsBar.fragmentStatisticsGridCardView.setOnClickListener(this)
        binding.fragmentStatisticsLine.fragmentStatisticsGridCardView.setOnClickListener(this)
        binding.fragmentStatisticsPie.fragmentStatisticsGridCardView.setOnClickListener(this)
        binding.fragmentStatisticsCombined.fragmentStatisticsGridCardView.setOnClickListener(this)
    }


    /*Place Images, Labels, Color in Grid Layout*/
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
                if (second != 0L) {
                    redirectBarChart(first, second)
                } else {
                    redirectBarChart()
                }
            }
            R.id.fragmentStatistics_line -> {
                if (second != 0L) {
                    redirectLineChart(first,  second)
                } else {
                    redirectLineChart()
                }
            }
            R.id.fragmentStatistics_pie ->{
                if(second != 0L){
                    redirectPieChart(first, second)
                }else{
                    redirectPieChart()
                }

            }
            R.id.fragmentStatistics_combined -> Log.i(TAG, "onClick: COMBINED")
        }
    }

    private fun redirectPieChart(first: Long = 0, second: Long = System.currentTimeMillis()) {
        lifecycleScope.launch(IO){
            val pieChartData = statisticsViewModel.pieChartData(first, second, singleItems)
            withContext(Main){
                val action = StatisticsDirections.statisticsStatisticsPieChart(pieChartData)
                Navigation.findNavController(binding.root).navigate(action)
            }

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
            val lineChartData = statisticsViewModel.lineChartData(
                firstRange,
                secondRange,
                filterItemChecked,
                singleItems
            )
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
                alertDialogModeOfExercise()
                true
            }
            else -> false
        }
    }

    /*Creates AlertDialog for Filter*/
    private fun alertDialogModeOfExercise() {
        var checkedItem = filterItemChecked
        MaterialAlertDialogBuilder(binding.root.context)
            .setTitle(resources.getString(R.string.workoutHistory_menuFilterTitle))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.historyMenu_filter)) { dialog, _ ->
                statisticsViewModel.setItemChecked(checkedItem)
                modeOfExerciseFilter()
                dialog.dismiss()
            }
            .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                checkedItem = which
            }
            .setCancelable(false)
            .show()
    }

    /*Filters Workout Data specified by Exercise*/
    private fun modeOfExerciseFilter() {
        if (second != 0L) {
            if (filterItemChecked != 3) {
                statisticsViewModel.workoutRecordWithFilter(
                    first,
                    second,
                    singleItems[filterItemChecked]
                )
            } else {
                statisticsViewModel.dateRangeRecord(first, second)
            }
        } else {
            if (filterItemChecked == 3) {
                statisticsViewModel.allWorkoutRecord()
            } else {
                statisticsViewModel.workoutRecordWithFilter(mode = singleItems[filterItemChecked])
            }
        }
    }
    /*Creates DatePicker to filter data*/
    private fun chooseDateRange() {
        val materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().build()
        materialDatePicker.show(
            parentFragmentManager,
            getString(R.string.workoutHistory_menuDateRange)
        )
        materialDatePicker.addOnPositiveButtonClickListener {
            statisticsViewModel.setMilliRange(
                it.first!!.minus(28_800_000L),
                it.second!!.plus(86_400_000L).minus(28_800_000L)
            )
            if (filterItemChecked == 3) {
                statisticsViewModel.dateRangeRecord(first, second)
            } else {
                statisticsViewModel.workoutRecordWithFilter(
                    first,
                    second,
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
            statisticsViewModel.setMilliRange(0L, 0L)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}