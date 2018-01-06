package com.sringa.unload.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.sringa.unload.service.ProtocolFormatter;
import com.sringa.unload.service.RequestManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.sringa.unload.db.Constants.DATABASE_NAME;
import static com.sringa.unload.db.Constants.DATABASE_VERSION;
import static com.sringa.unload.db.Constants.VEHICLE_ID_RESOURCE;
import static com.sringa.unload.db.Constants.VEHICLE_RESOURCE;

public final class AppDataBase extends SQLiteOpenHelper implements IDataBase {

    private final SQLiteDatabase db;

    public static AppDataBase INSTANCE;

    private Map<String, VehicleDetail> vehicleDetails;

    private AppUser user;

    public static void init(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new AppDataBase(context);
        }
    }

    private AppDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
    }

    public List<VehicleDetail> getVehicleDetails() {

        if (null == vehicleDetails) {
            vehicleDetails = new LinkedHashMap<>();
            final VehicleDetailService service = new VehicleDetailService();
            for (VehicleDetail detail : service.selectAll()) {
                vehicleDetails.put(detail.getId(), detail);
            }
        }
        return new ArrayList<>(vehicleDetails.values());
    }

    public int getVehicleCount() {
        if (null == vehicleDetails || vehicleDetails.isEmpty()) {
            return 0;
        } else {
            return vehicleDetails.size();
        }
    }

    public VehicleDetail getVehicle(String number) {
        return vehicleDetails.get(number);
    }

    public boolean add(VehicleDetail vehicle) {

        if (null == vehicle) {
            return false;
        }
        boolean success = false;
        try {
            final VehicleDetailService service = new VehicleDetailService();
            final VehicleDetail existing = vehicleDetails.get(vehicle.getId());
            if (null != existing) {
                throw new Exception("Vechicle " + existing.getId() + " already exists");
            }
            service.insert(vehicle);
            final JSONObject json = ProtocolFormatter.toJson(vehicle);
            final String response = RequestManager.sendPostRequest(VEHICLE_RESOURCE, json);
            if (null != response) {
                this.vehicleDetails.put(vehicle.getId(), vehicle);
            }
            success = true;
        } catch (Exception e) {
            Log.e("Add Vehicle", e.getLocalizedMessage());
        }
        return success;
    }

    public boolean update(VehicleDetail vehicle) {

        if (null == vehicle) {
            return false;
        }
        boolean success = false;
        try {
            final VehicleDetailService service = new VehicleDetailService();
            final VehicleDetail existing = vehicleDetails.get(vehicle.getId());
            if (null == existing) {
                throw new Exception("Vechicle " + existing.getId() + " doesn't exists to udpate.");
            }
            service.update(vehicle);
            final JSONObject json = ProtocolFormatter.toJson(vehicle);
            String urlStr = String.format(VEHICLE_ID_RESOURCE, vehicle.getId());
            final String response = RequestManager.sendPutRequest(urlStr, json);
            if (null != response) {
                this.vehicleDetails.put(vehicle.getId(), vehicle);
            }
            success = true;
        } catch (Exception e) {
            Log.e("Add/Update Vehicle", e.getLocalizedMessage());
        }
        return success;
    }

    public boolean deleteVehicle(VehicleDetail vehicle) {

        if (null == vehicle || null == vehicle.getId()) {
            return false;
        }
        boolean success = true;
        try {
            final VehicleDetailService service = new VehicleDetailService();
            service.delete(vehicle.getId());
            final String urlStr = String.format(VEHICLE_ID_RESOURCE, vehicle.getId());
            final String response = RequestManager.sendDeleteRequest(urlStr);
            if (null != response) {
                this.vehicleDetails.remove(vehicle.getId());
            }
        } catch (Exception e) {
            Log.e("Delete Vehicle", e.getLocalizedMessage());
            success = false;
        }
        return success;
    }

    public AppUser getAppUser() {
        if (null == user) {
            final AppUserService userService = new AppUserService();
            this.user = userService.select();
        }
        return user;
    }

    public AppUser addAppUser(AppUser user) {
        final AppUserService userService = new AppUserService();
        long id = userService.insert(user);
        user.setId(id);
        this.user = user;
        return user;
    }

    public AppUser updateAppUser(String password, String mode) {
        if (null != user) {
            final AppUserService userService = new AppUserService();
            this.user.setMode(mode);
            this.user.setPassword(password);
            userService.update(user);
        }
        return user;
    }

    public String getUserMode() {
        return null == user ? null : user.getMode();
    }

    @Override
    public long insertOrThrow(String table, String nullColumnHack, ContentValues values) {
        return db.insertOrThrow(table, nullColumnHack, values);
    }

    @Override
    public int delete(String table, String whereClause, String[] whereArgs) {
        return db.delete(table, whereClause, whereArgs);
    }

    @Override
    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return db.rawQuery(sql, selectionArgs);
    }

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        return db.update(table, values, whereClause, whereArgs);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(PositionService.CREATE_TABLE);
        sqLiteDatabase.execSQL(AppUserService.CREATE_TABLE);
        sqLiteDatabase.execSQL(VehicleDetailService.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(PositionService.DROP_TABLE);
        sqLiteDatabase.execSQL(AppUserService.DROP_TABLE);
        sqLiteDatabase.execSQL(VehicleDetailService.DROP_TABLE);
        onCreate(sqLiteDatabase);
    }

    public String getVehicleNumber() {
        return null;
    }
}
