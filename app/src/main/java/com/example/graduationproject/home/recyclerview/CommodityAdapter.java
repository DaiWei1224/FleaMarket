package com.example.graduationproject.home.recyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.graduationproject.R;
import com.example.graduationproject.home.CommodityActivity;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class CommodityAdapter extends RecyclerView.Adapter<CommodityAdapter.ViewHolder> {
    private List<Commodity> mCommodityList;
    private RecyclerView mRecyclerView;

    static class ViewHolder extends RecyclerView.ViewHolder {

        View mCommodityView;
        ImageView mImage;
        TextView mContent;
        TextView mPrice;

        public ViewHolder(View view){
            super(view);
            mCommodityView = view;
            mImage = view.findViewById(R.id.image);
            mContent = view.findViewById(R.id.content);
            mPrice = view.findViewById(R.id.price);
        }
    }

    public CommodityAdapter(List<Commodity> commodityList, RecyclerView recyclerView) {
        this.mCommodityList = commodityList;
        this.mRecyclerView = recyclerView;
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
                bundle.putInt("image", commodity.getImage());
                bundle.putString("content", commodity.getContent());
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
        holder.mImage.setImageResource(commodity.getImage());
        holder.mContent.setText(commodity.getContent());
        holder.mPrice.setText(commodity.getPrice());
    }

    @Override
    public int getItemCount() {
        return mCommodityList.size();
    }

}
