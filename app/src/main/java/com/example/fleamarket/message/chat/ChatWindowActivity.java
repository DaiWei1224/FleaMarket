package com.example.fleamarket.message.chat;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.example.fleamarket.R;
import com.example.fleamarket.User;
import com.example.fleamarket.net.Commodity;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ChatWindowActivity extends AppCompatActivity {

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

        List<ChatMessage> dataList = new ArrayList<>();
        for(int i = 0; i < 10; i++) {
            ChatMessage chatMessage = new ChatMessage("床前明月光疑似地上霜举头望明月低头思故乡", true, User.getId());
            dataList.add(chatMessage);
            chatMessage = new ChatMessage("床前明月光", false, commodity.getSellerID());
            dataList.add(chatMessage);
            chatMessage = new ChatMessage("床前明月光疑似地上霜举头望明月低头思故乡\n床前明月光疑似地上霜举头望明月低头思故乡", false, commodity.getSellerID());
            dataList.add(chatMessage);
        }

        ChatAdapter adapter = new ChatAdapter(this, dataList);
        listView.setAdapter(adapter);

    }
}
