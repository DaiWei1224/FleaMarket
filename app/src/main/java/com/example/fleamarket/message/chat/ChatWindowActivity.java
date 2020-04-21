package com.example.fleamarket.message.chat;

import android.os.Bundle;
import android.os.Looper;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fleamarket.R;
import com.example.fleamarket.User;
import com.example.fleamarket.net.Chat;
import com.example.fleamarket.net.Commodity;
import com.example.fleamarket.net.NetHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ChatWindowActivity extends AppCompatActivity implements IChatListener {
    private EditText input;
    private ChatAdapter adapter;
    private List<ChatMessage> dataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        final Commodity commodity = (Commodity)getIntent().getExtras().getSerializable("commodity");
        TextView title = findViewById(R.id.title);
        title.setText(commodity.getSellerName());
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ListView listView = findViewById(R.id.chat_content);

        dataList = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            ChatMessage chatMessage = new ChatMessage("床前明月光疑似地上霜举头望明月低头思故乡", User.getId(), true);
            dataList.add(chatMessage);
            chatMessage = new ChatMessage("床前明月光", commodity.getSellerID(), false);
            dataList.add(chatMessage);
            chatMessage = new ChatMessage("床前明月光疑似地上霜举头望明月低头思故乡\n床前明月光疑似地上霜举头望明月低头思故乡", commodity.getSellerID(), false);
            dataList.add(chatMessage);
        }

        adapter = new ChatAdapter(this, dataList);
        listView.setAdapter(adapter);

        input = findViewById(R.id.input);
        findViewById(R.id.send).setOnClickListener((v) -> {
            if (!input.getText().toString().equals("")) {
                new Thread(() -> {
                    Date sendTime = new Date();
                    String sendTimeString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(sendTime);
                    Chat chat = new Chat(User.getId(), User.getNickname(), commodity.getSellerID(), sendTimeString, input.getText().toString());
                    NetHelper.sendMessage(chat, this);
                }).start();
            }
        });

    }

    @Override
    public void onSuccess(Chat info) {
        runOnUiThread(() -> {
            input.setText("");
            dataList.add(new ChatMessage(
                    info.getContent(),
                    info.getSendTime(),
                    info.getSenderID(),
                    info.getSenderName(),
                    true));
            adapter.notifyDataSetChanged();
            // 将聊天记录保存到数据库

        });
    }

    @Override
    public void onFailure(String info) {
        Looper.prepare();
        Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
