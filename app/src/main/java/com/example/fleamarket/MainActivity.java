package com.example.fleamarket;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.example.fleamarket.home.HomeFragment;
import com.example.fleamarket.login.LoginFragment;
import com.example.fleamarket.message.MsgFragment;
import com.example.fleamarket.mine.MineFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    FragmentManager fm = getSupportFragmentManager();
    FragmentTransaction ft;

    HomeFragment fHome = new HomeFragment();
    MsgFragment fMsg = new MsgFragment();
    MineFragment fMine = new MineFragment();
    LoginFragment fLogin = new LoginFragment();

    Button btnHome;
    Button btnMsg;
    Button btnMine;

    Drawable btnHomeSelect;
    Drawable btnHomeNormal;
    Drawable btnMsgSelect;
    Drawable btnMsgNormal;
    Drawable btnMineSelect;
    Drawable btnMineNormal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnHome = findViewById(R.id.page_home);
        btnMsg = findViewById(R.id.page_msg);
        btnMine = findViewById(R.id.page_mine);

        initialClickListener();
        initialFragmentTransaction();
        initialButtonDrawable();
        initialUserInfo();
    }

    @Override
    public void onClick(View v) {
        int buttonID = v.getId();
        updateButtonStyle(buttonID);
        switch (buttonID) {
            case R.id.page_home:
                loadFragment(fHome);
                break;
            case R.id.page_msg:
                loadFragment(fMsg);
                break;
            case R.id.page_mine:
                if(User.isLogin()){
                    loadFragment(fMine);
                }else{
                    loadFragment(fLogin);
                }
                break;
            default:
        }
    }

    // 初始化所有按钮的事件监听
    public void initialClickListener(){
        btnHome.setOnClickListener(this);
        btnMsg.setOnClickListener(this);
        btnMine.setOnClickListener(this);
    }

    // 向FragmentTransaction中添加所有的Fragment
    public void initialFragmentTransaction(){
        ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, fLogin);
        ft.add(R.id.fragment_container, fMine);
        ft.add(R.id.fragment_container, fMsg);
        ft.add(R.id.fragment_container, fHome);
        ft.commit();
        hideFragments(ft);
        ft.show(fHome);
    }

    // 载入Fragment
    public void loadFragment(@NonNull Fragment fragment){
        ft = fm.beginTransaction();
        hideFragments(ft);
        ft.show(fragment);
        ft.commit();
    }

    // 隐藏所有Fragment
    private void hideFragments(FragmentTransaction ft) {
        if (fHome != null) ft.hide(fHome);
        if (fMsg != null) ft.hide(fMsg);
        if (fMine != null) ft.hide(fMine);
        if (fLogin != null) ft.hide(fLogin);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (isTaskRoot()) {
                // 程序退到后台但不销毁
                moveTaskToBack(false);
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    public Fragment getFragmentByName(String f){
        switch (f){
            case "HomeFragment":
                return fHome;
            case "MsgFragment":
                return fMsg;
            case "MineFragment":
                return fMine;
            case "LoginFragment":
                return fLogin;
            default:
                return null;
        }
    }

    public void initialUserInfo(){
        SharedPreferences sp = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        User.setLogin(sp.getBoolean("login", false));
        User.setId(sp.getString("id", null));
        User.setNickname(sp.getString("nickname", null));
    }

    // 初始化按钮图片资源
    public void initialButtonDrawable(){
        btnHomeSelect = getResources().getDrawable(R.drawable.button_home_selected);
        btnHomeSelect.setBounds(0, 0, btnHomeSelect.getMinimumWidth(), btnHomeSelect.getMinimumHeight());
        btnHomeNormal = getResources().getDrawable(R.drawable.button_home);
        btnHomeNormal.setBounds(0, 0, btnHomeSelect.getMinimumWidth(), btnHomeSelect.getMinimumHeight());
        btnMsgSelect = getResources().getDrawable(R.drawable.button_message_selected);
        btnMsgSelect.setBounds(0, 0, btnHomeSelect.getMinimumWidth(), btnHomeSelect.getMinimumHeight());
        btnMsgNormal = getResources().getDrawable(R.drawable.button_message);
        btnMsgNormal.setBounds(0, 0, btnHomeSelect.getMinimumWidth(), btnHomeSelect.getMinimumHeight());
        btnMineSelect = getResources().getDrawable(R.drawable.button_mine_selected);
        btnMineSelect.setBounds(0, 0, btnHomeSelect.getMinimumWidth(), btnHomeSelect.getMinimumHeight());
        btnMineNormal = getResources().getDrawable(R.drawable.button_mine);
        btnMineNormal.setBounds(0, 0, btnHomeSelect.getMinimumWidth(), btnHomeSelect.getMinimumHeight());
    }

    // 更新按钮样式
    public void updateButtonStyle(int buttonID){
        switch (buttonID) {
            case R.id.page_home:
                btnHome.setCompoundDrawables(null, btnHomeSelect, null, null);
                btnHome.setTextColor(Color.WHITE);
                btnHome.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                btnMsg.setCompoundDrawables(null, btnMsgNormal, null, null);
                btnMsg.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnMsg.setBackgroundColor(Color.WHITE);

                btnMine.setCompoundDrawables(null, btnMineNormal, null, null);
                btnMine.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnMine.setBackgroundColor(Color.WHITE);
                break;
            case R.id.page_msg:
                btnMsg.setCompoundDrawables(null, btnMsgSelect, null, null);
                btnMsg.setTextColor(Color.WHITE);
                btnMsg.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                btnHome.setCompoundDrawables(null, btnHomeNormal, null, null);
                btnHome.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnHome.setBackgroundColor(Color.WHITE);

                btnMine.setCompoundDrawables(null, btnMineNormal, null, null);
                btnMine.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnMine.setBackgroundColor(Color.WHITE);
                break;
            case R.id.page_mine:
                btnMine.setCompoundDrawables(null, btnMineSelect, null, null);
                btnMine.setTextColor(Color.WHITE);
                btnMine.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                btnHome.setCompoundDrawables(null, btnHomeNormal, null, null);
                btnHome.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnHome.setBackgroundColor(Color.WHITE);

                btnMsg.setCompoundDrawables(null, btnMsgNormal, null, null);
                btnMsg.setTextColor(getResources().getColor(R.color.colorPrimary));
                btnMsg.setBackgroundColor(Color.WHITE);
                break;
            default:
        }
    }

}
