package com.example.fleamarket.mine;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fleamarket.MainActivity;
import com.example.fleamarket.R;
import com.example.fleamarket.User;
import com.example.fleamarket.net.Commodity;
import com.example.fleamarket.net.IServerListener;
import com.example.fleamarket.net.MessageType;
import com.example.fleamarket.net.NetHelper;
import com.example.fleamarket.net.NetMessage;
import com.example.fleamarket.utils.MyUtil;
import com.example.fleamarket.utils.PictureUtils;

import java.io.File;
import java.io.IOException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MineFragment extends Fragment implements View.OnClickListener, IServerListener {
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int CROP_PHOTO = 3;
    private ImageView avatar;
    private TextView nickname;
    private TextView id;
    private ImageView change_nickname;
    private ImageView check_nickname;
    private EditText edit_nickname;
    private Uri imageUri;
    private Activity currentActivity;
    private Fragment currentFragment;
    private ProgressDialog progressDialog;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.fragment_mine, container, false);
        currentActivity = getActivity();
        currentFragment = this;
        nickname = view.findViewById(R.id.nick_name);
        id = view.findViewById(R.id.id);
        avatar = view.findViewById(R.id.avatar);
        avatar.setOnClickListener(this);
        updateUserInfo();
        change_nickname = view.findViewById(R.id.change_nickname);
        check_nickname = view.findViewById(R.id.check_nickname);
        edit_nickname = view.findViewById(R.id.edit_nickname);
        change_nickname.setOnClickListener(this);
        check_nickname.setOnClickListener(this);
        view.findViewById(R.id.manage_commidy).setOnClickListener(this);
        view.findViewById(R.id.change_password).setOnClickListener(this);
        view.findViewById(R.id.clear_cache).setOnClickListener(this);
        view.findViewById(R.id.logout).setOnClickListener(this);
        view.findViewById(R.id.head).setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.avatar:
                showAvatarOptionDialog();
                break;
            case R.id.change_nickname: {
                nickname.setVisibility(View.INVISIBLE);
                v.setVisibility(View.INVISIBLE);
                showEditNickname();
                // 显示软键盘
                MyUtil.showKeyboard(getActivity(), edit_nickname);
            } break;
            case R.id.check_nickname: {
                if (edit_nickname.getText().toString().length() == 0) {
                    Toast.makeText(getContext(), "昵称不能为空", Toast.LENGTH_SHORT).show();
                } else if (User.getNickname().equals(edit_nickname.getText().toString())) {
                    Toast.makeText(getContext(), "昵称未修改", Toast.LENGTH_SHORT).show();
                    showNickname();
                    // 隐藏软键盘
                    MyUtil.hideKeyboard(getActivity());
                } else {
                    checkNicknameDialog(this, edit_nickname.getText().toString());
                    MyUtil.hideKeyboard(getActivity());
                }
            } break;
            case R.id.manage_commidy:{
                Commodity commodity = new Commodity();
                commodity.setSellerID(User.getId());
                Intent intent = new Intent(getActivity(), ManageCommodityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("commodity", commodity);
                intent.putExtras(bundle);
                startActivity(intent);
            } break;
            case R.id.change_password:
                showChangePasswordDialog(this);
                break;
            case R.id.clear_cache:
                Toast.makeText(getContext(), "该功能尚未完成", Toast.LENGTH_SHORT).show();
                break;
            case R.id.logout:
                showLogoutDialog();
                break;
            case R.id.head: {
                Commodity commodity = new Commodity();
                commodity.setSellerID(User.getId());
                commodity.setSellerName(User.getNickname());
                Intent intent = new Intent(getActivity(), PersonalHomepageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("commodity", commodity);
                intent.putExtras(bundle);
                startActivity(intent);
            } break;
                default:
                    break;
        }
    }

    public void updateUserInfo(){
        nickname.setText(User.getNickname());
        id.setText("ID:" + User.getId());
        // 加载图片
//        File outputImage = new File(currentActivity.getExternalCacheDir(), "avatar_" + User.getId() + ".jpg");
        File outputImage = new File(currentActivity.getExternalCacheDir().getAbsolutePath() +
                "/avatar/avatar_" + User.getId() + ".jpg");
        imageUri = Uri.fromFile(outputImage);
        if (outputImage.exists()) {
            PictureUtils.updatePictureView(avatar, outputImage, currentActivity);
        } else {
            avatar.setImageResource(R.mipmap.ic_launcher);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    PictureUtils.cropPhoto(imageUri, currentFragment, CROP_PHOTO, TAKE_PHOTO);
                }
                break;
            case CHOOSE_PHOTO:
                if (data != null) {
                    Uri uri = data.getData();
                    PictureUtils.cropPhoto(uri, currentFragment, CROP_PHOTO, CHOOSE_PHOTO);
//                    // 判断手机系统版本号
//                    if (Build.VERSION.SDK_INT >= 19) {
//                        // 4.4及以上系统使用这个方法处理图片
//                        handleImageOnKitKat(data);
//                    } else {
//                        // 4.4以下系统使用这个方法处理图片
//                        handleImageBeforeKitKat(data);
//                    }
                }
                break;
            case CROP_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    PictureUtils.displayImage(avatar, imageUri, currentActivity);
                    // 将头像存储到服务器
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            NetHelper.saveAvatar((IServerListener)currentFragment, currentActivity);
                        }
                    }).start();
                    showWaitingDialog("正在保存头像");
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 0:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    PictureUtils.openAlbum(currentFragment, CHOOSE_PHOTO);
                }
                break;
            default:
                break;
        }
    }

    private void showAvatarOptionDialog() {
        final String[] items = {"拍照", "从相册选取", "查看大图", "取消"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(currentActivity)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0: { // 使用相机拍照
                                // 创建File对象，用于存储拍照后的图片
//                                File outputImage = new File(currentActivity.getExternalCacheDir(), "avatar_" + User.getId() + ".jpg");
                                File outputImage = new File(currentActivity.getExternalCacheDir().getAbsolutePath() +
                                        "/avatar/avatar_" + User.getId() + ".jpg");
                                try {
                                    if (outputImage.exists()){
                                        outputImage.delete();
                                    }
                                    outputImage.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (Build.VERSION.SDK_INT >= 24){
                                    imageUri = FileProvider.getUriForFile(currentActivity,
                                            "com.example.fleamarket.fileprovider", outputImage);
                                }else {
                                    imageUri = Uri.fromFile(outputImage);
                                }
                                // 启动相机程序
                                Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                startActivityForResult(intent, TAKE_PHOTO);
                            }
                            break;
                            case 1: { // 从相册选取照片
                                if (ContextCompat.checkSelfPermission(currentActivity,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(currentActivity,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                                } else {
                                    PictureUtils.openAlbum(currentFragment, CHOOSE_PHOTO);
                                }
                            }
                            break;
                            case 2: { // 查看大图
                                Toast.makeText(getContext(), "该功能尚未完成", Toast.LENGTH_SHORT).show();
                            }
                            break;
                            case 3: { // 取消
                                dialog.dismiss();
                            }
                            default:
                        }
                    }
                });
        builder.create().show();
    }

    private void checkNicknameDialog(final IServerListener listener, final String nickname) {
        new AlertDialog.Builder(getContext())
                .setMessage("确认修改昵称？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                NetHelper.changeNickname(listener, nickname);
                            }
                        }).start();
                        showWaitingDialog("正在保存昵称");
                        showNickname();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        MyUtil.showKeyboard(getActivity(), edit_nickname);
                    }
        }).create().show();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(getContext())
                .setMessage("确定退出当前账号？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        User.setLogin(false);
                        MainActivity mainActivity = (MainActivity)getActivity();
                        // 删除本地的SharedPreferences
                        SharedPreferences sp = mainActivity.getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.apply();
                        // 将“我的”页面切换为“登录”页面
                        FragmentManager fm = mainActivity.getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.hide(mainActivity.getFragmentByName("MineFragment"));
                        ft.show(mainActivity.getFragmentByName("LoginFragment"));
                        ft.commit();
                        mainActivity.title.setText("登录");
                        // 清除消息页面……

                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
        }).create().show();
    }

    private void showNickname() {
        edit_nickname.setVisibility(View.INVISIBLE);
        check_nickname.setVisibility(View.INVISIBLE);
        nickname.setVisibility(View.VISIBLE);
        change_nickname.setVisibility(View.VISIBLE);
    }

    private void showEditNickname() {
        edit_nickname.setVisibility(View.VISIBLE);
        check_nickname.setVisibility(View.VISIBLE);
        edit_nickname.setText(User.getNickname());
        edit_nickname.setHint(User.getNickname());
    }

    private void showChangePasswordDialog(final IServerListener listener){
        @SuppressLint("InflateParams") View view = LayoutInflater.from(getContext()).inflate(R.layout.change_password, null);
        final EditText oldPw = view.findViewById(R.id.old_password);
        final EditText newPw = view.findViewById(R.id.new_password);
        final EditText confirmPw = view.findViewById(R.id.confirm_password);
        final TextView tips = view.findViewById(R.id.tips);
        final AlertDialog alertDialog = new AlertDialog.Builder(getContext()).setView(view).setTitle("修改密码")
                .setPositiveButton("确认修改", null)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        // 使用getButton方法设计“确认”按钮的逻辑可以防止点击一次对话框就关闭
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String oldPassword = oldPw.getText().toString();
                        String newPassword = newPw.getText().toString();
                        final String confirmPassword = confirmPw.getText().toString();
                        if (oldPassword.length() == 0 || newPassword.length() == 0 || confirmPassword.length() == 0) {
                            tips.setText("密码不能为空");
                        } else if (!oldPassword.equals(User.getPassword())) {
                            tips.setText("原密码错误");
                        } else if (!newPassword.equals(confirmPassword)) {
                            tips.setText("两次输入密码不一致");
                        } else if (newPassword.equals(oldPassword)) {
                            tips.setText("新密码与原密码相同");
                        } else {
                            boolean valid = true;
                            for(int i = 0; i < newPassword.length(); i++){
                                if(!((newPassword.charAt(i) >= '0' && newPassword.charAt(i) <= '9')||
                                        (newPassword.charAt(i) >= 'a' && newPassword.charAt(i) <= 'z')||
                                        (newPassword.charAt(i) >= 'A' && newPassword.charAt(i) <= 'Z'))){
                                    valid = false;
                                    tips.setText("密码只能由数字和大小写字母组成");
                                    break;
                                }
                            }
                            if (valid) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        NetHelper.changePassword(listener, confirmPassword);
                                    }
                                }).start();
                                alertDialog.dismiss();
                                showWaitingDialog("正在保存密码");
                            }
                        }
                        tips.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
        alertDialog.show();
    }

    private void showWaitingDialog(String message) {
        progressDialog = new ProgressDialog(currentActivity);
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(true); // 是否形成一个加载动画，true表示不明确加载进度形成转圈动画，false表示明确加载进度
        progressDialog.setCancelable(false); // 点击返回键或者dialog四周是否关闭dialog，true表示可以关闭，false表示不可关闭
        progressDialog.show();
    }

    @Override
    public void onSuccess(final NetMessage info) {
        progressDialog.dismiss();
        if (info.getType() == MessageType.CHANGE_NICKNAME) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), "昵称修改成功", Toast.LENGTH_SHORT).show();
                    User.setNickname(info.getNickname());
                    nickname.setText(User.getNickname());
                    // 修改本地的SharedPreferences
                    SharedPreferences sp = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("nickname", User.getNickname());
                    editor.apply();
                }
            });
        } else if (info.getType() == MessageType.CHANGE_PASSWORD) {
            Looper.prepare();
            Toast.makeText(getContext(), "密码修改成功", Toast.LENGTH_SHORT).show();
            User.setPassword(info.getPw());
            SharedPreferences sp = getActivity().getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("password", User.getPassword());
            editor.apply();
            Looper.loop();
        } else if (info.getType() == MessageType.SAVE_AVATAR) {
            Looper.prepare();
            Toast.makeText(getContext(), "头像设置成功", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }

    }

    @Override
    public void onFailure(String info) {
        Looper.prepare();
        progressDialog.dismiss();
        Toast.makeText(getContext(), info, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }

//    @TargetApi(19)
//    private void handleImageOnKitKat(Intent data) {
//        String imagePath = null;
//        Uri uri = data.getData();
//        if (DocumentsContract.isDocumentUri(this, uri)) {
//            // 如果是document类型的Uri，则通过document id处理
//            String docId = DocumentsContract.getDocumentId(uri);
//            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
//                String id = docId.split(":")[1]; // 解析出数字格式的id
//                String selection = MediaStore.Images.Media._ID + "=" + id;
//                imagePath = PictureUtils.getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, this);
//            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
//                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
//                imagePath = PictureUtils.getImagePath(contentUri, null, this);
//            }
//        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            // 如果是content类型的Uri，则使用普通方式处理
//            imagePath = PictureUtils.getImagePath(uri, null, this);
//        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            // 如果是file类型的Uri，直接获取图片路径即可
//            imagePath = uri.getPath();
//        }
//        PictureUtils.displayImage(picture, imagePath); // 根据图片路径显示图片
//    }
//
//    private void handleImageBeforeKitKat(Intent data) {
//        Uri uri = data.getData();
//        String imagePath = PictureUtils.getImagePath(uri, null, this);
//        PictureUtils.displayImage(picture, imagePath);
//    }
}
