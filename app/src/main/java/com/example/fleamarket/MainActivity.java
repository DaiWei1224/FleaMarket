package com.example.fleamarket;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fleamarket.home.HomeFragment;
import com.example.fleamarket.login.LoginFragment;
import com.example.fleamarket.message.MsgFragment;
import com.example.fleamarket.mine.MineFragment;
import com.example.fleamarket.net.NetHelper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    public TextView title;

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

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        title = findViewById(R.id.title);

        btnHome = findViewById(R.id.page_home);
        btnMsg = findViewById(R.id.page_msg);
        btnMine = findViewById(R.id.page_mine);

        initialClickListener();
        initialFragmentTransaction();
        initialButtonDrawable();
        initialUserInfo();
        initialServerSettings();
    }

    @Override
    public void onClick(View v) {
        int buttonID = v.getId();
        updateButtonStyle(buttonID);
        switch (buttonID) {
            case R.id.page_home:
                loadFragment(fHome);
                title.setText("在售商品");
                break;
            case R.id.page_msg:
                loadFragment(fMsg);
                title.setText("消息箱");
                break;
            case R.id.page_mine:
                if(User.isLogin()){
                    fMine.displayCacheSize();
                    loadFragment(fMine);
                    title.setText("个人中心");
                }else{
                    loadFragment(fLogin);
                    title.setText("登录");
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
        User.setPassword(sp.getString("password", null));
        User.setNickname(sp.getString("nickname", null));
    }

    public void initialServerSettings(){
        SharedPreferences sp = getSharedPreferences("ServerSettings", Context.MODE_PRIVATE);
        NetHelper.server_ip = sp.getString("ip", "192.168.0.103");
        NetHelper.server_port = sp.getInt("port", 1224);
        NetHelper.chat_port = sp.getInt("chat_port", 1225);
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

    private void showServerSettingDialog(){
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.server_setting, null);
        final EditText server_ip = view.findViewById(R.id.ip);
        final EditText server_port = view.findViewById(R.id.port);
        final EditText chat_port = view.findViewById(R.id.chat_port);
        server_ip.setText(NetHelper.server_ip);
        server_ip.setHint(NetHelper.server_ip);
        server_port.setText(NetHelper.server_port + "");
        server_port.setHint(NetHelper.server_port + "");
        chat_port.setText(NetHelper.chat_port + "");
        chat_port.setHint(NetHelper.chat_port + "");
        new AlertDialog.Builder(this).setView(view).setTitle("服务器设置")
                .setPositiveButton("确认", (dialog, which) -> {
                        SharedPreferences sp = getSharedPreferences("ServerSettings", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("ip", server_ip.getText().toString());
                        editor.putInt("port", Integer.parseInt(server_port.getText().toString()));
                        editor.putInt("chat_port", Integer.parseInt(chat_port.getText().toString()));
                        editor.apply();
                        NetHelper.server_ip = sp.getString("ip", "192.168.0.103");
                        NetHelper.server_port = sp.getInt("port", 1224);
                        NetHelper.chat_port = sp.getInt("chat_port", 1225);
                    }).setNegativeButton("取消", (dialog, which) -> dialog.dismiss()).create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.server_setting:
                showServerSettingDialog();
                break;
                default:
                    break;
        }
        return true;
    }

}
