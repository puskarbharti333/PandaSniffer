package com.pbharti.r64sniffer.utils;

import android.graphics.Point;
import android.util.DisplayMetrics;

import com.pbharti.r64sniffer.ContextMgr;



public class ScreenUtils {

    public static final int dp2px(float dp) {
        float scale = ContextMgr.getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static final Point getScreenSize() {
        DisplayMetrics dm = ContextMgr.getApplicationContext().getResources().getDisplayMetrics();
        Point p = new Point();
        p.x = dm.widthPixels;
        p.y = dm.heightPixels;
        return p;
    }

}
