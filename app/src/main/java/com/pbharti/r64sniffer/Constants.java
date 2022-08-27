package com.pbharti.r64sniffer;

import com.summer.netcore.VpnConfig;

import java.util.HashMap;
import java.util.Map;

public class Constants {

    public static final String TAG = "r64sniffer";
    public static int puid = 0;

    public static final byte DEBUG_LEV_RELEASE = 0;
    public static final byte DEBUG_LEV_DEBUG = 1;

    public static final byte DEBUG_LEV = DEBUG_LEV_DEBUG;

    //public static VpnConfig.AVAIL_CTRLS DEFAULT_SYSTEM_CTRL = VpnConfig.AVAIL_CTRLS.CAPTURE;
    //public static VpnConfig.AVAIL_CTRLS DEFAULT_UNKNOWN_CTRL = VpnConfig.AVAIL_CTRLS.CAPTURE;

    public static Map<String, VpnConfig.AVAIL_CTRLS> DEFAULT_APP_CTRLS = new HashMap<>();
    public static Map<String, VpnConfig.AVAIL_CTRLS> DEFAULT_IP_CTRLS = new HashMap<>();
    public static boolean USE_DEFAULT_PROXY = false;
    public static int DEFAULT_PROXY_IPVER = 4;
    public static String DEFAULT_PROXY_ADDR = "127.0.0.1";
    public static String DEFAULT_PROXY_PORT = "8080";
    public static boolean USE_DEFAULT_DNS = false;
    public static String DEFAULT_DNS = "8.8.8.8";
}
