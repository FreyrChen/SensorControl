package com.sensorcontrol.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by lizhe on 2017/10/10 0010.
 * 目标定在月亮之上，即使失败，也可以落在众星之间。
 */

public class SildeListView extends ListView{

    public SildeListView(Context context) {
        super(context);
    }

    public SildeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
