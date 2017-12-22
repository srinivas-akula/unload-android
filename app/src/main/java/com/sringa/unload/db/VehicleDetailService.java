package com.sringa.unload.db;

import android.content.ContentValues;
import android.database.Cursor;

public class VehicleDetailService extends DBService<VehicleDetail> implements Converter<VehicleDetail> {

    public static final String CREATE_TABLE = "CREATE TABLE vehicledetail (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "number TEXT NOT NULL," +
            "type TEXT," +
            "vmodel TEXT," +
            "load INTEGER DEFAULT 0," +
            "tonnage INTEGER)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS vehicledetail;";

    public VehicleDetailService() {
        super(VehicleDetail.class);
        setConverter(this);
    }

    @Override
    public ContentValues convertToValues(VehicleDetail vehicleDetail) {

        ContentValues values = new ContentValues();
        values.put("number", vehicleDetail.getNumber());
        values.put("type", vehicleDetail.getType());
        values.put("vmodel", vehicleDetail.getModel());
        values.put("load", vehicleDetail.getLoad());
        values.put("tonnage", vehicleDetail.getTonnage());
        return values;
    }

    @Override
    public VehicleDetail convertToObject(Cursor cursor) {

        final VehicleDetail vehicleDetail = new VehicleDetail();
        vehicleDetail.setId(cursor.getLong(cursor.getColumnIndex("id")));
        vehicleDetail.setNumber(cursor.getString(cursor.getColumnIndex("number")));
        vehicleDetail.setType(cursor.getString(cursor.getColumnIndex("type")));
        vehicleDetail.setModel(cursor.getString(cursor.getColumnIndex("vmodel")));
        vehicleDetail.setLoad(cursor.getInt(cursor.getColumnIndex("load")));
        vehicleDetail.setTonnage(cursor.getInt(cursor.getColumnIndex("tonnage")));
        return vehicleDetail;
    }
}
