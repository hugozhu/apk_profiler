package com.github.hugozhu.profiler.data;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * User: hugozhu
 * Date: 9/17/13
 * Time: 4:56 PM
 */
public class MemoryInfo {
    int pid;

    public MemoryInfo(int pid) {
        this.pid = pid;
    }

    /**
     * get total memory of certain device.
     *
     * @return total memory of device
     */
    public long getTotalMemory() {
        String memInfoPath = "/proc/meminfo";
        String readTemp = "";
        String memTotal = "";
        long memory = 0;
        try {
            FileReader fr = new FileReader(memInfoPath);
            BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
            while ((readTemp = localBufferedReader.readLine()) != null) {
                if (readTemp.contains("MemTotal")) {
                    String[] total = readTemp.split(":");
                    memTotal = total[1].trim();
                }
            }
            localBufferedReader.close();
            String[] memKb = memTotal.split(" ");
            memTotal = memKb[0].trim();
            memory = Long.parseLong(memTotal);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return memory/1024;
    }

    /**
     * get free memory.
     *
     * @return free memory of device
     *
     */
    public long getFreeMemorySize(Context context) {
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        am.getMemoryInfo(outInfo);
        long avaliMem = outInfo.availMem;
        return avaliMem / (1024*1024);
    }

    public int getPidMemorySize(Context context) {
        ActivityManager am = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        int[] myMempid = new int[] { pid };
        Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(myMempid);
        memoryInfo[0].getTotalSharedDirty();

        // int memSize = memoryInfo[0].dalvikPrivateDirty;
        // TODO PSS
        int memSize = memoryInfo[0].getTotalPss();
        // int memSize = memoryInfo[0].getTotalPrivateDirty();
        return memSize/1024;
    }
}
