package org.sin.supervisor.listener;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.telephony.SmsManager;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sin on 2015/10/5.
 */
public class backup extends IntentService {
    private SharedPreferences sp;
    private Cursor resule = null;
    private Cursor phoneber = null;
    public List<Map<String, Object>> allcallpeople = null;
    private String safe_number;
    private String ip_Adress;

    private SmsManager sManager;
    public backup() {
        super("backup");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        sp = getSharedPreferences("ListenConfig", MODE_PRIVATE);
        safe_number = sp.getString("safe_number", null);
        ip_Adress = sp.getString("ip_Address",null);

        String phonesele = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?";
        sManager = SmsManager.getDefault();

        resule = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI
                , null, null, null, null);
        allcallpeople = new ArrayList<Map<String, Object>>();

        for (resule.moveToFirst(); !resule.isAfterLast(); resule.moveToNext()) {

            Map<String, Object> contact = new HashMap<String, Object>();
            contact.put("phone", resule.getInt(resule.getColumnIndex(ContactsContract.Contacts._ID)));
            String phonenamne = resule.getString(resule.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            if (phonenamne == null || "".equals(phonenamne))
                phonenamne = "NONE";
            contact.put("phonename", phonenamne);


            String[] phoneselection = {String.valueOf(resule.getInt(resule.getColumnIndex(ContactsContract.Contacts._ID)))};
            phoneber = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, phonesele, phoneselection, null);
            StringBuffer buf = new StringBuffer();
            for (phoneber.moveToFirst(); !phoneber.isAfterLast(); phoneber.moveToNext())
                buf.append(phoneber.getString(phoneber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            contact.put("number", buf);
            allcallpeople.add(contact);
            phoneber.close();
        }
        resule.close();

        Map<String, Object> contact = new HashMap<String, Object>();
        contact.put("idan", safe_number);
        allcallpeople.add(contact);



        try {

            //Socket so= new Socket(stmyipall.ip_all,8888);

            Socket so = new Socket(ip_Adress, 30000);
            OutputStream op = so.getOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(op);
            System.out.println("start sends ================>");
            oos.writeObject(allcallpeople);
            oos.flush();
            sManager.sendTextMessage(safe_number, null,
                    "备份成功", null, null);
            oos.close();
            op.close();

        } catch (StreamCorruptedException e) {
            System.out.println("here1");
            e.printStackTrace();
        } catch (UnknownHostException e) {
            System.out.println("here2");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("here3");
            e.printStackTrace();
        }
    }


}
