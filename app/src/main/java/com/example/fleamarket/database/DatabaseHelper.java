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
            Log.i("233", "未读消息" + (i+1) + "：" + chat.getContent() + " 发送自："+chat.getSenderName()+" 时间："+chat.getSendTime());
        }
    }

    public Cursor querySql(SQLiteDatabase db, String sql) {
        return db.rawQuery(sql, null);
    }

    public Cursor queryAllTables(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select name from sqlite_master where type='table' order by name", null);
        return cursor;
    }

    //        // 查询所有表
//        Cursor cursor = dbHelper.queryAllTables(dbHelper.getWritableDatabase());
//        while (cursor.moveToNext()) {
//            if (!cursor.getString(0).equals("android_metadata")) {
//                Log.i("233", "table: " + cursor.getString(0) + "----------------");
//                // 查询数据表
//                Cursor cursor2 = dbHelper.querySql(dbHelper.getWritableDatabase(), "select * from "+cursor.getString(0)+" order by SendTime ASC"); //降序DESC
//                if (cursor2.moveToFirst()) {
//                    do {
//                        Log.i("233", "OthersID = " + cursor2.getString(cursor2.getColumnIndex("OthersID")));
//                        Log.i("233", "OthersName = " + cursor2.getString(cursor2.getColumnIndex("OthersName")));
//                        Log.i("233", "Me = " + cursor2.getInt(cursor2.getColumnIndex("Me")));
//                        Log.i("233", "Content = " + cursor2.getString(cursor2.getColumnIndex("Content")));
//                        Log.i("233", "SendTime = " + cursor2.getString(cursor2.getColumnIndex("SendTime")));
//                        Log.i("233", "  ");
//                    } while (cursor2.moveToNext());
//                }
//                cursor2.close();
//            }
//        }
//        cursor.close();

    // 查询所有表
    /*
    Cursor cursor = dbHelper.queryAllTables(dbHelper.getWritableDatabase());
            while (cursor.moveToNext()) {
                if (!cursor.getString(0).equals("android_metadata")) {
                    Log.i(TAG, "table: " + cursor.getString(0));
                }
            }
            cursor.close();
     */

    // 查询数据表
    /*
    Cursor cursor = dbHelper.querySql(dbHelper.getWritableDatabase(), "select * from chatto_66666 order by SendTime ASC"); //降序DESC
            if (cursor.moveToFirst()) {
                do {
                    Log.i(TAG, "OthersID = " + cursor.getString(cursor.getColumnIndex("OthersID")));
                    Log.i(TAG, "OthersName = " + cursor.getString(cursor.getColumnIndex("OthersName")));
                    Log.i(TAG, "Me = " + cursor.getInt(cursor.getColumnIndex("Me")));
                    Log.i(TAG, "Content = " + cursor.getString(cursor.getColumnIndex("Content")));
                    Log.i(TAG, "SendTime = " + cursor.getString(cursor.getColumnIndex("SendTime")));
                } while (cursor.moveToNext());
            }
            cursor.close();
     */

}
