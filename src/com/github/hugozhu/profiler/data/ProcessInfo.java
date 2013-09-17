package com.github.hugozhu.profiler.data;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * User: hugozhu
 * Date: 9/17/13
 * Time: 4:58 PM
 */
public class ProcessInfo {
    private static final String PACKAGE_NAME = "com.github.hugozhu.profiler";

    public List<App> getActiveProcess(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> run = am.getRunningAppProcesses();
        PackageManager pm = context.getPackageManager();
        List<App> progressList = new ArrayList<App>();

        for (ApplicationInfo appinfo : getPackagesInfo(context)) {
            App app = new App();
            if (((appinfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0)
                    || ((appinfo.processName != null) && (appinfo.processName
                    .equals(PACKAGE_NAME)))) {
                continue;
            }
            for (ActivityManager.RunningAppProcessInfo runningProcess : run) {
                if ((runningProcess.processName != null)
                        && runningProcess.processName
                        .equals(appinfo.processName)) {
                    app.setPid(runningProcess.pid);
                    app.setUid(runningProcess.uid);
                    break;
                }
            }
            app.setPackageName(appinfo.processName);
            app.setProcessName(appinfo.loadLabel(pm).toString());
            app.setIcon(appinfo.loadIcon(pm));
            progressList.add(app);
        }
        return progressList;
    }

    private List<ApplicationInfo> getPackagesInfo(Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        List<ApplicationInfo> appList = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        return appList;
    }
}
