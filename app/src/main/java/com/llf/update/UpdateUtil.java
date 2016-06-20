package com.llf.update;

import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import com.llf.update.net.JsonUtil;
import com.llf.update.net.OkHttpUtil;
import com.squareup.okhttp.Request;
import java.io.IOException;

/**
 * Created by Administrator on 2016/6/20.
 */
public class UpdateUtil {
    private static String urlHead = "http://www.chunyuyisheng.com/cmsapi/app/update?";
    public static  String DOWNLOAD_APK_ID_PREFS = "download_apk_id";

    public static void checkUpdate(String appCode, String curVersion, final UpdateCallback updateCallback) {
        OkHttpUtil.getInstanse().getAsyn(urlHead + "appName=" + appCode + "&version=" + curVersion, true, new OkHttpUtil.StringCallback() {
            @Override
            public void onFailure(Request request, IOException e) {
                //网络请求出错
                updateCallback.onError();
            }

            @Override
            public void onResponse(String response) {
                Log.e("请求结果", response);
                onNext(JsonUtil.getObject(response, InfoEntity.class), updateCallback);
            }
        });
    }

    // 显示信息
    private static void onNext(InfoEntity info, UpdateCallback updateCallback) {
        if (info.getError_code() != 0 || info.getData() == null ||
                info.getData().getAppURL() == null) {
            updateCallback.onError(); // 失败
        } else {
            updateCallback.onSuccess(info);
        }
    }

    public static void downloadApk(Context context, InfoEntity info, String infoName, String storeApk) {
        if (!isDownloadManagerAvailable()) {
            return;
        }

        if(!sdAvailable()){
           return;
        }
        String appUrl = "http://" +info.getData().getAppURL().trim();
        DownloadManager.Request request;
        try {
            request = new DownloadManager.Request(Uri.parse(appUrl));
        } catch (Exception e) {
            return;
        }

        request.setTitle(infoName);
        request.setDescription(info.getData().getDescription());
        //无线网状态下下载
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
        //设置漫游状态下不下载
        request.setAllowedOverRoaming(false);
        request.setMimeType("application/vnd.android.package-archive");
        //HONEYCOMB版本是11
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //是否允许本MediaScanner扫描
            request.allowScanningByMediaScanner();
            //下载时在通知栏显示下载信息,下载完成后保留下载信息,用Request.VISIBILITY_VISIBLE，下载完成后不保留下载信息
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        }

        //指定下载后的存储位置
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, storeApk);
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        long id = manager.enqueue(request);
        //存储下载的序列号，用于自动更新
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putLong(DOWNLOAD_APK_ID_PREFS,id).apply();
    }

    // SDK大于9才能使用DownloadManager
    private static boolean isDownloadManagerAvailable() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    //判断Sd卡是否可用
    private static boolean sdAvailable(){
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }
    }
    public interface UpdateCallback {
        void onSuccess(InfoEntity info);
        void onError();
    }
}
