package com.pbharti.r64sniffer.window;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pbharti.r64sniffer.Constants;
import com.pbharti.r64sniffer.R;
import com.pbharti.r64sniffer.message.Messege;
import com.pbharti.r64sniffer.message.MsgDispatcher;
import com.pbharti.r64sniffer.traffic.ConnInfo;
import com.pbharti.r64sniffer.traffic.IP;
import com.pbharti.r64sniffer.traffic.TrafficMgr;
import com.pbharti.r64sniffer.utils.Log;
import com.pbharti.r64sniffer.utils.PackageUtils;
import com.pbharti.r64sniffer.utils.ResTools;
import com.pbharti.r64sniffer.utils.ScreenUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;


public class AppConnectionsWindow extends AbsListContentWindow<ConnInfo, AppConnectionsWindow.ItemView> implements TrafficMgr.ITrafficListener {
    private static final String TAG = Constants.TAG + ".AppConnectionsWindow";

    private LinearLayout mTitleBar;
    private TextView mTitle;
    private ImageView mAppIcon;
    private int mUID;
    public static SparseArray<ConnInfo> mID2Info = new SparseArray<>();


    public AppConnectionsWindow(Context context) {
        super(context);
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
        }

        return mTitleBar;
    }
    @Override
    protected void onResume(){
        super.onResume();
        //mID2Info.clear();
    }
    private void setEmptyDesc() {
        setEmptyDescryption(ResTools.getString(R.string.tips_monitoring));
    }
    private void initData() {
        getData().clear();
        List<ConnInfo> conns = TrafficMgr.getInstance().getConnsOfUid(mUID);
        if (conns != null) {
            for (ConnInfo info : conns) {
                mID2Info.put(info.id, info);
                getData().add(info);
            }
        }
        //updateUID(Constants.puid);
        update();
        //updateUID(Constants.puid);
    }


    public void updateUID(int uid) {
        mUID = uid;
        initData();

        PackageUtils.AppInfo appInfo = PackageUtils.getAppInfo(uid);

        if (appInfo != null) {
            mTitle.setText(appInfo.name);
            mAppIcon.setImageDrawable(appInfo.icon);
        } else {
            mTitle.setText("Live Capture");
            mAppIcon.setImageDrawable(TrafficCtrlWindow.AppIcon);
        }

    }

    @Override
    protected void preSwitchIn() {
        super.preSwitchIn();
        TrafficMgr.getInstance().addListener(this);
    }

    @Override
    protected void postSwitchOut() {
        super.postSwitchOut();
        TrafficMgr.getInstance().removeListener(this);
    }

    @Override
    protected int getItemId(ConnInfo item) {
        return item.id;
    }

    @Override
    protected ItemView createItemView(int position) {
        return new ItemView(getContext());
    }

    @Override
    protected void bindItem(ConnInfo item, ItemView view) {
        view.bind(item);
    }

    @Override
    public void onConnectCreate(int id, int uid, byte protocol) {
        if (uid == mUID) {
            ConnInfo conn = TrafficMgr.getInstance().getConn(id);

            if (conn != null) {
                getData().add(conn);
                mID2Info.put(conn.id, conn);
                update();
            }
        }

    }

    @Override
    public void onConnectDestroy(int id, int uid) {
        if (uid == mUID) {
            ConnInfo conn = TrafficMgr.getInstance().getConn(id);
            if (conn != null) {
                updateItem(conn);
            }
        }
    }

    @Override
    public void onConnectState(int id, byte state) {
        ConnInfo info = mID2Info.get(id);
        if (info == null) {
            return;
        }

        updateItem(info);
    }

    @Override
    public void onTrafficAccept(int id, int bytes, long total, int flag) {
        ConnInfo info = mID2Info.get(id);
        if (info == null) {
            return;
        }

        updateItem(info);
    }

    @Override
    public void onTrafficBack(int id, int bytes, long total, int flag) {
        ConnInfo info = mID2Info.get(id);
        if (info == null) {
            return;
        }

        updateItem(info);
    }

    @Override
    public void onTrafficSent(int id, int bytes, long total, int flag) {
        ConnInfo info = mID2Info.get(id);
        if (info == null) {
            return;
        }

        updateItem(info);
    }

    @Override
    public void onTrafficRecv(int id, int bytes, long total, int flag) {
        ConnInfo info = mID2Info.get(id);
        if (info == null) {
            return;
        }

        updateItem(info);
    }


    public static class ItemView extends FrameLayout implements View.OnClickListener {

        LinearLayout mContent;
        TextView mProtocol;

        LinearLayout mInfo;
        TextView mDest;
        //TextView mTraffic;
        TextView mTime;

        TextView mState;
        TextView mid;
        Date mDate;
        SimpleDateFormat mFormat;

        View mMask;

        public ItemView(Context context) {
            super(context);
            mContent = new LinearLayout(context);
            mContent.setOrientation(LinearLayout.HORIZONTAL);
            mContent.setGravity(Gravity.CENTER_VERTICAL);
            mContent.setBackgroundColor(Color.TRANSPARENT);
            mProtocol = new TextView(context);
            int iconSize = (int) ResTools.getDimen(R.dimen.icon_size);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(iconSize, iconSize);
            lp.rightMargin = (int) ResTools.getDimen(R.dimen.hor_padding);
            mProtocol.setLayoutParams(lp);
            mProtocol.setTextSize(14);
            //mProtocol.setMinimumWidth(30);
            mContent.addView(mProtocol);

            mInfo = new LinearLayout(context);
            mInfo.setOrientation(LinearLayout.VERTICAL);


            int textsize = (int) ResTools.getDimen(R.dimen.textsize2);
            int textsize1 = (int) ResTools.getDimen(R.dimen.textsize3);
            mDest = new TextView(context);
            mDest.setTextSize(16);
            mDest.setTextColor(ResTools.getColor(R.color.text));
            mDest.setPadding(0, 0, 0, 10);
            mInfo.addView(mDest);

            //mTraffic = new TextView(context);
            //mTraffic.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize1);
            //mTraffic.setTextColor(ResTools.getColor(R.color.text1));
            //lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //lp.topMargin = ScreenUtils.dp2px(8);
            //mInfo.addView(mTraffic,lp);

            mTime = new TextView(context);
            mTime.setTextSize(12);
            mTime.setTextColor(ResTools.getColor(R.color.text1));
            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.topMargin = ScreenUtils.dp2px(3);
            mInfo.addView(mTime);

            lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.weight = 1;
            mContent.addView(mInfo, lp);

            mState = new TextView(context);
            mState.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
            mState.setGravity(Gravity.TOP | Gravity.RIGHT);
            mState.setTextColor(ResTools.getColor(R.color.text));
            mContent.addView(mState);

            mid = new TextView(context);
            mid.setTextSize(TypedValue.COMPLEX_UNIT_PX, textsize);
            mid.setGravity(Gravity.TOP | Gravity.RIGHT);
            mid.setTextColor(ResTools.getColor(R.color.text));
            mContent.addView(mid);

            addView(mContent, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            mMask = new View(context);
            mMask.setFocusable(false);
            mMask.setFocusableInTouchMode(false);
            mMask.setBackgroundResource(R.drawable.list_item_normal_died);
            addView(mMask, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            setBackgroundResource(R.drawable.list_item_normal);

            int hp = (int) ResTools.getDimen(R.dimen.p_padding);
            int vp = (int) ResTools.getDimen(R.dimen.p_padding);
            mContent.setPadding(hp, vp, hp, vp);

            //setOnClickListener(this);

            mDate = new Date();
            mFormat = new SimpleDateFormat("HH:mm:ss");
        }

        @SuppressLint("SetTextI18n")
        private void bind(ConnInfo conn) {
            setTag(conn);
            if (conn == null) {
                mProtocol.setText("?");
                mDest.setText("????");
                mState.setText("0");
                return;
            }

            if (!conn.dest.equals("8.8.8.8")) {
                if (conn.destPort != 80) {
                    mDest.setText(conn.dest + ":" + conn.destPort);
                    mProtocol.setText(IP.getProtocolName(conn.protocol));
                    mState.setText(IP.getStateName(conn.protocol, conn.state));
                    mTime.setText(getTime(conn.born_time));
                }
                else {
                    mContent.setPadding(0, 0, 0, 0);
                    mContent.removeAllViews();
                }
                if (conn.destPort != 443) {
                    mDest.setText(conn.dest + ":" + conn.destPort);
                    mProtocol.setText(IP.getProtocolName(conn.protocol));
                    mState.setText(IP.getStateName(conn.protocol, conn.state));
                    mTime.setText(getTime(conn.born_time));
                }
                else {
                    mContent.setPadding(0, 0, 0, 0);
                    mContent.removeAllViews();
                }
            }
            else {
                mContent.setPadding(0, 0, 0, 0);
                mContent.removeAllViews();
            }

            mMask.setVisibility(conn.alive ? GONE : GONE);
        }

        private final String getTime(long time) {
            mDate.setTime(time);
            return mFormat.format(mDate);
        }

        @Override
        public void onClick(View v) {
            Object tag = v.getTag();
            if (tag instanceof ConnInfo) {
                ConnInfo conn = (ConnInfo) tag;
                if (conn.protocol == IP.TCP) {
                    MsgDispatcher.get().dispatch(Messege.SHOW_CONN_LOGS, conn);
                }
            }
        }
    }

}
