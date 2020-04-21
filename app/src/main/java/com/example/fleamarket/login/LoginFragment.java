package com.example.fleamarket.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.example.fleamarket.MainActivity;
import com.example.fleamarket.R;
import com.example.fleamarket.User;
import com.example.fleamarket.database.DatabaseHelper;
import com.example.fleamarket.message.MsgFragment;
import com.example.fleamarket.mine.MineFragment;
import com.example.fleamarket.net.Chat;
import com.example.fleamarket.net.IServerListener;
import com.example.fleamarket.net.MessageType;
import com.example.fleamarket.net.NetHelper;
import com.example.fleamarket.net.NetMessage;
import com.example.fleamarket.utils.MyUtil;
import com.example.fleamarket.utils.PictureUtils;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class LoginFragment extends Fragment implements View.OnClickListener, IServerListener {
    EditText idText;
    EditText pwText;
    ProgressDialog progressDialog;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        idText = view.findViewById(R.id.id);
        pwText = view.findViewById(R.id.password);
        view.findViewById(R.id.login_button).setOnClickListener(this);
        view.findViewById(R.id.delete_id).setOnClickListener(this);
        view.findViewById(R.id.delete_pw).setOnClickListener(this);
        view.findViewById(R.id.register).setOnClickListener(this);
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
            case R.id.register:
                Intent intent = new Intent(getContext(), RegisterActivity.class);
                startActivity(intent);
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
                        new Thread(() -> NetHelper.requestLogin(LoginFragment.this, id, pw)).start();
                        showWaitingDialog("正在登陆");
                    }else{
                        Toast.makeText(getContext(), "账号或密码错误", Toast.LENGTH_SHORT).show();
                    }
                }
            } break;
            default:
                break;
        }
    }

    private void showWaitingDialog(String message) {
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true); // 是否形成一个加载动画，true表示不明确加载进度形成转圈动画，false表示明确加载进度
        progressDialog.setCancelable(false); // 点击返回键或者dialog四周是否关闭dialog，true表示可以关闭，false表示不可关闭
        progressDialog.show();
    }

    @Override
    public void onSuccess(NetMessage info) {
        Looper.prepare();
        final MainActivity mainActivity = (MainActivity)getActivity();
        MsgFragment msgFragment = (MsgFragment)mainActivity.getFragmentByName("MsgFragment");
        if (info.getType() == MessageType.GET_UNREAD_MESSAGE) {
            ArrayList<Chat> messageList = (ArrayList<Chat>)info.getMessageList();
            DatabaseHelper.insertData(getContext(), messageList);
            msgFragment.updateMessageListView();
        } else {
            progressDialog.dismiss();
            // 隐藏软键盘
            MyUtil.hideKeyboard(mainActivity);
            User.setLogin(true);
            User.setId(info.getId());
            User.setPassword(info.getPw());
            User.setNickname(info.getNickname());
            // 将头像保存到本地
            if (info.getAvatar() != null) {
                PictureUtils.saveImageFromByte(info.getAvatar().getData(),
                        getActivity().getExternalCacheDir().getAbsolutePath() +
                                "/avatar/avatar_" + User.getId() + ".jpg");
            }
            // 从“登录”页面切换到“我的”页面
            FragmentManager fm = mainActivity.getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.hide(this);
            MineFragment mineFragment = (MineFragment)mainActivity.getFragmentByName("MineFragment");
            mineFragment.updateUserInfo();
            ft.show(mineFragment);
            ft.commit();
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.title.setText("个人中心");
                }
            });
            // 使用SharedPreferences将用户信息存储在本地
            SharedPreferences sp= mainActivity.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putBoolean("login", true);
            editor.putString("id", User.getId());
            editor.putString("password", User.getPassword());
            editor.putString("nickname", User.getNickname());
            editor.apply();
            // 创建本地聊天记录数据库，存在则不会创建
            new DatabaseHelper(getContext(), "chat_" + User.getId(), null, 1).getWritableDatabase();
            // 开启线程监听聊天端口发来的消息
            new Thread(() -> {
                NetHelper.createChatSocket();
                NetHelper.sendKey();
                // 从服务器拉取未读消息
                NetHelper.getUnreadMessage(LoginFragment.this);
            }).start();
            msgFragment.updateMessageListView();
        }
        Looper.loop();
    }

    @Override
    public void onFailure(String info) {
        Looper.prepare();
        progressDialog.dismiss();
        Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
