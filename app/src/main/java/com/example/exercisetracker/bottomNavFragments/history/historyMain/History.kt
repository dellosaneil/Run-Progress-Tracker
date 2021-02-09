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

    private var sortItemChecked = 0
    private var filterItemChecked = 3

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
        historyViewModel.workoutList().observe(viewLifecycleOwner){
            historyAdapter?.placeWorkoutData(it)
        }
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

}



















