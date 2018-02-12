package com.sringa.unload.db;

import android.content.ContentValues;
import android.database.Cursor;

public class AppUserService extends DBService<AppUser> implements Converter<AppUser> {

    public static final String CREATE_TABLE = "CREATE TABLE appuser (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "phone TEXT," +
            "password TEXT," +
            "provider TEXT," +
            "uid TEXT," +
            "mode TEXT," +
            "name TEXT)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS appuser;";

    public AppUserService() {
        super(AppUser.class);
        setConverter(this);
    }

    @Override
    public ContentValues convertToValues(AppUser appUser) {

        ContentValues values = new ContentValues();
        values.put("phone", appUser.getPhone());
        values.put("password", appUser.getPassword());
        values.put("provider", appUser.getProviderid());
        values.put("uid", appUser.getUid());
        values.put("name", appUser.getDisplayname());
        values.put("mode", appUser.getMode());
        return values;
    }

    @Override
    public AppUser convertToObject(Cursor cursor) {
        AppUser appUser = new AppUser();
        appUser.setId(cursor.getLong(cursor.getColumnIndex("id")));
        appUser.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
        appUser.setPassword(cursor.getString(cursor.getColumnIndex("password")));
        appUser.setProviderid(cursor.getString(cursor.getColumnIndex("provider")));
        appUser.setUid(cursor.getString(cursor.getColumnIndex("uid")));
        appUser.setDisplayname(cursor.getString(cursor.getColumnIndex("name")));
        appUser.setMode(cursor.getString(cursor.getColumnIndex("mode")));
        return appUser;
    }
}
