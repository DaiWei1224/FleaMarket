package com.example.fleamarket.mine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fleamarket.MainActivity;
import com.example.fleamarket.R;
import com.example.fleamarket.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MineFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.fragment_mine, container, false);

        view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setMessage("确定退出当前账号？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "退出登录", Toast.LENGTH_SHORT).show();
                                User.setLogin(false);
                                MainActivity mainActivity = (MainActivity)getActivity();
                                // 删除本地的SharedPreferences
                                SharedPreferences sp = mainActivity.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sp.edit();
                                editor.clear();
                                editor.apply();
                                // 将“我的”页面切换为“登录”页面
                                FragmentManager fm = mainActivity.getSupportFragmentManager();
                                FragmentTransaction ft = fm.beginTransaction();
                                ft.hide(mainActivity.getFragmentByName("MineFragment"));
                                ft.show(mainActivity.getFragmentByName("LoginFragment"));
                                ft.commit();
                                // 清除消息页面……
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
        return view;
    }


}
