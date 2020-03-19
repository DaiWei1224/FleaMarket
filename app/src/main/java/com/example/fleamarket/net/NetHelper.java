package com.example.fleamarket.net;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class NetHelper {
    public static String server_ip = "192.168.0.104";
    public static int server_port = 1224;

    // 请求登录
    public static void requestLogin(IServerListener listener, String id, String pw){
        try {
            Socket socket = new Socket(server_ip, server_port);
            // 发送账号和密码信息
            ObjectOutputStream oos= new ObjectOutputStream(socket.getOutputStream());
            NetMessage message = new NetMessage();
            message.setType(MessageType.LOGIN);
            message.setId(id);
            message.setPw(pw);
            oos.writeObject(message);
            // 处理服务器的返回信息
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            NetMessage returnMessage = (NetMessage) ois.readObject();
            MessageType type = returnMessage.getType();
            if(type == MessageType.SUCCESS) {
                listener.onSuccess(returnMessage);
            } else{
                listener.onFailure();
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 请求注册
    public static void requestRegister(IServerListener listener, String ivCode, String pw){
        try {
            Socket socket = new Socket(server_ip, server_port);
            // 发送邀请码和密码信息
            ObjectOutputStream oos= new ObjectOutputStream(socket.getOutputStream());
            NetMessage message = new NetMessage();
            message.setType(MessageType.REGISTER);
            message.setId(ivCode);
            message.setPw(pw);
            oos.writeObject(message);
            // 处理服务器的返回信息
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            NetMessage returnMessage = (NetMessage) ois.readObject();
            MessageType type = returnMessage.getType();
            if(type == MessageType.SUCCESS) {
                listener.onSuccess(returnMessage);
            } else{
                listener.onFailure();
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
