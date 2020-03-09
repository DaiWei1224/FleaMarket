package com.example.fleamarket;

public class User {
    public static String server_ip = "192.168.0.103";
    public static int server_port = 1224;
    private static String id;               // 账号
    private static String password;         // 密码
    private static String nickname;         // 昵称
    private static String avatar;           // 头像
    private static String telephone;        // 电话
    private static String email;            // 电子邮箱
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

    public static String getAvatar() {
        return avatar;
    }

    public static void setAvatar(String avatar) {
        User.avatar = avatar;
    }

    public static String getTelephone() {
        return telephone;
    }

    public static void setTelephone(String telephone) {
        User.telephone = telephone;
    }

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        User.email = email;
    }

    public static boolean isLogin() {
        return login;
    }

    public static void setLogin(boolean login) {
        User.login = login;
    }

}
