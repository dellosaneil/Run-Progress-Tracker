package com.example.exercisetracker.bottomNavFragments.statistics.charts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentStatisticsBarChartBinding
import com.example.exercisetracker.databinding.FragmentStatisticsLineChartBinding

class StatisticsLineChart : Fragment() {

    private var _binding: FragmentStatisticsLineChartBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsLineChartBinding.inflate(inflater, container, false)
        return binding.root
    }

}