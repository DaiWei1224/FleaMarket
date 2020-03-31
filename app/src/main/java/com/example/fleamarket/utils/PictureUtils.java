package com.example.fleamarket.utils;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.fragment.app.Fragment;

public class PictureUtils {

    public static void updatePictureView(ImageView pictureView, File pictureFile, Activity activity) {
        if(pictureFile.exists()){
            // 因为View的尺寸还未计算完成，根据Activity的尺寸进行保守估算
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            Bitmap bitmap = getScaledBitmap(pictureFile.getPath(), size.x, size.y);
            pictureView.setImageBitmap(bitmap);
        }
    }

    // 尽可能地压缩图片，以防recyclerView绘制的时候卡顿
    public static void showPictureOnRecyclerView(ImageView pictureView, File pictureFile, Activity activity) {
        if(pictureFile.exists()){
            Point size = new Point();
            activity.getWindowManager().getDefaultDisplay().getSize(size);
            Bitmap bitmap = getScaledBitmap(pictureFile.getPath(), size.x / 3, size.y / 3);
            pictureView.setImageBitmap(bitmap);
        }
    }

    // 缩放图片
    public static Bitmap getScaledBitmap(String path , int destWidth, int destHeight) {
        // Read in the dimensions of the image on disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只读取图片，不加载到内存
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // Figure out how much to scale down by
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            float heightScale = srcHeight / destHeight;
            float widthScale = srcWidth / destWidth;
            // 四舍五入取整
            inSampleSize = Math.round(heightScale > widthScale ? heightScale : widthScale);
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // Read in and create final bitmap
        return BitmapFactory.decodeFile(path, options);
    }

    public static void cropPhoto(Uri uri, Fragment fragment, int type, int from){
        Intent intent = new Intent("com.android.camera.action.CROP");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // 添加这一句表示对目标应用临时授权该uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("scale", true); // 是否保留比例
        intent.putExtra("aspectX", 1); // X方向上的比例
        intent.putExtra("aspectY", 1); // Y方向上的比例
        intent.putExtra("outputX", 400); // 裁剪区的宽
        intent.putExtra("outputY", 400); // 裁剪区的高
        intent.putExtra("noFaceDetection", true); // 无人脸识别
        intent.putExtra("return-data", false); // 是否将数据保留在Bitmap中返回
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()); // 图片输出格式
        Uri outputUri = uri;
        if(from == 2) { // 从相册选取
//            File outputImage = new File(fragment.getActivity().getExternalCacheDir(), "avatar_" + User.getId() + ".jpg");
            File outputImage = new File(fragment.getActivity().getExternalCacheDir().getAbsolutePath() +
                    "/temporary/avatar.jpg");
            try {
                if (outputImage.exists()){
                    outputImage.delete();
                }
                outputImage.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputUri = Uri.fromFile(outputImage);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri); // 输出图片到指定位置
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri); // 输出图片到指定位置
//        intent = Intent.createChooser(intent, "裁剪图片");
        fragment.startActivityForResult(intent, type);
    }

    // 打开相册
    public static void openAlbum(Activity activity, int type) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        activity.startActivityForResult(intent, type);
    }

    public static void openAlbum(Fragment fragment, int type) {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        fragment.startActivityForResult(intent, type);
    }

    public static String getImagePath(Uri uri, String selection, Activity activity) {
        String path = null;
        // 通过Uri和selection来获取真实的图片路径
        Cursor cursor = activity.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    public static void displayImage(ImageView view, String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            view.setImageBitmap(bitmap);
        }
    }

    public static void displayImage(ImageView view, Uri uri, Activity activity) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().
                    openInputStream(uri));
            view.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static byte[] loadImageFromFile(File image) {
        try {
            InputStream is = new FileInputStream(image);
            byte[] data = new byte[(int)image.length()];
            is.read(data);
            is.close();
            return data;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveImageFromByte(byte[] data, String path) {
        try {
//            File image = new File(activity.getExternalCacheDir(), "avatar_" + User.getId() + ".jpg");
//            File image = new File(activity.getExternalCacheDir().getAbsolutePath() +
//                    "/avatar/avatar_" + User.getId() + ".jpg");
            File image = new File(path);
            if (image.exists()){
                image.delete();
            }
            image.createNewFile();
            OutputStream os = new FileOutputStream(image);
            os.write(data);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
