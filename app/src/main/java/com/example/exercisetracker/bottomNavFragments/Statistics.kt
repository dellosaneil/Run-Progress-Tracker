package com.example.exercisetracker.bottomNavFragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentStatisticsBinding
import com.example.exercisetracker.utility.FragmentLifecycleLog


class Statistics : FragmentLifecycleLog(), View.OnClickListener {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        populateGridLayout(view)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.fragmentStatisticsBar.fragmentStatisticsGridCardView.setOnClickListener(this)
        binding.fragmentStatisticsLine.fragmentStatisticsGridCardView.setOnClickListener(this)
        binding.fragmentStatisticsPie.fragmentStatisticsGridCardView.setOnClickListener(this)
        binding.fragmentStatisticsCombined.fragmentStatisticsGridCardView.setOnClickListener(this)
    }


    @SuppressLint("NewApi")
    private fun populateGridLayout(view: View) {
        val cardViewArray = arrayOf(
            binding.fragmentStatisticsBar.fragmentStatisticsGridCardView,
            binding.fragmentStatisticsLine.fragmentStatisticsGridCardView,
            binding.fragmentStatisticsPie.fragmentStatisticsGridCardView,
            binding.fragmentStatisticsCombined.fragmentStatisticsGridCardView
        )
        val imageViewArray = arrayOf(
            binding.fragmentStatisticsBar.fragmentStatisticsGridChart,
            binding.fragmentStatisticsLine.fragmentStatisticsGridChart,
            binding.fragmentStatisticsPie.fragmentStatisticsGridChart,
            binding.fragmentStatisticsCombined.fragmentStatisticsGridChart
        )
        val textViewArray = arrayOf(
            binding.fragmentStatisticsBar.fragmentStatisticsGridName,
            binding.fragmentStatisticsLine.fragmentStatisticsGridName,
            binding.fragmentStatisticsPie.fragmentStatisticsGridName,
            binding.fragmentStatisticsCombined.fragmentStatisticsGridName
        )
        val colorArray = arrayOf(
            R.color.chart_bar_light,
            R.color.chart_line_light,
            R.color.chart_pie_light,
            R.color.chart_combined_light
        )
        val textArray = resources.getStringArray(R.array.charts)
        val imageArray = arrayOf(
            R.drawable.ic_bar_chart_68,
            R.drawable.ic_line_chart_68,
            R.drawable.ic_pie_chart_68,
            R.drawable.ic_combined_chart_68
        )
        repeat(4) {
            Glide.with(view)
                .load(imageArray[it])
                .into(imageViewArray[it])
            cardViewArray[it].setCardBackgroundColor(requireContext().getColor(colorArray[it]))
            textViewArray[it].text = textArray[it]
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val TAG = "Statistics"

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.fragmentStatistics_bar -> Log.i(TAG, "onClick: ")


        }
    }


}