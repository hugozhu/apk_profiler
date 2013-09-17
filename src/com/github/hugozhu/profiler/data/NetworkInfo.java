package com.github.hugozhu.profiler.data;

import android.net.TrafficStats;

/**
 * Created with IntelliJ IDEA.
 * User: hugozhu
 * Date: 9/17/13
 * Time: 4:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class NetworkInfo {
    int uid = 0;
    long startTime = System.currentTimeMillis();
    long startTx = 0;
    long startRx = 0;

    public NetworkInfo(int uid) {
        this.uid = uid;
    }

    public long getUidTxBytes() {
        return TrafficStats.getUidTxBytes(uid) - startTx;
    }

    public long getUidRxBytes() {
        return TrafficStats.getUidRxBytes(uid) - startRx;
    }

    public void reset() {
        startTime = System.currentTimeMillis();
        startTx = TrafficStats.getUidTxBytes(uid);
        startRx = TrafficStats.getUidRxBytes(uid);
    }
}
