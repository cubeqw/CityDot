package com.cubeqw.citydot;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ScrollableListView extends ListView {

    public ScrollableListView (Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ScrollableListView (Context context) {
        super(context);
    }

    public ScrollableListView (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}