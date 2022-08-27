package com.pbharti.r64sniffer;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.pbharti.r64sniffer.message.IMsgListener;
import com.pbharti.r64sniffer.Constants;
import com.pbharti.r64sniffer.message.Messege;
import com.pbharti.r64sniffer.message.MsgDispatcher;
import com.pbharti.r64sniffer.startup.Starter;
import com.pbharti.r64sniffer.traffic.ConnInfo;
import com.pbharti.r64sniffer.utils.Log;
import com.pbharti.r64sniffer.utils.PackageUtils;
import com.pbharti.r64sniffer.window.AbsWindow;
import com.pbharti.r64sniffer.window.AppConnectionsWindow;
import com.pbharti.r64sniffer.window.TCPLogsWindow;
import com.pbharti.r64sniffer.window.TrafficCtrlWindow;
import com.pbharti.r64sniffer.window.WindowStack;
import com.summer.netcore.VpnConfig;

import java.util.List;

import static com.pbharti.r64sniffer.Constants.DEFAULT_APP_CTRLS;
import static com.pbharti.r64sniffer.Constants.TAG;

public class MainActivity2 extends Activity implements IMsgListener {

    public static final String ACT_OPEN_WINDOW_STRATEGY_CTRL = "intent_action_openwindow";
    public static final int ACT_REQ_CODE_VPN_PERMISSION = 0;
    private WindowStack mEnv;
    public static int cpj = 0;

    public static boolean ispackageinstalled(String packagename, PackageManager packageManager) {
        try{
            packageManager.getPackageInfo(packagename, 0);
            return true;
        }
        catch(PackageManager.NameNotFoundException e){
            return false;
        }
    }
    public static void AddApp(){
        PackageManager pm = ContextMgr.getApplicationContext().getPackageManager();
        List<PackageUtils.AppInfo> installedApps = PackageUtils.getAllInstallApps();

        VpnConfig.updateCtrl(null, null, null);

        if(ispackageinstalled("com.whatsapp", pm)){
            DEFAULT_APP_CTRLS.put("com.whatsapp", VpnConfig.AVAIL_CTRLS.CAPTURE);
        }
        if(ispackageinstalled("com.google.android.apps.tachyon", pm)){
            DEFAULT_APP_CTRLS.put("com.google.android.apps.tachyon", VpnConfig.AVAIL_CTRLS.CAPTURE);
        }
        if(ispackageinstalled("com.facebook.mlite", pm)){
            DEFAULT_APP_CTRLS.put("com.facebook.mlite", VpnConfig.AVAIL_CTRLS.CAPTURE);
        }
        if(ispackageinstalled("org.thoughtcrime.securesms", pm)){
            DEFAULT_APP_CTRLS.put("org.thoughtcrime.securesms", VpnConfig.AVAIL_CTRLS.CAPTURE);
        }
        if(ispackageinstalled("org.telegram.messenger", pm)){
            DEFAULT_APP_CTRLS.put("org.telegram.messenger", VpnConfig.AVAIL_CTRLS.CAPTURE);
        }
        if(ispackageinstalled("com.instagram.android", pm)){
            DEFAULT_APP_CTRLS.put("com.instagram.android", VpnConfig.AVAIL_CTRLS.CAPTURE);
        }
        if(ispackageinstalled("com.facebook.orca", pm)){
            DEFAULT_APP_CTRLS.put("com.facebook.orca", VpnConfig.AVAIL_CTRLS.CAPTURE);
        }
        for (PackageUtils.AppInfo ai : installedApps) {
            VpnConfig.updateCtrl(VpnConfig.CtrlType.APP, String.valueOf(ai.uid), Constants.DEFAULT_APP_CTRLS.get(ai.pkg));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ContextMgr.setContext(this);

        mEnv = new WindowStack(this);
        setContentView(mEnv.getView());

        MsgDispatcher.get().registerMsg(Messege.BACK_PRESSED, this);
        MsgDispatcher.get().registerMsg(Messege.PUSH_WINDOW, this);
        MsgDispatcher.get().registerMsg(Messege.PUSH_WINDOW_WITHOUT_ANIM, this);
        MsgDispatcher.get().registerMsg(Messege.POP_WINDOW, this);
        MsgDispatcher.get().registerMsg(Messege.SHOW_APP_CONNS_WINDOW, this);
        MsgDispatcher.get().registerMsg(Messege.SHOW_CONN_LOGS, this);
        MsgDispatcher.get().registerMsg(Messege.START_UP_FINISHED, this);
        AddApp();
        MsgDispatcher.get().dispatchSync(Messege.PUSH_WINDOW, new TrafficCtrlWindow(ContextMgr.getContext()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        ContextMgr.setForeground(false);
        mEnv.onPause();
        MsgDispatcher.get().dispatch(Messege.APP_ON_PAUSE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ContextMgr.setForeground(true);
        AddApp();
        mEnv.onResume();
        MsgDispatcher.get().dispatch(Messege.APP_ON_RESUME);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        checkIntent(intent);

        super.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            MsgDispatcher.get().dispatchSync(Messege.ACTIVITY_RESULT_OK, requestCode);
        } else {
            MsgDispatcher.get().dispatchSync(Messege.ACTIVITY_RESULT_NO, requestCode);
        }

    }

    private void checkIntent(Intent intent) {
        if (intent != null) {
            if (ACT_OPEN_WINDOW_STRATEGY_CTRL.equals(intent.getAction())) {
                if (!(mEnv.getTopWindow() instanceof TrafficCtrlWindow)) {
                    TrafficCtrlWindow w = TrafficCtrlWindow.createWindow(this, VpnConfig.CTRL_BITS.BASE);
                    MsgDispatcher.get().dispatch(Messege.PUSH_WINDOW, w);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        MsgDispatcher.get().dispatch(Messege.BACK_PRESSED);
    }

    @Override
    public void onMessage(int msgId, Object arg) {
        switch (msgId) {
            case Messege.PUSH_WINDOW: {
                mEnv.push((AbsWindow) arg);
                break;
            }
            case Messege.POP_WINDOW: {
                mEnv.pop();
                break;
            }
            case Messege.SHOW_APP_CONNS_WINDOW: {
                AppConnectionsWindow w = new AppConnectionsWindow(this);
                w.updateUID((int) arg);
                MsgDispatcher.get().dispatch(Messege.PUSH_WINDOW, w);

                break;
            }
            case Messege.SHOW_CONN_LOGS: {
                TCPLogsWindow w = new TCPLogsWindow(this);
                w.update((ConnInfo) arg);
                MsgDispatcher.get().dispatch(Messege.PUSH_WINDOW, w);
                break;
            }
            case Messege.BACK_PRESSED: {
                if (!mEnv.onBackPressed()) {
                    finish();
                }

                break;
            }
            case Messege.START_UP_FINISHED: {
                checkIntent(getIntent());
                break;
            }
            case Messege.PUSH_WINDOW_WITHOUT_ANIM: {
                mEnv.push((AbsWindow) arg, false);
                break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        ContextMgr.setContext(null);
        MsgDispatcher.get().unregisterMsg(Messege.BACK_PRESSED, this);
        MsgDispatcher.get().unregisterMsg(Messege.PUSH_WINDOW, this);
        MsgDispatcher.get().unregisterMsg(Messege.PUSH_WINDOW_WITHOUT_ANIM, this);
        MsgDispatcher.get().unregisterMsg(Messege.POP_WINDOW, this);
        MsgDispatcher.get().unregisterMsg(Messege.SHOW_APP_CONNS_WINDOW, this);
        MsgDispatcher.get().unregisterMsg(Messege.SHOW_CONN_LOGS, this);
        MsgDispatcher.get().unregisterMsg(Messege.START_UP_FINISHED, this);

        super.onDestroy();
    }

    @Override
    public Object onSyncMessage(int msgId, Object arg) {
        onMessage(msgId, arg);

        return null;
    }
}
