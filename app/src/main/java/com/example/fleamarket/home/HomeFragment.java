package com.example.fleamarket.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fleamarket.R;
import com.example.fleamarket.User;
import com.example.fleamarket.home.recyclerview.HomeCommodityAdapter;
import com.example.fleamarket.home.recyclerview.SpaceItemDecoration;
import com.example.fleamarket.net.Commodity;
import com.example.fleamarket.net.IServerListener;
import com.example.fleamarket.net.NetHelper;
import com.example.fleamarket.net.NetMessage;
import com.example.fleamarket.utils.PictureUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class HomeFragment extends Fragment implements IServerListener {
    private HomeFragment currentFragment;
    public List<Commodity> mCommodityList = new ArrayList<>();
    public int commodityIndex = 0;
    private SwipeRefreshLayout refresh;
    private HomeCommodityAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.fragment_home, container, false);
        currentFragment = this;

        final RecyclerView recyclerView = layout.findViewById(R.id.recycler_view);
//        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        // 解决item跳动
//        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
//        // 防止item交换位置
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                layoutManager.invalidateSpanAssignments(); // 防止第一行到顶部有空白区域
//            }
//        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(15, 0));
        adapter = new HomeCommodityAdapter(mCommodityList, this);
        recyclerView.setAdapter(adapter);

        FloatingActionButton scrollToTop = layout.findViewById(R.id.scroll_to_top);
        scrollToTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.smoothScrollToPosition(0); // 平滑滚动
//                recyclerView.scrollToPosition(0); // 非平滑滚动
            }
        });
        FloatingActionButton postButton = layout.findViewById(R.id.post_button);
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (User.isLogin()) {
                    Intent intent = new Intent(getContext(), PostActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getContext(), "请先登录", Toast.LENGTH_SHORT).show();
                }
            }
        });

        refresh = layout.findViewById(R.id.refresh);
        refresh.setColorSchemeResources(R.color.colorAccent);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshCommodities();
            }
        });

        return layout;
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
                NetHelper.getCommodity(currentFragment, commodityIndex, null);
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

    @Override
    public void onSuccess(NetMessage info) {
        if (info.getCommodityNum() == 0) {
            adapter.mLoadMore.setLoadFailed(false);
            adapter.mLoadMore.setLoadMoreEnabled(false);
            getActivity().runOnUiThread(new Runnable() {
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
                            getActivity().getExternalCacheDir().getAbsolutePath() +
                                    "/commodity/" + commodity.getCommodityID() + ".jpg");
                }
                // 保存对应用户头像到本地
                PictureUtils.saveImageFromByte(commodity.getAvatar().getData(),
                        getActivity().getExternalCacheDir().getAbsolutePath() +
                                "/avatar/avatar_" + commodity.getSellerID() + ".jpg");
            }
//            if (commodityNum < 20) {
//                adapter.mLoadMore.setLoadMoreEnabled(false);
//            }
            adapter.mLoadMore.setLoadFailed(false);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyItemInserted(commodityIndex);
                    commodityIndex += 20;
//                    adapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onFailure(final String info) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show();
                adapter.mLoadMore.setLoadFailed(true);
            }
        });
    }
}
