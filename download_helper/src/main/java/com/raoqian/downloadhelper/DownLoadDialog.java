package com.raoqian.downloadhelper;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;


public class DownLoadDialog extends Dialog implements View.OnClickListener {

    protected DownLoadDialog(Activity context, int themeResId) {
        super(context, themeResId);
    }

    protected DownLoadDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    protected DownLoadDialog(Activity context, String message, BaseDialogClickListener lis) {
        this((Context) context, message, lis);
    }

    public DownLoadDialog(Context context,String message, BaseDialogClickListener lis) {
        super(context,R.style.default_dialog_style);
        this.msg = message;
        this.listener = lis;
    }

    private String msg;
    protected String mLeft = "稍后再说";
    protected String mRight = "立即下载";
    private Handler mHandler = new Handler(Looper.myLooper());
    private final static int TAG_PROGRESS_CHANGE = 0x01;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_download);
        setCancelable(true);
        onInitView();
    }

    TextView tvMessage;
    TextView tvLeft;
    TextView tvRight;
    ProgressBar progressBar;


    private TextView getTvMessage() {
        return tvMessage;
    }

    protected void onInitView() {
        tvLeft = (TextView) findViewById(R.id.dialog_cancel);
        tvLeft.setOnClickListener(this);
        tvRight = (TextView) findViewById(R.id.dialog_sure);
        tvRight.setOnClickListener(this);
        tvMessage = (TextView) findViewById(R.id.dialog_content);
        tvMessage.setText(msg);
        progressBar = (ProgressBar) findViewById(R.id.progress);
    }


    protected String getString(int res) {
        if (res <= 0) {
            return null;
        }
        return getContext().getString(res);
    }

    BaseDialogClickListener listener;

    @Override
    public void onClick(View v) {
        if (listener != null) {
            if(v.getId()==R.id.dialog_cancel){
                listener.onCancel();
                dismiss();
            }else if(v.getId()==R.id.dialog_sure){
                listener.onSure();
            }
        }
    }

    public void showProgress(){
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                tvLeft.setText("后台下载");
                tvRight.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                tvMessage.setVisibility(View.GONE);
            }
        });
    }

    public void change(final int progress) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                progressBar.setProgress(progress);
            }
        });
    }

    public boolean isLoadFinish() {
        return progressBar.getProgress() == 100;
    }

    public interface BaseDialogClickListener {
        void onSure();

        void onCancel();
    }

    @Override
    public void show() {
        try {
            if (isShowing()) {
                return;
            }
            super.show();
        } catch (Exception e) {
            Log.e("DownLoadDialog", "e = " + e.getMessage());
        }
    }
}
