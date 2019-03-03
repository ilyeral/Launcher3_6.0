package com.android.launcher3.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.PreferenceCategory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.launcher3.R;

/**
 * Created by 25077 on 2019/2/17.
 */


public class MyPreferenceCategory extends PreferenceCategory {

    private String mTitle;
    private int mColor;
    public MyPreferenceCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);
        return LayoutInflater.from(getContext()).inflate(R.layout.preference_category, parent, false);
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);

        //在这里设置颜色值和字体大小
        TextView titleView = (TextView) view.findViewById(R.id.title);
        if (titleView != null) {
            final CharSequence title = getTitle();
            if (!TextUtils.isEmpty(title)) {
                titleView.setText(title);
                titleView.setVisibility(View.VISIBLE);
            } else {
                titleView.setVisibility(View.GONE);
            }
        }

        //设置线的颜色
        View divider = (View) view.findViewById(R.id.preference_divider);
    }
}