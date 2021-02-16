package com.example.exercisetracker.bottomNavFragments.statistics

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentStatisticsBinding
import com.example.exercisetracker.utility.FragmentLifecycleLog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class Statistics : FragmentLifecycleLog(), View.OnClickListener {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val statisticsViewModel: StatisticsViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateGridLayout(view)
        setOnClickListeners()
        setRecords(view)
        toolBarInitialize()
    }


    private fun toolBarInitialize(){
        statisticsViewModel.totalWorkout().observe(viewLifecycleOwner){
            binding.fragmentStatisticsToolBar.title = "Statistics (${it} workouts)"
        }
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
            R.id.fragmentStatistics_bar -> Log.i(TAG, "onClick: BAR")
            R.id.fragmentStatistics_line -> Log.i(TAG, "onClick: LINE")
            R.id.fragmentStatistics_pie -> Log.i(TAG, "onClick: PIE")
            R.id.fragmentStatistics_combined -> Log.i(TAG, "onClick: COMBINED")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}