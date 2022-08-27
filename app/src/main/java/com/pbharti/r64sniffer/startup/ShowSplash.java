package com.pbharti.r64sniffer.startup;

import android.os.Handler;

import com.pbharti.r64sniffer.Constants;
import com.pbharti.r64sniffer.utils.Log;



public class ShowSplash extends Starter.Task {
    private static final String TAG = Constants.TAG + ".ShowSplash";

    @Override
    protected int start() {
        if (!Starter.sFirstLaunch) {
            return 0;
        }

        Log.d(TAG, "show splash.");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 800);

        return 1;
    }
}
