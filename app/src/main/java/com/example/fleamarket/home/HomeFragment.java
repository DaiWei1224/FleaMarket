package com.example.fleamarket.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fleamarket.R;
import com.example.fleamarket.User;
import com.example.fleamarket.home.recyclerview.CommodityAdapter;
import com.example.fleamarket.home.recyclerview.SpaceItemDecoration;
import com.example.fleamarket.net.Commodity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class HomeFragment extends Fragment {
    private final String TAG = "233";
    public List<Commodity> mCommodityList = new ArrayList<>();
    private SwipeRefreshLayout refresh;
    private CommodityAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = layout.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(25));
        adapter = new CommodityAdapter(mCommodityList, recyclerView);
        recyclerView.setAdapter(adapter);
//        resetCommodities();

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

    private void resetCommodities(){
        adapter.mLoadMore.setLoadMoreEnabled(true);
        mCommodityList.clear();
        Commodity commodity;
        for(int i = 0 ;i < 5; i++){
            commodity = new Commodity();
            commodity.setCommodityName("蔡徐坤同款篮球");
            commodity.setPrice("¥998");
            commodity.setCommodityDetail("蔡徐坤同款篮球只要998！！只要998！！！蔡徐坤同款篮球只要998！！只要998！！！只要998！！！");
            mCommodityList.add(commodity);
            commodity = new Commodity();
            commodity.setCommodityName("蔡徐坤靓仔表情");
            commodity.setPrice("¥0.01");
            commodity.setCommodityDetail("蔡徐坤靓仔表情只要一分钱~");
            mCommodityList.add(commodity);
        }
    }

    public static void addCommodities(List<Commodity> commodityList){
        Commodity commodity;
        for(int i = 0 ;i < 5; i++){
            commodity = new Commodity();
            commodity.setCommodityName("蔡徐坤同款篮球");
            commodity.setPrice("¥998");
            commodity.setCommodityDetail("蔡徐坤同款篮球只要998！！只要998！！！蔡徐坤同款篮球只要998！！只要998！！！只要998！！！");
            commodityList.add(commodity);
            commodity = new Commodity();
            commodity.setCommodityName("蔡徐坤靓仔表情");
            commodity.setPrice("¥0.01");
            commodity.setCommodityDetail("蔡徐坤靓仔表情只要一分钱~");
            commodityList.add(commodity);
        }
    }

    private void refreshCommodities() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        resetCommodities();
                        adapter.notifyDataSetChanged();
                        refresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

}
