package com.raoqian.statusbaractivity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

/**
 * Created by raoqian on 2018/3/15.
 */

public class CompatStatusBarActivity2 extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        initUI();
    }

    protected View viewTitle;
    protected LinearLayout llRight;

    protected void initUI() {
//        4.4系统下移一个状态栏高度

        viewTitle = findViewById(R.id.view_title);
        llRight = (LinearLayout) findViewById(R.id.right_part);
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) viewTitle.getLayoutParams();
            params.height = getStatusBarHeight(getApplicationContext());
            viewTitle.setVisibility(View.VISIBLE);
            llRight.setPadding(0, getStatusBarHeight(getApplicationContext()), 0, 0);
        }
    }

    // 通过反射获取状态栏高度
    public static int getStatusBarHeight(Context context) {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
