package com.example.graduationproject.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.graduationproject.R;
import com.example.graduationproject.home.recyclerview.Commodity;
import com.example.graduationproject.home.recyclerview.CommodityAdapter;
import com.example.graduationproject.home.recyclerview.SpaceItemDecoration;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {
    private List<Commodity> mCommodityList = new ArrayList<>();

    @Nullable
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
        return layout;
    }

    private void initCommodity(){
        for(int i = 0 ;i < 10; i++){
            Commodity commodity = new Commodity(R.mipmap.icon_2,
                    "求德玛得只要998！！只要998！！！求德玛得只要998！！只要998！！！求德玛得只要998！！只要998！！！",
                    "¥998");
            mCommodityList.add(commodity);
            commodity = new Commodity(R.mipmap.icon_2,
                    "蔡徐坤靓仔表情只要一分钱！！！！！！！！！！！！！",
                    "¥0.01");
            mCommodityList.add(commodity);
        }
    }
}
