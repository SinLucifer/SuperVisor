package org.sin.supervisor;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

/**
 * Created by Sin on 2015/10/12.
 */
public class SplashActivity extends Activity {
    private LocationManagerProxy loadGps;
    private final static int SWITCH_MAINACTIVITY = 1000;
    private final static int SWITCH_GUIDACTIVITY = 1001;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_view);

        loadGps = LocationManagerProxy.getInstance(this);
        loadGps = LocationManagerProxy.getInstance(this);
        loadGps.setGpsEnable(true);
        GPS gps = new GPS();
        loadGps.requestLocationData(
                LocationProviderProxy.AMapNetwork, 60 * 1000, 15, gps);

        boolean mFirst = isFirstEnter();

        if (mFirst)
            mHandler.sendEmptyMessageDelayed(SWITCH_MAINACTIVITY, 2000);
        else
            mHandler.sendEmptyMessageDelayed(SWITCH_GUIDACTIVITY, 2000);
    }


    private boolean isFirstEnter() {
        String mResultStr = getSharedPreferences("ListenConfig", MODE_PRIVATE)
                .getString("guide_activity", "");

        if (mResultStr.equals("true"))
            return true;
        else
            return false;
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SWITCH_MAINACTIVITY:
                    Intent mIntent = new Intent();
                    mIntent.setClass(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(mIntent);
                    SplashActivity.this.finish();
                    break;
                case SWITCH_GUIDACTIVITY:
                    mIntent = new Intent();
                    mIntent.setClass(SplashActivity.this, GuideActivity.class);
                    SplashActivity.this.startActivity(mIntent);
                    SplashActivity.this.finish();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private class GPS implements AMapLocationListener {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            String address = aMapLocation.getAddress();
            Log.d("sin", "________Test________" + address);
            // 移除定位请求
            loadGps.removeUpdates(this);
            // 销毁定位
            loadGps.destroy();
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
