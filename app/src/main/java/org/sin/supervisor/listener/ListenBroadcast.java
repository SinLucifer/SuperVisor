package org.sin.supervisor.listener;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

import java.util.List;
import java.util.Map;

/**
 * Created by Sin on 2015/9/20.
 */
public class ListenBroadcast extends BroadcastReceiver {
    private SharedPreferences sp;
    private LocationManagerProxy mLocationManagerProxy;
    private TelephonyManager mTelephonyMgr;
    private String IMSI_number;
    private String SIM_listen;
    private String safe_number;
    private String tel_listen;
    private SmsManager sManager;
    private SmsMessage sms;
    private DevicePolicyManager deviceManager;
    private String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private String BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private String message_body;
    private String phone_number;
    private String address;

    @Override
    public void onReceive(Context context, Intent intent) {
        sp = context.getSharedPreferences("ListenConfig", context.MODE_PRIVATE);
        SIM_listen = sp.getString("sim_control", null);
        safe_number = sp.getString("safe_number", null);
        tel_listen = sp.getString("tel_control", null);

        deviceManager = (DevicePolicyManager) context.getSystemService(context.DEVICE_POLICY_SERVICE);

        mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        IMSI_number = mTelephonyMgr.getSubscriberId();

        sManager = SmsManager.getDefault();

        if (intent.getAction().equals(BOOT_COMPLETED)) {
            if (SIM_listen.equals("true")) {
                if (!IMSI_number.equals(sp.getString("IMSI", null))) {
                    sManager.sendTextMessage(safe_number, null,
                            "您的SIM卡发生改变", null, null);
                    context.startService(new Intent(context, SocketService.class));
                }
            }
        }


        if (intent.getAction().equals(SMS_RECEIVED)) {
            Object[] pdusData = (Object[]) intent.getExtras().get("pdus");
            for (int x = 0; x < pdusData.length; x++) {
                byte[] pdus = (byte[]) pdusData[x];
                sms = SmsMessage.createFromPdu(pdus);
                message_body = sms.getMessageBody();
                phone_number = sms.getOriginatingAddress();
                Log.d("Sin", phone_number);
            }
            if (phone_number.equals(safe_number) || phone_number.equals("86" + safe_number)
                    || phone_number.equals("+86" + safe_number)) {
                if (tel_listen.equals("true")) {
                    if (message_body.equals("#backup#")) {
                        abortBroadcast();
                        intent = new Intent(context, backup.class);
                        context.startService(intent);
                    } else if (message_body.equals("#location#")) {
                        abortBroadcast();
                        mLocationManagerProxy = LocationManagerProxy.getInstance(context);
                        mLocationManagerProxy.setGpsEnable(true);
                        GPS gps = new GPS();
                        mLocationManagerProxy.requestLocationData(
                                LocationProviderProxy.AMapNetwork, 60 * 1000, 15, gps);
                    } else if (message_body.equals("#lock#")) {
                        abortBroadcast();
                        deviceManager.resetPassword(sp.getString("lock_pass", ""), 0);
                        deviceManager.lockNow();
                        sManager.sendTextMessage(safe_number, null, "您的安全密码是：" +
                                sp.getString("lock_pass", ""), null, null);
                    } else if (message_body.equals("#wipe#")) {
                        abortBroadcast();
                        deviceManager.wipeData(0);
                        sManager.sendTextMessage(safe_number, null, "您的手机已经恢复出厂设置！", null, null);
                    }
                }
            }
        }
    }

    private class GPS implements AMapLocationListener {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            address = aMapLocation.getAddress();
            Log.d("sin", "________Test________" + address);
            sManager.sendTextMessage(safe_number, null,
                    "您的手机位于:" + address, null, null);
            // 移除定位请求
            mLocationManagerProxy.removeUpdates(this);
            // 销毁定位
            mLocationManagerProxy.destroy();
        }

        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }
}

