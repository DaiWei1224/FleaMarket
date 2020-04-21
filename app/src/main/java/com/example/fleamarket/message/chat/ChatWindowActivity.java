package com.example.fleamarket.message.chat;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Looper;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fleamarket.R;
import com.example.fleamarket.User;
import com.example.fleamarket.database.DatabaseHelper;
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
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;
    private ContentValues values = new ContentValues();
    private String tableName;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        final Commodity commodity = (Commodity)getIntent().getExtras().getSerializable("commodity");
        title = findViewById(R.id.title);
        title.setText(commodity.getSellerName());
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        ListView listView = findViewById(R.id.chat_content);
        dbHelper = new DatabaseHelper(this, "chat_" + User.getId(), null, 1);
        db = dbHelper.getWritableDatabase();
        tableName = "chatto_" + commodity.getSellerID();
        dbHelper.createTable(db, tableName);
        dataList = new ArrayList<>();
        Cursor cursor = dbHelper.querySql(db, "select * from chatto_" + commodity.getSellerID() + " order by SendTime ASC"); //降序DESC
        if (cursor.moveToFirst()) {
            do {
                ChatMessage chatMessage = new ChatMessage(
                        cursor.getString(cursor.getColumnIndex("Content")),
                        cursor.getString(cursor.getColumnIndex("SendTime")),
                        cursor.getString(cursor.getColumnIndex("OthersID")),
                        cursor.getString(cursor.getColumnIndex("OthersName")),
                        cursor.getInt(cursor.getColumnIndex("Me")));
                dataList.add(chatMessage);
            } while (cursor.moveToNext());
        }
        cursor.close();
        adapter = new ChatAdapter(this, dataList);
        listView.setAdapter(adapter);
        listView.setSelection(dataList.size() - 1);
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
                    1));
            adapter.notifyDataSetChanged();
            // 将聊天记录保存到数据库
            values.put("OthersID", info.getReceiverID());
            values.put("OthersName", title.getText().toString());
            values.put("Me", 1);
            values.put("Content", info.getContent());
            values.put("SendTime", info.getSendTime());
            db.insert(tableName, null, values);
            values.clear();
        });
    }

    @Override
    public void onFailure(String info) {
        Looper.prepare();
        Toast.makeText(getBaseContext(), info, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
