package com.leeky.myapplication;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.StatusUtil;

import java.io.File;

public class DownloadUtil {

    private Context context;
    private DownloadTask task;
    private NotificationSampleListener listener;


    public DownloadUtil(Context context, DownloadTask task, NotificationSampleListener listener) {
        this.context = context.getApplicationContext();
        this.task = task;
        this.listener = listener;
    }

    public void initListener(final TextView actionTv) {
        if (actionTv == null) return;
        listener = new NotificationSampleListener(context);
        listener.attachTaskEndRunnable(new Runnable() {
            @Override public void run() {
                actionTv.setText(R.string.start);
                actionTv.setTag(null);
            }
        });
        listener.initNotification();
    }

    public void initTask() {
        task = new DownloadTask
                .Builder(DemoUtil.URL, DemoUtil.getParentFile(context))
                .setPassIfAlreadyCompleted(false)
                .setMinIntervalMillisCallbackProcess(80)
                .setAutoCallbackToUIThread(false)
                .build();
    }

    public void initAction(final TextView actionTv) {
        if (actionTv == null) return;
        actionTv.setText(R.string.start);
        actionTv.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (task.getUrl().equals(task.getTag(20))) {
                    Toast.makeText(context, "已下载", Toast.LENGTH_SHORT).show();
                    installApk(task);
                } else {
                    if (v.getTag() == null) {
                        // need to start
                        GlobalTaskManager.getImpl().enqueueTask(task, listener);

                        actionTv.setText(R.string.cancel);
                        v.setTag(new Object());
                    } else {
                        // need to cancel
                        task.cancel();
                    }
                }


            }
        });
    }

    public void initManager() {
        GlobalTaskManager.getImpl().attachListener(task, listener);
        GlobalTaskManager.getImpl().addAutoRemoveListenersWhenTaskEnd(task.getId());
    }

    public void sameFileToDo(final TextView actionTv) {
        if (actionTv == null) return;
        if (StatusUtil.isSameTaskPendingOrRunning(task)) {
            actionTv.setText(R.string.cancel);
            actionTv.setTag(new Object());
        }
    }

    public void installApk(DownloadTask task) {
        String authority = context.getPackageName() + ".provider";
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File file = task.getFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri contentUri = FileProvider.getUriForFile(context, authority, file);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}
