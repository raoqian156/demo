package com.raoqian.listemptyclickhelper;

import android.app.Activity;
import android.database.DataSetObserver;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by raoqian on 2018/4/16.
 */

public abstract class ListHelper {

    public View helperEmptyView = null;

    /**
     * @param recycle 所绑定的视图,会自动根据视图内容判断
     */
    public void addEmptyViewOnRecycler(final Activity activity, final RecyclerView recycle) {
        if (recycle == null || recycle.getAdapter() == null) {
            return;
        }
//        if (canShowEmptyView(recycle)) {
//            return;
//        }
        this.helperEmptyView = activity.getLayoutInflater().inflate(R.layout.view_empty_on_recycleview, recycle, false);
        final TextView emptyText = (TextView) helperEmptyView.findViewById(R.id.empty_text);
        final ImageView emptyImage = (ImageView) helperEmptyView.findViewById(R.id.empty_image);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onAction(emptyText, emptyImage);
            }
        });
        View view = recycle.getRootView();
        addEmpty2RootView(view);
        recycle.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onItemCountChange(recycle.getAdapter().getItemCount(), emptyText, emptyImage);
                    }
                });
            }
        });
    }

    /**
     * @param activity
     * @param recycle   所绑定的视图,会自动根据视图内容判断,重写 onItemCountChange(final int itemCount, View empty)、onAction(View empty)
     * @param emptyView 自定义空视图
     */
    public void addEmptyViewOnRecycler(final Activity activity, final RecyclerView recycle, final View emptyView) {
        if (recycle == null || recycle.getAdapter() == null) {
            return;
        }
        this.helperEmptyView = emptyView;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onAction(emptyView);
            }
        });
        View view = recycle.getRootView();
        addEmpty2RootView(view);
        recycle.getAdapter().registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onItemCountChange(recycle.getAdapter().getItemCount(), emptyView);
                    }
                });
            }
        });
    }


    /**
     * @param listView 所绑定的视图,会自动根据视图内容判断
     */
    public void addEmptyViewOnListView(final Activity activity, final ListView listView) {
        if (listView == null || listView.getAdapter() == null) {
            return;
        }
//        if (canShowEmptyView(listView)) {
//            return;
//        }
        this.helperEmptyView = activity.getLayoutInflater().inflate(R.layout.view_empty_on_recycleview, listView, false);
        final TextView emptyText = (TextView) helperEmptyView.findViewById(R.id.empty_text);
        final ImageView emptyImage = (ImageView) helperEmptyView.findViewById(R.id.empty_image);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onAction(emptyText, emptyImage);
            }
        });
        View view = listView.getRootView();
        addEmpty2RootView(view);
        listView.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onItemCountChange(listView.getAdapter().getCount(), emptyText, emptyImage);
                    }
                });
            }
        });
    }

    /**
     * @param activity
     * @param listView  所绑定的视图,会自动根据视图内容判断,重写 onItemCountChange(final int itemCount, View empty)、onAction(View empty)
     * @param emptyView 自定义空视图
     */
    public void addEmptyViewOnRecycler(final Activity activity, final ListView listView, final View emptyView) {
        if (listView == null || listView.getAdapter() == null) {
            return;
        }
        this.helperEmptyView = emptyView;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onAction(emptyView);
            }
        });
        View view = listView.getRootView();
        addEmpty2RootView(view);
        listView.getAdapter().registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        onItemCountChange(listView.getAdapter().getCount(), emptyView);
                    }
                });
            }
        });
    }

    /**
     * 本工具类的核心方法 ，给对应的列表视图的父布局添加空视图
     */
    private void addEmpty2RootView(View view) {
        if (view instanceof RelativeLayout) {
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);
            ((RelativeLayout) view).addView(helperEmptyView, lp);
            Log.d("BaseActivity", "is RelativeLayout");
        } else if (view instanceof FrameLayout) {
            Log.d("BaseActivity", "is FrameLayout");
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            ((FrameLayout) view).addView(helperEmptyView, 1, lp);
        } else if (view instanceof LinearLayout) {
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
            ((LinearLayout) view).addView(helperEmptyView, 1, lp);
        }
    }


    public void onItemCountChange(final int itemCount, TextView emptyText, ImageView emptyImage) {
    }

    public void onAction(final TextView emptyText, final ImageView emptyImage) {
    }

    public void onItemCountChange(final int itemCount, View empty) {
    }

    public void onAction(View empty) {
    }

}