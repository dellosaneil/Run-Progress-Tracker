package com.example.exercisetracker.bottomNavFragments.statistics.charts

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentStatisticsPieChartBinding
import com.example.exercisetracker.utility.Constants.Companion.SAVED_STATE_BOOLEAN
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
    private var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsPieChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        savedInstanceState?.let{
            isPercent = it.getBoolean(SAVED_STATE_BOOLEAN)
        }
        exerciseArray = resources.getStringArray(R.array.mode_of_exercise)
        navigationUpListener()
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
            if(argArray[it] != 0.0f){
                pieEntries.add(PieEntry(argArray[it]!!, exerciseArray[it]))
            }

        }
        val pieDataSet = PieDataSet(pieEntries, getString(R.string.exerciseType))
        pieDataSet.apply {
            valueTextSize = 14f
            colors = colorArray
        }

        val pieData = PieData(pieDataSet)
        pieData.setValueFormatter(PercentFormatter())
        binding.statisticsPieChartChart.apply {
            data = pieData
            description.isEnabled = false
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.setDrawInside(false)
            legend.textSize = 14f
            setUsePercentValues(isPercent)
            isDrawHoleEnabled = false
        }
        binding.statisticsPieChartChart.setOnChartValueSelectedListener(this)
    }

    private fun navigationUpListener() {
        binding.statisticsPieChartToolBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onValueSelected(e: Entry?, h: Highlight?) {
        isPercent = !isPercent
        binding.statisticsPieChartChart.apply {
            setUsePercentValues(isPercent)
        }
        val inPercent = if (isPercent) "Data in Percentage" else "Data in numerical value"
        toast?.cancel()
        toast = Toast.makeText(requireContext(), inPercent, Toast.LENGTH_SHORT)
        toast!!.show()


    }

    override fun onNothingSelected() {

    }

}