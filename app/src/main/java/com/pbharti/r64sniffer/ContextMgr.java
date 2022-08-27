package com.pbharti.r64sniffer;

import android.content.Context;

import java.lang.ref.WeakReference;


public class ContextMgr {

    private static WeakReference<Context> sContext;
    private static Context sAppContext;
    private static boolean sForeground = false;

    public static Context getContext() {
        return sContext == null ? null : sContext.get();
    }

    public static void setContext(Context context) {
        if (context != null) {
            sContext = new WeakReference<>(context);
        } else {
            sContext = null;
        }
    }

    public static Context getApplicationContext() {
        return sAppContext;
    }

    public static void setApplicationContext(Context context) {
        sAppContext = context;
    }

    public static boolean isForeground() {
        return sForeground;
    }

    public static void setForeground(boolean foreground) {
        sForeground = foreground;
    }

}
