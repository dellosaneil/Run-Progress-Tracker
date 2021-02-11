package com.example.exercisetracker.utility

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

class SwipeListener(private val listener: SwipeListenerViewHolderAdapter) :
    ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onViewHolderIndex(viewHolder.adapterPosition)
    }

    interface SwipeListenerViewHolderAdapter {
        fun onViewHolderIndex(index: Int)
    }

}