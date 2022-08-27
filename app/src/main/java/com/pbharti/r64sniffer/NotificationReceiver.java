package com.pbharti.r64sniffer;

import android.app.Activity;
import android.os.Bundle;

import com.pbharti.r64sniffer.traffic.TrafficMgr;
import com.summer.netcore.NetCoreIface;

public class NotificationReceiver extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        moveTaskToBack(true);

        if (NetCoreIface.isServerRunning()) {
            TrafficMgr.getInstance().stop();
        } else {
            TrafficMgr.getInstance().start();
        }

        finish();
    }

}
