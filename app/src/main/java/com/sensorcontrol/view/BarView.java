package com.sensorcontrol.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.sensorcontrol.R;
import com.sensorcontrol.bean.ATBean;

/**
 * Created by lizhe on 2017/9/23 0023.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class BarView extends View{

    private ATBean data;
    private String[] y;
    private String[] x;

    private Paint paintX;
    private Paint paintY;
    private Paint paintText;
    private Paint paintBiao;

    private int window;

    private int height;

    private int X;
    private int Y;

    private Canvas mCanvas;

    public BarView(Context context) {
        super(context);
        init();
    }



    public BarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mCanvas = canvas;
        drawX(canvas);
        drawY(canvas);
        drawText(canvas);
        drawinitBar(canvas);
        drawBar(canvas);
    }

    private void drawText(Canvas canvas) {
        Paint paintText = new Paint();
        paintText.setAntiAlias(true);
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(14);
        int j = 0;
        for (int i = 0; i < 3; i++) {
            if (i == 0) {
                canvas.drawText(data.getAT()[i], X /3 + j -14, height, paintText);
                j = X / 3 + j -14;
            }else {
                j = j + 80;
                canvas.drawText(data.getAT()[i], j, height, paintText);

            }
        }
    }

    private void drawinitBar(Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.device_detail_service));
        int k = 0;
        for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    RectF r = new RectF(X / 3 + k - 14, height - 20 - Y, X / 3 + k + 21, height - 20);
                    canvas.drawRoundRect(r,3,3,paint);
                    k = X / 3 + k - 14;
                } else {
                    k = k + 80;
                    RectF r = new RectF(k, height - 20 - Y, k + 35, height - 20);
                    canvas.drawRoundRect(r,3,3,paint);
                }
        }
    }

    private void drawBar(Canvas canvas) {
        paintBiao = new Paint();
        paintBiao.setAntiAlias(true);
        paintBiao.setColor(getResources().getColor(R.color.guangzhu));
        paintBiao.setStyle(Paint.Style.FILL);
        paintText = new Paint();
        paintText.setColor(Color.RED);
        paintText.setAntiAlias(true);
        int k = 0;
        float num = 0;
        for (int i = 0; i < 3; i++) {
            num =(data.getNum()[i]*(float)(Y/1024f));
            if (num <= 0){
                num = 0;
            }
            if (num > 1024){
                num = Y;
            }
            if (i == 0) {
                canvas.drawText(String.valueOf(data.getNum()[i]), X / 3 + k - 14, height - 20 - num - 10, paintText);
                RectF r = new RectF(X / 3 + k - 14, height - 20 - num, X / 3 + k + 21, height - 20);
                canvas.drawRoundRect(r,3,3,paintBiao);
                k = X / 3 + k - 14;
            } else {
                k = k + 80;
                canvas.drawText(String.valueOf(data.getNum()[i]), k, height - 20 - num - 10, paintText);
                RectF r = new RectF(k, height - 20 - num, k + 35, height - 20);
                canvas.drawRoundRect(r,3,3,paintBiao);
            }
        }
    }



    public void setData(ATBean data) {
        this.data = data;
        postInvalidate();

    }

    private void drawY(Canvas canvas) {
        paintY = new Paint();
        paintY.setAntiAlias(true);
        paintY.setColor(Color.parseColor("#CCCCCC"));//设置画笔颜色
        paintY.setStrokeWidth((float) 0.5);
        canvas.drawLine(10,height - 20,10,10,paintY);
        Paint paintYText1 = new Paint();
        paintYText1.setAntiAlias(true);
        paintYText1.setColor(Color.BLACK);
        paintYText1.setTextSize(12);
        int s = 0;
        for (int i = 0; i < y.length; i++) {
            if (i == 0) {
                canvas.drawText(y[i], 10, height - 10, paintYText1);
                s = height - 10;
            }else {
                canvas.drawText(y[i],10,s - Y/4,paintYText1);
                s = s - Y/4;
            }
        }
    }

    private void drawX(Canvas canvas) {
        window = getMeasuredWidth();
        height = getMeasuredHeight();
        paintX = new Paint();
        paintX.setAntiAlias(true);
        paintX.setColor(Color.parseColor("#CCCCCC"));                    //设置画笔颜色
        paintX.setStrokeWidth((float) 0.5);
        canvas.drawLine(20,height - 20,window - 10,height - 20,paintX);        //绘制直线
        X = window-20;
        Y = height-40;
    }


    private void init() {
        y = new String[]{"0","250","500","750","1024"};
    }
}
