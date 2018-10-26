package com.lx.sms.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lx.sms.R;

import java.io.File;

/**
 * SMS
 * author:Created by admin on 2018-10-22.
 * date:demo06@126.com
 * QQ:290221978
 */
public class DownloadAppUtils {
    private static final String TAG = DownloadAppUtils.class.getSimpleName();
    //下载更新Apk 下载任务对应的Id
    public static long downloadUpdateApkId = -1;
    //下载更新Apk 文件路径
    public static String downloadUpdateApkFilePath;
    Context context;

    /**
     * 通过浏览器下载APK包
     *
     * @param context
     * @param url
     */
    public static void downloadForWebView(Context context, String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    /**
     * 下载更新apk包
     * 权限:1,<uses-permission android:name="android.permission.DOWNLOAD_WITHOUT_NOTIFICATION" />
     *
     * @param context
     * @param url
     */
    public static void downloadForAutoInstall(Context context, String url, String fileName, String title) {
        if (TextUtils.isEmpty(url)) {
            return;
        }
        try {
            Uri uri = Uri.parse(url);
            DownloadManager downloadManager = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            //在通知栏中显示
            request.setVisibleInDownloadsUi(true);
            request.setTitle(title);
            // VISIBILITY_VISIBLE:                   下载过程中可见, 下载完后自动消失 (默认)
            // VISIBILITY_VISIBLE_NOTIFY_COMPLETED:  下载过程中和下载完成后均可见
            // VISIBILITY_HIDDEN:                    始终不显示通知
            if (!UpdateAppUtils.showNotification) {
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            }
            String filePath = null;
            //外部存储卡
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            } else {
                Log.i(TAG, "没有SD卡");
                return;
            }
            downloadUpdateApkFilePath = filePath + File.separator + fileName;
            // 若存在，则删除
            deleteFile(downloadUpdateApkFilePath);
            Log.i("downloadPath:", "下载的路径========>"+downloadUpdateApkFilePath);
            Uri fileUri = Uri.fromFile(new File(downloadUpdateApkFilePath));
            request.setDestinationUri(fileUri);
            downloadUpdateApkId = downloadManager.enqueue(request);
        } catch (Exception e) {
            e.printStackTrace();
            downloadForWebView(context, url);
        }
    }

    private static boolean deleteFile(String fileStr) {
        File apkFile = new File(fileStr);
        if (apkFile.exists()) {
            apkFile.delete();
        }
        return apkFile.delete();
    }


    /**
     * 打开安装包
     *
     * @param context
     * @param fileUri
     */
    public static void openAPKFile(Context context, String fileUri) {
        // 核心是下面几句代码
        if (null != fileUri) {
            try {
                //兼容7.0
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    File file = new File(fileUri);
                    //在AndroidManifest中的android:authorities值
                    Uri apkUri = FileProvider.getUriForFile(context, "com.lx.sms.FileProvider", file);
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    install.setDataAndType(apkUri, "application/vnd.android.package-archive");
                    context.startActivity(install);
                    //兼容8.0
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        boolean hasInstallPermission = context.getPackageManager().canRequestPackageInstalls();
                        if (!hasInstallPermission) {
                            Toast.makeText(context, context.getString(R.string.string_install_unknow_apk_note), Toast.LENGTH_SHORT).show();
                            startInstallPermissionSettingActivity(context);
                            return;
                        }
                    }
                } else {
                    Intent install = new Intent(Intent.ACTION_VIEW);
                    install.setDataAndType(Uri.fromFile(new File(fileUri)), "application/vnd.android.package-archive");
                    install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(install);
                }
//                if (context.getPackageManager().queryIntentActivities(intent, 0).size() > 0) {
//                    context.startActivity(intent);
//                }

            } catch (Throwable e) {
                e.printStackTrace();
//                Toast.makeText(context, context.getString(R.string.download_hint), Toast.LENGTH_SHORT).show();

            }
        }
    }

    /**
     * 跳转到设置-允许安装未知来源-页面
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void startInstallPermissionSettingActivity(Context context) {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
