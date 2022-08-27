package com.pbharti.r64sniffer.window;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Process;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.pbharti.r64sniffer.Constants;
import com.pbharti.r64sniffer.ContextMgr;
import com.pbharti.r64sniffer.MainActivity2;
import com.pbharti.r64sniffer.R;
import com.pbharti.r64sniffer.message.Messege;
import com.pbharti.r64sniffer.message.MsgDispatcher;
import com.pbharti.r64sniffer.traffic.ConnInfo;
import com.pbharti.r64sniffer.traffic.IP;
import com.pbharti.r64sniffer.traffic.TrafficMgr;
import com.pbharti.r64sniffer.utils.PackageUtils;
import com.pbharti.r64sniffer.utils.ResTools;
import com.pbharti.r64sniffer.utils.SystemUtils;
import com.summer.netcore.Config;
import com.summer.netcore.VpnConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.pbharti.r64sniffer.Constants.DEFAULT_APP_CTRLS;
import static com.pbharti.r64sniffer.MainActivity2.AddApp;

public class TrafficCtrlWindow extends AbsListContentWindow<TrafficCtrlWindow.CtrlItem, TrafficCtrlWindow.ItemView>
        implements VpnConfig.IListener, AppsSelectWindow.IResultCallback {

    private static final String TAG = "pandasniffer";
    private final List<CtrlItem> mDataList = new ArrayList<>();
    private int mCtrl;
    final List<PackageUtils.AppInfo> installedApps = PackageUtils.getAllInstallApps();
    final PackageManager pm = ContextMgr.getApplicationContext().getPackageManager();
    public static String AppName = "";
    public static Drawable AppIcon = null;

    //private String mTitle;
    //private TitleBar mTitleBar;
    //private AddSpinner mAdd;
    private TextView mEdit;
    private HostInputDialog mHostInputDialog;
    private MainPageTitleBar mTitleBar;

    private boolean mEditMode = false;

    public TrafficCtrlWindow(Context context) {
        super(context);
        setEmptyDescryption(ResTools.getString(R.string.tips_none_ctrl));
    }

    public static TrafficCtrlWindow createWindow(Context context, int ctrl) {
        TrafficCtrlWindow w = new TrafficCtrlWindow(context);
        //w.mCtrl = ctrl;
        return w;
    }

    private static String getCustomCtrlName(VpnConfig.AVAIL_CTRLS ctrls) {
        if (ctrls != null) {
            switch (ctrls) {
                case BASE:
                    return ResTools.getString(R.string.ctrl_type_none);
                case PROXY:
                    return ResTools.getString(R.string.ctrl_type_proxy);
                case CAPTURE:
                    return ResTools.getString(R.string.ctrl_type_capture);
                case PROXY_CAPTURE:
                    return ResTools.getString(R.string.ctrl_type_pxy_cap);
            }
        }
        return "";
    }

    @Override
    protected View getTitleBar() {
        if (mTitleBar == null) {
            mTitleBar = new MainPageTitleBar(getContext());
        }
        return mTitleBar.getView();
    }


    @Override
    protected void onPause() {
        super.onPause();
        //mAdd.onDetachedFromWindow();
    }

    @Override
    protected void preSwitchIn() {
        super.preSwitchIn();
        VpnConfig.addListener(this);

        initData();
        updateData(mDataList);
    }
    @Override
    protected void onResume() {
        super.onResume();
        TrafficMgr.getInstance().stop();
        AddApp();
    }

    @Override
    protected void postSwitchOut() {
        super.postSwitchOut();
        VpnConfig.removeListener(this);
    }

    @Override
    protected int getItemId(CtrlItem item) {
        return mDataList.indexOf(item);
    }

    @Override
    protected ItemView createItemView(int position) {
        return new ItemView(getContext());
    }

    @Override
    protected void bindItem(CtrlItem item, ItemView view) {
        //item.value = null;
        view.bindCtrlItem(item);
    }

    private void initDataAndRefreshInUIThread() {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                initData();
                updateData(mDataList);
            }
        });
    }

    @Override
    public void onVpnConfigLoaded() {

        initDataAndRefreshInUIThread();
    }

    @Override
    public void onVpnConfigItemUpdated(int key, String value) {
        if (Config.CTRL_PACKAGES == key
                || Config.CTRL_DOMAIN == key
                || Config.CTRL_IP == key) {


            initDataAndRefreshInUIThread();
        }
    }

    private void initData() {
        mDataList.clear();
        mDataList.addAll(getItems(VpnConfig.CtrlType.APP, mCtrl));
        mDataList.addAll(getItems(VpnConfig.CtrlType.DOMAIN, mCtrl));
        mDataList.addAll(getItems(VpnConfig.CtrlType.IP, mCtrl));

        Collections.sort(mDataList, new Comparator<CtrlItem>() {
            @Override
            public int compare(CtrlItem o1, CtrlItem o2) {
                int t1 = o1.type == VpnConfig.CtrlType.APP ? 1 : (o1.type == VpnConfig.CtrlType.DOMAIN ? 2 : 3);
                int t2 = o2.type == VpnConfig.CtrlType.APP ? 1 : (o2.type == VpnConfig.CtrlType.DOMAIN ? 2 : 3);
                if (t1 != t2) {
                    return t1 - t2;
                }

                return o1.value.compareTo(o2.value);
            }
        });
    }

    private List<CtrlItem> getItems(VpnConfig.CtrlType type, int ctrl) {
        List<CtrlItem> items = new ArrayList<>();
        List<Pair<String, VpnConfig.AVAIL_CTRLS>> kvs = VpnConfig.getCtrls(type, ctrl);
        if (kvs != null) {
            for (Pair<String, VpnConfig.AVAIL_CTRLS> kv : kvs) {
                CtrlItem item = new CtrlItem();
                PackageUtils.AppInfo appInfo = PackageUtils.getAppInfo(Integer.parseInt(kv.first));
                if (appInfo!=null){
                    item.type = type;
                    item.value = kv.first;
                    item.ctrl = kv.second;
                    items.add(item);
                }

            }
        }

        return items;
    }

    private void showAppSelectWindow() {
        List<Integer> excludes = new ArrayList<>();
        excludes.add(Process.myUid());
        for (CtrlItem item : mDataList) {
            if (item.type == VpnConfig.CtrlType.APP) {
                excludes.add(Integer.valueOf(item.value));
            }
        }
        MsgDispatcher.get().dispatchSync(Messege.PUSH_WINDOW, new AppsSelectWindow(ContextMgr.getContext(), excludes, this));
    }

    private void addCtrlItem(CtrlItem ctrlItem) {
        for (CtrlItem c : mDataList) {
            if (c.type == ctrlItem.type && ctrlItem.value.equals(c.value)) {
                return;
            }
        }

        VpnConfig.updateCtrl(ctrlItem.type, ctrlItem.value, ctrlItem.ctrl);

    }

    private void showHostInputDialog(final boolean ip) {
        if (mHostInputDialog != null) {
            return;
        }

        mHostInputDialog = new HostInputDialog(getContext(), new HostInputDialog.IDialogCallback() {
            @Override
            public void onHostInput(String value) {
                if (VpnConfig.isValidateHost(value)) {
                    TrafficCtrlWindow.CtrlItem ctrlItem = new TrafficCtrlWindow.CtrlItem();
                    ctrlItem.type = ip ? VpnConfig.CtrlType.IP : VpnConfig.CtrlType.DOMAIN;
                    ctrlItem.value = value;
                    ctrlItem.ctrl = VpnConfig.AVAIL_CTRLS.BASE;
                    addCtrlItem(ctrlItem);
                    mHostInputDialog.dismiss();
                    return;
                }

                Toast.makeText(getContext(), R.string.tips_invalid_input, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDismiss() {
                mHostInputDialog = null;
            }
        });

        mHostInputDialog.show(ip ? EditorInfo.TYPE_CLASS_PHONE : 0);
    }

    /*private void switchEditMode() {
        mEditMode = !mEditMode;
        if (mEditMode) {
            mAdd.setVisibility(View.INVISIBLE);
            mEdit.setText(R.string.done);
        } else {
            mAdd.setVisibility(View.VISIBLE);
            mEdit.setText(R.string.edit);
        }
        update();
    }*/

    private void onDelete(ItemView view) {
        if (view.getTag() instanceof CtrlItem) {
            CtrlItem item = (CtrlItem) view.getTag();
            VpnConfig.updateCtrl(item.type, item.value, null);
        }
    }

    @Override
    public void onSelect(List<Integer> uids) {
        for (int uid : uids) {
            String suid = String.valueOf(uid);
            VpnConfig.updateCtrl(VpnConfig.CtrlType.APP, suid, VpnConfig.AVAIL_CTRLS.BASE);

            CtrlItem item = new CtrlItem();
            PackageUtils.AppInfo appInfo = PackageUtils.getAppInfo(uid);
            if (appInfo!=null){
                item.type = VpnConfig.CtrlType.APP;
                item.value = suid;
                mDataList.add(item);
            }
        }

        updateData(mDataList);
    }

    @Override
    protected boolean onBackPressed() {
        if (mEditMode) {
            //switchEditMode();
            return true;
        }
        return false;
    }

    public class CtrlItem {
        VpnConfig.CtrlType type;
        String value;
        VpnConfig.AVAIL_CTRLS ctrl;

        boolean selected = false;
    }

    public class ItemView extends LinearLayout {

        ImageView mIcon;
        TextView mAppName;
        TextView mDelete;
        Spinner mCtrl;
        MySpinnerAdapter mAdapter;
        CtrlItem mCtrlItem;

        public ItemView(Context context) {
            super(context);

            setOrientation(HORIZONTAL);
            setGravity(Gravity.CENTER_VERTICAL);
            //setOnClickListener(this);

            int hp = (int) ResTools.getDimen(R.dimen.hor_padding);
            int vp = (int) ResTools.getDimen(R.dimen.vtl_padding);

            mIcon = new ImageView(context);
            int iconSize = (int) ResTools.getDimen(R.dimen.icon_size);
            LayoutParams lp = new LayoutParams(iconSize, iconSize);
            lp.rightMargin = (int) ResTools.getDimen(R.dimen.hor_padding);
            mIcon.setLayoutParams(lp);
            mIcon.setMinimumHeight(80);
            addView(mIcon);

            mAppName = new TextView(context);
            mAppName.setTextSize(20);
            mAppName.setTextColor(ResTools.getColor(R.color.text));
            lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.weight = 1;
            addView(mAppName, lp);

            FrameLayout rightMenu = new FrameLayout(getContext());
            rightMenu.setBackgroundColor(Color.TRANSPARENT);
            rightMenu.setForegroundGravity(Gravity.CENTER);


            mDelete = new TextView(getContext());
            mDelete.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            int buttonVtlPadding = (int) ResTools.getDimen(R.dimen.btn_vtl_padding);
            mDelete.setPadding(hp, buttonVtlPadding, hp, buttonVtlPadding);
            //mDelete.setOnClickListener(this);
            mDelete.setText(R.string.delete);
            mDelete.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) ResTools.getDimen(R.dimen.textsize2));
            mDelete.setTextColor(ResTools.getColor(R.color.blue));
            mDelete.setBackgroundResource(R.drawable.button_blue);

            FrameLayout.LayoutParams rlp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            rlp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
            //rightMenu.addView(mDelete, rlp);

            mCtrl = new Spinner(getContext());
            mCtrl.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
            mAdapter = new MySpinnerAdapter();
            mCtrl.setAdapter(mAdapter);
            mCtrl.setBackgroundColor(Color.TRANSPARENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mCtrl.setPopupBackgroundResource(R.drawable.popwindow_bg);
            }
            mCtrl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    mCtrlItem.ctrl = VpnConfig.AVAIL_CTRLS.values()[position];
                    VpnConfig.updateCtrl(mCtrlItem.type, mCtrlItem.value, mCtrlItem.ctrl);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            //rightMenu.addView(mCtrl, rlp);*/

            lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
            addView(rightMenu, lp);

            setBackgroundResource(R.drawable.list_item_bg);
            setMinimumHeight((int) ResTools.getDimen(R.dimen.item_height));

            setPadding(30,30,30,30);
        }

        public void bindCtrlItem(CtrlItem item) {

            switch (item.type) {
                case APP: {
                    bindApp(Integer.valueOf(item.value));
                    break;
                }
                default: {
                    bindHost(item.value);
                }
            }
            for (int i = 0; i < VpnConfig.AVAIL_CTRLS.values().length; i++) {
                if (item.ctrl == VpnConfig.AVAIL_CTRLS.values()[i]) {
                    mCtrl.setSelection(i);
                    break;
                }
            }
            //mAdapter.update(VpnConfig.AVAIL_CTRLS.values());

            mCtrlItem = item;
            setTag(item);
        }

        public void bindApp(int uid) {
            final int did = uid;
            String temp = String.valueOf(uid);
            if(temp != null){
                final PackageUtils.AppInfo appInfo = PackageUtils.getAppInfo(uid);
            if (appInfo != null) {
                mIcon.setImageDrawable(appInfo.icon);
                mIcon.setVisibility(VISIBLE);
                mAppName.setText(appInfo.name);
                setOnClickListener(new OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        TrafficMgr.getInstance().stop();
                        final String[] Pkgname = pm.getPackagesForUid(did);
                        AppName = appInfo.name;
                        AppIcon = appInfo.icon;
                        String name = Pkgname[0];
                        DEFAULT_APP_CTRLS.clear();
                        for (PackageUtils.AppInfo ai : installedApps) {
                            VpnConfig.updateCtrl(VpnConfig.CtrlType.APP, String.valueOf(ai.uid), Constants.DEFAULT_APP_CTRLS.get(ai.pkg));
                        }
                        DEFAULT_APP_CTRLS.put(name, VpnConfig.AVAIL_CTRLS.CAPTURE);
                        for (PackageUtils.AppInfo ai : installedApps) {
                            VpnConfig.updateCtrl(VpnConfig.CtrlType.APP, String.valueOf(ai.uid), Constants.DEFAULT_APP_CTRLS.get(ai.pkg));
                        }
                        MsgDispatcher.get().dispatchSync(Messege.PUSH_WINDOW, new MainWindow(ContextMgr.getContext()));
                        TrafficMgr.getInstance().start();
                    }
                });
            } else {
                mAppName.setText(temp);
                mIcon.setImageResource(PackageUtils.getDefaultAppIcon(uid));
            }
            }

            mDelete.setVisibility(mEditMode ? VISIBLE : INVISIBLE);
            mCtrl.setVisibility(!mEditMode ? VISIBLE : INVISIBLE);
            if (mEditMode) {
                if (uid > 0) {
                    mDelete.setTextColor(ResTools.getColor(R.color.blue));
                    mDelete.setBackgroundResource(R.drawable.button_blue);
                    mDelete.setEnabled(true);
                } else {
                    mDelete.setTextColor(ResTools.getColor(R.color.gray));
                    mDelete.setBackgroundResource(R.drawable.button_gray);
                    mDelete.setEnabled(false);
                }
            }
        }

        public void bindHost(String host) {
            mAppName.setText(host);
            mIcon.setVisibility(GONE);
            mDelete.setVisibility(mEditMode ? VISIBLE : INVISIBLE);
            mCtrl.setVisibility(!mEditMode ? VISIBLE : INVISIBLE);
            if (mEditMode) {
                mDelete.setTextColor(ResTools.getColor(R.color.blue));
                mDelete.setBackgroundResource(R.drawable.button_blue);
            }
        }

    }

    /*private class AddSpinner extends Spinner {

        public AddSpinner(Context context) {
            super(context);
        }

        @Override
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
        }
    }*/

    private class AddSpinnerAdapter extends BaseAdapter {
        VpnConfig.CtrlType[] items;

        private AddSpinnerAdapter(VpnConfig.CtrlType[] items) {
            this.items = items;
        }

        @Override
        public int getCount() {
            return items == null ? 0 : items.length;
        }

        @Override
        public VpnConfig.CtrlType getItem(int position) {
            return items == null ? null : (0 <= position && position < items.length ? items[position] : null);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //@Override
        /*public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(getContext());
                convertView.setBackgroundColor(Color.TRANSPARENT);
                ((TextView) convertView).setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) ResTools.getDimen(R.dimen.textsize2));
                int vp = ScreenUtils.dp2px(12);
                int hp = (int) ResTools.getDimen(R.dimen.hor_padding);
                convertView.setPadding(hp, vp, hp, vp);
            }

            VpnConfig.CtrlType ctrlType = getItem(position);
            convertView.setTag(ctrlType);

            convertView.setOnClickListener(TrafficCtrlWindow.this);
            ((TextView) convertView).setText(getAddDesc(ctrlType));
            return convertView;
        }*/

        private String getAddDesc(VpnConfig.CtrlType ctrlType) {
            if (ctrlType != null) {
                switch (ctrlType) {
                    case APP:
                        return ResTools.getString(R.string.ctrl_add_app);
                    case IP:
                        return ResTools.getString(R.string.ctrl_add_ip);
                    case DOMAIN:
                        return ResTools.getString(R.string.ctrl_add_domain);
                }
            }

            return "null";
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) convertView;
            if (textView == null) {
                textView = new TextView(getContext());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) ResTools.getDimen(R.dimen.textsize1));
                textView.setTextColor(ResTools.getColor(R.color.background));
            }

            textView.setText(R.string.add);
            return textView;
        }
    }

    private class MySpinnerAdapter extends BaseAdapter {

        VpnConfig.AVAIL_CTRLS[] items;

        private MySpinnerAdapter() {

        }

        private void update(VpnConfig.AVAIL_CTRLS[] items) {
            this.items = items;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return items == null ? 0 : items.length;
        }

        @Override
        public VpnConfig.AVAIL_CTRLS getItem(int position) {
            return items == null ? null : (0 <= position && position < items.length ? items[position] : null);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = new TextView(getContext());
                convertView.setBackgroundColor(Color.TRANSPARENT);
                ((TextView) convertView).setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) ResTools.getDimen(R.dimen.textsize2));
                int vp = (int) ResTools.getDimen(R.dimen.btn_vtl_padding2);
                int hp = (int) ResTools.getDimen(R.dimen.btn_hor_padding);
                convertView.setPadding(hp, vp, hp, vp);
            }

            VpnConfig.AVAIL_CTRLS ctrl = getItem(position);
            ((TextView) convertView).setText(getCustomCtrlName(ctrl));
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) convertView;
            if (textView == null) {
                int hp = (int) ResTools.getDimen(R.dimen.btn_hor_padding);
                int vp = (int) ResTools.getDimen(R.dimen.btn_vtl_padding);
                textView = new TextView(getContext());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) ResTools.getDimen(R.dimen.textsize2));
                textView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
                textView.setPadding(hp, vp, hp, vp);
            }

            VpnConfig.AVAIL_CTRLS ctrl = getItem(position);
            textView.setText(getCustomCtrlName(ctrl));
            if ((ctrl.ctrls & VpnConfig.CTRL_BITS.BLOCK) > 0) {
                textView.setTextColor(ResTools.getColor(R.color.red));
                textView.setBackgroundResource(R.drawable.button_red);
            } else if ((ctrl.ctrls & VpnConfig.CTRL_BITS.PROXY) > 0) {
                textView.setTextColor(ResTools.getColor(R.color.green));
                textView.setBackgroundResource(R.drawable.button_green);
            } else if ((ctrl.ctrls & VpnConfig.CTRL_BITS.CAPTURE) > 0) {
                textView.setTextColor(ResTools.getColor(R.color.blue));
                textView.setBackgroundResource(R.drawable.button_blue);
            } else {
                textView.setTextColor(ResTools.getColor(R.color.gray));
                textView.setBackgroundResource(R.drawable.button_gray);
            }

            return textView;
        }
    }
}
