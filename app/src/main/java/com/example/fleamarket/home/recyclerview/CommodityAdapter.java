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
import com.github.nukc.LoadMoreWrapper.LoadMoreAdapter;
import com.github.nukc.LoadMoreWrapper.LoadMoreWrapper;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CommodityAdapter extends RecyclerView.Adapter<CommodityAdapter.ViewHolder> {
    private List<Commodity> mCommodityList;
    private RecyclerView mRecyclerView;
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

    public CommodityAdapter(List<Commodity> commodityList, RecyclerView recyclerView) {
        this.mCommodityList = commodityList;
        this.mRecyclerView = recyclerView;
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
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                HomeFragment.addCommodities(mCommodityList);
//                enabled.setLoadMoreEnabled(false);
//                enabled.setLoadFailed(true);
                notifyDataSetChanged();
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
                .inflate(R.layout.home_commodity, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.mCommodityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Commodity commodity = mCommodityList.get(position);
                Intent intent = new Intent(mRecyclerView.getContext(), CommodityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", commodity.getCommodityName());
                bundle.putString("content", commodity.getCommodityDetail());
                bundle.putString("price", commodity.getPrice());
                intent.putExtras(bundle);
                mRecyclerView.getContext().startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Commodity commodity = (Commodity)mCommodityList.get(position);
        holder.mImage.setImageResource(R.drawable.ic_launcher_background);
        holder.mName.setText(commodity.getCommodityName());
        holder.mContent.setText(commodity.getCommodityDetail());
        holder.mPrice.setText(commodity.getPrice());
    }

    @Override
    public int getItemCount() {
        return mCommodityList.size();
    }

}
