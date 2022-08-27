package com.pbharti.r64sniffer.utils;

import com.pbharti.r64sniffer.ContextMgr;



public class ResTools {

    public static final float getDimen(int resId) {
        return ContextMgr.getContext().getResources().getDimension(resId);
    }

    public static final int getColor(int resId) {
        return ContextMgr.getContext().getResources().getColor(resId);
    }

    public static final String getString(int resId) {
        return ContextMgr.getApplicationContext().getResources().getString(resId);
    }

}
