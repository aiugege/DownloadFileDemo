/*
 * Copyright (c) 2017 LingoChamp Inc.
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

package com.leeky.myapplication.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.widget.ProgressBar;

import java.io.File;

public class DemoUtil {
//    https://res.51renrenhui.com/app/rrh/android/yhhz_sogou.apk
//    https://cdn.llscdn.com/yy/files/tkzpx40x-lls-LLS-5.7-785-20171108-111118.apk
    public static final String URL =
            "https://cdn.llscdn.com/yy/files/tkzpx40x-lls-LLS-5.7-785-20171108-111118.apk";

    public static final String APK_NAME =
            "notification-file.apk";

    public static final String ADURL = "http://img.zcool.cn/community/01e51e581074cda84a0d304ff02d18.png@1280w_1l_2o_100sh.png";
    public static final String ADURL1 = "https://wx1.sinaimg.cn/mw690/7fca44e7gy1fujiej32k9j20u01hcnjq.jpg";
    public static final String ADIMAGE_NAME = "AdImg";


    public static void calcProgressToView(ProgressBar progressBar, long offset, long total) {
        final float percent = (float) offset / total;
        progressBar.setProgress((int) (percent * progressBar.getMax()));
    }


    public static File getParentFile(@NonNull Context context) {
        final File externalSaveDir = context.getExternalCacheDir();
        if (externalSaveDir == null) {
            return context.getCacheDir();
        } else {
            return externalSaveDir;
        }
    }

    public static String getPicName(String imgUrl){
        if (TextUtils.isEmpty(imgUrl) || imgUrl.indexOf(".") == -1){
            return ""; //如果图片地址为null或者地址中没有"."就返回""
        }
        int start = imgUrl.lastIndexOf("/") + 1;
        int end = imgUrl.lastIndexOf(".");
        return imgUrl.substring(start, end);
    }
}
