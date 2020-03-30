package com.example.fleamarket.mine;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fleamarket.R;
import com.example.fleamarket.home.recyclerview.SpaceItemDecoration;
import com.example.fleamarket.net.Commodity;
import com.example.fleamarket.net.IServerListener;
import com.example.fleamarket.net.NetHelper;
import com.example.fleamarket.net.NetMessage;
import com.example.fleamarket.utils.PictureUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class PersonalHomepageActivity extends AppCompatActivity implements IServerListener {
    public List<Commodity> mCommodityList = new ArrayList<>();
    public int commodityIndex = 0;
    private SwipeRefreshLayout refresh;
    private PersonalCommodityAdapter adapter;
    private Activity currentActivity;
    private Commodity commodity;
    private ImageView avatar;
    private String sNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_homepage);
        currentActivity = this;
        commodity = (Commodity)getIntent().getExtras().getSerializable("commodity");
        sNickname = commodity.getSellerName();
        avatar = findViewById(R.id.avatar);
        PictureUtils.displayImage(avatar, getExternalCacheDir().getAbsolutePath() +
                "/avatar/avatar_" + commodity.getSellerID() + ".jpg");
        TextView nickname = findViewById(R.id.nick_name);
        nickname.setText(sNickname);
        TextView id = findViewById(R.id.id);
        id.setText("ID:" + commodity.getSellerID());
        setToolBar();
        final RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(30, 1));
        adapter = new PersonalCommodityAdapter(mCommodityList, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton scrollTotop = findViewById(R.id.scroll_to_top);
        scrollTotop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0);
            }
        });

        refresh = findViewById(R.id.refresh);
        refresh.setColorSchemeResources(R.color.colorAccent);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCommodities();
            }
        });
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetHelper.getCommodity((IServerListener)currentActivity, commodityIndex, commodity.getSellerID());
            }
        }).start();
    }

    private void refreshCommodities() {
        resetCommodities();
        refresh.setRefreshing(false);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
    }

    private void setToolBar(){
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final CollapsingToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener(){
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                //appBarLayout.getTotalScrollRange()为滑动的最多范围maxRange
                //参数i为滑动的偏移值，为0 至 -maxRange
                int offset = Math.abs(i);
                //标题栏渐变
                toolbar.setBackgroundColor(changeAlpha(offset * 1.0f / appBarLayout.getTotalScrollRange()));
                //拉到底设置标题
                if(-i != appBarLayout.getTotalScrollRange()){
                    toolbarLayout.setTitle("");
                } else{
                    toolbarLayout.setTitle(sNickname + "的商品");
                }
            }
        });

    }

    //根据百分比改变颜色透明度
    public int changeAlpha(float fraction){
        int alpha = (int)(255 * fraction);
        return Color.argb(alpha, 10, 142, 233);
    }


    @Override
    public void onSuccess(NetMessage info) {
        if (info.getCommodityNum() == 0) {
            adapter.mLoadMore.setLoadFailed(false);
            adapter.mLoadMore.setLoadMoreEnabled(false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemInserted(commodityIndex);
                }
            });
        } else {
            List<Commodity> serverCommodity = info.getCommodityList();
            int commodityNum = serverCommodity.size();
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
//            if (commodityNum < 20) {
//                adapter.mLoadMore.setLoadMoreEnabled(false);
//            }
            adapter.mLoadMore.setLoadFailed(false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemInserted(commodityIndex);
                    commodityIndex += 20;
                }
            });
        }
    }

    @Override
    public void onFailure(final String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();
                adapter.mLoadMore.setLoadFailed(true);
            }
        });
    }
}
