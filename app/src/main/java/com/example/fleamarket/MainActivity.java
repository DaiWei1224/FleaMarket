package com.example.fleamarket;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnHome = findViewById(R.id.page_home);
        btnMsg = findViewById(R.id.page_msg);
        btnMine = findViewById(R.id.page_mine);

        initialClickListener();
        initialFragmentTransaction();
        initialUserInfo();
    }

    @Override
    public void onClick(View v) {
        int buttonID = v.getId();
        resetButtonColor();
        v.setBackgroundColor(Color.parseColor("#DCDCDC"));
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
                break;
        }
    }

    // 初始化所有按钮的事件监听
    public void initialClickListener(){
        btnHome.setOnClickListener(this);
        btnMsg.setOnClickListener(this);
        btnMine.setOnClickListener(this);
    }

    // 重置按钮颜色
    public void resetButtonColor(){
        btnHome.setBackgroundColor(Color.parseColor("#FFFFFF"));
        btnMsg.setBackgroundColor(Color.parseColor("#FFFFFF"));
        btnMine.setBackgroundColor(Color.parseColor("#FFFFFF"));
    }

    // 向FragmentTransaction中添加所有的Fragment
    public void initialFragmentTransaction(){
        ft = fm.beginTransaction();
        ft.add(R.id.fragment_container, fLogin);
        ft.add(R.id.fragment_container, fMine);
        ft.add(R.id.fragment_container, fMsg);
        ft.add(R.id.fragment_container, fHome);
        ft.commit();
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
        boolean login = sp.getBoolean("login", false);
        String id = sp.getString("id", null);
        String pw = sp.getString("pw", null);
        User.setLogin(login);
        User.setId(id);
        User.setPassword(pw);
    }

}
