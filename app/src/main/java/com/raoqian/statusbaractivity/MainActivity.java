package com.raoqian.statusbaractivity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.raoqian.downloadhelper.ApkDownLoadUtil;
import com.raoqian.downloadhelper.DownLoadDialog;

import java.io.File;

public class MainActivity extends Activity implements ApkDownLoadUtil.DownloadDialogListener {
    private static Handler handler = new Handler(Looper.myLooper());

    public static Handler getHandler() {
        return handler;
    }

    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//        StatusBarUtil.setTranslucent(this);

        getHandler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showDownload("测试下载", "http://123.207.37.53/exportFile/app-release.apk");
            }
        }, 1000);

//        instance = ScreenSwitchUtils.init(this.getApplicationContext());
        index = 0;
    }


    //####################################################################################################################
    //####################################################################################################################
    //####################################################################################################################
    //####################################################################################################################


    private ScreenSwitchUtils instance;

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        instance = ScreenSwitchUtils.init(this.getApplicationContext());
//    }

//    @Override
//    protected void onStart() {
//        super.onStart();
//        instance.start(this);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        instance.stop();
//    }
//
//    @Override
//    public void onConfigurationChanged(Configuration newConfig) {
//        super.onConfigurationChanged(newConfig);
//        if (instance.isPortrait()) {
//            Log.e("MainActivity", "切换成竖屏");
//// 切换成竖屏
////            LayoutParams params1 = new RelativeLayout.LayoutParams(screenWidth, DensityUtil.dip2px(this, 160));
////            videoView.setLayoutParams(params1);
////            Toast.makeText(getApplicationContext(), 竖屏, 0).show();
////            Log.e(test, 竖屏);
//        } else {
//            Log.e("MainActivity", "切换成横屏");
//// 切换成横屏
////            LayoutParams params1 = new RelativeLayout.LayoutParams(screenHeight, screenWidth);
////            videoView.setLayoutParams(params1);
////            Toast.makeText(getApplicationContext(), 横屏, 0).show();
////            Log.e(test, 横屏);
//        }
//    }

//    @Override
//    public void onClick(View arg0) {
//        switch (arg0.getId()) {
//            case R.id.iv_stretch:
//                instance.toggleScreen();
//                break;
//        }
//    }
    //####################################################################################################################
    //####################################################################################################################
    //####################################################################################################################
    //####################################################################################################################


    DownLoadDialog downDialog;

    public static String getOutputDirPath() {
        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/aa_vedio/");
        if (!file.exists()) {
            file.mkdirs();
        }
        return Environment.getExternalStorageDirectory().getPath() + "/aa_vedio/";
    }

    /**
     * 提示进行下载
     *
     * @param showContent 下载提示信息，来自于后台
     * @param url         下载链接，来自于后台
     */
    private void showDownload(String showContent, final String url) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        if (downDialog != null && downDialog.isShowing()) {
            return;
        }
        downDialog = new DownLoadDialog(MainActivity.this, showContent, new DownLoadDialog.BaseDialogClickListener() {

            @Override
            public void onSure() {
                downDialog.showProgress();
                new ApkDownLoadUtil(MainActivity.this, MainActivity.this, getOutputDirPath()) {

                    @Override
                    protected Uri getApkUrlInAndroid_N(File file) {
                        return null;// FileProvider.getUriForFile(MainActivity.this, "com.zkingsoft.dianxin.fileProvider", file);
                    }
                }.downloadApk(url);
            }

            @Override
            public void onCancel() {
            }
        });
        downDialog.show();
    }

    @Override
    public void progressChange(int progress) {
        Log.e("MainActivity", "progress = " + progress);
        downDialog.change(progress);

    }

    @Override
    public void OnDownloadError(String error) {

    }

    public void test(View view) {
        index++;
        Toast.makeText(this, "index = " + index, Toast.LENGTH_SHORT).show();
    }
}
