package com.example.exercisetracker.bottomNavFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentHistoryBinding
import com.example.exercisetracker.databinding.FragmentStatisticsBinding

class Statistics : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}