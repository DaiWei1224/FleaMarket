package com.example.fleamarket.home.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class SpaceItemDecoration(private val space: Int, private val type: Int) : ItemDecoration() {
    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        if (type == 0) { // 瀑布流布局
            outRect.bottom = space * 2
            outRect.left = space
            outRect.right = space
//            if(parent.getChildAdapterPosition(view) % 2 == 0) {
//                outRect.left = space
//                outRect.right = space
//            } else {
//                outRect.left = space / 2
//                outRect.right = space
//            }
            if (parent.getChildAdapterPosition(view) < 2) {
                outRect.top = space * 2
            }
        } else if (type == 1) { // 线性布局
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space
            }
            outRect.left = space
            outRect.right = space
            outRect.bottom = space
        } else if (type == 2) { // 聊天窗口列表
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = space
            }
        }
    }
}