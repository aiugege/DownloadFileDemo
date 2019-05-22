package com.leeky.myapplication.AdLaunch;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.leeky.myapplication.MainActivity;
import com.leeky.myapplication.R;

import com.leeky.myapplication.DemoUtil;

/**
 * Created by Leeky on 2019/5/22.
 */
public class AdActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView imageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad);
        imageView = findViewById(R.id.ad_image);
        imageView.setImageBitmap(SplashActivity.getBitmap(DemoUtil.ADIMAGE_NAME));
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ad_image) {
            startActivity(new Intent(AdActivity.this, MainActivity.class));
        }
    }
}
