package org.sin.supervisor.blackNumber;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sin on 2015/9/24.
 */
public class BlackNumberDao {
    private DbHelper dbhelper;
    private Context context;

    public BlackNumberDao(Context context) {
        this.context = context;
        dbhelper = new DbHelper(context);
    }

    public List<String> getAllNumbers() {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        List<String> numbers = new ArrayList<String>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select number from blackNumber", null);
            while (cursor.moveToNext()) {
                String number = cursor.getString(0);
                numbers.add(number);
            }
            cursor.close();
            db.close();
        }
        return numbers;
    }

    public boolean find(String number) {
        boolean result = false;
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select number from blackNumber where number=?",
                    new String[]{number});
            if (cursor.moveToNext()) {
                result = true;
            }
            cursor.close();
            db.close();
        }
        return result;
    }

    public void add(String number) {
        if (find(number)) {
            return;
        }
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("insert into blackNumber (number) values (?)", new Object[]{number});
            db.close();
        }
    }

    public void delete(String number) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        if (db.isOpen()) {
            db.execSQL("delete from blackNumber where number=?", new Object[]{number});
            db.close();
        }
    }

    public void update(String newNumber, String oldNumber) {
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        if (db.isOpen())
        {
            db.execSQL("update blackNumber set number=? where number=?",
                    new Object[]{newNumber,oldNumber});
            db.close();
            //Log.d("Sin~", "OK!" + newNumber + " "+oldNumber);
        }
    }
}
