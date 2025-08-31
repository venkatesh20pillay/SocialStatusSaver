package com.statuses.statussavers

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewItemDecorator(private val spaceInPixels: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.left = spaceInPixels
        outRect.right = spaceInPixels
        outRect.bottom = 0

        outRect.top = if (parent.getChildLayoutPosition(view) == 0) spaceInPixels else 0
    }
}
