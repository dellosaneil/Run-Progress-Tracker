package com.example.exercisetracker.bottomNavFragments.history

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentHistoryBinding

class History : Fragment() {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Navigation.findNavController(view).navigate(R.id.history_workoutHistoryPolyline)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}