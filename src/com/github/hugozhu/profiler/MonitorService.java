package com.github.hugozhu.profiler;

import android.app.Activity;
import android.app.Service;
import android.content.*;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import com.github.hugozhu.profiler.data.App;
import com.github.hugozhu.profiler.data.CPUInfo;
import com.github.hugozhu.profiler.data.MemoryInfo;
import com.github.hugozhu.profiler.data.NetworkInfo;

/**
 * The service to monitor target activity's CPUInfo, Memory, Networking Traffic consumption.
 * User: hugozhu
 * Date: 9/17/13
 * Time: 4:46 PM
 */
public class MonitorService extends Service {

    private TextView txtTraffic;
    private TextView txtCpu;
    private TextView txtMem;

    private Handler handler = new Handler();
    private int uid;
    private NetworkInfo networkInfo;
    private CPUInfo cpuInfo;
    private MemoryInfo memInfo;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onCreate() {
        super.onCreate();
    }

    private Runnable task = new Runnable() {
        public void run() {
            long tx = networkInfo.getUidTxBytes();
            long rx = networkInfo.getUidRxBytes();
            txtTraffic.setText(String.format("流量 - tx: %s rx: %s total: %s bytes" ,tx,rx, (tx+rx) ));

            cpuInfo.readCpuStat();
            txtCpu.setText(String.format("CPU - %.2f%% pid: %d uid: %d", (float) cpuInfo.getProcessCPU()*100/cpuInfo.getTotalCPU(), cpuInfo.pid, uid));

            txtMem.setText(String.format("内存 - process: %dM free: %dM total: %dM" ,
                    memInfo.getPidMemorySize(MonitorService.this.getApplicationContext()),
                    memInfo.getFreeMemorySize(MonitorService.this.getApplicationContext()),
                    memInfo.getTotalMemory() ));

            handler.postDelayed(this, 1000);
        }
    };


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int r = super.onStartCommand(intent, flags, startId);
        uid = intent.getExtras().getInt("uid");
        int pid = intent.getExtras().getInt("pid");
        cpuInfo = new CPUInfo(pid);
        memInfo = new MemoryInfo(pid);
        networkInfo  = new NetworkInfo(uid);

        reset();

        floatingView = LayoutInflater.from(this).inflate(R.layout.monitor, null);
        txtTraffic = (TextView) floatingView.findViewById(R.id.traffic);
        txtCpu = (TextView) floatingView.findViewById(R.id.cpu);
        txtMem = (TextView) floatingView.findViewById(R.id.mem);

        floatingView.findViewById(R.id.reset).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MonitorService.this.reset();
                    }
                }
        );

        floatingView.findViewById(R.id.close).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        handler.removeCallbacks(task);
                        if (windowManager != null)
                            windowManager.removeView(floatingView);
                        windowManager = null;
                    }
                }
        );

        Log.e(App.TAG, "create floating window");

        createFloatingWindow();

        handler.postDelayed(task, 1000);
        return r;
    }

    public void reset() {
        networkInfo.reset();
        cpuInfo.reset();
    }

    @Override
    public void onDestroy() {
        if (windowManager != null)
            windowManager.removeView(floatingView);
        handler.removeCallbacks(task);
        super.onDestroy();
    }

    private WindowManager windowManager = null;
    private WindowManager.LayoutParams wmParams = null;
    private View floatingView = null;
    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;


    private void createFloatingWindow() {
        SharedPreferences shared = getSharedPreferences("float_flag",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = shared.edit();
        editor.putInt("float", 1);
        editor.commit();
        windowManager = (WindowManager) getApplicationContext()
                .getSystemService("window");
        wmParams = ((ProfilerApplication) getApplication()).getMywmParams();
        wmParams.type = 2002;
        wmParams.flags |= 8;
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        wmParams.x = 0;
        wmParams.y = 0;
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.format = 1;
        windowManager.addView(floatingView, wmParams);
        floatingView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                x = event.getRawX();
                y = event.getRawY() - 25;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        updateViewPosition();
                        break;

                    case MotionEvent.ACTION_UP:
                        updateViewPosition();
                        mTouchStartX = mTouchStartY = 0;
                        break;
                }
                return true;
            }
        });
    }

    /**
     * update the position of floating window.
     */
    private void updateViewPosition() {
        wmParams.x = (int) (x - mTouchStartX);
        wmParams.y = (int) (y - mTouchStartY);
        windowManager.updateViewLayout(floatingView, wmParams);
    }
}
