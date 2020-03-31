package com.example.fleamarket.net;

import android.app.Activity;

import com.example.fleamarket.User;
import com.example.fleamarket.utils.PictureUtils;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class NetHelper {
    public static String server_ip = "192.168.0.103";
    public static int server_port = 1224;
    private static final int CONNECT_TIMEOUT = 10000; // 连接请求超时时间
    private static final int READ_TIMEOUT = 10000; // 读操作超时时间
    private static final String CONNECT_SERVER_FAILED = "连接服务器失败";

    private static Socket createConnection() {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(server_ip, server_port), CONNECT_TIMEOUT);
            socket.setSoTimeout(READ_TIMEOUT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }

    // 请求登录
    public static void requestLogin(IServerListener listener, String id, String pw){
        try {
//            Socket socket = new Socket(server_ip, server_port);
            Socket socket = createConnection();
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
                listener.onFailure("账号或密码错误");
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailure(CONNECT_SERVER_FAILED);
        }
    }

    // 请求注册
    public static void requestRegister(IServerListener listener, String ivCode, String pw){
        try {
            Socket socket = createConnection();
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
                listener.onFailure("邀请码不存在或已被使用");
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailure(CONNECT_SERVER_FAILED);
        }
    }

    // 修改昵称
    public static void changeNickname(IServerListener listener, String nickname){
        try {
            Socket socket = createConnection();
            ObjectOutputStream oos= new ObjectOutputStream(socket.getOutputStream());
            NetMessage message = new NetMessage();
            message.setType(MessageType.CHANGE_NICKNAME);
            message.setNickname(nickname);
            message.setId(User.getId());
            oos.writeObject(message);
            // 处理服务器的返回信息
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            NetMessage returnMessage = (NetMessage) ois.readObject();
            MessageType type = returnMessage.getType();
            if(type == MessageType.FAILURE) {
                listener.onFailure("昵称修改失败");
            } else{
                listener.onSuccess(returnMessage);
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailure(CONNECT_SERVER_FAILED);
        }
    }

    // 修改密码
    public static void changePassword(IServerListener listener, String password){
        try {
            Socket socket = createConnection();
            ObjectOutputStream oos= new ObjectOutputStream(socket.getOutputStream());
            NetMessage message = new NetMessage();
            message.setType(MessageType.CHANGE_PASSWORD);
            message.setPw(password);
            message.setId(User.getId());
            oos.writeObject(message);
            // 处理服务器的返回信息
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            NetMessage returnMessage = (NetMessage) ois.readObject();
            MessageType type = returnMessage.getType();
            if(type == MessageType.FAILURE) {
                listener.onFailure("密码修改失败");
            } else{
                listener.onSuccess(returnMessage);
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailure(CONNECT_SERVER_FAILED);
        }
    }

    // 将头像存储到服务器
    public static void saveAvatar(IServerListener listener, Activity activity){
        try {
            Socket socket = createConnection();
            ObjectOutputStream oos= new ObjectOutputStream(socket.getOutputStream());
            NetMessage message = new NetMessage();
            message.setType(MessageType.SAVE_AVATAR);
            message.setId(User.getId());
            NetImage netImage = new NetImage();
//            netImage.setData(PictureUtils.loadImageFromFile(
//                    new File(activity.getExternalCacheDir(), "avatar_" + User.getId() + ".jpg")));
            netImage.setData(PictureUtils.loadImageFromFile(
                    new File(activity.getExternalCacheDir().getAbsolutePath() +
                            "/temporary/avatar.jpg")));
            message.setAvatar(netImage);
            oos.writeObject(message);
            // 处理服务器的返回信息
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            NetMessage returnMessage = (NetMessage) ois.readObject();
            MessageType type = returnMessage.getType();
            if(type == MessageType.SAVE_AVATAR) {
                listener.onSuccess(returnMessage);
            } else{
                listener.onFailure("头像设置失败");
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailure(CONNECT_SERVER_FAILED);
        }
    }

    // 请求发布商品
    public static void requestPostCommodity(IServerListener listener, Commodity commodity){
        try {
            Socket socket = createConnection();
            ObjectOutputStream oos= new ObjectOutputStream(socket.getOutputStream());
            NetMessage message = new NetMessage();
            message.setType(MessageType.POST_COMMODITY);
            message.setCommodity(commodity);
            oos.writeObject(message);
            // 处理服务器的返回信息
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            NetMessage returnMessage = (NetMessage) ois.readObject();
            MessageType type = returnMessage.getType();
            if(type == MessageType.SUCCESS) {
                listener.onSuccess(returnMessage);
            } else{
                listener.onFailure("商品发布失败");
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailure(CONNECT_SERVER_FAILED);
        }
    }

    // 请求服务器下发商品
    public static void getCommodity(IServerListener listener, int index, String id){
        try {
            Socket socket = createConnection();
            ObjectOutputStream oos= new ObjectOutputStream(socket.getOutputStream());
            NetMessage message = new NetMessage();
            message.setType(MessageType.GET_COMMODITY);
            if (id != null) {
                message.setId(id);
            }
            message.setCommodityNum(index);
            oos.writeObject(message);
            // 处理服务器的返回信息
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            NetMessage returnMessage = (NetMessage) ois.readObject();
            MessageType type = returnMessage.getType();
            if(type == MessageType.SUCCESS) {
                listener.onSuccess(returnMessage);
            } else{
                listener.onFailure("加载商品失败");
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailure(CONNECT_SERVER_FAILED);
        }
    }

    // 编辑商品
    public static void editCommodity(IServerListener listener, Commodity commodity){
        try {
            Socket socket = createConnection();
            ObjectOutputStream oos= new ObjectOutputStream(socket.getOutputStream());
            NetMessage message = new NetMessage();
            message.setType(MessageType.EDIT_COMMODITY);
            message.setCommodity(commodity);
            oos.writeObject(message);
            // 处理服务器的返回信息
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            NetMessage returnMessage = (NetMessage) ois.readObject();
            MessageType type = returnMessage.getType();
            if(type == MessageType.EDIT_COMMODITY) {
                listener.onSuccess(returnMessage);
            } else{
                listener.onFailure("编辑商品失败");
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailure(CONNECT_SERVER_FAILED);
        }
    }

    // 删除商品
    public static void deleteCommodity(IServerListener listener, String commodityID){
        try {
            Socket socket = createConnection();
            ObjectOutputStream oos= new ObjectOutputStream(socket.getOutputStream());
            NetMessage message = new NetMessage();
            message.setType(MessageType.DELETE_COMMODITY);
            message.setId(commodityID);
            oos.writeObject(message);
            // 处理服务器的返回信息
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            NetMessage returnMessage = (NetMessage) ois.readObject();
            MessageType type = returnMessage.getType();
            if(type == MessageType.DELETE_COMMODITY) {
                listener.onSuccess(returnMessage);
            } else{
                listener.onFailure("删除商品失败");
            }
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            listener.onFailure(CONNECT_SERVER_FAILED);
        }
    }
}
