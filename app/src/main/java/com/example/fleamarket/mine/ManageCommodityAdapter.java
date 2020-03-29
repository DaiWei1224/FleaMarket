package com.example.fleamarket.mine;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fleamarket.R;
import com.example.fleamarket.home.CommodityActivity;
import com.example.fleamarket.net.Commodity;
import com.example.fleamarket.net.IServerListener;
import com.example.fleamarket.net.NetHelper;
import com.example.fleamarket.net.NetMessage;
import com.example.fleamarket.utils.PictureUtils;
import com.github.nukc.LoadMoreWrapper.LoadMoreAdapter;
import com.github.nukc.LoadMoreWrapper.LoadMoreWrapper;

import java.io.File;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ManageCommodityAdapter extends RecyclerView.Adapter<ManageCommodityAdapter.ViewHolder> implements IServerListener {
    private List<Commodity> mCommodityList;
    private ManageCommodityActivity mActivity;
    public LoadMoreWrapper mLoadMore;
    private ManageCommodityAdapter mAdapter = this;
    private int mPosition;

    static class ViewHolder extends RecyclerView.ViewHolder {

        View mCommodityView;
        ImageView mImage;
        TextView mName;
        TextView mContent;
        ImageView mEdit;
        ImageView mDelete;

        public ViewHolder(View view){
            super(view);
            mCommodityView = view;
            mImage = view.findViewById(R.id.image);
            mName = view.findViewById(R.id.commodity_name);
            mContent = view.findViewById(R.id.commodity_detail);
            mEdit = view.findViewById(R.id.edit);
            mDelete = view.findViewById(R.id.delete);
        }
    }

    public ManageCommodityAdapter(List<Commodity> commodityList, ManageCommodityActivity activity) {
        this.mCommodityList = commodityList;
        mActivity = activity;
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
                mActivity.addCommodities();
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
                .inflate(R.layout.commodity_manage, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.mCommodityView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Commodity commodity = mCommodityList.get(position);
                Intent intent = new Intent(mActivity, CommodityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("commodity", commodity);
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
            }
        });
        holder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        holder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Commodity commodity = mCommodityList.get(position);
                showDeleteDialog(commodity.getCommodityID());
                mPosition = position;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Commodity commodity = mCommodityList.get(position);
        if (commodity.isHavePhoto()) {
            PictureUtils.showPictureOnRecyclerView(holder.mImage,
                    new File(mActivity.getExternalCacheDir().getAbsolutePath() +
                            "/commodity/" + commodity.getCommodityID() + ".jpg"),
                    mActivity);
        } else {
            holder.mImage.setImageResource(R.drawable.image);
        }
        holder.mName.setText(commodity.getCommodityName());
        holder.mContent.setText(commodity.getCommodityDetail());
    }

    @Override
    public int getItemCount() {
        return mCommodityList.size();
    }

    private void showDeleteDialog(final String commodityID) {
        new AlertDialog.Builder(mActivity)
                .setMessage("确认删除商品？")
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                NetHelper.deleteCommodity(mAdapter, commodityID);
                            }
                        }).start();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
        }).create().show();
    }

    @Override
    public void onSuccess(NetMessage info) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCommodityList.remove(mPosition);
                notifyItemRemoved(mPosition);
                Toast.makeText(mActivity, "删除成功", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onFailure(String info) {
        Looper.prepare();
        Toast.makeText(mActivity, info, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

}