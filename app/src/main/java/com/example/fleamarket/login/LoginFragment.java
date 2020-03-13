package com.example.fleamarket.login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fleamarket.MainActivity;
import com.example.fleamarket.utils.MyUtil;
import com.example.fleamarket.R;
import com.example.fleamarket.User;
import com.example.fleamarket.net.ILoginListener;
import com.example.fleamarket.net.SocketLogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class LoginFragment extends Fragment implements View.OnClickListener, ILoginListener {
    EditText idText;
    EditText pwText;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        idText = view.findViewById(R.id.id);
        pwText = view.findViewById(R.id.password);
        view.findViewById(R.id.login_button).setOnClickListener(this);
        view.findViewById(R.id.delete_id).setOnClickListener(this);
        view.findViewById(R.id.delete_pw).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_id:
                idText.setText("");
                break;
            case R.id.delete_pw:
                pwText.setText("");
                break;
            case R.id.login_button:{
                final String id = idText.getText().toString();
                final String pw = pwText.getText().toString();
                // 检验账号和密码是否为空
                if(id.length() == 0 || pw.length() == 0){
                    Toast.makeText(getContext(), "账号和密码不能为空", Toast.LENGTH_SHORT).show();
                }else{
                    boolean valid = true;
                    // 检验账号是否有效，账号为5位数字
                    if(id.length() != 5){
                        valid = false;
                    }else{
                        for(int i = 0; i < id.length(); i++){
                            if(id.charAt(i) < '0' || id.charAt(i) > '9'){
                                valid = false;
                                break;
                            }
                        }
                        if(valid){
                            // 检验密码是否有效，密码只能由数字和大小写字母组成
                            for(int i = 0; i < pw.length(); i++){
                                if(!((pw.charAt(i) >= '0' && pw.charAt(i) <= '9')||
                                        (pw.charAt(i) >= 'a' && pw.charAt(i) <= 'z')||
                                        (pw.charAt(i) >= 'A' && pw.charAt(i) <= 'Z'))){
                                    valid = false;
                                    break;
                                }
                            }
                        }
                    }
                    if(valid){
//                    Toast.makeText(getContext(), "正在登陆……", Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                SocketLogin.doLogin(LoginFragment.this, id, pw);
                            }
                        }).start();
                    }else{
                        Toast.makeText(getContext(), "账号或密码错误", Toast.LENGTH_SHORT).show();
                    }
                }
            } break;
            default:
                break;
        }
    }

    @Override
    public void onLoginSuccess() {
        Looper.prepare();
        Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
        User.setLogin(true);
        User.setId(idText.getText().toString());
        User.setPassword(pwText.getText().toString());
        // 从“登录”页面切换到“我的”页面
        MainActivity mainActivity = (MainActivity)getActivity();
        FragmentManager fm = mainActivity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.hide(this);
        ft.show(mainActivity.getFragmentByName("MineFragment"));
        ft.commit();
        // 隐藏软键盘
        MyUtil.hideKeyboard(mainActivity);
        // 使用SharedPreferences将用户信息存储在本地
        SharedPreferences sp= mainActivity.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("login", true);
        editor.putString("id", User.getId());
        editor.putString("pw", User.getPassword());
        editor.apply();
        Looper.loop();
    }

    @Override
    public void onLoginFailure() {
        Looper.prepare();
        Toast.makeText(getContext(), "账号或密码错误", Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
