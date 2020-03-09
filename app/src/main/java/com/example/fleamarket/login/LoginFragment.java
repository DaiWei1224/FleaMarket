package com.example.fleamarket.login;

import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fleamarket.R;
import com.example.fleamarket.net.ILoginListener;
import com.example.fleamarket.net.SocketLogin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment implements ILoginListener {
    EditText idText;
    EditText pwText;
    Button loginButton;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        idText = view.findViewById(R.id.id);
        pwText = view.findViewById(R.id.password);
        loginButton = view.findViewById(R.id.login_button);
        loginButton.setOnClickListener(new loginListener());

        return view;
    }

    class loginListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            final String id = idText.getText().toString();
            final String pw = pwText.getText().toString();
            Log.i("233", "id = " + id);
            Log.i("233", "pw = " + pw);
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
        }
    }

    @Override
    public void onLoginSuccess() {
        Looper.prepare();
        Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
        Looper.loop();
        // 登陆成功后……
    }

    @Override
    public void onLoginFailure() {
        Looper.prepare();
        Toast.makeText(getContext(), "账号或密码错误", Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
