package com.github.hugozhu.profiler;

import android.app.Application;
import android.view.WindowManager;

/**
 * User: hugozhu
 * Date: 9/18/13
 * Time: 9:49 AM
 */
public class ProfilerApplication extends Application {

    private WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

    public WindowManager.LayoutParams getMywmParams() {
        return wmParams;
    }
}
