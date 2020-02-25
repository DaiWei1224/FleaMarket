package com.example.graduationproject.home;

import android.os.Bundle;

import com.example.graduationproject.R;

import androidx.appcompat.app.AppCompatActivity;

public class CommodityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commodity_detail);
        Bundle bundle = getIntent().getExtras();

//        SquareImageView imageView = findViewById(R.id.image);
//        imageView.setImageResource(R.mipmap.ic_launcher);

    }
}
