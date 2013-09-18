package com.github.hugozhu.profiler;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Instrumentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.github.hugozhu.profiler.data.App;
import com.github.hugozhu.profiler.data.ProcessInfo;

public class MainActivity extends Activity {
    private Intent monitor;
    Instrumentation inst = new Instrumentation();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final ListView listView = (ListView) findViewById(R.id.listview);
        App[] values = new ProcessInfo().getActiveProcess(this.getApplicationContext()).toArray(new App[]{});
        final MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(this, values);
        listView.setAdapter(adapter);

        monitor = new Intent();
        monitor.setClass(MainActivity.this, MonitorService.class);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final App app = (App) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "开始测试 " + app.getProcessName(), Toast.LENGTH_LONG)
                        .show();
                Intent intent = getPackageManager()
                        .getLaunchIntentForPackage(app.getPackageName());
                try {
                    startActivity(intent);
                    App runningApp = waitForAppStart(app.getPackageName());
                    showToast(runningApp.getProcessName()+" 已启动 pid: "+runningApp.getPid()+" uid:"+runningApp.getUid());

                    if (runningApp.getUid()>0) {
                        monitor.putExtra("uid", runningApp.getUid());
                        monitor.putExtra("pid", runningApp.getPid());
                        monitor.putExtra("packageName", runningApp.getPackageName());
                        monitor.putExtra("processName", runningApp.getProcessName());

                        if (monitor!=null) {
                            stopService(monitor);
                        }
                        startService(monitor);
                        startMyInstrumentation();
                    }

                } catch (NullPointerException e) {
                    showToast(app.getProcessName()+"该程序无法启动");
                    return;
                }
            }
        });
    }

    private void showToast(String str) {
        Toast.makeText(this, str,Toast.LENGTH_LONG).show();
    }

    /**
     * wait for test application started.
     *
     * @param packageName
     *            package name of test application
     */
    private App waitForAppStart(String packageName) {
        long startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() < startTime + 30*1000l) {
            for (App app : new ProcessInfo().getActiveProcess(getBaseContext())) {
                if ((app.getPackageName() != null)
                        && (app.getPackageName().equals(packageName))) {
                    int pid = app.getPid();
                    if (pid != 0) {
                        return app;
                    }
                }
            }
        }
        return null;
    }


    public void startMyInstrumentation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000l);
                    Log.e(App.TAG,"send a click");
                    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 240, 1000, 0));
                    inst.sendPointerSync(MotionEvent.obtain(SystemClock.uptimeMillis(),SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 240, 1000, 0));
                } catch (Exception e) {
                    Log.e(App.TAG,"Can't send instrumentation: "+e.getMessage());
                }
            }

        }).start();
    }

    private class MySimpleArrayAdapter extends ArrayAdapter<App> {
        private final Context context;
        App[] apps;

        class ViewHolder {
            public TextView title;
            public TextView description;
            public ImageView icon;
        }

        public MySimpleArrayAdapter(Context context, App[] values) {
            super(context, R.layout.app_row, values);
            this.context = context;
            this.apps = values;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.app_row, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.title = (TextView) rowView.findViewById(R.id.title);
                viewHolder.description = (TextView) rowView.findViewById(R.id.secondLine);
                viewHolder.icon = (ImageView) rowView.findViewById(R.id.icon);
                rowView.setTag(viewHolder);
            }
            ViewHolder holder = (ViewHolder) rowView.getTag();
            App app = this.apps[position];
            holder.title.setText(app.getProcessName());
            holder.description.setText(app.getPackageName());
            Drawable dr = app.getIcon();
            Bitmap bitmap = ((BitmapDrawable) dr).getBitmap();
            dr = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 120, 120, true));
            holder.icon.setImageDrawable(dr);
            return rowView;
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, Menu.FIRST, 0, "退出").setIcon(
                android.R.drawable.ic_menu_delete);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getOrder()) {
            case 0:
                if (monitor!=null) {
                    stopService(monitor);
                }
                finish();
                System.exit(0);
            default:
                break;
        }
        return false;
    }
}
