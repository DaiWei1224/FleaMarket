package com.example.fleamarket.message.chat;

import com.example.fleamarket.net.Chat;

public interface IChatListener {
    public void onSuccess(Chat info);
    public void onFailure(String info);
}
