package com.lob.mwhd.views;

/**
 * ******************************************
 * Create a square ImageView for the GridView
 * *******************************************
 */

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class SquaredImageView extends ImageView {

    public SquaredImageView(Context context) {
        super(context);
    }

    public SquaredImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }
}
