package com.liangnie.xmap.dbhelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.liangnie.xmap.bean.User;

import java.util.ArrayList;

public class UserDBHelper extends SQLiteOpenHelper {

    private SQLiteDatabase db;

    public UserDBHelper(Context context) {
        super(context, "user", null, 1);
        db = getReadableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists user(" +
                "id integer primary key autoincrement," +
                "username text," +
                "password text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user");
        onCreate(db);
    }

    public void addUser(String username, String password) {
//        List<String> data = new ArrayList<>();
//        data.add(username);
//        data.add(password);
//        db.execSQL("insert into user (username, password) values(?,?)", data.toArray());
        db.execSQL("insert into user (username, password) values(?,?)", new Object[]{username, password});
    }

    public ArrayList<User> getAllData() {
        ArrayList<User> list = new ArrayList<User>();
        Cursor cursor = db.query("user",null, null, null, null, null, "username DESC");
        while(cursor.moveToNext()) {
            String username = cursor.getString(cursor.getColumnIndex("username"));
            String password = cursor.getString(cursor.getColumnIndex("password"));
            list.add(new User(username, password));
        }
        return list;
    }

    public int isExist(String username, String password) {
        int event = 0;

        Cursor cursor1 = db.rawQuery("select * from user", null);

        if(cursor1.getCount() != 0) {    //用户表中存在数据
//            String selection = "username=?";
//            String[] seclectonArgs = new String[]{username};
//            Cursor cursor2 = db.query("user", null, selection, seclectonArgs, null, null, null);
            Cursor cursor2 = db.rawQuery("select * from user where username = ?", new String[]{username});
            Cursor cursor3 = db.rawQuery("select * from user where username = ? and password = ?", new String[]{username, password});
            if(cursor2.getCount() == 0) {
                event = 0;  //当前用户名不存在
            } else if(cursor3.getCount() != 0) {
                event = 1;  //存在当前用户，并且账号密码匹配
            } else {
                event = 2;  //用户名存在，但密码不匹配
            }
        } else {
            event = 3;  //用户表中的数据为空
        }
        return event;
    }

}
