package com.example.fleamarket.message.chat;

public class ChatMessage {
    private String content;
    private String sendTime;
    private String userID;
    private boolean me; // 标识该条信息是自己发的还是对方发的

    public ChatMessage(String content, boolean me, String userID) {
        this.content = content;
        this.me = me;
        this.userID = userID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public boolean isMe() {
        return me;
    }

    public void setMe(boolean me) {
        this.me = me;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

}
