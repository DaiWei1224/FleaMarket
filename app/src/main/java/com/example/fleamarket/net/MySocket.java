package com.example.fleamarket.net;

import com.example.fleamarket.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.Socket;

public class MySocket {

    // 请求登录
    public static void requestLogin(IServerListener listener, String id, String pw){
        try {
            Socket socket = new Socket(User.server_ip, User.server_port);
            // 发送账号和密码信息
            ObjectOutputStream oos= new ObjectOutputStream(socket.getOutputStream());
            NetMessage message = new NetMessage();
            message.setType(MessageType.LOGIN);
            message.setId(id);
            message.setPw(pw);
            oos.writeObject(message);
            // 处理服务器的返回信息
            Reader reader = new InputStreamReader(socket.getInputStream()/*, Charset.forName("utf-8")*/);
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();
            if(line.equals("login success")) {
                listener.onSuccess(null);
            } else{
                listener.onFailure(null);
            }
            // 关闭socket连接
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 请求注册
    public static void requestRegister(IServerListener listener, String ivCode, String pw){
        try {
            Socket socket = new Socket(User.server_ip, User.server_port);
            // 发送邀请码和密码信息
            ObjectOutputStream oos= new ObjectOutputStream(socket.getOutputStream());
            NetMessage message = new NetMessage();
            message.setType(MessageType.REGISTER);
            message.setId(ivCode);
            message.setPw(pw);
            oos.writeObject(message);
            // 处理服务器的返回信息
            Reader reader = new InputStreamReader(socket.getInputStream()/*, Charset.forName("utf-8")*/);
            BufferedReader br = new BufferedReader(reader);
            String line = br.readLine();
            if(line.equals("register failure")) {
                listener.onFailure(null);
            } else{
                listener.onSuccess(line);
            }
            // 关闭socket连接
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
