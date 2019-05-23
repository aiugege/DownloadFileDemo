package com.leeky.myapplication.AdLaunch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.facebook.common.executors.CallerThreadExecutor;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.leeky.myapplication.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import com.leeky.myapplication.DemoUtil;
import com.leeky.myapplication.utils.AESEncryption;

/**
 * Created by Leeky on 2019/5/22.
 */
public class SplashActivity extends AppCompatActivity {

    String imgUrl = "";
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mPreferences = getSharedPreferences("APP", Context.MODE_PRIVATE);
        editor = mPreferences.edit();

        checkPermission();

    }

    private void gotoNext(String url) {
        if (!TextUtils.isEmpty(url)) {
            if (getBitmap(DemoUtil.getPicName(url)) == null || !url.equals(mPreferences.getString("imgUrl", ""))) {
                editor.putString("imgUrl", url);
                editor.commit();
                SaveImageFromDataSource(url, DemoUtil.getPicName(url));
            } else {

            }
        }

    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        } else {
            getWindow().getDecorView().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, AdActivity.class));
                }
            }, 2000);

            gotoNext(DemoUtil.ADURL);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getWindow().getDecorView().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(SplashActivity.this, AdActivity.class));
                        }
                    }, 2000);

                    gotoNext(DemoUtil.ADURL);
                } else {
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;


        }
    }

    private void SaveImageFromDataSource(String url, final String fileName) {

        ImageRequest imageRequest = ImageRequestBuilder
                .newBuilderWithSource( Uri.parse(url) )
                .setProgressiveRenderingEnabled(true)
                .build();

        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchDecodedImage(imageRequest,this);

        dataSource.subscribe(new BaseBitmapDataSubscriber() {

            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                // You can use the bitmap in only limited ways
                // No need to do any cleanup.
                if (bitmap == null) {
                    Toast.makeText(SplashActivity.this, "bitmap为空", Toast.LENGTH_SHORT).show();
                } else {
                    saveBitmap(bitmap, fileName);
                }
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                // No cleanup required here.
            }

        }, CallerThreadExecutor.getInstance());


    }

    public Boolean saveBitmap(Bitmap bitmap, String fileName) {

        String SAVE_PIC_PATH = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)
                ? Environment.getExternalStorageDirectory().getAbsolutePath()
                : "/mnt/sdcard";

        File appDir = new File(SAVE_PIC_PATH + "/ABC/");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        long nowSystemTime = System.currentTimeMillis();
        File file = new File(appDir, fileName + ".png");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;

    }

    public static Bitmap getBitmap(String fileName) {

        String SAVE_PIC_PATH = Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)
                ? Environment.getExternalStorageDirectory().getAbsolutePath()
                : "/mnt/sdcard";

        File appDir = new File(SAVE_PIC_PATH + "/ABC/");
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        File file = new File(appDir, fileName + ".png");

        if (!file.exists()) {
            return null;
        } else {
            if (file.length() > 0) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                return bitmap;
            }
        }
        return null;
    }
}
