package com.example.exercisetracker.bottomNavFragments.statistics.charts

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentStatisticsBinding
import com.example.exercisetracker.databinding.FragmentStatisticsPieChartBinding
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class StatisticsPieChart : Fragment() {

    private var _binding: FragmentStatisticsPieChartBinding? = null
    private val binding get() = _binding!!
    private val args: StatisticsPieChartArgs by navArgs()
    private lateinit var exerciseArray: Array<String>
    private val pieEntries = mutableListOf<PieEntry>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsPieChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        exerciseArray = resources.getStringArray(R.array.mode_of_exercise)
        navigationUpListener()
        populatePieChart()
    }

    private fun populatePieChart() {
        val colorArray = mutableListOf(Color.GRAY, Color.RED, Color.GREEN)
        val argArray = arrayOf(args.pieChartData?.cycling, args.pieChartData?.walking, args.pieChartData?.jogging)
        repeat(3){
            pieEntries.add(PieEntry(argArray[it]!!, exerciseArray[it]))
        }
        val pieDataSet = PieDataSet(pieEntries, "Mode of Exercise")
        pieDataSet.apply{
            valueTextSize = 14f
            colors = colorArray
        }

        val pieData = PieData(pieDataSet)
        binding.statisticsPieChartChart.apply{
            data = pieData
            description.isEnabled = false
            legend.orientation = Legend.LegendOrientation.VERTICAL
            legend.verticalAlignment = Legend.LegendVerticalAlignment.CENTER
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.RIGHT
            legend.setDrawInside(false)
        }
    }

    private fun navigationUpListener() {
        binding.statisticsPieChartToolBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

}