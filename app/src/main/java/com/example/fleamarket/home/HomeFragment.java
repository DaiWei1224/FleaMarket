package com.example.fleamarket.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fleamarket.R;
import com.example.fleamarket.User;
import com.example.fleamarket.home.recyclerview.Commodity;
import com.example.fleamarket.home.recyclerview.CommodityAdapter;
import com.example.fleamarket.home.recyclerview.SpaceItemDecoration;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {
    private final String TAG = "Homefragment";
    private List<Commodity> mCommodityList = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.fragment_home, container, false);

        initCommodity();
        RecyclerView recyclerView = layout.findViewById(R.id.recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this.getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(25));
        CommodityAdapter adapter = new CommodityAdapter(mCommodityList, recyclerView);
        recyclerView.setAdapter(adapter);

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

        return layout;
    }

    private void initCommodity(){
        for(int i = 0 ;i < 10; i++){
            Commodity commodity = new Commodity(R.drawable.ic_launcher_background,
                    "蔡徐坤同款篮球",
                    "蔡徐坤同款篮球只要998！！只要998！！！蔡徐坤同款篮球只要998！！只要998！！！只要998！！！",
                    "¥998");
            mCommodityList.add(commodity);
            commodity = new Commodity(R.drawable.ic_launcher_background,
                    "蔡徐坤靓仔表情",
                    "蔡徐坤靓仔表情只要一分钱~",
                    "¥0.01");
            mCommodityList.add(commodity);
        }
    }
}
