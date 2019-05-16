package com.leeky.myapplication;

import android.support.annotation.NonNull;

import com.liulishuo.okdownload.DownloadListener;
import com.liulishuo.okdownload.DownloadTask;
import com.liulishuo.okdownload.UnifiedListenerManager;

public class GlobalTaskManager {
    private UnifiedListenerManager manager;

    private GlobalTaskManager() {
        manager = new UnifiedListenerManager();
    }

    private static class ClassHolder {
        private static final GlobalTaskManager INSTANCE = new GlobalTaskManager();
    }

    static GlobalTaskManager getImpl() {
        return GlobalTaskManager.ClassHolder.INSTANCE;
    }

    void addAutoRemoveListenersWhenTaskEnd(int id) {
        manager.addAutoRemoveListenersWhenTaskEnd(id);
    }

    void attachListener(@NonNull DownloadTask task, @NonNull DownloadListener listener) {
        manager.attachListener(task, listener);
    }

    void enqueueTask(@NonNull DownloadTask task,
                     @NonNull DownloadListener listener) {
        manager.enqueueTaskWithUnifiedListener(task, listener);
    }
}
