package com.example.fleamarket.message;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.fleamarket.MyApplication;
import com.example.fleamarket.R;
import com.example.fleamarket.User;
import com.example.fleamarket.database.DatabaseHelper;
import com.example.fleamarket.home.recyclerview.SpaceItemDecoration;
import com.example.fleamarket.message.chat.ChatMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MsgFragment extends Fragment {
    private MessageAdapter adapter;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View layout = inflater.inflate(R.layout.fragment_msg, container, false);
        RecyclerView recyclerView = layout.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration(2, 2));
        adapter = new MessageAdapter(this);
        recyclerView.setAdapter(adapter);
        updateMessageListView();
        MyApplication.setMsgFragment(this);
        return layout;
    }

    public MessageAdapter getAdapter() {
        return adapter;
    }

    public void updateMessageListView() {
        List<ChatMessage> messageList = adapter.getData();
        messageList.clear();
        DatabaseHelper dbHelper = new DatabaseHelper(getContext(), "chat_" + User.getId(), null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = dbHelper.queryAllTables(db);
        while (cursor.moveToNext()) {
            if (!cursor.getString(0).equals("android_metadata")) {
                Cursor cursor2 = dbHelper.querySql(db, "select * from " + cursor.getString(0) + " order by SendTime DESC");
                if (cursor2.moveToFirst()) {
                    ChatMessage chatMessage = new ChatMessage(
                            cursor2.getString(cursor2.getColumnIndex("Content")),
                            cursor2.getString(cursor2.getColumnIndex("SendTime")),
                            cursor2.getString(cursor2.getColumnIndex("OthersID")),
                            cursor2.getString(cursor2.getColumnIndex("OthersName")));
                    messageList.add(chatMessage);
                } else {
                    dbHelper.deleteTable(db, cursor.getString(0));
                }
                cursor2.close();
            }
        }
        cursor.close();
        sortByTime(messageList);
        adapter.notifyDataSetChanged();
    }

    private void sortByTime(List<ChatMessage> messageList) {
        for (int i = messageList.size() - 1; i >= 0 ; i--) {
            for (int j = 0; j < i; j ++) {
                if (compareTime(messageList.get(j).getSendTime(), messageList.get(j + 1).getSendTime()) == -1) {
                    ChatMessage temp = messageList.get(j);
                    messageList.set(j, messageList.get(j + 1));
                    messageList.set(j + 1, temp);
                }
            }
        }
    }

    private int compareTime(String time1, String time2) {
        if (dateToStamp(time1) > dateToStamp(time2)) {
            return 1;
        } else {
            return -1;
        }
    }

    // 将时间转换为时间戳
    private long dateToStamp(String time) {
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
