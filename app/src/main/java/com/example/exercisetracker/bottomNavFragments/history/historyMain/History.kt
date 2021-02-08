package com.example.exercisetracker.bottomNavFragments.history.historyMain

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exercisetracker.databinding.FragmentHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class History : Fragment(), HistoryAdapter.HistoryListener {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private var historyAdapter : HistoryAdapter? = null
    private val historyViewModel : HistoryViewModel by viewModels()

    private val TAG = "History"


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        historyAdapter = HistoryAdapter(this)
        binding.historyRecyclerView.apply{
            layoutManager = LinearLayoutManager(activity)
            adapter = historyAdapter
        }
        historyViewModel.workoutData().observe(viewLifecycleOwner){
            Log.i(TAG, "initializeRecyclerView: $it")
            historyAdapter!!.placeWorkoutData(it)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHistoryWorkoutClicked(index: Int) {
        Log.i(TAG, "onHistoryWorkoutClicked: $index")
    }

}