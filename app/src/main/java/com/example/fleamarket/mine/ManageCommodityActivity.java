package com.example.fleamarket.mine;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fleamarket.R;
import com.example.fleamarket.home.recyclerview.SpaceItemDecoration;
import com.example.fleamarket.net.Commodity;
import com.example.fleamarket.net.IServerListener;
import com.example.fleamarket.net.NetHelper;
import com.example.fleamarket.net.NetMessage;
import com.example.fleamarket.utils.PictureUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class ManageCommodityActivity extends AppCompatActivity implements IServerListener {
    public List<Commodity> mCommodityList = new ArrayList<>();
    public int commodityIndex = 0;
    private SwipeRefreshLayout refresh;
    private ManageCommodityAdapter adapter;
    private Activity currentActivity;
    private Commodity commodity;
    private ImageView avatar;
    private String sNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_commodity);
        currentActivity = this;
        commodity = (Commodity)getIntent().getExtras().getSerializable("commodity");
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(30, 1));
        adapter = new ManageCommodityAdapter(mCommodityList, this);
        recyclerView.setAdapter(adapter);
        FloatingActionButton scrollTotop = findViewById(R.id.scroll_to_top);
        scrollTotop.setOnClickListener((v) -> recyclerView.smoothScrollToPosition(0));
        refresh = findViewById(R.id.refresh);
        refresh.setColorSchemeResources(R.color.colorAccent);
        refresh.setOnRefreshListener(() -> refreshCommodities());
    }

    // 下拉刷新
    private void resetCommodities(){
        mCommodityList.clear();
        adapter.notifyDataSetChanged();
        commodityIndex = 0;
        // setLoadMoreEnabled(true)会执行loadmore响应函数，所以不必手动调用
        adapter.mLoadMore.setLoadMoreEnabled(true);
    }

    // 加载更多商品
    public void addCommodities(){
        new Thread(() -> NetHelper.getCommodity((IServerListener)currentActivity, commodityIndex, commodity.getSellerID())).start();
    }

    private void refreshCommodities() {
        resetCommodities();
        refresh.setRefreshing(false);
    }


    @Override
    public void onSuccess(NetMessage info) {
        if (info.getCommodityNum() == 0) {
            adapter.mLoadMore.setLoadFailed(false);
            adapter.mLoadMore.setLoadMoreEnabled(false);
            runOnUiThread(() -> adapter.notifyItemChanged(commodityIndex));
        } else {
            List<Commodity> serverCommodity = info.getCommodityList();
            final int commodityNum = serverCommodity.size();
            Commodity commodity;
            for (int i = 0; i < commodityNum; i++) {
                commodity = serverCommodity.get(i);
                mCommodityList.add(commodity);
                // 将商品照片存到本地
                if (commodity.isHavePhoto()) {
                    PictureUtils.saveImageFromByte(commodity.getCommodityPhoto().getData(),
                            getExternalCacheDir().getAbsolutePath() +
                                    "/commodity/" + commodity.getCommodityID() + ".jpg");
                }
                // 保存对应用户头像到本地
                PictureUtils.saveImageFromByte(commodity.getAvatar().getData(),
                        getExternalCacheDir().getAbsolutePath() +
                                "/avatar/avatar_" + commodity.getSellerID() + ".jpg");
            }
            if (commodityNum < 20) {
                adapter.mLoadMore.setLoadMoreEnabled(false);
            }
            adapter.mLoadMore.setLoadFailed(false);
            runOnUiThread(() -> {
                    adapter.notifyItemInserted(commodityIndex);
                    commodityIndex += commodityNum;
//                    adapter.notifyDataSetChanged();
                });
        }
    }

    @Override
    public void onFailure(final String info) {
        runOnUiThread(() -> {
                Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();
                adapter.mLoadMore.setLoadFailed(true);
            });
    }
}