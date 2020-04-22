package com.example.fleamarket;

import android.app.Application;
import android.content.Context;

import com.example.fleamarket.message.MsgFragment;

public class MyApplication extends Application {

    private static Context context;
    private static MsgFragment mMsgFragment = null;
    private static String chatting = "no chat";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }

    public static MsgFragment getMsgFragment() {
        return mMsgFragment;
    }

    public static void setMsgFragment(MsgFragment msgFragment) {
        mMsgFragment = msgFragment;
    }

    public static String getChatting() {
        return chatting;
    }

    public static void setChatting(String chatting) {
        MyApplication.chatting = chatting;
    }

}
