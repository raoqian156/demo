package com.raoqian.statusbaractivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.IntRange;
import android.support.v4.widget.DrawerLayout;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.lang.reflect.Method;

/**
 * Created by raoqian on 2018/1/19.
 */

public class StatusBarUtil {
    public static final int DEFAULT_STATUS_BAR_ALPHA = 112;
    private static final int FAKE_STATUS_BAR_VIEW_ID = android.R.id.statusBarBackground;
    private static final int FAKE_TRANSLUCENT_VIEW_ID = android.R.id.statusBarBackground;
    private static final int TAG_KEY_HAVE_SET_OFFSET = -123;

    /**
     * 设置状态栏颜色
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    public static void setColor(Activity activity, @ColorInt int color) {
        setColor(activity, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 设置状态栏颜色
     *
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */

    public static void setColor(Activity activity, @ColorInt int color, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(calculateStatusColor(color, statusBarAlpha));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
            if (fakeStatusBarView != null) {
                if (fakeStatusBarView.getVisibility() == View.GONE) {
                    fakeStatusBarView.setVisibility(View.VISIBLE);
                }
                fakeStatusBarView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
            } else {
                decorView.addView(createStatusBarView(activity, color, statusBarAlpha));
            }
            setRootView(activity);
        }
    }

    /**
     * 为滑动返回界面设置状态栏颜色
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     */
    public static void setColorForSwipeBack(Activity activity, int color) {
        setColorForSwipeBack(activity, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 为滑动返回界面设置状态栏颜色
     *
     * @param activity       需要设置的activity
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */
    public static void setColorForSwipeBack(Activity activity, @ColorInt int color,
                                            @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            ViewGroup contentView = ((ViewGroup) activity.findViewById(android.R.id.content));
            View rootView = contentView.getChildAt(0);
            int statusBarHeight = getStatusBarHeight(activity);
//            if (rootView != null && rootView instanceof CoordinatorLayout) {
//                final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) rootView;
//                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                    coordinatorLayout.setFitsSystemWindows(false);
//                    contentView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
//                    boolean isNeedRequestLayout = contentView.getPaddingTop() < statusBarHeight;
//                    if (isNeedRequestLayout) {
//                        contentView.setPadding(0, statusBarHeight, 0, 0);
//                        coordinatorLayout.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                coordinatorLayout.requestLayout();
//                            }
//                        });
//                    }
//                } else {
//                    coordinatorLayout.setStatusBarBackgroundColor(calculateStatusColor(color, statusBarAlpha));
//                }
//            } else {
            contentView.setPadding(0, statusBarHeight, 0, 0);
            contentView.setBackgroundColor(calculateStatusColor(color, statusBarAlpha));
//            }
            setTransparentForWindow(activity);
        }
    }

    /**
     * 设置状态栏纯色 不加半透明效果
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    public static void setColorNoTranslucent(Activity activity, @ColorInt int color) {
        setColor(activity, color, 0);
    }

    /**
     * 设置状态栏颜色(5.0以下无半透明效果,不建议使用)
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     */
    @Deprecated
    public static void setColorDiff(Activity activity, @ColorInt int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        // 移除半透明矩形,以免叠加
        View fakeStatusBarView = contentView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE);
            }
            fakeStatusBarView.setBackgroundColor(color);
        } else {
            contentView.addView(createStatusBarView(activity, color));
        }
        setRootView(activity);
    }

    /**
     * 使状态栏半透明
     * <p>
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity 需要设置的activity
     */
//    compile 'com.jaeger.statu、sbarutil:library:1.4.0'
    public static void setTranslucent(Activity activity) {
        usingTintMA(activity);
        setTranslucent(activity, DEFAULT_STATUS_BAR_ALPHA);
    }

    //    compile 'com.readystatesoftware.systembartint:systembartint:1.0.3'
    private static void usingTintMA(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = activity.getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
            SystemBarTintManager mTintManager = new SystemBarTintManager(activity);
            mTintManager.setStatusBarTintEnabled(true);

            mTintManager.setStatusBarTintResource(R.drawable.trance_change_blue);//通知栏所需颜色
        }
    }

    /**
     * 使状态栏半透明
     * <p>
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity       需要设置的activity
     * @param statusBarAlpha 状态栏透明度
     */
    public static void setTranslucent(Activity activity, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setTransparent(activity);
        addTranslucentView(activity, statusBarAlpha);
    }

    /**
     * 针对根布局是 CoordinatorLayout, 使状态栏半透明
     * <p>
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity       需要设置的activity
     * @param statusBarAlpha 状态栏透明度
     */
    public static void setTranslucentForCoordinatorLayout(Activity activity, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        addTranslucentView(activity, statusBarAlpha);
    }

    /**
     * 设置状态栏全透明
     *
     * @param activity 需要设置的activity
     */
    public static void setTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        transparentStatusBar(activity);
        setRootView(activity);
    }

    /**
     * 使状态栏透明(5.0以上半透明效果,不建议使用)
     * <p>
     * 适用于图片作为背景的界面,此时需要图片填充到状态栏
     *
     * @param activity 需要设置的activity
     */
    @Deprecated
    public static void setTranslucentDiff(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            setRootView(activity);
        }
    }

    /**
     * 为DrawerLayout 布局设置状态栏变色
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     * @param color        状态栏颜色值
     */
    public static void setColorForDrawerLayout(Activity activity, DrawerLayout drawerLayout, @ColorInt int color) {
        setColorForDrawerLayout(activity, drawerLayout, color, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 为DrawerLayout 布局设置状态栏颜色,纯色
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     * @param color        状态栏颜色值
     */
    public static void setColorNoTranslucentForDrawerLayout(Activity activity, DrawerLayout drawerLayout, @ColorInt int color) {
        setColorForDrawerLayout(activity, drawerLayout, color, 0);
    }

    /**
     * 为DrawerLayout 布局设置状态栏变色
     *
     * @param activity       需要设置的activity
     * @param drawerLayout   DrawerLayout
     * @param color          状态栏颜色值
     * @param statusBarAlpha 状态栏透明度
     */
    public static void setColorForDrawerLayout(Activity activity, DrawerLayout drawerLayout, @ColorInt int color,
                                               @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        // 生成一个状态栏大小的矩形
        // 添加 statusBarView 到布局中
        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
        View fakeStatusBarView = contentLayout.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (fakeStatusBarView != null) {
            if (fakeStatusBarView.getVisibility() == View.GONE) {
                fakeStatusBarView.setVisibility(View.VISIBLE);
            }
            fakeStatusBarView.setBackgroundColor(color);
        } else {
            contentLayout.addView(createStatusBarView(activity, color), 0);
        }
        // 内容布局不是 LinearLayout 时,设置padding top
        if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
            contentLayout.getChildAt(1)
                    .setPadding(contentLayout.getPaddingLeft(), getStatusBarHeight(activity) + contentLayout.getPaddingTop(),
                            contentLayout.getPaddingRight(), contentLayout.getPaddingBottom());
        }
        // 设置属性
        setDrawerLayoutProperty(drawerLayout, contentLayout);
        addTranslucentView(activity, statusBarAlpha);
    }

    /**
     * 设置 DrawerLayout 属性
     *
     * @param drawerLayout              DrawerLayout
     * @param drawerLayoutContentLayout DrawerLayout 的内容布局
     */
    private static void setDrawerLayoutProperty(DrawerLayout drawerLayout, ViewGroup drawerLayoutContentLayout) {
        ViewGroup drawer = (ViewGroup) drawerLayout.getChildAt(1);
        drawerLayout.setFitsSystemWindows(false);
        drawerLayoutContentLayout.setFitsSystemWindows(false);
        drawerLayoutContentLayout.setClipToPadding(true);
        drawer.setFitsSystemWindows(false);
    }

    /**
     * 为DrawerLayout 布局设置状态栏变色(5.0以下无半透明效果,不建议使用)
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     * @param color        状态栏颜色值
     */
    @Deprecated
    public static void setColorForDrawerLayoutDiff(Activity activity, DrawerLayout drawerLayout, @ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 生成一个状态栏大小的矩形
            ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
            View fakeStatusBarView = contentLayout.findViewById(FAKE_STATUS_BAR_VIEW_ID);
            if (fakeStatusBarView != null) {
                if (fakeStatusBarView.getVisibility() == View.GONE) {
                    fakeStatusBarView.setVisibility(View.VISIBLE);
                }
                fakeStatusBarView.setBackgroundColor(calculateStatusColor(color, DEFAULT_STATUS_BAR_ALPHA));
            } else {
                // 添加 statusBarView 到布局中
                contentLayout.addView(createStatusBarView(activity, color), 0);
            }
            // 内容布局不是 LinearLayout 时,设置padding top
            if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
                contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0);
            }
            // 设置属性
            setDrawerLayoutProperty(drawerLayout, contentLayout);
        }
    }

    /**
     * 为 DrawerLayout 布局设置状态栏透明
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    public static void setTranslucentForDrawerLayout(Activity activity, DrawerLayout drawerLayout) {
        setTranslucentForDrawerLayout(activity, drawerLayout, DEFAULT_STATUS_BAR_ALPHA);
    }

    /**
     * 为 DrawerLayout 布局设置状态栏透明
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    public static void setTranslucentForDrawerLayout(Activity activity, DrawerLayout drawerLayout,
                                                     @IntRange(from = 0, to = 255) int statusBarAlpha) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setTransparentForDrawerLayout(activity, drawerLayout);
        addTranslucentView(activity, statusBarAlpha);
    }

    /**
     * 为 DrawerLayout 布局设置状态栏透明
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    public static void setTransparentForDrawerLayout(Activity activity, DrawerLayout drawerLayout) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
        // 内容布局不是 LinearLayout 时,设置padding top
        if (!(contentLayout instanceof LinearLayout) && contentLayout.getChildAt(1) != null) {
            contentLayout.getChildAt(1).setPadding(0, getStatusBarHeight(activity), 0, 0);
        }

        // 设置属性
        setDrawerLayoutProperty(drawerLayout, contentLayout);
    }

    /**
     * 为 DrawerLayout 布局设置状态栏透明(5.0以上半透明效果,不建议使用)
     *
     * @param activity     需要设置的activity
     * @param drawerLayout DrawerLayout
     */
    @Deprecated
    public static void setTranslucentForDrawerLayoutDiff(Activity activity, DrawerLayout drawerLayout) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 设置状态栏透明
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 设置内容布局属性
            ViewGroup contentLayout = (ViewGroup) drawerLayout.getChildAt(0);
            contentLayout.setFitsSystemWindows(true);
            contentLayout.setClipToPadding(true);
            // 设置抽屉布局属性
            ViewGroup vg = (ViewGroup) drawerLayout.getChildAt(1);
            vg.setFitsSystemWindows(false);
            // 设置 DrawerLayout 属性
            drawerLayout.setFitsSystemWindows(false);
        }
    }

    /**
     * 为头部是 ImageView 的界面设置状态栏全透明
     *
     * @param activity       需要设置的activity
     * @param needOffsetView 需要向下偏移的 View
     */
    public static void setTransparentForImageView(Activity activity, View needOffsetView) {
        setTranslucentForImageView(activity, 0, needOffsetView);
    }

    /**
     * 为头部是 ImageView 的界面设置状态栏透明(使用默认透明度)
     *
     * @param activity       需要设置的activity
     * @param needOffsetView 需要向下偏移的 View
     */
    public static void setTranslucentForImageView(Activity activity, View needOffsetView) {
        setTranslucentForImageView(activity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView);
    }

    /**
     * 为头部是 ImageView 的界面设置状态栏透明
     *
     * @param activity       需要设置的activity
     * @param statusBarAlpha 状态栏透明度
     * @param needOffsetView 需要向下偏移的 View
     */
    public static void setTranslucentForImageView(Activity activity, @IntRange(from = 0, to = 255) int statusBarAlpha,
                                                  View needOffsetView) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return;
        }
        setTransparentForWindow(activity);
        addTranslucentView(activity, statusBarAlpha);
        if (needOffsetView != null) {
            Object haveSetOffset = needOffsetView.getTag(TAG_KEY_HAVE_SET_OFFSET);
            if (haveSetOffset != null && (Boolean) haveSetOffset) {
                return;
            }
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) needOffsetView.getLayoutParams();
            layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin + getStatusBarHeight(activity),
                    layoutParams.rightMargin, layoutParams.bottomMargin);
            needOffsetView.setTag(TAG_KEY_HAVE_SET_OFFSET, true);
        }
    }

    /**
     * 为 fragment 头部是 ImageView 的设置状态栏透明
     *
     * @param activity       fragment 对应的 activity
     * @param needOffsetView 需要向下偏移的 View
     */
    public static void setTranslucentForImageViewInFragment(Activity activity, View needOffsetView) {
        setTranslucentForImageViewInFragment(activity, DEFAULT_STATUS_BAR_ALPHA, needOffsetView);
    }

    /**
     * 为 fragment 头部是 ImageView 的设置状态栏透明
     *
     * @param activity       fragment 对应的 activity
     * @param needOffsetView 需要向下偏移的 View
     */
    public static void setTransparentForImageViewInFragment(Activity activity, View needOffsetView) {
        setTranslucentForImageViewInFragment(activity, 0, needOffsetView);
    }

    /**
     * 为 fragment 头部是 ImageView 的设置状态栏透明
     *
     * @param activity       fragment 对应的 activity
     * @param statusBarAlpha 状态栏透明度
     * @param needOffsetView 需要向下偏移的 View
     */
    public static void setTranslucentForImageViewInFragment(Activity activity, @IntRange(from = 0, to = 255) int statusBarAlpha,
                                                            View needOffsetView) {
        setTranslucentForImageView(activity, statusBarAlpha, needOffsetView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            clearPreviousSetting(activity);
        }
    }

    /**
     * 隐藏伪状态栏 View
     *
     * @param activity 调用的 Activity
     */
    public static void hideFakeStatusBarView(Activity activity) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (fakeStatusBarView != null) {
            fakeStatusBarView.setVisibility(View.GONE);
        }
        View fakeTranslucentView = decorView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
        if (fakeTranslucentView != null) {
            fakeTranslucentView.setVisibility(View.GONE);
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void clearPreviousSetting(Activity activity) {
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        View fakeStatusBarView = decorView.findViewById(FAKE_STATUS_BAR_VIEW_ID);
        if (fakeStatusBarView != null) {
            decorView.removeView(fakeStatusBarView);
            ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(android.R.id.content)).getChildAt(0);
            rootView.setPadding(0, 0, 0, 0);
        }
    }

    /**
     * 添加半透明矩形条
     *
     * @param activity       需要设置的 activity
     * @param statusBarAlpha 透明值
     */
    private static void addTranslucentView(Activity activity, @IntRange(from = 0, to = 255) int statusBarAlpha) {
        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        View fakeTranslucentView = contentView.findViewById(FAKE_TRANSLUCENT_VIEW_ID);
        if (fakeTranslucentView != null) {
            if (fakeTranslucentView.getVisibility() == View.GONE) {
                fakeTranslucentView.setVisibility(View.VISIBLE);
            }
            fakeTranslucentView.setBackgroundColor(Color.argb(statusBarAlpha, 0, 0, 0));
        } else {
            contentView.addView(createTranslucentStatusBarView(activity, statusBarAlpha));
        }
    }

    /**
     * 生成一个和状态栏大小相同的彩色矩形条
     *
     * @param activity 需要设置的 activity
     * @param color    状态栏颜色值
     * @return 状态栏矩形条
     */
    private static View createStatusBarView(Activity activity, @ColorInt int color) {
        return createStatusBarView(activity, color, 0);
    }

    /**
     * 生成一个和状态栏大小相同的半透明矩形条
     *
     * @param activity 需要设置的activity
     * @param color    状态栏颜色值
     * @param alpha    透明值
     * @return 状态栏矩形条
     */
    private static View createStatusBarView(Activity activity, @ColorInt int color, int alpha) {
        // 绘制一个和状态栏一样高的矩形
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(calculateStatusColor(color, alpha));
        statusBarView.setId(FAKE_STATUS_BAR_VIEW_ID);
        return statusBarView;
    }

    /**
     * 设置根布局参数
     */
    private static void setRootView(Activity activity) {
        ViewGroup parent = (ViewGroup) activity.findViewById(android.R.id.content);
        for (int i = 0, count = parent.getChildCount(); i < count; i++) {
            View childView = parent.getChildAt(i);
            if (childView instanceof ViewGroup) {
                childView.setFitsSystemWindows(true);
                ((ViewGroup) childView).setClipToPadding(true);
            }
        }
    }

    /**
     * 设置透明
     */
    private static void setTransparentForWindow(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            activity.getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 使状态栏透明
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static void transparentStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
        } else {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 创建半透明矩形 View
     *
     * @param alpha 透明值
     * @return 半透明 View
     */
    private static View createTranslucentStatusBarView(Activity activity, int alpha) {
        // 绘制一个和状态栏一样高的矩形
        View statusBarView = new View(activity);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(activity));
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(Color.argb(alpha, 0, 0, 0));
        statusBarView.setId(FAKE_TRANSLUCENT_VIEW_ID);
        return statusBarView;
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 计算状态栏颜色
     *
     * @param color color值
     * @param alpha alpha值
     * @return 最终的状态栏颜色
     */
    private static int calculateStatusColor(@ColorInt int color, int alpha) {
        if (alpha == 0) {
            return color;
        }
        float a = 1 - alpha / 255f;
        int red = color >> 16 & 0xff;
        int green = color >> 8 & 0xff;
        int blue = color & 0xff;
        red = (int) (red * a + 0.5);
        green = (int) (green * a + 0.5);
        blue = (int) (blue * a + 0.5);
        return 0xff << 24 | red << 16 | green << 8 | blue;
    }

    static {
        // Android allows a system property to override the presence of the navigation bar.
        // Used by the emulator.
        // See https://github.com/android/platform_frameworks_base/blob/master/policy/src/com/android/internal/policy/impl/PhoneWindowManager.java#L1076
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                Class c = Class.forName("android.os.SystemProperties");
                Method m = c.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                sNavBarOverride = (String) m.invoke(null, "qemu.hw.mainkeys");
            } catch (Throwable e) {
                sNavBarOverride = null;
            }
        }
    }

    static String sNavBarOverride;

    public static class SystemBarTintManager {

        /**
         * The default system bar tint color value.
         */
        public static final int DEFAULT_TINT_COLOR = 0x99000000;


        private final SystemBarConfig mConfig;
        private boolean mStatusBarAvailable;
        private boolean mNavBarAvailable;
        private boolean mStatusBarTintEnabled;
        private boolean mNavBarTintEnabled;
        private View mStatusBarTintView;
        private View mNavBarTintView;

        /**
         * Constructor. Call this in the host activity onCreate method after its
         * content view has been set. You should always create new instances when
         * the host activity is recreated.
         *
         * @param activity The host activity.
         */
        @TargetApi(19)
        public SystemBarTintManager(Activity activity) {

            Window win = activity.getWindow();
            ViewGroup decorViewGroup = (ViewGroup) win.getDecorView();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                // check theme attrs
                int[] attrs = {android.R.attr.windowTranslucentStatus,
                        android.R.attr.windowTranslucentNavigation};
                TypedArray a = activity.obtainStyledAttributes(attrs);
                try {
                    mStatusBarAvailable = a.getBoolean(0, false);
                    mNavBarAvailable = a.getBoolean(1, false);
                } finally {
                    a.recycle();
                }

                // check window flags
                WindowManager.LayoutParams winParams = win.getAttributes();
                int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
                if ((winParams.flags & bits) != 0) {
                    mStatusBarAvailable = true;
                }
                bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;
                if ((winParams.flags & bits) != 0) {
                    mNavBarAvailable = true;
                }
            }

            mConfig = new SystemBarConfig(activity, mStatusBarAvailable, mNavBarAvailable);
            // device might not have virtual navigation keys
            if (!mConfig.hasNavigtionBar()) {
                mNavBarAvailable = false;
            }

            if (mStatusBarAvailable) {
                setupStatusBarView(activity, decorViewGroup);
            }
            if (mNavBarAvailable) {
                setupNavBarView(activity, decorViewGroup);
            }

        }

        /**
         * Enable tinting of the system status bar.
         * <p>
         * If the platform is running Jelly Bean or earlier, or translucent system
         * UI modes have not been enabled in either the theme or via window flags,
         * then this method does nothing.
         *
         * @param enabled True to enable tinting, false to disable it (default).
         */
        public void setStatusBarTintEnabled(boolean enabled) {
            mStatusBarTintEnabled = enabled;
            if (mStatusBarAvailable) {
                mStatusBarTintView.setVisibility(enabled ? View.VISIBLE : View.GONE);
            }
        }

        /**
         * Enable tinting of the system navigation bar.
         * <p>
         * If the platform does not have soft navigation keys, is running Jelly Bean
         * or earlier, or translucent system UI modes have not been enabled in either
         * the theme or via window flags, then this method does nothing.
         *
         * @param enabled True to enable tinting, false to disable it (default).
         */
        public void setNavigationBarTintEnabled(boolean enabled) {
            mNavBarTintEnabled = enabled;
            if (mNavBarAvailable) {
                mNavBarTintView.setVisibility(enabled ? View.VISIBLE : View.GONE);
            }
        }

        /**
         * Apply the specified color tint to all system UI bars.
         *
         * @param color The color of the background tint.
         */
        public void setTintColor(int color) {
            setStatusBarTintColor(color);
            setNavigationBarTintColor(color);
        }

        /**
         * Apply the specified drawable or color resource to all system UI bars.
         *
         * @param res The identifier of the resource.
         */
        public void setTintResource(int res) {
            setStatusBarTintResource(res);
            setNavigationBarTintResource(res);
        }

        /**
         * Apply the specified drawable to all system UI bars.
         *
         * @param drawable The drawable to use as the background, or null to remove it.
         */
        public void setTintDrawable(Drawable drawable) {
            setStatusBarTintDrawable(drawable);
            setNavigationBarTintDrawable(drawable);
        }

        /**
         * Apply the specified alpha to all system UI bars.
         *
         * @param alpha The alpha to use
         */
        public void setTintAlpha(float alpha) {
            setStatusBarAlpha(alpha);
            setNavigationBarAlpha(alpha);
        }

        /**
         * Apply the specified color tint to the system status bar.
         *
         * @param color The color of the background tint.
         */
        public void setStatusBarTintColor(int color) {
            if (mStatusBarAvailable) {
                mStatusBarTintView.setBackgroundColor(color);
            }
        }

        /**
         * Apply the specified drawable or color resource to the system status bar.
         *
         * @param res The identifier of the resource.
         */
        public void setStatusBarTintResource(int res) {
            if (mStatusBarAvailable) {
                mStatusBarTintView.setBackgroundResource(res);
            }
        }

        /**
         * Apply the specified drawable to the system status bar.
         *
         * @param drawable The drawable to use as the background, or null to remove it.
         */
        @SuppressWarnings("deprecation")
        public void setStatusBarTintDrawable(Drawable drawable) {
            if (mStatusBarAvailable) {
                mStatusBarTintView.setBackgroundDrawable(drawable);
            }
        }

        /**
         * Apply the specified alpha to the system status bar.
         *
         * @param alpha The alpha to use
         */
        @TargetApi(11)
        public void setStatusBarAlpha(float alpha) {
            if (mStatusBarAvailable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mStatusBarTintView.setAlpha(alpha);
            }
        }

        /**
         * Apply the specified color tint to the system navigation bar.
         *
         * @param color The color of the background tint.
         */
        public void setNavigationBarTintColor(int color) {
            if (mNavBarAvailable) {
                mNavBarTintView.setBackgroundColor(color);
            }
        }

        /**
         * Apply the specified drawable or color resource to the system navigation bar.
         *
         * @param res The identifier of the resource.
         */
        public void setNavigationBarTintResource(int res) {
            if (mNavBarAvailable) {
                mNavBarTintView.setBackgroundResource(res);
            }
        }

        /**
         * Apply the specified drawable to the system navigation bar.
         *
         * @param drawable The drawable to use as the background, or null to remove it.
         */
        @SuppressWarnings("deprecation")
        public void setNavigationBarTintDrawable(Drawable drawable) {
            if (mNavBarAvailable) {
                mNavBarTintView.setBackgroundDrawable(drawable);
            }
        }

        /**
         * Apply the specified alpha to the system navigation bar.
         *
         * @param alpha The alpha to use
         */
        @TargetApi(11)
        public void setNavigationBarAlpha(float alpha) {
            if (mNavBarAvailable && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                mNavBarTintView.setAlpha(alpha);
            }
        }

        /**
         * Get the system bar configuration.
         *
         * @return The system bar configuration for the current device configuration.
         */
        public SystemBarConfig getConfig() {
            return mConfig;
        }

        /**
         * Is tinting enabled for the system status bar?
         *
         * @return True if enabled, False otherwise.
         */
        public boolean isStatusBarTintEnabled() {
            return mStatusBarTintEnabled;
        }

        /**
         * Is tinting enabled for the system navigation bar?
         *
         * @return True if enabled, False otherwise.
         */
        public boolean isNavBarTintEnabled() {
            return mNavBarTintEnabled;
        }

        private void setupStatusBarView(Context context, ViewGroup decorViewGroup) {
            mStatusBarTintView = new View(context);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mConfig.getStatusBarHeight());
            params.gravity = Gravity.TOP;
            if (mNavBarAvailable && !mConfig.isNavigationAtBottom()) {
                params.rightMargin = mConfig.getNavigationBarWidth();
            }
            mStatusBarTintView.setLayoutParams(params);
            mStatusBarTintView.setBackgroundColor(DEFAULT_TINT_COLOR);
            mStatusBarTintView.setVisibility(View.GONE);
            decorViewGroup.addView(mStatusBarTintView);
        }

        private void setupNavBarView(Context context, ViewGroup decorViewGroup) {
            mNavBarTintView = new View(context);
            FrameLayout.LayoutParams params;
            if (mConfig.isNavigationAtBottom()) {
                params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, mConfig.getNavigationBarHeight());
                params.gravity = Gravity.BOTTOM;
            } else {
                params = new FrameLayout.LayoutParams(mConfig.getNavigationBarWidth(), FrameLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.RIGHT;
            }
            mNavBarTintView.setLayoutParams(params);
            mNavBarTintView.setBackgroundColor(DEFAULT_TINT_COLOR);
            mNavBarTintView.setVisibility(View.GONE);
            decorViewGroup.addView(mNavBarTintView);
        }

        /**
         * Class which describes system bar sizing and other characteristics for the current
         * device configuration.
         */
        public class SystemBarConfig {

            private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
            private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
            private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
            private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";
            private static final String SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar";

            private final boolean mTranslucentStatusBar;
            private final boolean mTranslucentNavBar;
            private final int mStatusBarHeight;
            private final int mActionBarHeight;
            private final boolean mHasNavigationBar;
            private final int mNavigationBarHeight;
            private final int mNavigationBarWidth;
            private final boolean mInPortrait;
            private final float mSmallestWidthDp;

            private SystemBarConfig(Activity activity, boolean translucentStatusBar, boolean traslucentNavBar) {
                Resources res = activity.getResources();
                mInPortrait = (res.getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT);
                mSmallestWidthDp = getSmallestWidthDp(activity);
                mStatusBarHeight = getInternalDimensionSize(res, STATUS_BAR_HEIGHT_RES_NAME);
                mActionBarHeight = getActionBarHeight(activity);
                mNavigationBarHeight = getNavigationBarHeight(activity);
                mNavigationBarWidth = getNavigationBarWidth(activity);
                mHasNavigationBar = (mNavigationBarHeight > 0);
                mTranslucentStatusBar = translucentStatusBar;
                mTranslucentNavBar = traslucentNavBar;
            }

            @TargetApi(14)
            private int getActionBarHeight(Context context) {
                int result = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    TypedValue tv = new TypedValue();
                    context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
                    result = context.getResources().getDimensionPixelSize(tv.resourceId);
                }
                return result;
            }

            @TargetApi(14)
            private int getNavigationBarHeight(Context context) {
                Resources res = context.getResources();
                int result = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    if (hasNavBar(context)) {
                        String key;
                        if (mInPortrait) {
                            key = NAV_BAR_HEIGHT_RES_NAME;
                        } else {
                            key = NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME;
                        }
                        return getInternalDimensionSize(res, key);
                    }
                }
                return result;
            }

            @TargetApi(14)
            private int getNavigationBarWidth(Context context) {
                Resources res = context.getResources();
                int result = 0;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                    if (hasNavBar(context)) {
                        return getInternalDimensionSize(res, NAV_BAR_WIDTH_RES_NAME);
                    }
                }
                return result;
            }

            @TargetApi(14)
            private boolean hasNavBar(Context context) {
                Resources res = context.getResources();
                int resourceId = res.getIdentifier(SHOW_NAV_BAR_RES_NAME, "bool", "android");
                if (resourceId != 0) {
                    boolean hasNav = res.getBoolean(resourceId);
                    // check override flag (see static block)
                    if ("1".equals(sNavBarOverride)) {
                        hasNav = false;
                    } else if ("0".equals(sNavBarOverride)) {
                        hasNav = true;
                    }
                    return hasNav;
                } else { // fallback
                    return !ViewConfiguration.get(context).hasPermanentMenuKey();
                }
            }

            private int getInternalDimensionSize(Resources res, String key) {
                int result = 0;
                int resourceId = res.getIdentifier(key, "dimen", "android");
                if (resourceId > 0) {
                    result = res.getDimensionPixelSize(resourceId);
                }
                return result;
            }

            @SuppressLint("NewApi")
            private float getSmallestWidthDp(Activity activity) {
                DisplayMetrics metrics = new DisplayMetrics();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
                } else {
                    // TODO this is not correct, but we don't really care pre-kitkat
                    activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                }
                float widthDp = metrics.widthPixels / metrics.density;
                float heightDp = metrics.heightPixels / metrics.density;
                return Math.min(widthDp, heightDp);
            }

            /**
             * Should a navigation bar appear at the bottom of the screen in the current
             * device configuration? A navigation bar may appear on the right side of
             * the screen in certain configurations.
             *
             * @return True if navigation should appear at the bottom of the screen, False otherwise.
             */
            public boolean isNavigationAtBottom() {
                return (mSmallestWidthDp >= 600 || mInPortrait);
            }

            /**
             * Get the height of the system status bar.
             *
             * @return The height of the status bar (in pixels).
             */
            public int getStatusBarHeight() {
                return mStatusBarHeight;
            }

            /**
             * Get the height of the action bar.
             *
             * @return The height of the action bar (in pixels).
             */
            public int getActionBarHeight() {
                return mActionBarHeight;
            }

            /**
             * Does this device have a system navigation bar?
             *
             * @return True if this device uses soft key navigation, False otherwise.
             */
            public boolean hasNavigtionBar() {
                return mHasNavigationBar;
            }

            /**
             * Get the height of the system navigation bar.
             *
             * @return The height of the navigation bar (in pixels). If the device does not have
             * soft navigation keys, this will always return 0.
             */
            public int getNavigationBarHeight() {
                return mNavigationBarHeight;
            }

            /**
             * Get the width of the system navigation bar when it is placed vertically on the screen.
             *
             * @return The width of the navigation bar (in pixels). If the device does not have
             * soft navigation keys, this will always return 0.
             */
            public int getNavigationBarWidth() {
                return mNavigationBarWidth;
            }

            /**
             * Get the layout inset for any system UI that appears at the top of the screen.
             *
             * @param withActionBar True to include the height of the action bar, False otherwise.
             * @return The layout inset (in pixels).
             */
            public int getPixelInsetTop(boolean withActionBar) {
                return (mTranslucentStatusBar ? mStatusBarHeight : 0) + (withActionBar ? mActionBarHeight : 0);
            }

            /**
             * Get the layout inset for any system UI that appears at the bottom of the screen.
             *
             * @return The layout inset (in pixels).
             */
            public int getPixelInsetBottom() {
                if (mTranslucentNavBar && isNavigationAtBottom()) {
                    return mNavigationBarHeight;
                } else {
                    return 0;
                }
            }

            /**
             * Get the layout inset for any system UI that appears at the right of the screen.
             *
             * @return The layout inset (in pixels).
             */
            public int getPixelInsetRight() {
                if (mTranslucentNavBar && !isNavigationAtBottom()) {
                    return mNavigationBarWidth;
                } else {
                    return 0;
                }
            }

        }

    }
}
