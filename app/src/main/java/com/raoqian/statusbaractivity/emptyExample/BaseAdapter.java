package com.raoqian.statusbaractivity.emptyExample;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAdapter<VH extends BaseHolder, D> extends RecyclerView.Adapter<VH> {

    public BaseAdapter(Activity context) {
        mContext = context;
    }

    public BaseAdapter(Activity context, AdapterHelper helper) {
        mContext = context;
        mHelper = helper;
    }

    protected Activity mContext;
    protected AdapterHelper mHelper;

    public AdapterHelper getHelper() {
        return mHelper;
    }

    public void setHelper(AdapterHelper mHelper) {
        this.mHelper = mHelper;
    }

    protected Activity getContext() {
        return mContext;
    }

    protected LayoutInflater getInflater() {
        return getContext().getLayoutInflater();
    }

    protected View getView(int layoutRes, ViewGroup parent) {
        return getView(layoutRes, parent, false);
    }

    protected View getView(int layoutRes, ViewGroup parent, boolean attachToRoot) {
        return getContext().getLayoutInflater().inflate(layoutRes, parent, attachToRoot);
    }

//    @Override  L75加了初始值  -  出问题的话放开
//    public int getItemViewType(int position) {
//        if (getData() == null) {
//        }
//        return super.getItemViewType(position);
//    }

    public final int LIST_FOOT = 1;

    protected String getString(int res) {
        return mContext.getString(res);
    }


    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewVHype) {
//        switch (viewVHype) {
//            case LIST_FOOT:
//                View view_no_data = LayoutInflater.from(mContext).inflate(R.layout.view_no_data, parent, false);
//                return (VH) new MarginViewHolder(view_no_data);
//            default:
//        }
        return onCreateHolder(parent, viewVHype);
    }

    public abstract VH onCreateHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(VH holder, int position) {
//        if (holder instanceof MarginViewHolder) {
//            return;
//        }
        if (getDataItem(position) == null) {
            return;
        }
        onBindingHolder(holder, position);
    }

    public abstract void onBindingHolder(VH holder, int position);

    public List<D> getData() {
        return this.mData;
    }

    private List<D> mData = new ArrayList<>();

    public void setData(List<D> data) {
        if (data == null || data.size() == 0) {
            mData.clear();
            notifyDataSetChanged();
            return;
        }
        this.mData = data;
        notifyDataSetChanged();
    }

    public D getDataItem(int position) {
        if (position < getItemCount()) {
            return getData().get(position);
        }
        return null;
    }

    public void removeDataItem(int position) {
        if (position < getItemCount()) {
            mData.remove(position);
            notifyDataSetChanged();
        }
    }

    public void removeDataItem(D item) {
        if (item != null) {
            mData.remove(item);
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return getData() == null ? 0 : getData().size();
    }

    public void addData(List<D> data) {
        if (mData != null && data != null && data.size() > 0) {
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void addFooterData() {
        getData().add(null);
        notifyDataSetChanged();
    }


    public interface AdapterHelper {
        /**
         * 适配器助手
         *
         * @param action 动作区别
         * @param data   参数，一般为String 或 position,由具体实现自行转换
         */
        void onResult(int action, Object data);
    }
}
