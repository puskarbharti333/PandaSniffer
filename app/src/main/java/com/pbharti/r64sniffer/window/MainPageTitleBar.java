package com.pbharti.r64sniffer.window;

import android.content.Context;
import android.os.Process;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.pbharti.r64sniffer.ContextMgr;
import com.pbharti.r64sniffer.R;
import com.pbharti.r64sniffer.message.IMsgListener;
import com.pbharti.r64sniffer.message.Messege;
import com.pbharti.r64sniffer.message.MsgDispatcher;
import com.pbharti.r64sniffer.traffic.TrafficMgr;
import com.pbharti.r64sniffer.utils.ResTools;
import com.pbharti.r64sniffer.utils.ScreenUtils;
import com.summer.netcore.VpnConfig;

import java.util.ArrayList;
import java.util.List;


public class MainPageTitleBar implements IMsgListener, View.OnClickListener {

    private final TitleBar mTitlebar;
    private final TextView mStart;
    private final TextView mSetting;


    public MainPageTitleBar(Context context) {

        int textSize = (int) ResTools.getDimen(R.dimen.textsize1);
        mTitlebar = new TitleBar(context);
        mTitlebar.setTitle(R.string.app_name);
        mTitlebar.setMinimumHeight(ScreenUtils.dp2px(55));
        //mTitlebar.getTitle().setOnClickListener(this);

        int hpd = ScreenUtils.dp2px(5);
        int vpd = ScreenUtils.dp2px(2);
        mStart = new TextView(context);
        mStart.setOnClickListener(this);

        mStart.setPadding(hpd, vpd, hpd, vpd);
        mStart.setGravity(Gravity.CENTER);
        mStart.setBackgroundResource(R.drawable.button_blue);
        mStart.setTextColor(ResTools.getColor(R.color.background));
        mStart.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mStart.setMinWidth(ScreenUtils.dp2px(30));
        mTitlebar.addRight(mStart);

        mSetting = new TextView(context);
        mSetting.setOnClickListener(this);
        hpd = ScreenUtils.dp2px(5);
        vpd = ScreenUtils.dp2px(2);
        mSetting.setPadding(hpd, vpd, hpd, vpd);
        mSetting.setGravity(Gravity.CENTER);
        mSetting.setBackgroundResource(R.drawable.ic_stat_name);
        mSetting.setTextColor(ResTools.getColor(R.color.background));
        mSetting.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        mSetting.setMinWidth(ScreenUtils.dp2px(30));
        mTitlebar.addRight(mSetting);
        //mSetting.setSelected(true);
        //mSetting.setText("Settings");
        mSetting.setOnClickListener(this);

        MsgDispatcher.get().registerMsg(Messege.VPN_START, this);
        MsgDispatcher.get().registerMsg(Messege.VPN_STOP, this);

        updateVPNState();
    }

    public View getView() {
        return mTitlebar;
    }

    public void update() {
        updateVPNState();
    }

    private void updateVPNState() {
        if (TrafficMgr.getInstance().isCtrlSetEmpty()) {
            mStart.setSelected(true);
            mStart.setText(R.string.add);
           mStart.setBackgroundResource(R.drawable.add);
        } else {
            if (TrafficMgr.getInstance().isEnable()) {
                mStart.setSelected(true);
                //mStart.setText(R.string.stop);
               mStart.setBackgroundResource(R.drawable.stop);
            } else {
                mStart.setSelected(false);
                //mStart.setText(R.string.start);
               mStart.setBackgroundResource(R.drawable.start);
            }
        }

    }

    @Override
    public void onMessage(int msgId, Object arg) {
        switch (msgId) {
            case Messege.VPN_START:
            case Messege.VPN_STOP: {
                updateVPNState();
                break;
            }
        }
    }

    @Override
    public Object onSyncMessage(int msgId, Object arg) {
        // return null;
        return arg;
    }

    @Override
    public void onClick(View v) {
        if (v == mStart) {
            if (TrafficMgr.getInstance().isCtrlSetEmpty()) {
                List<Integer> excludes = new ArrayList<>();
                excludes.add(Process.myUid());
                AppsSelectWindow w = new AppsSelectWindow(v.getContext(), excludes, new AppsSelectWindow.IResultCallback() {
                    @Override
                    public void onSelect(List<Integer> uids) {
                        for (int uid : uids) {
                            String suid = String.valueOf(uid);
                            VpnConfig.updateCtrl(VpnConfig.CtrlType.APP, suid, VpnConfig.AVAIL_CTRLS.BASE);
                        }
                    }
                });
                MsgDispatcher.get().dispatch(Messege.PUSH_WINDOW, w);
            } else {
                if (TrafficMgr.getInstance().isEnable()) {
                    TrafficMgr.getInstance().stop();
                } else {
                    TrafficMgr.getInstance().start();
                }
            }

        } if (v == mSetting) {
            MsgDispatcher.get().dispatchSync(Messege.PUSH_WINDOW, new SettingsWindow(ContextMgr.getContext()));
        }
    }
}
