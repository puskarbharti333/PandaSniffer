package com.pbharti.r64sniffer.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;

import com.pbharti.r64sniffer.Constants;
import com.pbharti.r64sniffer.ContextMgr;
import com.pbharti.r64sniffer.R;
import com.pbharti.r64sniffer.window.TrafficCtrlWindow;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PackageUtils {
    private static final String TAG = Constants.TAG + ".PackageUtils";

    private static final SparseArray<AppInfo> sUID2AppInfo = new SparseArray<>();
    private static String appdata;

    public static List<AppInfo> getAllInstallApps() {
        PackageManager pm = ContextMgr.getApplicationContext().getPackageManager();
        List<ApplicationInfo> appsInfo = pm.getInstalledApplications(0);
        List<AppInfo> appInfos = new ArrayList<>();
        if (appsInfo != null) {
            appdata = String.valueOf(appsInfo);
            for (ApplicationInfo i : appsInfo) {
                AppInfo appInfo = new AppInfo();
                appInfo.uid = i.uid;
                appInfo.icon = i.loadIcon(pm);
                appInfo.pkg = i.packageName;
                appInfo.name = i.loadLabel(pm).toString();
                appInfos.add(appInfo);
            }
        }

        return appInfos;
    }

    public static String getPackageName(int uid) {
        PackageManager pm = ContextMgr.getApplicationContext().getPackageManager();
        String[] pkgs = pm.getPackagesForUid(uid);

        if (pkgs != null) {
            for (String pkg : pkgs) {
                Log.d(TAG, "pkg: " + pkg);
            }

            return pkgs[0];
        }

        return TrafficCtrlWindow.AppName;
    }

    public static AppInfo getAppInfo(int uid) {
        AppInfo ret = getInfo(uid);
        if (ret != null) {
            return ret;
        }
        PackageManager pm = ContextMgr.getApplicationContext().getPackageManager();
        String pkg = getPackageName(uid);
        try {
            PackageInfo info = pm.getPackageInfo(pkg, 0);
            if (info != null) {
                AppInfo appInfo = new AppInfo();
                appInfo.uid = uid;
                appInfo.icon = info.applicationInfo.loadIcon(pm);
                appInfo.pkg = info.packageName;
                appInfo.name = info.applicationInfo.loadLabel(pm).toString();
                addInfo(appInfo);
                return appInfo;
            }
        } catch (Throwable t) {
            ExceptionHandler.handleException(t);
        }

        return null;
    }

    public static int getDefaultAppIcon(int uid) {
        return R.drawable.icon144;
    }

    public static String getDefaultAppName(int uid) {
        return TrafficCtrlWindow.AppName;
    }

    private static AppInfo getInfo(int uid) {
        Log.d(TAG, "DataUID: " + uid);
        Constants.puid = uid;
        synchronized (sUID2AppInfo) {
            return sUID2AppInfo.get(uid);
        }
    }

    private static void addInfo(AppInfo info) {
        synchronized (sUID2AppInfo) {
            sUID2AppInfo.put(info.uid, info);
        }
    }

    public static class AppInfo {
        public int uid;
        public String pkg;
        public String name;
        public Drawable icon;
    }

}
