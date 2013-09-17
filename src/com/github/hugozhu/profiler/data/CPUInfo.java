package com.github.hugozhu.profiler.data;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * User: hugozhu
 * Date: 9/17/13
 * Time: 4:53 PM
 */
public class CPUInfo {
    public int pid;
    private long processCpu;
    private long processCpuBase;
    private long totalCpu;
    private long totalCpuBase;

    public CPUInfo(int pid) {
        this.pid = pid;
        readCpuStat();
    }

    public long getProcessCPU() {
        return processCpu - processCpuBase;
    }

    public long getTotalCPU() {
        return totalCpu - totalCpuBase;
    }

    public void reset() {
        processCpuBase = processCpu;
        totalCpuBase = totalCpu;
    }

    public void readCpuStat() {
        String processPid = Integer.toString(pid);
        String cpuStatPath = "/proc/" + processPid + "/stat";
        RandomAccessFile processCpuInfo = null;
        try {
            // monitor cpu stat of certain process
            processCpuInfo = new RandomAccessFile(cpuStatPath,
                    "r");
            String line = "";
            StringBuffer stringBuffer = new StringBuffer();
            while ((line = processCpuInfo.readLine()) != null) {
                stringBuffer.append(line + "\n");
            }
            String[] tok = stringBuffer.toString().split(" ");
            processCpu = Long.parseLong(tok[13]) + Long.parseLong(tok[14]);
        } catch (FileNotFoundException e) {
            Log.e(App.TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(App.TAG, cpuStatPath + " " + e.getMessage());
        } finally {
            try {
                processCpuInfo.close();
            } catch (IOException e) {
            }
        }

        try {
            RandomAccessFile cpuInfo = new RandomAccessFile("/proc/stat", "r");
            String[] toks = cpuInfo.readLine().split("\\s+");
            totalCpu = Long.parseLong(toks[1]) + Long.parseLong(toks[2])
                    + Long.parseLong(toks[3]) + Long.parseLong(toks[4])
                    + Long.parseLong(toks[6]) + Long.parseLong(toks[5])
                    + Long.parseLong(toks[7]);
            cpuInfo.close();
        } catch (FileNotFoundException e) {
            Log.e(App.TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(App.TAG, cpuStatPath + " " + e.getMessage());
        }
    }
}
