package com.example.exercisetracker.bottomNavFragments.statistics.charts

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentStatisticsLineChartBinding
import com.example.exercisetracker.utility.UtilityFunctions.convertMilliSecondsToText
import com.example.exercisetracker.utility.UtilityFunctions.dateFormatter
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatisticsLineChart : Fragment(), CompoundButton.OnCheckedChangeListener, OnChartValueSelectedListener {

    private var _binding: FragmentStatisticsLineChartBinding? = null
    private val binding get() = _binding!!
    private val args: StatisticsLineChartArgs by navArgs()

    //    private val startTimeData = mutableListOf<Entry>()
    private val totalKMData = mutableListOf<Entry>()
    private val totalTimeData = mutableListOf<Entry>()
    private val averageSpeedData = mutableListOf<Entry>()

    private var kmSet: LineDataSet? = null
    private var timeSet: LineDataSet? = null
    private var avgSpeedSet: LineDataSet? = null

    private val checkBoxState = arrayOf(true, true, true)


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
        setOnCheckListener()
        toolBarInitialize()
    }

    private fun toolBarInitialize() {
        binding.statisticsLineChartToolBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
        if(args.lineChartData?.startDate != 0L){
            binding.statisticsLineChartToolBar.title = "Line Chart\t\t(${dateFormatter(args.lineChartData?.startDate!!)} - ${dateFormatter(args.lineChartData?.endDate!!)})"
        }
    }

    private fun setOnCheckListener() {
        binding.statisticsLineChartDistance.setOnCheckedChangeListener(this)
        binding.statisticsLineChartSpeed.setOnCheckedChangeListener(this)
        binding.statisticsLineChartTime.setOnCheckedChangeListener(this)
        binding.statisticsLineChartChart.setOnChartValueSelectedListener(this)
    }


    private fun prepareLineDataSets() {
        val lineLabelsArray = arrayOf("Kilometers", "Minutes", "km/h")
        val colorsArray = arrayOf(Color.RED, Color.BLUE, Color.GREEN)
        val entryArray = arrayOf(totalKMData, totalTimeData, averageSpeedData)
        kmSet = LineDataSet(entryArray[0], lineLabelsArray[0])
        timeSet = LineDataSet(entryArray[1], lineLabelsArray[1])
        avgSpeedSet = LineDataSet(entryArray[2], lineLabelsArray[2])
        val dataSetArray = arrayOf(kmSet, timeSet, avgSpeedSet)
        repeat(3) {
            dataSetArray[it]!!.apply {
                axisDependency = YAxis.AxisDependency.LEFT
                color = colorsArray[it]
                setDrawCircles(false)
                setDrawValues(false)
            }
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
            withContext(Main) {
                populateLineChart()
            }
        }
    }

    private fun populateLineChart() {
        val dataSets = mutableListOf<ILineDataSet>()
        dataSets.add(kmSet!!)
        dataSets.add(timeSet!!)
        dataSets.add(avgSpeedSet!!)
        drawLineChart(LineData(dataSets))
    }

    private fun checkBoxLineChart(index: Int, isChecked: Boolean) {
        val dataSetArray = arrayOf(kmSet, timeSet, avgSpeedSet)
        val dataSets = mutableListOf<ILineDataSet>()
        checkBoxState[index] = isChecked
        repeat(3) {
            if (checkBoxState[it]) {
                dataSets.add(dataSetArray[it]!!)
            }
        }
        drawLineChart(LineData(dataSets))
    }

    private fun drawLineChart(lineData: LineData) {
        binding.statisticsLineChartChart.apply {
            invalidate()
            data = lineData
            description.isEnabled = false
            animateY(500)
        }
    }
    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        when (buttonView?.id) {
            R.id.statisticsLineChart_distance -> checkBoxLineChart(0, isChecked)
            R.id.statisticsLineChart_time -> checkBoxLineChart(1, isChecked)
            R.id.statisticsLineChart_speed -> checkBoxLineChart(2, isChecked)
        }
    }
    override fun onValueSelected(e: Entry?, h: Highlight?) {
        val index = e?.x?.toInt()
        val lineChartData = args.lineChartData
        binding.statisticsLineChartDetails.apply{
            lineChartStartDate.text = getString(R.string.lineChart_startDate, dateFormatter(lineChartData!!.startTime[index!!]))
            lineChartKm.text = getString(R.string.lineChart_km, String.format("%.2f km",lineChartData.totalKM[index]))
            lineChartSpeed.text = getString(R.string.lineChart_avgSpeed, String.format("%.2f km/h",lineChartData.averageSpeed[index]))
            lineChartWorkout.text = getString(R.string.lineChart_workout, lineChartData.modeOfExercise[index])
            lifecycleScope.launch(Main){
                lineChartTime.text = getString(R.string.lineChart_minutes, convertMilliSecondsToText(lineChartData.totalTime[index], false, lifecycleScope))
            }
        }
    }

    override fun onNothingSelected() {

    }
}















