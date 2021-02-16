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
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter


class StatisticsBarChart : Fragment(), RadioGroup.OnCheckedChangeListener {

    private val args: StatisticsBarChartArgs? by navArgs()
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
        navigationUp()
        populateBarChart()
        binding.radioGroup.setOnCheckedChangeListener(this)
    }

    private fun navigationUp() {
        binding.statisticsBarChartToolBar.setNavigationOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun populateBarChart(label: String = "Kilometers") {
        xAxisLabel = resources.getStringArray(R.array.mode_of_exercise)
        val entries = mutableListOf<BarEntry>()
        args?.barChartData.let {
            if (label == "Kilometers") {
                entries.add(BarEntry(0f, it!!.cyclingKilometers))
                entries.add(BarEntry(1f, it.joggingKilometers))
                entries.add(BarEntry(2f, it.walkingKilometers))
            } else {
                entries.add(BarEntry(0f, it!!.cyclingTime / 60_000))
                entries.add(BarEntry(1f, it.joggingTime / 60_000))
                entries.add(BarEntry(2f, it.walkingTime / 60_000))
            }
        }
        val dataSet = BarDataSet(entries, label)
        val barData = BarData(dataSet)
        binding.statisticsBarChartChart.apply {
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

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {
        if(group?.id == R.id.radioGroup){
            when(checkedId){
                R.id.time -> populateBarChart("Minutes")
                R.id.distance -> populateBarChart("Kilometers")
            }
        }
    }
}
















