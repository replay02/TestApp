package com.kt.testapp.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;

/**
 * Created by kim-young-hyun on 25/01/2019.
 */

public class SqureImageView extends android.support.v7.widget.AppCompatImageView {
    public SqureImageView(Context context) {
        super(context);
    }

    public SqureImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SqureImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 가로 넓이를 기준으로 세로 넓이도 설정
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
