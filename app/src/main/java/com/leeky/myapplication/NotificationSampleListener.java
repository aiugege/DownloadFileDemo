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

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.leeky.myapplication.view.ProgressCustomDialog;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.SpeedCalculator;
import com.liulishuo.okdownload.core.breakpoint.BlockInfo;
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo;
import com.liulishuo.okdownload.core.cause.EndCause;
import com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed;
import com.liulishuo.okdownload.core.listener.assist.Listener4SpeedAssistExtend;

import java.io.File;
import java.util.List;
import java.util.Map;

public class NotificationSampleListener extends DownloadListener4WithSpeed {
    private int totalLength;

    private NotificationCompat.Builder builder;
    private NotificationManager manager;
    private Runnable taskEndRunnable;
    private Context context;

    private NotificationCompat.Action action;
    private String savePath;
    private ProgressCustomDialog progressCustomDialog;
    private Handler handler = new Handler(Looper.getMainLooper());

    public NotificationSampleListener(Context context, ProgressCustomDialog progressCustomDialog) {
        this.context = context.getApplicationContext();
        this.progressCustomDialog = progressCustomDialog;
    }

    public void attachTaskEndRunnable(Runnable taskEndRunnable) {
        this.taskEndRunnable = taskEndRunnable;
    }

    public void releaseTaskEndRunnable() {
        taskEndRunnable = null;
    }

    public String getSavePath(String savePath) {
        return this.savePath = savePath;
    }

    public void setAction(NotificationCompat.Action action) {
        this.action = action;
    }

    public void initNotification() {
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        final String channelId = "okdownload";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            final NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "OkDownload",
                    NotificationManager.IMPORTANCE_MIN);
            manager.createNotificationChannel(channel);
        }

        builder = new NotificationCompat.Builder(context, channelId);


        builder.setDefaults(Notification.DEFAULT_LIGHTS)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_MIN)
                .setContentTitle("文件下载")
                .setContentText("");

        if (action != null) {
            builder.addAction(action);
        }
    }

    @Override public void taskStart(@NonNull DownloadTask task) {
        Log.d("NotificationActivity", "taskStart");
        handler.post(() -> {
            progressCustomDialog.showPrgoress();
            progressCustomDialog.setUpDataInfo("更改日志");
            progressCustomDialog.setForceUpDate(true);
        });

        builder.setTicker("taskStart");
        builder.setOngoing(true);
        builder.setAutoCancel(false);
        builder.setContentText(context.getResources().getString(R.string.start_download));
        builder.setProgress(0, 0, true);
        manager.notify(task.getId(), builder.build());
    }

    @Override
    public void connectStart(@NonNull DownloadTask task, int blockIndex,
                             @NonNull Map<String, List<String>> requestHeaderFields) {
        builder.setTicker("connectStart");
        builder.setContentText(
                "The connect of " + blockIndex + " block for this task is connecting");
        builder.setProgress(0, 0, true);
        manager.notify(task.getId(), builder.build());
    }

    @Override
    public void connectEnd(@NonNull DownloadTask task, int blockIndex, int responseCode,
                           @NonNull Map<String, List<String>> responseHeaderFields) {
        builder.setTicker("connectStart");
        builder.setContentText(
                "The connect of " + blockIndex + " block for this task is connected");
        builder.setProgress(0, 0, true);
        manager.notify(task.getId(), builder.build());
    }

    @Override
    public void infoReady(@NonNull DownloadTask task, @NonNull BreakpointInfo info,
                          boolean fromBreakpoint,
                          @NonNull Listener4SpeedAssistExtend.Listener4SpeedModel model) {
        Log.d("NotificationActivity", "infoReady " + info + " " + fromBreakpoint);

        if (fromBreakpoint) {
            builder.setTicker("fromBreakpoint");
        } else {
            builder.setTicker("fromBeginning");
        }
        builder.setContentText(
                "This task is download fromBreakpoint[" + fromBreakpoint + "]");
        builder.setProgress((int) info.getTotalLength(), (int) info.getTotalOffset(), true);
        manager.notify(task.getId(), builder.build());

        totalLength = (int) info.getTotalLength();
    }

    @Override
    public void progressBlock(@NonNull DownloadTask task, int blockIndex,
                              long currentBlockOffset,
                              @NonNull SpeedCalculator blockSpeed) {
    }

    @Override public void progress(@NonNull DownloadTask task, long currentOffset,
                                   @NonNull SpeedCalculator taskSpeed) {
        Log.d("NotificationActivity", "progress " + currentOffset);

//        builder.setContentText("downloading with speed: " + taskSpeed.speed());
        float percent = (float) currentOffset / totalLength;
        int progress = (int) (percent * 100);
        handler.post(() -> progressCustomDialog.setProgressbar(progress));

        builder.setContentText(context.getResources().getString(R.string.downloading) + progress + "%");
        builder.setProgress(totalLength, (int) currentOffset, false);
        manager.notify(task.getId(), builder.build());
    }

    @Override
    public void blockEnd(@NonNull DownloadTask task, int blockIndex, BlockInfo info,
                         @NonNull SpeedCalculator blockSpeed) {
    }

    @Override public void taskEnd(@NonNull final DownloadTask task, @NonNull EndCause cause,
                                  @android.support.annotation.Nullable Exception realCause,
                                  @NonNull SpeedCalculator taskSpeed) {
        Log.d("NotificationActivity", "taskEnd " + cause + " " + realCause);
        builder.setOngoing(false);
        builder.setAutoCancel(true);

        builder.setTicker("taskEnd " + cause);
//        builder.setContentText(
//                "task end " + cause + " average speed: " + taskSpeed.averageSpeed());


        // because of on some android phone too frequency notify for same id would be
        // ignored.
        handler.postDelayed(() -> {
            if (taskEndRunnable != null) taskEndRunnable.run();
        }, 100);

        if (cause == EndCause.COMPLETED) {
            builder.setContentText(context.getResources().getString(R.string.download_complete));
            builder.setProgress(1, 1, false);
            new Handler(Looper.getMainLooper()).post(() -> progressCustomDialog.finshLoad());
            task.addTag(20, DemoUtil.URL);
            new File(task.getFile().getPath()).renameTo(new File(savePath));
            installApk(new File(savePath));
            manager.cancel(task.getId());
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//            builder.setContentIntent(pendingIntent);
        } else {

           handler.postDelayed(() -> {
               progressCustomDialog.dismiss();
               manager.notify(task.getId(), builder.build());
           }, 100);
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

    public void installApk(File file) {
        String authority = context.getPackageName() + ".provider";
        Intent intent = new Intent(Intent.ACTION_VIEW);
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
