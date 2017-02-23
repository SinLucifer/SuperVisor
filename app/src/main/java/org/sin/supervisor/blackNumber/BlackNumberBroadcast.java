package org.sin.supervisor.blackNumber;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


/**
 * Created by Sin on 2015/9/30.
 */
public class BlackNumberBroadcast extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        String ACTION = "android.intent.action.BOOT_COMPLETED";


        if (intent.getAction().equals(ACTION)) {

            System.out.println("hehehehehhe");
            context.startService(new Intent(context,
                    BlackNumberServer.class));

        } else {
            System.out.println("hehehehehhe");
            context.startService(new Intent(context,
                    BlackNumberServer.class));

        }

    }

}
