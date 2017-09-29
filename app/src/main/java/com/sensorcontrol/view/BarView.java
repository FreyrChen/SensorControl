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
import com.sensorcontrol.util.DensityUtils;

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
    private Context mContext;

    public BarView(Context context) {
        super(context);
        init(context);
    }



    public BarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
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
        paintText.setTextSize(DensityUtils.dip2px(mContext,10));
        int j = X / 5;
        for (int i = 0; i < 3; i++) {
            canvas.drawText(data.getAT()[i], j, height, paintText);
            j = j + juli;
        }
    }

    private int juli;
    private int size;
    private void drawinitBar(Canvas canvas){
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(R.color.device_detail_service));
        int k = X / 5;
        for (int i = 0; i < 3; i++) {
            RectF r = new RectF(k, height - Y, k + size,height - lift);
            canvas.drawRoundRect(r,3,3,paint);
            k = k + juli;
        }
    }

    private void drawBar(Canvas canvas) {
        paintBiao = new Paint();
        paintBiao.setAntiAlias(true);
        paintBiao.setColor(getResources().getColor(R.color.guangzhu));
        paintBiao.setStyle(Paint.Style.FILL);
        paintText = new Paint();
        paintText.setColor(Color.RED);
        paintText.setTextSize(DensityUtils.dip2px(mContext,10));
        paintText.setAntiAlias(true);
        int k = X / 5;
        float num = 0;
        for (int i = 0; i < 3; i++) {
            num =(data.getNum()[i]*(float)(Y/1024f));
            if (num <= 0){
                num = 0;
            }
            if (num > 1024){
                num = Y;
            }
            canvas.drawText(String.valueOf(data.getNum()[i]), k, Y - num, paintText);
            RectF r = new RectF(k, height-lift - num, k + size, height-lift);
            canvas.drawRoundRect(r,3,3,paintBiao);
            k = k + juli;
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
        paintY.setStrokeWidth(DensityUtils.dip2px(mContext,0.5f));
        canvas.drawLine(lift,height - bottom,lift,bottom,paintY);
        Paint paintYText1 = new Paint();
        paintYText1.setAntiAlias(true);
        paintYText1.setColor(Color.BLACK);
        paintYText1.setTextSize(DensityUtils.dip2px(mContext,10));
        int s = 0;
        for (int i = 0; i < y.length; i++) {
            if (i == 0) {
                canvas.drawText(y[i], lift, height, paintYText1);
                s = height;
            }else {
                s = s - Y/4;
                canvas.drawText(y[i],lift,s,paintYText1);

            }
        }
    }

    private void drawX(Canvas canvas) {
        window = getMeasuredWidth();
        height = getMeasuredHeight();
        paintX = new Paint();
        paintX.setAntiAlias(true);
        paintX.setColor(Color.parseColor("#CCCCCC"));                    //设置画笔颜色
        paintX.setStrokeWidth(DensityUtils.dip2px(mContext,0.5f));
        canvas.drawLine(lift,height - bottom,window - lift,height - bottom,paintX);        //绘制直线
        X = window-lift;
        Y = height-bottom;
    }

    private int lift;
    private int bottom;
    private void init(Context context) {
        mContext = context;
        y = new String[]{"0","250","500","750","1024"};
        juli = DensityUtils.dip2px(context,50);
        size = DensityUtils.dip2px(context,30);
        lift = DensityUtils.dip2px(context,10);
        bottom = DensityUtils.dip2px(context,10);
    }
}
