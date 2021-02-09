package com.example.exercisetracker.utility

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class MyItemDecoration(private val upper : Int, private val lower : Int, private val sides : Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.top = upper
        outRect.bottom = lower
        outRect.left = sides
        outRect.right = sides
    }

}