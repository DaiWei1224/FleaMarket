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
    private int mType;

    public SpaceItemDecoration(int mSpace, int type){
        this.mSpace = mSpace;
        this.mType = type;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
        if (mType == 0) { // 瀑布流间隔type = 0
            // 适用于两列的间隔设置
            outRect.bottom = mSpace * 2;
            outRect.left = mSpace;
            outRect.right = mSpace;
//        if(parent.getChildAdapterPosition(view) % 2 == 0){
//            outRect.left = mSpace;
//            outRect.right = mSpace / 2;
//        }else{
//            outRect.left = mSpace / 2;
//            outRect.right = mSpace;
//        }
            if(parent.getChildAdapterPosition(view) < 2){
                outRect.top = mSpace * 2;
            }
        } else if (mType == 1) { // 线性间隔type = 1
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = mSpace;
            }
            outRect.left = mSpace;
            outRect.right = mSpace;
            outRect.bottom = mSpace;
        } else if (mType == 2) { // 用于消息页面
            if (parent.getChildAdapterPosition(view) != 0) {
                outRect.top = mSpace;
            }
        }

    }
}
