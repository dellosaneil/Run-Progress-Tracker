package com.example.exercisetracker.bottomNavFragments.history.historyMain

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.exercisetracker.R
import com.example.exercisetracker.databinding.FragmentHistoryBinding
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
        }
        historyViewModel.workoutByStartTime().observe(viewLifecycleOwner) {
            historyAdapter!!.placeWorkoutData(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onHistoryWorkoutClicked(index: Int) {
    }

    private val TAG = "History"

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
        val singleItems = resources.getStringArray(R.array.mode_of_exercise)
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
        when(filterBy){
            0 -> historyViewModel.workoutFilter("Bicycle").observe(viewLifecycleOwner){
                historyAdapter?.placeWorkoutData(it)
            }
            1 -> historyViewModel.workoutFilter("Walking").observe(viewLifecycleOwner){
                historyAdapter?.placeWorkoutData(it)
            }
            2 -> historyViewModel.workoutFilter("Jogging").observe(viewLifecycleOwner){
                historyAdapter?.placeWorkoutData(it)
            }


        }
    }

//    <item>Bicycle</item>
//        <item>Walking</item>
//        <item>Jogging</item>



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
            0 -> historyViewModel.workoutByStartTime().observe(viewLifecycleOwner) {
                historyAdapter!!.placeWorkoutData(it)
            }
            1 -> historyViewModel.workoutByTotalTime().observe(viewLifecycleOwner) {
                historyAdapter!!.placeWorkoutData(it)
            }
            2 -> historyViewModel.workoutByTotalDistance().observe(viewLifecycleOwner) {
                historyAdapter!!.placeWorkoutData(it)
            }
        }
    }

}



















