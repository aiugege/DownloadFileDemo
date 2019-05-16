package com.leeky.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.liulishuo.okdownload.DownloadTask;

public class CancelReceiver extends BroadcastReceiver {
    static final String ACTION = "cancelOkdownload";

    private DownloadTask task;

    CancelReceiver(@NonNull DownloadTask task) {
        this.task = task;
    }

    @Override public void onReceive(Context context, Intent intent) {
        this.task.cancel();
    }
}
