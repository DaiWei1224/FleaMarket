package com.example.fleamarket.utils;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyUtil {

    // 隐藏软键盘
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
    }

    // 弹出软键盘
    public static void showKeyboard(Activity activity, EditText editText) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(editText, 0);
    }

    // 软键盘在窗口上已经显示则隐藏，反之则显示
    public static void showOrHideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    // 根据当前时间确定时间的显示格式
    public static String handleDate(String time) {
        try {
            long oldTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time).getTime();
            long nowTime = new Date().getTime();
            long day = (nowTime - oldTime) / (24 * 60 * 60 * 1000);
            if (day < 1) { // 今天
                if (oldTime % (24 * 60 * 60 * 1000) > nowTime % (24 * 60 * 60 * 1000)) {
                    return "昨天 " + time.substring(11, 16);
                }
                return time.substring(11, 16);
            } else if (day == 1) { // 昨天
                if (oldTime % (24 * 60 * 60 * 1000) > nowTime % (24 * 60 * 60 * 1000)) {
                    return "前天 " + time.substring(11, 16);
                }
                return "昨天 " + time.substring(11, 16);
            } else if (day == 2 && (oldTime % (24 * 60 * 60 * 1000) <= nowTime % (24 * 60 * 60 * 1000))) {
                return "前天 " + time.substring(11, 16);
            } else {
                if((nowTime - oldTime) / 365.0 < 24 * 60 * 60 * 1000) { // 今年
                    return time.substring(5, 16);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time.substring(2, 16);
    }

}
