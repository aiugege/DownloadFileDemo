package com.leeky.myapplication.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.leeky.myapplication.R;


/**
 * Created by Administrator on 2017/11/21.
 */

public class ProgressBar extends View implements Runnable {
    private PorterDuffXfermode xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP);

    private int DEFAULT_HEIGHT_DP = 35;

    private int borderWidth;

    public float getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(float maxProgress) {
        this.maxProgress = maxProgress;
    }

    private float maxProgress = 100f;

    public Paint textPaint;

    public Paint bgPaint;

    public Paint pgPaint;

    public String progressText;

    public Rect textRect;

    public RectF bgRectf;

    /**
     * 左右来回移动的滑块
     */
    private Bitmap flikerBitmap;

    /**
     * 滑块移动最左边位置，作用是控制移动
     */
    private float flickerLeft;

    /**
     * 进度条 bitmap ，包含滑块
     */
    private Bitmap pgBitmap;

    private Canvas pgCanvas;

    /**
     * 当前进度
     */
    private float progress;

    public boolean isFinish() {
        return isFinish;
    }

    private boolean isFinish;

    public boolean isStop;

    /**
     * 下载中颜色
     */
    private int loadingColor;

    /**
     * 暂停时颜色
     */
    private int stopColor;

    /**
     * 进度文本、边框、进度条颜色
     */
    private int progressColor;

    private int textSize;

    private int radius;

    private Thread thread;

    BitmapShader bitmapShader;

    public ProgressBar(Context context) {
        this(context, null, 0);
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.ProgressBarDownload);
        try {
            textSize = (int) ta.getDimension(R.styleable.ProgressBarDownload_textSize, 12);
            loadingColor = ta.getColor(R.styleable.ProgressBarDownload_loadingColor,getContext().getResources().getColor(R.color.greed));
            stopColor = ta.getColor(R.styleable.ProgressBarDownload_stopColor, getContext().getResources().getColor(R.color.greed));
            radius = (int) ta.getDimension(R.styleable.ProgressBarDownload_radius, 0);
            borderWidth = (int) ta.getDimension(R.styleable.ProgressBarDownload_borderWidth, 1);
        } finally {
            ta.recycle();
        }
    }

    private void init() {
        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeWidth(borderWidth);
        pgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pgPaint.setStyle(Paint.Style.FILL);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textRect = new Rect();
        bgRectf = new RectF(borderWidth, borderWidth, getMeasuredWidth() - borderWidth, getMeasuredHeight() - borderWidth);
        if (isStop) {
            progressColor = stopColor;
        } else {
            progressColor = loadingColor;
        }
        flikerBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.flicker);
        flickerLeft = flikerBitmap.getWidth();
        initPgBitmap();
    }

    private void initPgBitmap() {
        pgBitmap = Bitmap.createBitmap(getMeasuredWidth() - borderWidth, getMeasuredHeight() - borderWidth, Bitmap.Config.ARGB_8888);
        pgCanvas = new Canvas(pgBitmap);
        thread = new Thread(this);
        thread.start();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int height = 0;
        switch (mode) {
            case MeasureSpec.AT_MOST:
                height = dp2px(DEFAULT_HEIGHT_DP);
                break;
            case MeasureSpec.EXACTLY:
            case MeasureSpec.UNSPECIFIED:
                height = heightSize;
                break;
        }
        setMeasuredDimension(widthSpecSize, height);
        if (pgBitmap == null) {
            init();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBackGround(canvas);
        drawProgress(canvas);
        //进度text，这是原始文字
        drawProgressText(canvas);
        //变色处理，矩形直接绘制到文字上方
        drawColorProgressText(canvas);

    }

    @SuppressLint("WrongConstant")
    public void drawColorProgressText(Canvas canvas) {
        textPaint.setColor(getContext().getResources().getColor(R.color.white));
        float textWidth = textPaint.measureText(progressText);
        float mLeft = (getMeasuredWidth() - textWidth) / 2;
        float mRight = bgRectf.width() * progress / 100;
        canvas.save();
        canvas.clipRect(mLeft, 0, mRight, getMeasuredHeight());
        canvas.drawText(progressText, mLeft, getY(), textPaint);
        canvas.restore();
    }

    public void drawProgressText(Canvas canvas) {
        textPaint.setColor(getResources().getColor(R.color.download_progress));
        progressText = getProgressText();
        textPaint.getTextBounds(progressText, 0, progressText.length(), textRect);
        int tWidth = textRect.width();
        float xCoordinate = (getMeasuredWidth() - tWidth) / 2;
        canvas.drawText(progressText, xCoordinate, getY(), textPaint);
    }

    public float getY() {
        Paint.FontMetricsInt fm = textPaint.getFontMetricsInt();
        return (getHeight() + fm.descent - fm.ascent) / 2 - fm.descent;
    }

    @SuppressLint("WrongConstant")
    public void drawProgress(Canvas canvas) {
        pgPaint.setColor(progressColor);
        float right = (progress / maxProgress) * getMeasuredWidth();
        pgCanvas.save();
        pgCanvas.clipRect(0, 0, right, getMeasuredHeight());
        pgCanvas.drawColor(progressColor);
        pgCanvas.restore();
        if (!isStop) {
            pgPaint.setXfermode(xfermode);
            pgCanvas.drawBitmap(flikerBitmap, flickerLeft, 0, pgPaint);
            pgPaint.setXfermode(null);
        }
        bitmapShader = new BitmapShader(pgBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        pgPaint.setShader(bitmapShader);
        canvas.drawRoundRect(bgRectf, radius, radius, pgPaint);
    }

    public void drawBackGround(Canvas canvas) {
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setColor(progressColor);
        canvas.drawRoundRect(bgRectf, radius, radius, bgPaint);
        bgPaint.setColor(0xffEBEBEB);
        canvas.drawRoundRect(bgRectf, radius, radius, bgPaint);
    }

    private int dp2px(int dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

    @Override
    public void run() {
        int width = flikerBitmap.getWidth();
        while (!isStop && !thread.isInterrupted()) {
            flickerLeft += dp2px(5);
            float progressWidth = (progress / maxProgress) * getMeasuredWidth();
            if (flickerLeft >= progressWidth) {
                flickerLeft = -width;
            }
            postInvalidate();
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public String getProgressText() {
        String text = "";
        if (!isFinish) {
            if (!isStop) {
                text = "正在下载中" + progress + "%";
            } else {
                text = "继续";
            }
        } else {
            text = "下载完成";
        }

        return text;
    }

    public String getProgressTextValue() {
        String text = "" + progress + "%";

        return text;
    }

    public void setProgress(float progress) {
        if (!isStop) {
            if (progress < maxProgress) {
                this.progress = progress;
            } else {
                this.progress = maxProgress;
                finishLoad();
            }
            invalidate();
        }
    }

    public void finishLoad() {
        isFinish = true;
        setStop(true);
    }

    public void setStop(boolean stop) {
        isStop = stop;
        if (isStop) {
            progressColor = stopColor;
            if (thread != null)
                thread.interrupt();
        } else {
            progressColor = loadingColor;
            thread = new Thread(this);
            thread.start();
        }
        invalidate();
    }

    public void setStopSate(boolean stop) {
        isStop = stop;
    }

    /**
     * 重置
     */
    public void reset() {
        setStop(true);
        progress = 0;
        isFinish = false;
        isStop = false;
        progressColor = loadingColor;
        progressText = "";
        flickerLeft = -flikerBitmap.getWidth();
        initPgBitmap();
    }

}
