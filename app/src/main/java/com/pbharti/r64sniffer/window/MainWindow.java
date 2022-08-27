package com.pbharti.r64sniffer.window;

import android.content.Context;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pbharti.r64sniffer.Constants;
import com.pbharti.r64sniffer.R;
import com.pbharti.r64sniffer.message.IMsgListener;
import com.pbharti.r64sniffer.message.Messege;
import com.pbharti.r64sniffer.message.MsgDispatcher;
import com.pbharti.r64sniffer.traffic.ConnInfo;
import com.pbharti.r64sniffer.traffic.TrafficMgr;
import com.pbharti.r64sniffer.utils.Log;
import com.pbharti.r64sniffer.utils.PackageUtils;
import com.pbharti.r64sniffer.utils.ResTools;
import com.pbharti.r64sniffer.utils.ScreenUtils;
import com.summer.netcore.VpnConfig;

import java.util.List;



public class MainWindow extends AbsListContentWindow<Integer, MainWindow.ItemView> implements IMsgListener, TrafficMgr.ITrafficListener, VpnConfig.IListener {

    private static final String TAG = Constants.TAG + ".MainWindow";
    private LinearLayout mTitleBar;
    private TextView mTitle;
    private ImageView mAppIcon;
    private final SparseArray<AppConnInfo> mUID2AppInfo = new SparseArray<>();

    public MainWindow(Context context) {
        super(context);

        VpnConfig.addListener(this);

        setEmptyDesc();
    }

    @Override
    protected int getItemId(Integer item) {
        return item;
    }

    @Override
    protected ItemView createItemView(int position) {
        return new ItemView(getContext());
    }

    @Override
    protected void bindItem(Integer item, ItemView view) {
        AppConnInfo info = mUID2AppInfo.get(item);
        if (info != null) {
            view.bind(info);
        }
    }

    private void setEmptyDesc() {
        setEmptyDescryption(ResTools.getString(R.string.tips_monitoring));
    }

    private void initData() {
        SparseArray<List<ConnInfo>> uid2Conns = TrafficMgr.getInstance().getConnsCategoryByUid();

        if (uid2Conns != null) {
            for (int i = 0; i < uid2Conns.size(); i++) {
                int uid = uid2Conns.keyAt(i);
                List<ConnInfo> conns = uid2Conns.get(uid);
                AppConnInfo appConn = new AppConnInfo();
                appConn.uid = uid;
                appConn.connNum = TrafficMgr.getInstance().getConnNum(uid);

                if (conns != null && !conns.isEmpty()) {
                    for (ConnInfo conn : conns) {
                        appConn.accept += conn.accept;
                        appConn.back += conn.back;
                        appConn.sent += conn.sent;
                        appConn.recv += conn.recv;

                        if (conn.alive) {
                            ++appConn.alive;
                        }
                    }
                }
                mUID2AppInfo.put(uid, appConn);
                getData().add(uid);
            }
        }
    }

    @Override
    protected void preSwitchIn() {
        super.preSwitchIn();

        TrafficMgr.getInstance().addListener(this);
        initData();
        update();
    }

    @Override
    protected void preSwitchOut() {
        super.preSwitchOut();

        TrafficMgr.getInstance().removeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        MsgDispatcher.get().registerMsg(Messege.VPN_START, this);
        MsgDispatcher.get().registerMsg(Messege.VPN_STOP, this);
        setEmptyDesc();
        //mTitleBar.update();
        update();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MsgDispatcher.get().unregisterMsg(Messege.VPN_START, this);
        MsgDispatcher.get().unregisterMsg(Messege.VPN_STOP, this);
    }

    @Override
    protected View getTitleBar() {
        if (mTitleBar == null) {
            mTitleBar = new LinearLayout(getContext());
            mTitleBar.setGravity(Gravity.CENTER_VERTICAL);
            mTitleBar.setMinimumHeight(ScreenUtils.dp2px(55));
            mTitleBar.setOrientation(LinearLayout.HORIZONTAL);
            mTitleBar.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
            mTitleBar.setBackgroundResource(R.color.black);

            mAppIcon = new ImageView(getContext());
            int iconSize = (int) ResTools.getDimen(R.dimen.icon_size);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(iconSize, iconSize);
            lp.leftMargin = (int) ResTools.getDimen(R.dimen.hor_padding);
            mAppIcon.setLayoutParams(lp);
            mTitleBar.addView(mAppIcon, lp);


            int textSize = (int) ResTools.getDimen(R.dimen.textsize1);
            mTitle = new TextView(getContext());
            mTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
            mTitle.setTextColor(ResTools.getColor(R.color.background));
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.leftMargin = (int) ResTools.getDimen(R.dimen.hor_padding);
            mTitleBar.addView(mTitle, lp);

            mTitle.setText(TrafficCtrlWindow.AppName);
            mAppIcon.setImageDrawable(TrafficCtrlWindow.AppIcon);
        }

        return mTitleBar;
    }

    @Override
    public void onMessage(int msgId, Object arg) {
        switch (msgId) {
            case Messege.VPN_START:
            case Messege.VPN_STOP: {
                setEmptyDesc();
            }
        }
        super.onMessage(msgId, arg);
    }

    @Override
    public void onConnectCreate(int id, int uid, byte protocol) {
        Log.d(TAG, "Puskar45" + uid);
        AppConnInfo appConnInfo = mUID2AppInfo.get(uid);
        if (appConnInfo == null) {
            appConnInfo = new AppConnInfo();
            appConnInfo.uid = uid;
            appConnInfo.connNum = 1;
            appConnInfo.alive = 1;
            mUID2AppInfo.put(uid, appConnInfo);
            getData().add(uid);
            update();
        } else {
            ++appConnInfo.connNum;
            ++appConnInfo.alive;
            updateItem(uid);
        }
    }

    @Override
    public void onConnectDestroy(int id, int uid) {
        AppConnInfo appConnInfo = mUID2AppInfo.get(uid);
        if (appConnInfo == null) {
            Log.e(TAG, "wrong destroy event: id = " + id + " uid = " + uid);
        } else {
            --appConnInfo.alive;
            updateItem(uid);
        }
    }

    @Override
    public void onConnectState(int id, byte state) {

    }

    @Override
    public void onTrafficAccept(int id, int bytes, long total, int flag) {
        int uid = TrafficMgr.getInstance().getUid(id);
        AppConnInfo appConnInfo = mUID2AppInfo.get(uid);
        if (appConnInfo == null) {
            Log.e(TAG, "wrong traffic accept event, not find target conn info.");
            return;
        }
        appConnInfo.accept += bytes;
        updateItem(uid);
    }

    @Override
    public void onTrafficBack(int id, int bytes, long total, int flag) {
        int uid = TrafficMgr.getInstance().getUid(id);
        AppConnInfo appConnInfo = mUID2AppInfo.get(uid);
        if (appConnInfo == null) {
            Log.e(TAG, "wrong traffic back event, not find target conn info.");
            return;
        }
        appConnInfo.back += bytes;
        updateItem(uid);
    }

    @Override
    public void onTrafficSent(int id, int bytes, long total, int flag) {
        int uid = TrafficMgr.getInstance().getUid(id);
        AppConnInfo appConnInfo = mUID2AppInfo.get(uid);
        if (appConnInfo == null) {
            Log.e(TAG, "wrong traffic sent event, not find target conn info.");
            return;
        }
        appConnInfo.sent += bytes;
        updateItem(uid);
    }

    @Override
    public void onTrafficRecv(int id, int bytes, long total, int flag) {
        int uid = TrafficMgr.getInstance().getUid(id);
        AppConnInfo appConnInfo = mUID2AppInfo.get(uid);
        if (appConnInfo == null) {
            Log.e(TAG, "wrong traffic recv event, not find target conn info.");
            return;
        }
        appConnInfo.recv += bytes;
        updateItem(uid);
    }

    @Override
    public void onVpnConfigLoaded() {
        setEmptyDesc();
        if (mTitleBar != null) {
            //mTitleBar.update();
        }

    }

    @Override
    public void onVpnConfigItemUpdated(int i, String s) {
        setEmptyDesc();
        if (mTitleBar != null) {
            //mTitleBar.update();
        }

    }

    public static class AppConnInfo {
        int uid;
        int connNum;
        int alive;
        long accept;
        long back;
        long sent;
        long recv;
        byte protocol;
    }

    public static class ItemView extends LinearLayout implements View.OnClickListener {

        ImageView mIcon;

        LinearLayout mInfo;
        TextView mAppName;
        TextView mDetails1;

        TextView mConnNum;

        public ItemView(Context context) {
            super(context);

            setOrientation(HORIZONTAL);
            setGravity(Gravity.CENTER_VERTICAL);
            mIcon = new ImageView(context);
            int iconSize = (int) ResTools.getDimen(R.dimen.icon_size);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(70, 70);
            lp.rightMargin = (int) ResTools.getDimen(R.dimen.hor_padding);
            mIcon.setLayoutParams(lp);
            addView(mIcon);

            mInfo = new LinearLayout(context);
            mInfo.setOrientation(VERTICAL);
            mInfo.setPadding(20,12,20,12);
            lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.weight = 1;
            addView(mInfo, lp);

            int textsize = (int) ResTools.getDimen(R.dimen.textsize2);
            int textsize1 = (int) ResTools.getDimen(R.dimen.textsize3);
            mAppName = new TextView(context);
            mAppName.setTextSize(18);
            mAppName.setTextColor(ResTools.getColor(R.color.text));
            mInfo.addView(mAppName);
            mDetails1 = new TextView(context);
            mDetails1.setTextSize(14);
            mDetails1.setTextColor(ResTools.getColor(R.color.text1));
            lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = ScreenUtils.dp2px(3);
            mInfo.addView(mDetails1, lp);


            setBackgroundResource(R.drawable.list_item_bg);

            setMinimumHeight((int) ResTools.getDimen(R.dimen.item_height));
            setPadding(40, 10,40,10);

            setOnClickListener(this);
        }

        private void bind(AppConnInfo appConnInfo) {
            setTag(appConnInfo);
            if (appConnInfo == null) {
                mAppName.setText("????");
                mDetails1.setText("..........");
                return;
            }

            PackageUtils.AppInfo appInfo = PackageUtils.getAppInfo(appConnInfo.uid);
            if (appInfo != null) {
                mIcon.setImageDrawable(appInfo.icon);
                mAppName.setText(appInfo.name);
            } else {
                mIcon.setImageResource(R.drawable.icon144);
                mAppName.setText("Panda Live Capture");
            }

            mDetails1.setText("Alive : "+ appConnInfo.alive + " / " + "Total : " + appConnInfo.connNum);
            //mConnNum.setText(appConnInfo.protocol);
        }

        @Override
        public void onClick(View v) {
            Object o = getTag();
            if (o instanceof AppConnInfo) {
                AppConnInfo info = (AppConnInfo) o;
                MsgDispatcher.get().dispatch(Messege.SHOW_APP_CONNS_WINDOW, info.uid);
            }
        }
    }

}
