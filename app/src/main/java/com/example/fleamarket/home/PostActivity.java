package com.example.fleamarket.home;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fleamarket.R;
import com.example.fleamarket.User;
import com.example.fleamarket.net.Commodity;
import com.example.fleamarket.net.IServerListener;
import com.example.fleamarket.net.NetHelper;
import com.example.fleamarket.net.NetImage;
import com.example.fleamarket.net.NetMessage;
import com.example.fleamarket.utils.PictureUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

public class PostActivity extends AppCompatActivity implements IServerListener {
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    Activity currentActivity;
    TextView commodityName;
    TextView price;
    TextView commodityDetail;
    String area = null;
    boolean havePhoto = false;
    ImageView camera;
    ImageView commodityPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        currentActivity = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        commodityName = findViewById(R.id.commodity_name);
        price = findViewById(R.id.price);
        commodityDetail = findViewById(R.id.commodity_detail);
        // 发布区域下拉框
        Spinner chooseArea = findViewById(R.id.choose_area);
        chooseArea.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] areas = getResources().getStringArray(R.array.areas);
                area = areas[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        camera = findViewById(R.id.camera);
        commodityPhoto = findViewById(R.id.commodity_photo);
        // 发布按键
        findViewById(R.id.post_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (commodityName.getText().toString().length() == 0) {
                    Toast.makeText(getBaseContext(), "商品名称不能为空", Toast.LENGTH_SHORT).show();
                } else if(price.getText().toString().length() == 0) {
                    Toast.makeText(getBaseContext(), "商品价格不能为空", Toast.LENGTH_SHORT).show();
                } else if(commodityDetail.getText().toString().length() == 0) {
                    Toast.makeText(getBaseContext(), "商品详情不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    // 检查商品价格是否有效
                    String priceString = price.getText().toString();
                    int priceLen = priceString.length();
                    boolean valid = true;
                    char number;
                    for(int i = 0; i < priceLen; i++) {
                        number = priceString.charAt(i);
                        if(number < '0' || number > '9') {
                            if(!(number == '.' && i != 0 && (i == priceLen -2 || i == priceLen - 3))) {
                                valid = false;
                                break;
                            }
                        }
                    }
                    if (valid) {
                        showPostConfirmDialog();
                    } else {
                        Toast.makeText(getBaseContext(), "价格无效", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoOptionDialog(0);
            }
        });
        commodityPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoOptionDialog(1);
            }
        });

    }

    private void showPostConfirmDialog() {
        new AlertDialog.Builder(this)
                .setMessage("确认发布商品？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Commodity commodity = new Commodity();
                        Date postTime = new Date();
                        commodity.setCommodityID(User.getId() + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(postTime));
                        commodity.setCommodityName(commodityName.getText().toString());
                        commodity.setCommodityDetail(commodityDetail.getText().toString());
                        commodity.setPrice(price.getText().toString());
                        commodity.setSellerID(User.getId());
                        commodity.setSellerName(User.getNickname());
                        commodity.setArea(area);
                        commodity.setPostTime(postTime);
                        commodity.setHavePhoto(havePhoto);
                        if (havePhoto) {
                            NetImage netImage = new NetImage();
                            netImage.setData(PictureUtils.loadImageFromFile(
                                    new File(getExternalCacheDir().getAbsolutePath() + "post_image.jpg")));
                            commodity.setCommodityPhoto(netImage);
                        }
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                NetHelper.requestPostCommodity((IServerListener)currentActivity, commodity);
                            }
                        }).start();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    private void showPhotoOptionDialog(int type) {
        final String[] items;
        if(type == 0) {
            // 0代表点击照相机按钮
            items = new String[2];
            items[0] = "拍照";
            items[1] = "从相册选取";
        } else {
            // 1代表点击照片增加一个删除照片的选项
            items = new String[3];
            items[0] = "拍照";
            items[1] = "从相册选取";
            items[2] = "删除图片";
        }
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        switch (i){
                            case 0: { // 使用相机拍照
                                // 创建File对象，用于存储拍照后的图片
                                File outputImage = new File(getExternalCacheDir().getAbsolutePath() + "post_image.jpg");
                                try {
                                    if (outputImage.exists()){
                                        outputImage.delete();
                                    }
                                    outputImage.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                Uri imageUri;
                                if (Build.VERSION.SDK_INT >= 24){
                                    imageUri = FileProvider.getUriForFile(getBaseContext(),
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
                                if (ContextCompat.checkSelfPermission(getBaseContext(),
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(currentActivity,
                                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                                } else {
                                    PictureUtils.openAlbum(currentActivity, CHOOSE_PHOTO);
                                }
                            }
                            break;
                            case 2: { // 删除照片
                                commodityPhoto.setVisibility(View.GONE);
                                camera.setVisibility(View.VISIBLE);
                                havePhoto = false;
                            }
                            default:
                        }
                    }
                });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == Activity.RESULT_OK) {
                    // 压缩排完的图片
                    Point size = new Point();
                    this.getWindowManager().getDefaultDisplay().getSize(size);
                    Bitmap bitmap = PictureUtils.getScaledBitmap(
                            getExternalCacheDir().getAbsolutePath() + "post_image.jpg" , size.x, size.y);
                    try {
                        File file = new File(getExternalCacheDir().getAbsolutePath() + "post_image.jpg");
                        if (file.exists()) {
                            file.delete();
                        }
                        file.createNewFile();
                        FileOutputStream out = new FileOutputStream(file);
                        // quality为图像压缩率，0-100。0压缩100%，100不压缩
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    PictureUtils.updatePictureView(commodityPhoto, new File(getExternalCacheDir().getAbsolutePath() + "post_image.jpg"), this);
                    camera.setVisibility(View.GONE);
                    commodityPhoto.setVisibility(View.VISIBLE);
                    havePhoto = true;
                }
                break;
            case CHOOSE_PHOTO:
                if (data != null) {
                    Uri uri = data.getData();
                    // 将照片保存到cache
                    File file = null;
                    try {
                        file = new File(getExternalCacheDir().getAbsolutePath() + "post_image.jpg");
                        if (file.exists()) {
                            file.delete();
                        }
                        file.createNewFile();
                        FileOutputStream out = new FileOutputStream(file);
                        Bitmap bitmap = BitmapFactory.decodeStream(currentActivity.getContentResolver().
                                openInputStream(uri));
                        // quality为图像压缩率，0-100。0压缩100%，100不压缩
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    PictureUtils.updatePictureView(commodityPhoto, file, this);
                    camera.setVisibility(View.GONE);
                    commodityPhoto.setVisibility(View.VISIBLE);
                    havePhoto = true;
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
                    PictureUtils.openAlbum(this, CHOOSE_PHOTO);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onSuccess(NetMessage info) {
        Looper.prepare();
        Toast.makeText(this, "商品发布成功", Toast.LENGTH_SHORT).show();
        currentActivity.finish();
        Looper.loop();
    }

    @Override
    public void onFailure(String info) {
        Looper.prepare();
        Toast.makeText(this, info, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
