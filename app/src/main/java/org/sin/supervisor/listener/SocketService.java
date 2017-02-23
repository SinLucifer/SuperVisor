package org.sin.supervisor.listener;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by Sin on 2015/10/4.
 */
public class SocketService extends Service {
    private Handler handler;
    private ClientThread clientThread;
    private Intent intent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        this.intent = intent;
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        /*handler = new Handler() // ②
        {
            @Override
            public void handleMessage(Message msg)
            {
                // 如果消息来自于子线程
                if (msg.what == 0x123)
                {
                    // 将读取的内容追加显示在文本框中
                    Toast.makeText(getApplicationContext(), msg.obj.toString(), Toast.LENGTH_LONG).show();
                    if(msg.obj.toString().equals("backup")){
                        intent = new Intent(getApplicationContext(),backup.class);
                        startService(intent);
                    }


                    try
                    {
                        Message msg2 = new Message();
                        msg2.what = 0x345;
                        msg2.obj = "complete";
                        clientThread.revHandler.sendMessage(msg2);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }


                }
            }
        };
        clientThread = new ClientThread(handler);
        // 客户端启动ClientThread线程创建网络连接、读取来自服务器的数据
        new Thread(clientThread).start();*/
    }
}
