package com.example.exercisetracker.bottomNavFragments.statistics.charts

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.databinding.FragmentStatisticsLineChartBinding
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ColorFormatter
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatisticsLineChart : Fragment() {

    private var _binding: FragmentStatisticsLineChartBinding? = null
    private val binding get() = _binding!!
    private val args: StatisticsLineChartArgs by navArgs()

    //    private val startTimeData = mutableListOf<Entry>()
    private val totalKMData = mutableListOf<Entry>()
    private val totalTimeData = mutableListOf<Entry>()
    private val averageSpeedData = mutableListOf<Entry>()

    private lateinit var kmSet: LineDataSet
    private lateinit var timeSet: LineDataSet
    private lateinit var avgSpeedSet: LineDataSet


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsLineChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prepareLineChartData()
    }

    private fun prepareLineDataSets() {
        kmSet = LineDataSet(totalKMData, "Kilometers")
        kmSet.apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = Color.RED
        }
        timeSet = LineDataSet(totalTimeData, "Minutes")
        timeSet.apply {
            axisDependency = YAxis.AxisDependency.LEFT
            color = Color.BLUE
        }

        avgSpeedSet = LineDataSet(averageSpeedData, "km/h")
        avgSpeedSet.apply{
            axisDependency = YAxis.AxisDependency.LEFT
            color = Color.GREEN
        }
    }

    private fun prepareLineChartData() {
        lifecycleScope.launch {
            args.lineChartData?.let { lineChartData ->
                repeat(lineChartData.startTime.size) {
                    val tempTotalKMEntry = Entry(it.toFloat(), lineChartData.totalKM[it])
                    val tempTotalTimeEntry =
                        Entry(it.toFloat(), lineChartData.totalTime[it].toFloat() / 60_000)
                    val tempAvgSpeedEntry = Entry(it.toFloat(), lineChartData.averageSpeed[it])
                    totalKMData.add(tempTotalKMEntry)
                    totalTimeData.add(tempTotalTimeEntry)
                    averageSpeedData.add(tempAvgSpeedEntry)
                }
            }
            prepareLineDataSets()
            withContext(Main){
                populateLineChart()
            }
        }
    }

    private fun populateLineChart() {
        val dataSets = mutableListOf<ILineDataSet>()
        dataSets.add(kmSet)
        dataSets.add(timeSet)
        dataSets.add(avgSpeedSet)
        val lineData = LineData(dataSets)
        binding.statisticsLineChartChart.data = lineData
    }
}















