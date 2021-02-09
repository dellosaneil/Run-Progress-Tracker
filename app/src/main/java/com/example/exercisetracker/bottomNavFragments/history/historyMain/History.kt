package com.example.exercisetracker.bottomNavFragments.history.historyMain

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentHistoryBinding
import com.example.exercisetracker.utility.MyItemDecoration
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class History : Fragment(), HistoryAdapter.HistoryListener,
    androidx.appcompat.widget.Toolbar.OnMenuItemClickListener {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private var historyAdapter: HistoryAdapter? = null
    private val historyViewModel: HistoryViewModel by viewModels()

    private var sortNumber = -1
    private var filterNumber = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        binding.historyToolbar.setOnMenuItemClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        historyAdapter = HistoryAdapter(this)
        binding.historyRecyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = historyAdapter
            addItemDecoration(MyItemDecoration(5,5,5))
        }
        historyAdapter?.placeWorkoutData(historyViewModel.workoutByStartTime())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHistoryWorkoutClicked(index: Int) {}


    override fun onMenuItemClick(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.historyMenu_sort -> {
                createSortDialog()
                true
            }
            R.id.historyMenu_filter -> {
                createFilterDialog()
                true
            }
            else -> false
        }
    }

    private fun createFilterDialog(){
        val singleItems = resources.getStringArray(R.array.historyMenu_filter)
        var checkedItem = 0

        MaterialAlertDialogBuilder(binding.root.context)
            .setTitle(resources.getString(R.string.workoutHistory_menuFilterTitle))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.historyMenu_filter)) { dialog, _ ->
                filterWorkout(checkedItem)
                dialog.dismiss()
            }
            .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                checkedItem = which
            }
            .setCancelable(false)
            .show()
    }

    private fun filterWorkout(filterBy : Int){
        filterNumber = filterBy
        when(filterBy){
            0 -> historyAdapter?.placeWorkoutData(historyViewModel.workoutFilter("Cycling"))
            1 -> historyAdapter?.placeWorkoutData(historyViewModel.workoutFilter("Walking"))
            2 -> historyAdapter?.placeWorkoutData(historyViewModel.workoutFilter("Jogging"))
            3 -> historyAdapter?.placeWorkoutData(historyViewModel.workoutByStartTime())
        }
    }



    private fun createSortDialog() {
        val singleItems = resources.getStringArray(R.array.historyMenu_sort)
        var checkedItem = 0

        MaterialAlertDialogBuilder(binding.root.context)
            .setTitle(resources.getString(R.string.workoutHistory_menuSortTitle))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.historyMenu_sort)) { dialog, _ ->
                sortWorkout(checkedItem)
                dialog.dismiss()
            }
            .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                checkedItem = which
            }
            .setCancelable(false)
            .show()
    }

    private fun sortWorkout(sortBy: Int) {
        sortNumber = sortBy
        when (sortBy) {
            0 -> historyAdapter?.placeWorkoutData(historyViewModel.workoutByStartTime())
            1 -> historyAdapter?.placeWorkoutData(historyViewModel.workoutByTotalTime())
            2 -> historyAdapter?.placeWorkoutData(historyViewModel.workoutByTotalDistance())
            3 -> historyAdapter?.placeWorkoutData(historyViewModel.workoutByAverageSpeed())
        }
    }

}



















