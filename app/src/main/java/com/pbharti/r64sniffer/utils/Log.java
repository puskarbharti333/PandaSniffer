package com.pbharti.r64sniffer.utils;

import com.pbharti.r64sniffer.Constants;



public class Log {

    public static final int d(String tag, String log) {
        if (Constants.DEBUG_LEV != Constants.DEBUG_LEV_RELEASE) {
            return android.util.Log.d(tag, log);
        }

        return 0;
    }

    public static final int e(String tag, String log) {
        return android.util.Log.e(tag, log);
    }

    public static final int i(char tag, String log) {

        if (Constants.DEBUG_LEV != Constants.DEBUG_LEV_RELEASE) {
            //return android.util.Log.i(tag,log);
        }

        return 0;
    }

    public static final String getStackTraceString(Throwable t) {
        return android.util.Log.getStackTraceString(t);
    }

}
