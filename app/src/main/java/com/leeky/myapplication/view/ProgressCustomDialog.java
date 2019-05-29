package com.leeky.myapplication.view;

import android.app.Dialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.leeky.myapplication.R;

/**
 * Created by Leeky on 2019/5/29.
 */
public class ProgressCustomDialog {

    private AlertDialog simpleDialog;
    private ProgressBar progressBar;
    private final TextView updateInfo;
    private final View updateClose;

    public ProgressCustomDialog(Context mcontext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mcontext);
        View inflate = LayoutInflater.from(mcontext).inflate(R.layout.version_upading, null);
        builder.setView(inflate);
        progressBar = inflate.findViewById(R.id.round_flikerbar);
        updateInfo = inflate.findViewById(R.id.update_info);
        updateClose = inflate.findViewById(R.id.update_close);
        simpleDialog = builder.create();
//        simpleDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        simpleDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        simpleDialog.setCancelable(false);

        updateClose.setOnClickListener(v -> {
            progressBar.reset();
            simpleDialog.dismiss();
        });
    }

    public Dialog getSimpleDialog() {
        return simpleDialog;
    }

    public void showPrgoress() {
        if (simpleDialog != null) {
            simpleDialog.show();
        }
    }

    public void setProgressbar(int progress) {
        if (progressBar != null && simpleDialog.isShowing()) {
            progressBar.setProgress((float) progress);
        }
    }

    public void finshLoad() {
        if (progressBar != null && simpleDialog.isShowing()) {
            progressBar.finishLoad();
            progressBar.reset();
            simpleDialog.dismiss();
        }
    }

    public void dismiss() {
        if (simpleDialog.isShowing()) {
            progressBar.reset();
            simpleDialog.dismiss();
        }
    }

    public void setUpDataInfo(String changelog) {
        updateInfo.setText(Html.fromHtml(changelog));
    }

    public void setForceUpDate(Boolean forceUpdate) {
        updateClose.setVisibility(forceUpdate ? View.GONE : View.VISIBLE);
    }
}
