package com.example.fleamarket.home.recyclerview;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;
/**
 * Created by DaiWei on 23/02/2020
 * Function: set the spacing of items in RecyclerView
 */
public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int mSpace;

    public SpaceItemDecoration(int mSpace){
        this.mSpace = mSpace;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        // 适用于两列的间隔设置
        outRect.bottom = mSpace;
        if(parent.getChildAdapterPosition(view) % 2 == 0){
            outRect.left = mSpace;
            outRect.right = mSpace / 2;
        }else{
            outRect.left = mSpace / 2;
            outRect.right = mSpace;
        }
        if(parent.getChildAdapterPosition(view) < 2){
            outRect.top = mSpace;
        }
    }
}
