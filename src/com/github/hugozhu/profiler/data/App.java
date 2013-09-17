package com.github.hugozhu.profiler.data;

import android.graphics.drawable.Drawable;

/**
 * User: hugozhu
 * Date: 9/17/13
 * Time: 4:54 PM
 */
public class App {
    public final static String TAG = "apk_profiler";
    private Drawable icon;
    private String processName;
    private String packageName;
    private int pid;
    private int uid;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {

        this.processName = processName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }
}
