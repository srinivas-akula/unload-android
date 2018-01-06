package com.sringa.unload.db;

import android.content.ContentValues;
import android.database.Cursor;

public class VehicleDetailService extends DBService<VehicleDetail> implements Converter<VehicleDetail> {

    public static final String CREATE_TABLE = "CREATE TABLE vehicledetail (" +
            "id TEXT PRIMARY KEY," +
            "axle TEXT," +
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
        values.put("id", vehicleDetail.getId());
        values.put("axle", vehicleDetail.getAxle());
        values.put("model", vehicleDetail.getModel());
        values.put("load", vehicleDetail.getLoad());
        values.put("tonnage", vehicleDetail.getTonnage());
        return values;
    }

    @Override
    public VehicleDetail convertToObject(Cursor cursor) {

        final VehicleDetail vehicleDetail = new VehicleDetail();
        vehicleDetail.setId(cursor.getString(cursor.getColumnIndex("id")));
        vehicleDetail.setAxle(cursor.getString(cursor.getColumnIndex("axle")));
        vehicleDetail.setModel(cursor.getString(cursor.getColumnIndex("model")));
        vehicleDetail.setLoad(cursor.getInt(cursor.getColumnIndex("load")));
        vehicleDetail.setTonnage(cursor.getInt(cursor.getColumnIndex("tonnage")));
        return vehicleDetail;
    }
}
