package com.example.exercisetracker.bottomNavFragments.history.historyMain

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exercisetracker.R
import com.example.exercisetracker.data.WorkoutData
import com.example.exercisetracker.databinding.FragmentHistoryBinding
import com.example.exercisetracker.utility.MyItemDecoration
import com.example.exercisetracker.utility.SwipeListener
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class History : Fragment(), HistoryAdapter.HistoryListener,
    androidx.appcompat.widget.Toolbar.OnMenuItemClickListener,
    SwipeListener.SwipeListenerViewHolderAdapter {

    private var _binding: FragmentHistoryBinding? = null
    private val binding get() = _binding!!
    private var historyAdapter: HistoryAdapter? = null
    private val historyViewModel: HistoryViewModel by viewModels()
    private lateinit var currentWorkoutList: List<WorkoutData>

    private var sortItemChecked = 0
    private var filterItemChecked = 3
    private lateinit var swipeListener: SwipeListener
    private var latestDeletedWorkout: WorkoutData? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        binding.historyToolbar.setOnMenuItemClickListener(this)
        swipeListener = SwipeListener(this)

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
            addItemDecoration(MyItemDecoration(5, 5, 5))
            val itemTouchHelper = ItemTouchHelper(swipeListener)
            itemTouchHelper.attachToRecyclerView(this)
        }

        historyViewModel.workoutList().observe(viewLifecycleOwner) {
            historyAdapter?.placeWorkoutData(it)
            currentWorkoutList = it
        }
    }

    override fun onHistoryWorkoutClicked(index: Int) {
        val action = HistoryDirections.historyWorkoutHistoryPolyline(currentWorkoutList[index])
        Navigation.findNavController(binding.root).navigate(action)
    }


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
            R.id.historyMenu_dateRange ->{
                pickDateRange()
                true
            }
            else -> false
        }
    }

    private fun pickDateRange() {
        val materialDatePicker = MaterialDatePicker.Builder.dateRangePicker().build()
        materialDatePicker.show(parentFragmentManager, getString(R.string.workoutHistory_menuDateRange))
        materialDatePicker.addOnPositiveButtonClickListener {

        }


    }

    private fun createFilterDialog() {
        val singleItems = resources.getStringArray(R.array.historyMenu_filter)
        var checkedItem = filterItemChecked

        MaterialAlertDialogBuilder(binding.root.context)
            .setTitle(resources.getString(R.string.workoutHistory_menuFilterTitle))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.historyMenu_filter)) { dialog, _ ->
                historyViewModel.filterWorkoutList(checkedItem)
                filterItemChecked = checkedItem
                dialog.dismiss()
            }
            .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                checkedItem = which
            }
            .setCancelable(false)
            .show()
    }


    private fun createSortDialog() {
        val singleItems = resources.getStringArray(R.array.historyMenu_sort)
        var checkedItem = sortItemChecked

        MaterialAlertDialogBuilder(binding.root.context)
            .setTitle(resources.getString(R.string.workoutHistory_menuSortTitle))
            .setNeutralButton(resources.getString(R.string.cancel)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(resources.getString(R.string.historyMenu_sort)) { dialog, _ ->
                historyViewModel.sortWorkoutList(checkedItem)
                sortItemChecked = checkedItem
                dialog.dismiss()
            }
            .setSingleChoiceItems(singleItems, checkedItem) { _, which ->
                checkedItem = which
            }
            .setCancelable(false)
            .show()
    }

    private fun undoDelete() {
        Snackbar.make(
            binding.root,
            getString(R.string.workoutHistory_snackBarDelete),
            Snackbar.LENGTH_LONG
        )
            .setAction(R.string.workoutHistory_snackBarUndo) {
                latestDeletedWorkout?.let {
                    historyViewModel.insertNewWorkout(it)
                }
            }
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewHolderIndex(index: Int) {
        latestDeletedWorkout = currentWorkoutList[index]
        historyViewModel.deleteWorkout(latestDeletedWorkout!!)
        undoDelete()
    }
}