package com.pbharti.r64sniffer.startup;

import com.pbharti.r64sniffer.Constants;
import com.pbharti.r64sniffer.traffic.TrafficMgr;
import com.pbharti.r64sniffer.utils.Log;



public class InitCore extends Starter.Task {
    private static final String TAG = Constants.TAG + ".InitCore";

    @Override
    protected int start() {
        Log.d(TAG, "init net core...");

        TrafficMgr.getInstance().init();


        return 0;
    }
}
