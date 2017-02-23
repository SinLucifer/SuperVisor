package org.sin.supervisor.listener;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.sin.supervisor.MainActivity;
import org.sin.supervisor.R;

/**
 * Created by Sin on 2015/9/18.
 */
public class ListenSetting extends Activity {
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private EditText passSet;
    private EditText safeNumberSet;
    private EditText safepassSet;
    private EditText ipSet;

    private String passwd;
    private String safe_number;
    private String safe_pass;
    private String ip_Address;
    private String IMSI;

    private Intent intent = null;
    private TelephonyManager mTelephonyMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listen_setting);
        sp = getSharedPreferences("ListenConfig", MODE_PRIVATE);
        editor = sp.edit();

        mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        IMSI = mTelephonyMgr.getSubscriberId();

        editor.putString("IMSI", IMSI);
        editor.commit();

        passSet = (EditText) findViewById(R.id.pass);
        safeNumberSet = (EditText) findViewById(R.id.safe_number);
        safepassSet = (EditText) findViewById(R.id.safe_passwd);
        ipSet = (EditText) findViewById(R.id.ip_edit);

        ImageView next = (ImageView) findViewById(R.id.next_1);
        next.setOnClickListener(new NextOnclick());

        ImageView returnbn = (ImageView) findViewById(R.id.return_lis1);
        returnbn.setOnClickListener(new BackOnclickListener());

    }

    private class NextOnclick implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            passwd = passSet.getText().toString().trim();
            safe_number = safeNumberSet.getText().toString().trim();
            safe_pass = safepassSet.getText().toString().trim();
            ip_Address = ipSet.getText().toString().trim();

            if (passwd.length() < 5 || safe_number.length() != 11 || safe_pass.length() != 4) {
                Toast.makeText(ListenSetting.this
                        , "您输入的密码或安全手机的长度不正确，请检查后输入！"
                        , Toast.LENGTH_SHORT).show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(ListenSetting.this);
                builder.setTitle("请确认您的设定");

                TextView show = new TextView(ListenSetting.this);
                show.setText("\n\n" + "您要设置的密码是:" + passwd + "\n\n"
                        + "您要设置的安全手机是:" + safe_number + "\n\n"
                        + "您要设置的安全密码是:" + safe_pass + "\n\n"
                        + "您设置的服务器IP为：" + ip_Address);
                builder.setView(show);

                builder.setPositiveButton("下一步", new PositOnclick());
                builder.setNegativeButton("返回", new NegatOnclick());

                builder.create().show();
            }
        }
    }

    private class PositOnclick implements Dialog.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            editor.putString("password", passwd);
            editor.putString("safe_number", safe_number);
            editor.putString("lock_pass", safe_pass);
            editor.putString("sim_control", "false");
            editor.putString("tel_control", "false");
            editor.putString("SMS_Help", "false");
            editor.putString("ip_Address", ip_Address);
            editor.commit();
            intent = new Intent(ListenSetting.this, ListenView.class);
            startActivity(intent);
            ListenSetting.this.finish();
        }
    }

    private class NegatOnclick implements Dialog.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
        }
    }

    private class BackOnclickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            intent = new Intent(ListenSetting.this, MainActivity.class);
            startActivity(intent);
            ListenSetting.this.finish();
        }
    }
}
