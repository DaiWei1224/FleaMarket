package com.example.fleamarket.login;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Looper;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fleamarket.R;
import com.example.fleamarket.net.IServerListener;
import com.example.fleamarket.net.NetHelper;
import com.example.fleamarket.net.NetMessage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, IServerListener {
    EditText invitationCodeText;
    EditText pwText;
    EditText pwConfirmText;
    Button btnRegister;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        invitationCodeText = findViewById(R.id.invitation_code);
        pwText = findViewById(R.id.password);
        pwConfirmText = findViewById(R.id.confirm_password);
        findViewById(R.id.delete_ivcode).setOnClickListener(this);
        findViewById(R.id.delete_pw).setOnClickListener(this);
        findViewById(R.id.delete_pwcf).setOnClickListener(this);
        btnRegister = findViewById(R.id.register_button);
        btnRegister.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete_ivcode:
                invitationCodeText.setText("");
                break;
            case R.id.delete_pw:
                pwText.setText("");
                break;
            case R.id.delete_pwcf:
                pwConfirmText.setText("");
                break;
            case R.id.register_button:{
                final String invitationCode = invitationCodeText.getText().toString();
                final String pw = pwText.getText().toString();
                final String pwConfirm = pwConfirmText.getText().toString();
                // 检验邀请码和密码是否为空
                if(invitationCode.length() == 0 || pw.length() == 0 || pwConfirm.length() == 0){
                    Toast.makeText(this, "邀请码和密码不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    boolean valid = true;
                    // 检验邀请码是否有效，长度为6，由数字和大写字母组成
                    if(invitationCode.length() != 6){
                        valid = false;
                        Toast.makeText(this, "邀请码不存在或已被使用", Toast.LENGTH_SHORT).show();
                    }else{
                        for(int i = 0; i < invitationCode.length(); i++){
                            if(!((invitationCode.charAt(i) >= '0' && invitationCode.charAt(i) <= '9')||
                                    (invitationCode.charAt(i) >= 'A' && invitationCode.charAt(i) <= 'Z'))){
                                valid = false;
                                Toast.makeText(this, "邀请码不存在或已被使用", Toast.LENGTH_SHORT).show();
                                break;
                            }
                        }
                        if(valid) {
                            // 检验两次输入密码是否相同
                            if (!pw.equals(pwConfirm)) {
                                valid = false;
                                Toast.makeText(this, "两次输入密码不一致", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (valid) {
                            // 检验密码是否有效，密码只能由数字和大小写字母组成
                            for(int i = 0; i < pw.length(); i++){
                                if(!((pw.charAt(i) >= '0' && pw.charAt(i) <= '9')||
                                        (pw.charAt(i) >= 'a' && pw.charAt(i) <= 'z')||
                                        (pw.charAt(i) >= 'A' && pw.charAt(i) <= 'Z'))){
                                    valid = false;
                                    Toast.makeText(this, "密码只能由数字和大小写字母组成", Toast.LENGTH_SHORT).show();
                                    break;
                                }
                            }
                        }
                    }
                    if(valid){
//                    Toast.makeText(getContext(), "正在注册……", Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                NetHelper.requestRegister(RegisterActivity.this, invitationCode, pw);
                            }
                        }).start();
                        showWaitingDialog("正在注册");
                    }
                }
            } break;
                default:
        }
    }

    private void showWaitingDialog(String message) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true); // 是否形成一个加载动画，true表示不明确加载进度形成转圈动画，false表示明确加载进度
        progressDialog.setCancelable(false); // 点击返回键或者dialog四周是否关闭dialog，true表示可以关闭，false表示不可关闭
        progressDialog.show();
    }

    @Override
    public void onSuccess(final NetMessage info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialog.dismiss();
                String s = "注册成功！\n您的账号为" + info.getId();
                SpannableString ss = new SpannableString(s);
                //将获取的账号设置为蓝色
                ss.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary)),
                        11, 16, Spanned.SPAN_INCLUSIVE_INCLUSIVE);
                TextView successInfo = findViewById(R.id.register_success);
                successInfo.setText(ss);
                // 因此注册按钮，显示注册成功信息
                btnRegister.setVisibility(View.GONE);
                successInfo.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onFailure(String info) {
        Looper.prepare();
        progressDialog.dismiss();
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
