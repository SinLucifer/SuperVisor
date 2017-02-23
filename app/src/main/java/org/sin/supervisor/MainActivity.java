package org.sin.supervisor;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.sin.supervisor.blackNumber.BlackNumberSet;
import org.sin.supervisor.clean.CleanActivity;
import org.sin.supervisor.listener.ListenSetting;
import org.sin.supervisor.listener.ListenView;
import org.sin.supervisor.listener.LockReceiver;

import java.util.List;


public class MainActivity extends Activity {
    private static final String TAG = "ClearMemoryActivity";
    private int click_time = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        ButtonUi listening = (ButtonUi) findViewById(R.id.listen);
        listening.setOnClickListener(new ListenClick());

        ButtonUi blacknumber = (ButtonUi) findViewById(R.id.blacknumber);
        blacknumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, BlackNumberSet.class);
                startActivity(intent);
            }
        });

        ButtonUi clean = (ButtonUi) findViewById(R.id.clean);
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CleanActivity.class);
                startActivity(intent);
            }
        });

        ButtonUi free = (ButtonUi) findViewById(R.id.free);
        free.setOnClickListener(new FreeClick());

        ButtonUi bigeye = (ButtonUi)findViewById(R.id.bigeye);
        bigeye.setOnClickListener(new BigEyeClick());
    }

    private long getAvailMemory(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        am.getMemoryInfo(mi);
        Log.d(TAG, "可用内存---->>>" + mi.availMem / (1024 * 1024));
        return mi.availMem / (1024 * 1024);
    }

    private class FreeClick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> infoList = am.getRunningAppProcesses();
            List<ActivityManager.RunningServiceInfo> serviceInfos = am.getRunningServices(100);

            long beforeMem = getAvailMemory(MainActivity.this);
            Log.d(TAG, "-----------before memory info : " + beforeMem);
            int count = 0;
            if (infoList != null) {
                for (int i = 0; i < infoList.size(); ++i) {
                    ActivityManager.RunningAppProcessInfo appProcessInfo = infoList.get(i);
                    Log.d(TAG, "process name : " + appProcessInfo.processName);
                    Log.d(TAG, "importance : " + appProcessInfo.importance);

                    // 一般数值大于RunningAppProcessInfo.IMPORTANCE_SERVICE的进程都长时间没用或者空进程了
                    // 一般数值大于RunningAppProcessInfo.IMPORTANCE_VISIBLE的进程都是非可见进程，也就是在后台运行着
                    if (appProcessInfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                        String[] pkgList = appProcessInfo.pkgList;
                        for (int j = 0; j < pkgList.length; ++j) {
                            Log.d(TAG, "It will be killed, package name : " + pkgList[j]);
                            am.killBackgroundProcesses(pkgList[j]);
                            count++;
                        }
                    }
                }
            }

            long afterMem = getAvailMemory(MainActivity.this);
            Log.d(TAG, "----------- after memory info : " + afterMem);
            Toast.makeText(MainActivity.this, "清理了 " + count + " 个进程，一共"
                    + (afterMem - beforeMem) + "M", Toast.LENGTH_LONG).show();

        }
    }

    private class ListenClick implements View.OnClickListener {
        private SharedPreferences sp;
        private Intent intent = null;
        private EditText passInput;
        private String key;

        @Override
        public void onClick(View v) {
            sp = getSharedPreferences("ListenConfig", MODE_PRIVATE);
            key = sp.getString("password", "default");

            if (!key.equals("default")) {
                AlertDialog.Builder passBuilder = new AlertDialog.Builder(MainActivity.this);
                passBuilder.setTitle("请输入密码");

                passInput = new EditText(MainActivity.this);
                passInput.setInputType(InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                passBuilder.setView(passInput);

                passBuilder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (passInput.getText().toString().equals(key)) {
                            intent = new Intent(MainActivity.this, ListenView.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getApplicationContext(), "密码错误！"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                passBuilder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                passBuilder.create().show();

            } else {
                intent = new Intent(MainActivity.this, ListenSetting.class);
                startActivity(intent);
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class BigEyeClick implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            click_time++;
            if (click_time == 3)
            {
                Toast.makeText(MainActivity.this,"CopyRight@SIPC115",Toast.LENGTH_LONG).show();
                click_time = 0;
            }
        }
    }
}
