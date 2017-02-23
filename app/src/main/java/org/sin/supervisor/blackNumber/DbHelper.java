package org.sin.supervisor.blackNumber;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Sin on 2015/9/24.
 */
public class DbHelper extends SQLiteOpenHelper{
    public DbHelper(Context context)
    {
        super(context,"blackNumber.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE blacknumber (_id integer "
                +"primary key autoincrement, "
                +"number varchar(20))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
}
