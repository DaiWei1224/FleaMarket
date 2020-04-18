package com.example.fleamarket.message.chat;

public class ChatMessage {
    private String content;
    private String sendTime;
    private String userID;
    private String userName;
    private boolean me; // 标识该条信息是自己发的还是对方发的

    public ChatMessage(String content, String userID, boolean me) {
        this.content = content;
        this.userID = userID;
        this.me = me;
    }

    public ChatMessage(String content, String sendTime, String userID, String userName) {
        this.content = content;
        this.sendTime = sendTime;
        this.userID = userID;
        this.userName = userName;
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

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isMe() {
        return me;
    }

    public void setMe(boolean me) {
        this.me = me;
    }

}
