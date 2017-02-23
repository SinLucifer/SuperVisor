package org.sin.supervisor.blackNumber;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.WindowManager;

import com.android.internal.telephony.ITelephony;

import org.sin.supervisor.R;

import java.lang.reflect.Method;


/**
 * Created by Sin on 2015/9/30.
 */
public class BlackNumberServer extends Service {
    private TelephonyManager tManager;
    private BlackNumberDao dao;
    private WindowManager winManager;
    private View view;
    private Intent intent;
    private MyPhoneListener listener;
    private long firstRingTime;
    private long endRingTime;
    private long calltime;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();

        dao = new BlackNumberDao(this);
        listener = new MyPhoneListener();
        winManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tManager.listen(listener, PhoneStateListener.LISTEN_NONE);
        listener = null;
    }

    public void showNotification(String incomingNumber) {
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        intent = new Intent(this, BlackNumberSet.class);
        intent.putExtra("number", incomingNumber);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, 0);


        Notification notify = new Notification.Builder(this)

                .setAutoCancel(true)
                .setSmallIcon(R.drawable.bigeye)
                .setTicker("发现响一声号码")
                .setContentTitle("响一声号码")
                .setContentText(incomingNumber)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(contentIntent).build();

        nm.notify(0, notify);

    }

    public void deleteCallLog(String incomingNumber) {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, null
                , "number=?", new String[]{incomingNumber}, null);
        if (cursor.moveToFirst()) {
            String id = cursor.getString(cursor.getColumnIndex("_id"));
            resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?", new String[]{id});
        }
    }

    public void endCall(){
        try {
            Method method = Class.forName("android.os.ServiceManager").getMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, new Object[]{TELEPHONY_SERVICE});
            ITelephony telephony = ITelephony.Stub.asInterface(binder);
            telephony.endCall();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class MyObserver extends ContentObserver {
        private String incomingnumber;

        public MyObserver(Handler handler, String incomingnumber) {
            super(handler);
            this.incomingnumber = incomingnumber;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            deleteCallLog(incomingnumber);
            getContentResolver().unregisterContentObserver(this);
        }

    }

    private class MyPhoneListener extends PhoneStateListener {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    //Log.d("Sin","heheheheheh IDLE");
                    endRingTime = System.currentTimeMillis();
                    calltime = endRingTime - firstRingTime;
                    if (firstRingTime < endRingTime && calltime < 5000 && calltime > 0) {
                        endRingTime = 0;
                        firstRingTime = 0;
                        showNotification(incomingNumber);
                    }

                    if (view != null) {
                        winManager.removeView(view);
                        view = null;
                    }

                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    //Log.d("Sin","heheheheheh RINGING");
                    firstRingTime = System.currentTimeMillis();
                    if (dao.find(incomingNumber)) {
                        endCall();

                        getContentResolver().registerContentObserver(CallLog.Calls.CONTENT_URI,
                                true, new MyObserver(new Handler(), incomingNumber));
                    }
                    break;
            }
        }
    }
}
