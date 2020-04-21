package com.example.fleamarket.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.fleamarket.User;
import com.example.fleamarket.net.Chat;

import java.util.ArrayList;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context mContext;
    private String dbName;

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
        dbName = name;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("233", "数据库创建成功，路径:" + db.getPath());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {}

    public void executeSql(SQLiteDatabase db, String sql) {
        db.execSQL(sql);
    }

    public void createTable(SQLiteDatabase db, String tableName) {
        db.execSQL("create table if not exists " + tableName + "("
                + "OthersID text not null,"
                + "OthersName text not null,"
                + "Me integer not null,"
                + "Content text not null,"
                + "SendTime text not null)");
        Log.i("233", "ceateTable: " + tableName);
    }

    public void deleteTable(SQLiteDatabase db, String tableName) {
        db.execSQL( "drop table if exists " + tableName);
        Log.i("233", "deleteTable: " + tableName);
    }

    public static void insertData(Context context, ArrayList<Chat> messageList) {
        Chat chat;
        DatabaseHelper dbHelper = new DatabaseHelper(context, "chat_" + User.getId(), null, 1);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values;
        String tableName;
        for (int i = 0; i < messageList.size(); i++) {
            chat = messageList.get(i);
            values = new ContentValues();
            // 组装第一组数据
            values.put("OthersID", chat.getSenderID());
            values.put("OthersName", chat.getSenderName());
            values.put("Me", 0);
            values.put("Content", chat.getContent());
            values.put("SendTime", chat.getSendTime());
            tableName = "chatto_" + chat.getSenderID();
            dbHelper.createTable(db, tableName); // 创建该表如果不存在
            db.insert(tableName, null, values);
            values.clear();
        }
    }

    public Cursor querySql(SQLiteDatabase db, String sql) {
        return db.rawQuery(sql, null);
    }

    public Cursor queryAllTables(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
        return cursor;
    }

}
