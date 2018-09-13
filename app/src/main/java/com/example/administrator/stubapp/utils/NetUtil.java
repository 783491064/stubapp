package com.example.administrator.stubapp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 文件描述：网络连接状态判断
 * 作者：Created by BiJingCun on 2018/9/11.
 */
public class NetUtil {

    //没有连接网络
    public static final int NETWORK_NONE = -1;
    //移动网络
    public static final int NETWORK_MOBILE = 0;
    //无线网络
    public static final int NETWORK_WIFI = 1;

    /**
     * 获取网络的链接类型
     */
    public static int getNetWorkState(Context context) {
        // 得到连接管理器对象
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_WIFI)) {

                return NETWORK_WIFI;
            } else if (activeNetworkInfo.getType() == (ConnectivityManager.TYPE_MOBILE)) {
                return NETWORK_MOBILE;
            }
        } else {
            return NETWORK_NONE;
        }
        return NETWORK_NONE;
    }

    /**
     * 网络状况
     */
    public static boolean isNetworkConnected(Context context) {
        if (context == null) {
            return false;
        }

        boolean isOK = false;
        boolean isWifiOK = false;
        boolean isGprsOK = false;

        ConnectivityManager connManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo.State state = null;
        NetworkInfo mWIFINetworkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (mWIFINetworkInfo != null) {
            state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            if (NetworkInfo.State.CONNECTED == state) {
                isWifiOK = true;
            } else {
                isWifiOK = false;
            }
        } else {
            isWifiOK = false;
        }
        NetworkInfo m3GNetworkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (m3GNetworkInfo != null) {
            state = m3GNetworkInfo.getState();
            if (NetworkInfo.State.CONNECTED == state) {
                isGprsOK = true;
            } else {
                isGprsOK = false;
            }

        } else {
            isGprsOK = false;
        }
        if (isGprsOK || isWifiOK) {
            isOK = true;
        }
        return isOK;
    }
}
