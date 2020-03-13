package com.example.fleamarket.mine;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.fleamarket.MainActivity;
import com.example.fleamarket.R;
import com.example.fleamarket.User;
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

public class MineFragment extends Fragment {
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int CROP_PHOTO = 3;
    private ImageView avatar;
    private Uri imageUri;
    private Activity currentActivity;
    private Fragment currentFragment;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.fragment_mine, container, false);
        currentActivity = getActivity();
        currentFragment = this;
        view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(getContext())
                        .setMessage("确定退出当前账号？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getContext(), "退出登录", Toast.LENGTH_SHORT).show();
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
                                // 清除消息页面……
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });
        avatar = view.findViewById(R.id.avatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionListDialog();
            }
        });
        // 加载图片
        File outputImage = new File(currentActivity.getExternalCacheDir(), "avatar_" + User.getId() + ".jpg");
        imageUri = Uri.fromFile(outputImage);
        if (outputImage.exists()) {
            PictureUtils.updatePictureView(avatar, outputImage, currentActivity);
        }
        return view;
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

    private void showOptionListDialog() {
        final String[] items = {"拍照", "从相册选取", "查看大图", "取消"};
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(currentActivity)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0: { // 使用相机拍照
                                // 创建File对象，用于存储拍照后的图片
                                File outputImage = new File(currentActivity.getExternalCacheDir(), "avatar_" + User.getId() + ".jpg");
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
