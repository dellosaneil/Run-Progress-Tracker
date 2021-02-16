package com.example.exercisetracker.bottomNavFragments.statistics.charts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentStatisticsBarChartBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class StatisticsBarChart : Fragment() {

    private val args : StatisticsBarChartArgs? by navArgs()
    private var _binding: FragmentStatisticsBarChartBinding? = null
    private val binding get() = _binding!!

    private var xAxisLabel = arrayOf<String>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBarChartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateBarChart()
    }

    private fun populateBarChart() {
        xAxisLabel = resources.getStringArray(R.array.mode_of_exercise)
        val entries = mutableListOf<BarEntry>()
        args?.barChartData.let{
            entries.add(BarEntry(0f, it!!.cyclingKilometers))
            entries.add(BarEntry(1f, it.joggingKilometers))
            entries.add(BarEntry(2f, it.walkingKilometers))
        }

        val dataSet  = BarDataSet(entries, "Kilometers")
        val barData = BarData(dataSet)
        binding.statisticsBarChartChart.apply{
            setFitBars(true)
            data = barData
            setDrawValueAboveBar(true)
            xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabel)
            animateY(1000)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}