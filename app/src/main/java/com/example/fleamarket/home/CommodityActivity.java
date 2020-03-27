package com.example.fleamarket.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fleamarket.R;
import com.example.fleamarket.User;
import com.example.fleamarket.net.Commodity;
import com.example.fleamarket.utils.PictureUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class CommodityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commodity_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        final Commodity commodity = (Commodity)getIntent().getExtras().getSerializable("commodity");
        ImageView avatar = findViewById(R.id.avatar);
        PictureUtils.displayImage(avatar, getExternalCacheDir().getAbsolutePath() +
                "/avatar/avatar_" + commodity.getSellerID() + ".jpg");
        TextView sellerName = findViewById(R.id.nick_name);
        sellerName.setText(commodity.getSellerName());
        TextView area = findViewById(R.id.area);
        area.setText("来自" + commodity.getArea());
        TextView price = findViewById(R.id.price);
        price.setText("¥" + commodity.getPrice());
        TextView commodityName = findViewById(R.id.title);
        commodityName.setText(commodity.getCommodityName());
        TextView postTime = findViewById(R.id.post_time);
        postTime.setText("发布于" + commodity.getPostTimeString());
        TextView detail = findViewById(R.id.commodity_detail);
        detail.setText(commodity.getCommodityDetail());
        if (commodity.isHavePhoto()) {
            ImageView commodityPhoto = findViewById(R.id.commodity_photo);
            PictureUtils.displayImage(commodityPhoto, getExternalCacheDir().getAbsolutePath() +
                    "/commodity/" + commodity.getCommodityID() + ".jpg");
        }
        Button contactSeller = findViewById(R.id.chat_button);
        if (User.getId().equals(commodity.getSellerID())) {
            // 隐藏联系卖家按钮
            contactSeller.setVisibility(View.GONE);
        } else {
            contactSeller.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getBaseContext(), "该功能尚未完成", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}
