package com.sringa.unload.db;

import android.content.ContentValues;
import android.database.Cursor;

public class LogService extends DBService<LogDetail> implements Converter<LogDetail> {

    public static final String CREATE_TABLE = "CREATE TABLE logdetail (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "log TEXT" +
            ")";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS logdetail;";

    public LogService() {
        super(LogDetail.class);
        setConverter(this);
    }

    @Override
    public ContentValues convertToValues(LogDetail logDetail) {

        ContentValues values = new ContentValues();
        values.put("log", logDetail.getLog());
        return values;
    }

    @Override
    public LogDetail convertToObject(Cursor cursor) {

        LogDetail logDetail = new LogDetail(cursor.getString(cursor.getColumnIndex("log")));
        return logDetail;
    }
}
