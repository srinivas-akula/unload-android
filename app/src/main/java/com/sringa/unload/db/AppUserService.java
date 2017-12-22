package com.sringa.unload.db;

import android.content.ContentValues;
import android.database.Cursor;

public class AppUserService extends DBService<AppUser> implements Converter<AppUser> {

    public static final String CREATE_TABLE = "CREATE TABLE appuser (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "phone TEXT," +
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
        values.put("provider", appUser.getProviderId());
        values.put("uid", appUser.getuId());
        values.put("name", appUser.getDisplayName());
        values.put("mode", appUser.getMode());
        return values;
    }

    @Override
    public AppUser convertToObject(Cursor cursor) {
        AppUser appUser = new AppUser();
        appUser.setId(cursor.getLong(cursor.getColumnIndex("id")));
        appUser.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
        appUser.setProviderId(cursor.getString(cursor.getColumnIndex("provider")));
        appUser.setuId(cursor.getString(cursor.getColumnIndex("uid")));
        appUser.setDisplayName(cursor.getString(cursor.getColumnIndex("name")));
        appUser.setMode(cursor.getString(cursor.getColumnIndex("mode")));
        return appUser;
    }
}
