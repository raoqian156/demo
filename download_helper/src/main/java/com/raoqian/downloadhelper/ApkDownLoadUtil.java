package com.raoqian.downloadhelper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by raoqian on 2017/8/16.
 */

public abstract class ApkDownLoadUtil {

    public ApkDownLoadUtil(Context context, DownloadDialogListener listener, String downSavePath) {
        mContext = context;
        downloadDialogListener = listener;
        this.savePath = downSavePath;
    }

    static Context mContext;

    DownloadDialogListener downloadDialogListener;

    public String getApkUrl() {
        return apkUrl;
    }

    public void setApkUrl(String apkUrl) {
        this.apkUrl = apkUrl;
    }

    public String apkUrl; // 返回的安装包url
    String savePath = "";//BaseApplication.getOutputDirPath() + "apk/"; // 保存文件夹的地址
    public static String saveFileName = "";


    private static final int DOWN_UPDATE = 1;

    private static final int DOWN_OVER = 2;

    private String downSize;

    private Thread downLoadThread;

    private boolean interceptFlag = false;

    private boolean cancelAble = true;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    break;
                case DOWN_OVER:
                    installApk();
                    break;
                default:
                    break;
            }
        }
    };
    private Runnable mdownApkRunnable = new Runnable() {

        @Override
        public void run() {
            try {
                Log.e("ApkDownLoadUtil", "apkUrl.." + getApkUrl());
                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection) url
                        .openConnection();
                int length = Integer.parseInt(conn.getHeaderField("Content-Length"));
                Log.e("ApkDownLoadUtil", "Accept-Length:" + conn.getHeaderField("Accept-Length"));
                Log.e("ApkDownLoadUtil", "Content-Length:" + conn.getHeaderField("Content-Length"));
                InputStream is = conn.getInputStream();
                File file = new File(savePath);
                if (!file.exists()) {
                    file.mkdirs();
                }
                Log.e("ApkDownLoadUtil", "saveFileName:" + saveFileName);
                String apkFile = savePath + File.separator + saveFileName;
                File ApkFile = new File(apkFile);
                FileOutputStream fos = new FileOutputStream(ApkFile);
                int count = 0;
                byte buf[] = new byte[1024];
                do {
                    int numread = is.read(buf);
                    count += numread;
                    int progress = (int) (((float) count / length) * 100);
                    change(progress);
                    Log.e("ApkDownLoadUtil", Float.parseFloat(count + "") / (1024 * 1024) + "M/" + Float.parseFloat(length + "") / (1024 * 1024) + "M");
//                    downSize = DateStringUtils.getMoney(Float.parseFloat(count + "") / (1024 * 1024)) + "M/" + DateStringUtils.getMoney(Float.parseFloat(length + "") / (1024 * 1024)) + "M";
                    // 更新进度
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if (numread <= 0) {
                        // 下载完成通知安装
                        mHandler.sendEmptyMessage(DOWN_OVER);
                        break;
                    }
                    fos.write(buf, 0, numread);
                } while (!interceptFlag);// 点击取消就停止下载

                fos.close();
                is.close();
            } catch (Exception e) {
                if (downloadDialogListener != null) {
                    downloadDialogListener.OnDownloadError(e.getMessage());
                }
                Log.e("ApkDownLoadUtil", "Exception.." + e.getMessage());
                e.printStackTrace();
            }
        }
    };

    private int progressValue = 0;

    public int getProgressValue() {
        return progressValue;
    }

    public void setProgressValue(int progressValue) {
        this.progressValue = progressValue;
    }

    private void change(int value) {
        if (value > getProgressValue()) {
            setProgressValue(value);
            if (downloadDialogListener != null) {
                downloadDialogListener.progressChange(value);
            }
            Log.e("ApkDownLoadUtil", "progressValue = " + getProgressValue());
        }
    }

    /**
     * 下载apk
     */
    public void downloadApk(String filename, String version_update_url) {
        saveFileName = filename;
        setApkUrl(version_update_url);
        downLoadThread = new Thread(mdownApkRunnable);
        downLoadThread.start();
    }

    /**
     * 下载apk
     */
    public void downloadApk(String version_update_url) {
        downloadApk("zhongche.apk", version_update_url);
    }

    protected String getSavePath() {
        return savePath + File.separator + saveFileName;
    }

    /**
     * 安装apk
     */
    public void installApk() {
        if (!isApkDownloadAndToInstallApk()) {
            return;
        }


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            installApkAndroid_N(new File(savePath + saveFileName));
            return;
        }
        Log.e("ApkDownLoadUtil", "savePath:" + savePath);
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + savePath + File.separator + saveFileName),
                "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);

    }

    /**
     * 安卓7.0以后的安装
     */
    public void installApkAndroid_N(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+以上版本

            Uri apkUri = getApkUrlInAndroid_N(file);//FileProvider.getUriForFile(mContext, adnroid7FileProviderPath, file);  //包名.fileprovider ."com.zkingsoft.dianxin.fileProvider"
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }
        mContext.startActivity(intent);
    }

    protected abstract Uri getApkUrlInAndroid_N(File file);

    public boolean isApkDownloadAndToInstallApk() {
        return true;
    }


//    @Override
//    public void onClick(View view) {
////        if (view == cancleButton) {
////            this.dismiss();
////            downloadDialogListener.OnCancelClick();
////            Log.e("ApkDownLoadUtil", "  deleteFile：savePath + saveFileName = " + savePath + saveFileName);
//////            FileUtils.deleteFile(savePath + saveFileName);//中断下载的时候删除不完整的文件
////            interceptFlag = true;
////        }
//    }

    public interface DownloadDialogListener {
        void progressChange(int progress);

        void OnDownloadError(String error);
    }
}
