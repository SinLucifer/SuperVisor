package org.sin.supervisor.listener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.sin.supervisor.MainActivity;
import org.sin.supervisor.R;

/**
 * Created by Sin on 2015/9/19.
 */
public class ListenView extends Activity {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private TextView state;
    private Switch tel_switch;
    private Switch sim_switch;
    private EditText safe_number;
    private EditText IMSI_number;
    private ImageView initial;
    private ImageView back;
    private TelephonyManager mTelephonyMgr;

    private Intent intent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listen_view);

        sp = getSharedPreferences("ListenConfig", MODE_PRIVATE);
        editor = sp.edit();

        state = (TextView) findViewById(R.id.state);
        if (sp.getString("tel_control", null).equals("true") &&
                sp.getString("sim_control", null).equals("true")) {
            state.setText("您的手机已开启保护！");
        }

        tel_switch = (Switch) findViewById(R.id.tel_switch);
        if (sp.getString("tel_control", null).equals("true")) {
            //Log.d("sin",sp.getString("tel_control", null));
            tel_switch.setChecked(true);
        }
        tel_switch.setOnCheckedChangeListener(new TelChangeListener());

        sim_switch = (Switch) findViewById(R.id.sim_switch);
        if (sp.getString("sim_control", null).equals("true")) {
            //Log.d("sin",sp.getString("sim_control", null));
            sim_switch.setChecked(true);
        }
        sim_switch.setOnCheckedChangeListener(new SimChangeListener());

        safe_number = (EditText) findViewById(R.id.safe_number2);
        safe_number.setText(sp.getString("safe_number", null));

        mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        IMSI_number = (EditText) findViewById(R.id.imsi_number);
        IMSI_number.setText(mTelephonyMgr.getSubscriberId());


        initial = (ImageView) findViewById(R.id.initial);
        initial.setOnClickListener(new InitialOnclickListener());
        back = (ImageView) findViewById(R.id.back);
        back.setOnClickListener(new BackOnclickListener());
    }

    public void getDevice() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName componentName = new ComponentName(this,
                LockReceiver.class);
        if (!devicePolicyManager.isAdminActive(componentName)) {
            Intent intent = new Intent(
                    DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    componentName);
            startActivity(intent);
        }
    }

    public void smsHelp() {
        if (sp.getString("SMS_Help", null).equals("true")) {
            return;
        } else {
            SmsManager sManager = SmsManager.getDefault();
            sManager.sendTextMessage(sp.getString("safe_number", null), null,
                    "您好！您的手机已被设为本机的安全号码，在必要时您可以使用以下指令对本机进行操作：", null, null);
            sManager.sendTextMessage(sp.getString("safe_number", null), null,
                    "#backup#  备份通讯录" +
                            "#location#  获取手机位置", null, null);
            sManager.sendTextMessage(sp.getString("safe_number", null), null,
                    "#lock#  以您设定的安全密码锁定手机" +
                            "#wipe# 清除所有数据  " +
                            "请妥善保管好本条信息以备不时之需，感谢您使用本软件！", null, null);
        }
    }

    private class TelChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                editor.putString("tel_control", "true");
                getDevice();
                smsHelp();
                editor.putString("SMS_Help", "true");
                editor.commit();
                if (sp.getString("sim_control", null).equals("true")) {
                    state.setText("您的手机已开启保护！");
                }
            } else {
                editor.putString("tel_control", "false");
                editor.commit();
                state.setText("您的手机还未开启保护！");
            }
        }
    }

    private class SimChangeListener implements CompoundButton.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                editor.putString("sim_control", "true");
                editor.commit();
                if (sp.getString("tel_control", null).equals("true")) {
                    state.setText("您的手机已开启保护！");
                }
            } else {
                editor.putString("sim_control", "false");
                editor.commit();
                state.setText("您的手机还未开启保护！");
            }
        }
    }

    private class InitialOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(ListenView.this);
            builder.setTitle("您确定要初始化数据么？");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    editor.clear();
                    editor.putString("guide_activity", "true");
                    editor.commit();
                    intent = new Intent(ListenView.this, ListenSetting.class);
                    startActivity(intent);
                    ListenView.this.finish();
                }
            });
            builder.setNegativeButton("返回", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();

        }
    }

    private class BackOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            intent = new Intent(ListenView.this, MainActivity.class);
            startActivity(intent);
            ListenView.this.finish();
        }
    }
}
