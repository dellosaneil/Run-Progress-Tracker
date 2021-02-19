package com.example.exercisetracker.bottomNavFragments.statistics.charts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentStatisticsBarChartBinding
import com.example.exercisetracker.utility.UtilityFunctions
import com.example.exercisetracker.utility.UtilityFunctions.dateFormatter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*


class StatisticsBarChart : Fragment(), RadioGroup.OnCheckedChangeListener {

    private val args: StatisticsBarChartArgs? by navArgs()
    private var _binding: FragmentStatisticsBarChartBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBarChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpMaterialToolbar()
        populateBarChart()
        binding.statisticsBarChartRadioGroup.setOnCheckedChangeListener(this)
    }

    private fun setUpMaterialToolbar() {
        binding.statisticsBarChartToolBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }

        if (args?.barChartData?.startDate != 0L) {
            binding.statisticsBarChartToolBar.title =
                "Bar Chart (${dateFormatter(args?.barChartData?.startDate!!)} - ${dateFormatter(args?.barChartData?.endDate!!)})"
        }
    }




    private fun populateBarChart(label: String = "Kilometers") {
        val xAxisLabel = arrayOf("Cycling", "Walking", "Jogging")
        val entries = mutableListOf<BarEntry>()
        args?.barChartData.let {
            if (label == "Kilometers") {
                entries.add(BarEntry(0f, it!!.cyclingKilometers))
                entries.add(BarEntry(1f, it.walkingKilometers))
                entries.add(BarEntry(2f, it.joggingKilometers))
            } else {
                entries.add(BarEntry(0f, it!!.cyclingTime / 60_000))
                entries.add(BarEntry(1f, it.walkingTime / 60_000))
                entries.add(BarEntry(2f, it.joggingTime / 60_000))
            }
        }
        val dataSet = BarDataSet(entries, label)
        dataSet.apply {
            valueTextSize = 16f
        }
        val barData = BarData(dataSet)
        binding.statisticsBarChartChart.apply {
            setFitBars(true)
            data = barData
            xAxis.isGranularityEnabled = true
            setDrawValueAboveBar(true)
            xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabel)
            xAxis.position = XAxis.XAxisPosition.BOTTOM_INSIDE
            xAxis.granularity = 1f
            description.isEnabled = false
            animateY(500)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if (group?.id == R.id.statisticsBarChart_radioGroup) {
            when (checkedId) {
                R.id.statisticsBarChart_time -> populateBarChart("Minutes")
                R.id.statisticsBarChart_distance -> populateBarChart("Kilometers")
            }
        }
    }
}
















