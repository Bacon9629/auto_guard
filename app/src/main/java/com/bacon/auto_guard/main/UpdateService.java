package com.bacon.auto_guard.main;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;

import androidx.core.content.FileProvider;

import static android.content.ContentValues.TAG;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class UpdateService extends Service {
    private String PATH;
    /**
     * 安卓系統下載類
     **/
    private DownloadManager manager;
    /**
     * 接收下載完的廣播
     **/
    private DownloadCompleteReceiver receiver;
    private String url;
    private final String DOWNLOADPATH = "/newAPK/" ;//下載路徑，如果不定義自己的路徑，6.0的手機不自動安裝
    private String apkName = "/";

    /**
     * 初始化下載器
     **/
    private void initDownManager() {
        Log.d(TAG, "down2");
        manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        receiver = new DownloadCompleteReceiver();
        //設定下載地址
        DownloadManager.Request down = new DownloadManager.Request(Uri.parse(url));
        // 設定允許使用的網路型別，這裡是行動網路和wifi都可以
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE
                | DownloadManager.Request.NETWORK_WIFI);
        down.setAllowedOverRoaming(false);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        down.setMimeType(mimeString);
        // 下載時，通知欄顯示途中
        down.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // 顯示下載介面
        down.setVisibleInDownloadsUi(true);
        // 設定下載後文件存放的位置
        down.setDestinationInExternalFilesDir(this , DOWNLOADPATH, apkName);
        down.setTitle(apkName);
        // 將下載請求放入佇列
        manager.enqueue(down);
        //註冊下載廣播
        registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        apkName += intent.getStringExtra("apkName");
        apkName += ".apk";
        url = intent.getStringExtra("downloadurl");
        String path = this.getExternalFilesDir(DOWNLOADPATH).getAbsolutePath() + apkName;
        PATH = path;
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
        try {
            // 呼叫下載
            initDownManager();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "下載失敗", Toast.LENGTH_SHORT).show();
        }
        return Service.START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onDestroy() {
        if (receiver != null)
            // 登出下載廣播
            unregisterReceiver(receiver);
        super.onDestroy();
    }

    // 接受下載完成後的intent
    class DownloadCompleteReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            //判斷是否下載完成的廣播
            if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                //獲取下載的檔案id
                long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (manager.getUriForDownloadedFile(downId) != null) {
                    //自動安裝apk
                    planeB(context);

                } else {
                    Toast.makeText(context, "下載失敗", Toast.LENGTH_SHORT).show();
                }
                //停止服務並關閉廣播
                UpdateService.this.stopSelf();
            }
        }

        private void planeB(Context context) {

            File file = new File(PATH);
            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);

            Intent intent = new Intent();
//            intent.setAction("android.intent.action.INSTALL_PACKAGE");
            intent.setAction(Intent.ACTION_INSTALL_PACKAGE);
//            intent.addCategory("android.intent.category.DEFAULT");
            intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            intent.setData(uri);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            startActivity(intent);
        }
    }
}