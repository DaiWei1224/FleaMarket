package com.example.fleamarket;

public class User {
    public static String server_ip = "192.168.0.104";
    public static int server_port = 1224;
    private static String id;               // 账号
    private static String password;         // 密码
    private static String nickname;         // 昵称
    private static boolean login = false;   // 是否登录

    public static String getId() {
        return id;
    }

    public static void setId(String id) {
        User.id = id;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        User.password = password;
    }

    public static String getNickname() {
        return nickname;
    }

    public static void setNickname(String nickname) {
        User.nickname = nickname;
    }

    public static boolean isLogin() {
        return login;
    }

    public static void setLogin(boolean login) {
        User.login = login;
    }

}
