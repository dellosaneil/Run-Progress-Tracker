package com.example.exercisetracker.bottomNavFragments.statistics.charts

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentStatisticsPieChartBinding
import com.example.exercisetracker.utility.Constants.Companion.SAVED_STATE_BOOLEAN
import com.example.exercisetracker.utility.UtilityFunctions.dateFormatter
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener


class StatisticsPieChart : Fragment(), OnChartValueSelectedListener {

    private var _binding: FragmentStatisticsPieChartBinding? = null
    private val binding get() = _binding!!
    private val args: StatisticsPieChartArgs by navArgs()
    private lateinit var exerciseArray: Array<String>
    private val pieEntries = mutableListOf<PieEntry>()
    private var isPercent = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsPieChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let {
            isPercent = it.getBoolean(SAVED_STATE_BOOLEAN)
        }
        exerciseArray = resources.getStringArray(R.array.mode_of_exercise)
        initializeMaterialToolbar()
        populatePieChart()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_STATE_BOOLEAN, isPercent)
    }


    @SuppressLint("NewApi")
    private fun populatePieChart() {
        val colorArray = mutableListOf(
            requireContext().getColor(R.color.chart_bar_light), requireContext().getColor(
                R.color.chart_combined_light
            ), requireContext().getColor(R.color.chart_line_light)
        )
        val argArray = arrayOf(
            args.pieChartData?.cycling,
            args.pieChartData?.walking,
            args.pieChartData?.jogging
        )
        repeat(3) {
            if (argArray[it] != 0.0f) {
                pieEntries.add(PieEntry(argArray[it]!!, exerciseArray[it]))
            }

        }
        val pieDataSet = PieDataSet(pieEntries, getString(R.string.exerciseType))
        pieDataSet.apply {
            valueTextSize = 14f
            colors = colorArray
        }

        val pieData = PieData(pieDataSet)
        pieData.setValueFormatter(PercentFormatter(binding.statisticsPieChartChart))
        binding.statisticsPieChartChart.apply {
            data = pieData
            description.isEnabled = false
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.setDrawInside(false)
            legend.textSize = 14f
            setUsePercentValues(isPercent)
            isRotationEnabled = false
            isDrawHoleEnabled = false
            animateY(500)
            checkNightMode(this)

        }
        binding.statisticsPieChartChart.setOnChartValueSelectedListener(this)
    }

    private fun checkNightMode(pieChart: PieChart) {
        val test = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if(test == Configuration.UI_MODE_NIGHT_YES){
            pieChart.apply{
                legend.textColor = Color.WHITE
            }
        }
    }

    private fun initializeMaterialToolbar() {
        binding.statisticsPieChartToolBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        if (args.pieChartData?.startDate != 0L) {
            binding.statisticsPieChartToolBar.title = "Pie Chart\t\t(${dateFormatter(args.pieChartData?.startDate!!)} - ${dateFormatter(args.pieChartData?.endDate!!)})"
        }
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        isPercent = !isPercent
        binding.statisticsPieChartChart.apply {
            setUsePercentValues(isPercent)
            highlightValue(null)
        }
    }

    override fun onNothingSelected() {
        isPercent = !isPercent
        binding.statisticsPieChartChart.apply {
            setUsePercentValues(isPercent)
            highlightValue(null)
        }
    }
}