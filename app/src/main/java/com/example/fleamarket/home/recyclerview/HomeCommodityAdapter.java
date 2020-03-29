package com.example.fleamarket.home.recyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fleamarket.R;
import com.example.fleamarket.home.CommodityActivity;
import com.example.fleamarket.home.HomeFragment;
import com.example.fleamarket.net.Commodity;
import com.example.fleamarket.utils.PictureUtils;
import com.github.nukc.LoadMoreWrapper.LoadMoreAdapter;
import com.github.nukc.LoadMoreWrapper.LoadMoreWrapper;

import java.io.File;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class HomeCommodityAdapter extends RecyclerView.Adapter<HomeCommodityAdapter.ViewHolder> {
    private List<Commodity> mCommodityList;
    private HomeFragment mFragment;
    public LoadMoreWrapper mLoadMore;

    static class ViewHolder extends RecyclerView.ViewHolder {

        View mCommodityView;
        ImageView mImage;
        TextView mName;
        TextView mContent;
        TextView mPrice;

        public ViewHolder(View view){
            super(view);
            mCommodityView = view;
            mImage = view.findViewById(R.id.image);
            mName = view.findViewById(R.id.commodity_name);
            mContent = view.findViewById(R.id.commodity_detail);
            mPrice = view.findViewById(R.id.price);
        }
    }

    public HomeCommodityAdapter(List<Commodity> commodityList, HomeFragment fragment) {
        this.mCommodityList = commodityList;
        mFragment = fragment;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mLoadMore = LoadMoreWrapper.with(this);
        mLoadMore.setListener(new LoadMoreAdapter.OnLoadMoreListener() {
            @Override
            public void onLoadMore(LoadMoreAdapter.Enabled enabled) {
                // you can enabled.setLoadMoreEnabled(false) when do not need load more
                // you can enabled.setLoadFailed(true) when load failed
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                mFragment.addCommodities();
            }
        })
//        .setFooterView(R.layout.load_more)
        .setNoMoreView(R.layout.no_more)
        .setShowNoMoreEnabled(true)
        .into(recyclerView);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.commodity_vertical, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.mCommodityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Commodity commodity = mCommodityList.get(position);
//                Intent intent = new Intent(mActivity, CommodityActivity.class);
                Intent intent = new Intent(mFragment.getActivity(), CommodityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("commodity", commodity);
                intent.putExtras(bundle);
                mFragment.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Commodity commodity = mCommodityList.get(position);
        if (commodity.isHavePhoto()) {
//            // 用这种方法展示图片图片没经过压缩，recyclerView滑动重新绘制的时候会卡顿
//            PictureUtils.displayImage(holder.mImage, mActivity.getExternalCacheDir().getAbsolutePath() +
//                    "/commodity/" + commodity.getCommodityID() + ".jpg");
            PictureUtils.showPictureOnRecyclerView(holder.mImage,
                    new File(mFragment.getActivity().getExternalCacheDir().getAbsolutePath() +
                            "/commodity/" + commodity.getCommodityID() + ".jpg"),
                    mFragment.getActivity());
//            holder.mImage.setVisibility(View.VISIBLE);
        } else {
//            holder.mImage.setVisibility(View.GONE);
            holder.mImage.setImageResource(R.drawable.image);
        }
        holder.mName.setText(commodity.getCommodityName());
        holder.mContent.setText(commodity.getCommodityDetail());
        holder.mPrice.setText("¥" + commodity.getPrice());
    }

    @Override
    public int getItemCount() {
        return mCommodityList.size();
    }

}
