package com.sringa.unload.db;

import android.content.ContentValues;
import android.database.Cursor;

public class VehicleDetailService extends DBService<VehicleDetail> implements Converter<VehicleDetail> {

    public static final String CREATE_TABLE = "CREATE TABLE vehicledetail (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "number TEXT UNIQUE," +
            "axle TEXT," +
            "location TEXT," +
            "model TEXT," +
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
        if (vehicleDetail.getId() != 0)
            values.put("id", vehicleDetail.getId());
        values.put("number", vehicleDetail.getNumber());
        values.put("axle", vehicleDetail.getAxle());
        values.put("location", vehicleDetail.getLocation());
        values.put("model", vehicleDetail.getModel());
        values.put("load", vehicleDetail.getLoad());
        values.put("tonnage", vehicleDetail.getTonnage());
        return values;
    }

    @Override
    public VehicleDetail convertToObject(Cursor cursor) {

        final VehicleDetail vehicleDetail = new VehicleDetail();
        vehicleDetail.setId(cursor.getLong(cursor.getColumnIndex("id")));
        vehicleDetail.setNumber(cursor.getString(cursor.getColumnIndex("number")));
        vehicleDetail.setAxle(cursor.getString(cursor.getColumnIndex("axle")));
        vehicleDetail.setLocation(cursor.getString(cursor.getColumnIndex("location")));
        vehicleDetail.setModel(cursor.getString(cursor.getColumnIndex("model")));
        vehicleDetail.setLoad(cursor.getInt(cursor.getColumnIndex("load")));
        vehicleDetail.setTonnage(cursor.getInt(cursor.getColumnIndex("tonnage")));
        return vehicleDetail;
    }
}
