package com.example.fleamarket.mine;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.github.nukc.LoadMoreWrapper.LoadMoreWrapper;

import java.io.File;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class ManageCommodityAdapter extends RecyclerView.Adapter<ManageCommodityAdapter.ViewHolder> implements IServerListener {
    private static List<Commodity> mCommodityList;
    private ManageCommodityActivity mActivity;
    public LoadMoreWrapper mLoadMore;
    private static ManageCommodityAdapter mAdapter;
    private static int mPosition;
    ProgressDialog progressDialog;

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
        mAdapter = this;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        mLoadMore = LoadMoreWrapper.with(this);
        // you can enabled.setLoadMoreEnabled(false) when do not need load more
        // you can enabled.setLoadFailed(true) when load failed
        mLoadMore.setListener((enabled) -> mActivity.addCommodities())
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
        holder.mCommodityView.setOnClickListener((v) -> {
                int position = holder.getAdapterPosition();
                Commodity commodity = mCommodityList.get(position);
                Intent intent = new Intent(mActivity, CommodityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("commodity", commodity);
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
            });
        holder.mEdit.setOnClickListener((v) -> {
                int position = holder.getAdapterPosition();
                Commodity commodity = mCommodityList.get(position);
                Intent intent = new Intent(mActivity, EditCommodityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("commodity", commodity);
                intent.putExtras(bundle);
                mActivity.startActivity(intent);
                mPosition = position;
            });
        holder.mDelete.setOnClickListener((v) -> {
                int position = holder.getAdapterPosition();
                Commodity commodity = mCommodityList.get(position);
                showDeleteDialog(commodity.getCommodityID());
                mPosition = position;
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
                .setPositiveButton("确认", (dialog, which) -> {
                        new Thread(() -> NetHelper.deleteCommodity(mAdapter, commodityID)).start();
                        showWaitingDialog("正在删除");
                    }).setNegativeButton("取消", (dialog, which) -> dialog.dismiss()).create().show();
    }

    public static void notifyItemChanged(Commodity commodity) {
        mCommodityList.set(mPosition, commodity);
        mAdapter.notifyItemChanged(mPosition);
    }

    private void showWaitingDialog(String message) {
        progressDialog = new ProgressDialog(mActivity);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true); // 是否形成一个加载动画，true表示不明确加载进度形成转圈动画，false表示明确加载进度
        progressDialog.setCancelable(false); // 点击返回键或者dialog四周是否关闭dialog，true表示可以关闭，false表示不可关闭
        progressDialog.show();
    }

    @Override
    public void onSuccess(final NetMessage info) {
        mActivity.runOnUiThread(() -> {
                progressDialog.dismiss();
                mCommodityList.remove(mPosition);
                notifyItemRemoved(mPosition);
                Toast.makeText(mActivity, "删除成功", Toast.LENGTH_SHORT).show();
            });
    }

    @Override
    public void onFailure(String info) {
        Looper.prepare();
        progressDialog.dismiss();
        Toast.makeText(mActivity, info, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

}