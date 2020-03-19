package com.example.fleamarket.net;

public interface IServerListener {
    public void onSuccess(NetMessage info);
    public void onFailure();
}
