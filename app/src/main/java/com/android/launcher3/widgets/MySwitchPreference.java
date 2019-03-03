package com.android.launcher3.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.SwitchPreference;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.CompoundButton;
import com.android.launcher3.R;

import java.lang.reflect.Field;

/**
 * Created by 25077 on 2019/2/13.
 */

public class MySwitchPreference extends SwitchPreference {
    private final Listener mListener = new Listener();
    private Drawable mIcon;

    public MySwitchPreference(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        setLayoutResource(R.layout.preference_switch);//设置自己的更布局
    }

    public MySwitchPreference(Context context, AttributeSet attrs) {

        this(context, attrs, 0);//替换成我们自己的样式,同样需要配置自己的主题
    }

    public MySwitchPreference(Context context) {

        this(context, null);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        super.onCreateView(parent);//自己处理View的创建
        final LayoutInflater layoutInflater =
                (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View layout = layoutInflater.inflate(getLayoutResource(), parent, false);

        final ViewGroup widgetFrame = (ViewGroup) layout
                .findViewById(R.id.widget_frame);
//        if (widgetFrame != null) {
//            if (getWidgetLayoutResource() != 0) {
//                layoutInflater.inflate(getWidgetLayoutResource(), widgetFrame);
//            } else {
//                widgetFrame.setVisibility(View.GONE);
//            }
//        }

        return layout;
    }

    @Override
    protected void onBindView(View view) {

        super.onBindView(view);//自己处理View 的绑定

        final TextView titleView = (TextView) view.findViewById(R.id.title);
        if (titleView != null) {
            final CharSequence title = getTitle();
            if (!TextUtils.isEmpty(title)) {
                titleView.setText(title);
                titleView.setVisibility(View.VISIBLE);
            } else {
                titleView.setVisibility(View.GONE);
            }
        }

        final TextView summaryView = (TextView) view.findViewById(R.id.summary);
        if (summaryView != null) {
            final CharSequence summary = getSummary();
            if (!TextUtils.isEmpty(summary)) {
                summaryView.setText(summary);
                summaryView.setVisibility(View.VISIBLE);
            } else {
                summaryView.setVisibility(View.GONE);
            }
        }

//        final ImageView imageView = (ImageView) view.findViewById(R.id.icon);
//        try {//反射资源id
//            Class clazz = getClass();
//            Field Field = clazz.getDeclaredField("mIconResId");
//            Field.setAccessible(true);
//            int mIconResId = (int) Field.get(this);
//            if (imageView != null) {
//                if (mIconResId != 0 || mIcon != null) {
//                    if (mIcon == null) {
//                        mIcon = getContext().getResources().getDrawable(mIconResId);
//                    }
//                    if (mIcon != null) {
//                        imageView.setImageDrawable(mIcon);
//                    }
//                }
//                imageView.setVisibility(mIcon != null ? View.VISIBLE : View.GONE);
//            }
//
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        }

        View checkableView = view.findViewById(R.id.switchWidget);
        if (checkableView != null && checkableView instanceof Checkable) {
            if (checkableView instanceof Switch) {
                final Switch switchView = (Switch) checkableView;
                switchView.setOnCheckedChangeListener(null);
            }

            ((Checkable) checkableView).setChecked(isChecked());

            if (checkableView instanceof Switch) {
                final Switch switchView = (Switch) checkableView;
                switchView.setTextOn(getSwitchTextOn());
                switchView.setTextOff(getSwitchTextOff());
                switchView.setOnCheckedChangeListener(mListener);
            }
        }



    }

    private class Listener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!callChangeListener(isChecked)) {
                // Listener didn't like it, change it back.
                // CompoundButton will make sure we don't recurse.
                buttonView.setChecked(!isChecked);
                return;
            }

            MySwitchPreference.this.setChecked(isChecked);
        }
    }
}
