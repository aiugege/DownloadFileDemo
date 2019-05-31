package com.leeky.myapplication;

import android.support.annotation.NonNull;

import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.UnifiedListenerManager;

public class GlobalTaskManager {
    private UnifiedListenerManager manager;

    public GlobalTaskManager() {
        manager = new UnifiedListenerManager();
    }

    public static class ClassHolder {
        private static final GlobalTaskManager INSTANCE = new GlobalTaskManager();
    }

    public static GlobalTaskManager getImpl() {
        return GlobalTaskManager.ClassHolder.INSTANCE;
    }

    public void addAutoRemoveListenersWhenTaskEnd(int id) {
        manager.addAutoRemoveListenersWhenTaskEnd(id);
    }

    public void attachListener(@NonNull DownloadTask task, @NonNull DownloadListener listener) {
        manager.attachListener(task, listener);
    }

    public void enqueueTask(@NonNull DownloadTask task,
                     @NonNull DownloadListener listener) {
        manager.enqueueTaskWithUnifiedListener(task, listener);
    }
}
