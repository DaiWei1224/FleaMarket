package com.example.fleamarket.home;

import android.os.Bundle;

import com.example.fleamarket.R;

import androidx.appcompat.app.AppCompatActivity;

public class CommodityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.commodity_detail);
        Bundle bundle = getIntent().getExtras();

    }
}
