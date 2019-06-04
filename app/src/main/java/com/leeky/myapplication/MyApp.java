package com.leeky.myapplication;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.leeky.myapplication.utils.ImagePipelineConfigFactory;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by Leeky on 2019/5/22.
 */
public class MyApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this, ImagePipelineConfigFactory.getImagePipelineConfig(this));
        CrashReport.initCrashReport(getApplicationContext(), "3d9ad99d5b", true);

    }
}
