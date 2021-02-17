package com.example.exercisetracker.bottomNavFragments.statistics.charts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentStatisticsBinding
import com.example.exercisetracker.databinding.FragmentStatisticsPieChartBinding

class StatisticsPieChart : Fragment() {

    private var _binding: FragmentStatisticsPieChartBinding? = null
    private val binding get() = _binding!!
    private val args : StatisticsLineChartArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsPieChartBinding.inflate(inflater, container, false)
        return binding.root
    }
}