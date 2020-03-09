package com.example.fleamarket.net;

import com.example.fleamarket.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.net.Socket;

public class SocketLogin {

    public static void doLogin(ILoginListener listener, String id, String pw){
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
                listener.onLoginSuccess();
            } else{
                listener.onLoginFailure();
            }
            // 关闭socket连接
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
