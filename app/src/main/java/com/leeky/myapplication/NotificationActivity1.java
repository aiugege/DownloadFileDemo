/*
 * Copyright (c) 2018 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leeky.myapplication;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.StatusUtil;

import java.io.File;

public class NotificationActivity1 extends AppCompatActivity {

    private CancelReceiver cancelReceiver;

    private DownloadTask task;
    private NotificationSampleListener listener;

    private TextView actionTv;
    private View actionView;

    private DownloadUtil downloadUtil;


    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        actionTv = findViewById(R.id.actionTv);
        actionView = findViewById(R.id.actionView);

        String versionCode = "v" + String.valueOf(35).hashCode();
        String sdCardPath = DemoUtil.getParentFile(this).getPath();
        String fileName = "app_" + versionCode + ".apk";
        String savePath = sdCardPath + File.separator + fileName;

        listener = new NotificationSampleListener(this);
        task = new DownloadTask
                .Builder(DemoUtil.URL, sdCardPath, fileName)
                .setPassIfAlreadyCompleted(false)
                .setMinIntervalMillisCallbackProcess(80)
                .setAutoCallbackToUIThread(false)
                .build();

        downloadUtil = new DownloadUtil(NotificationActivity1.this, task, listener);
        downloadUtil.initListener(actionTv, savePath);
        downloadUtil.initTask(sdCardPath, fileName);
        downloadUtil.initManager();
        downloadUtil.initAction(actionTv, savePath);

        downloadUtil.sameFileToDo(actionTv);
    }

    @Override protected void onDestroy() {
        super.onDestroy();
        if (listener != null) {
            listener.releaseTaskEndRunnable();
        }
    }

}
