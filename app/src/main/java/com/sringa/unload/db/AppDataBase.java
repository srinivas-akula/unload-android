package com.sringa.unload.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.PowerManager;
import android.util.Log;

import com.sringa.unload.service.DummyRequestManager;
import com.sringa.unload.service.IRequestManager;
import com.sringa.unload.service.NetworkManager;
import com.sringa.unload.service.RequestManagerImpl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.sringa.unload.db.Constants.DATABASE_NAME;
import static com.sringa.unload.db.Constants.DATABASE_VERSION;

public final class AppDataBase extends SQLiteOpenHelper implements IDataBase, NetworkManager.NetworkHandler {

    private final SQLiteDatabase db;

    public static AppDataBase INSTANCE;

    private Map<String, VehicleDetail> vehicleDetails = new LinkedHashMap<>();

    private PowerManager.WakeLock wakeLock;

    private static final int WAKE_LOCK_TIMEOUT = 120 * 1000;

    private String vehicleNumber;

    private AppUser user;

    private final IRequestManager requestManager;

    private boolean isOnline;
    private NetworkManager networkManager;

    private boolean isServiceRunning = false;

    public void serviceStarted() {
        this.isServiceRunning = true;
    }

    public void serviceStopped() {
        this.isServiceRunning = false;
    }

    public boolean isServiceStarted() {
        return isServiceRunning;
    }

    public static void init(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new AppDataBase(context);
        }
    }

    private AppDataBase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = getWritableDatabase();
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
        networkManager = new NetworkManager(context, this);
        isOnline = networkManager.isOnline();
        this.requestManager = new RequestManagerImpl();
    }

    public IRequestManager getRequestManager() {
        return this.requestManager;
    }

    public List<VehicleDetail> getVehicleDetails() {
        if (vehicleDetails.isEmpty()) {
            final VehicleDetailService service = new VehicleDetailService();
            for (VehicleDetail detail : service.selectAll()) {
                vehicleDetails.put(detail.getNumber(), detail);
            }
        }
        return new ArrayList<>(vehicleDetails.values());
    }

    public int getVehicleCount() {
        return vehicleDetails.size();
    }

    public VehicleDetail getVehicle(String number) {
        return vehicleDetails.get(number);
    }

    public boolean add(final VehicleDetail vehicle) {

        if (null == vehicle) {
            return false;
        }
        boolean success = false;
        try {
            final VehicleDetailService service = new VehicleDetailService();
            final VehicleDetail existing = vehicleDetails.get(vehicle.getNumber());
            if (null == existing) {
                long id = service.insert(vehicle);
                vehicle.setId(id);
                this.vehicleDetails.put(vehicle.getNumber(), vehicle);
                success = true;
            }
        } catch (Exception e) {
            Log.e("Add Vehicle", e.toString());
        }
        if (getAppUser().isDriverMode()) {
            vehicleNumber = vehicle.getNumber();
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
            final VehicleDetail existing = vehicleDetails.get(vehicle.getNumber());
            if (null != existing) {
                service.update(vehicle, existing.getId());
                this.vehicleDetails.put(vehicle.getNumber(), vehicle);
                success = true;
            }
        } catch (Exception e) {
            Log.e("Update Vehicle", e.toString());
        }
        return success;
    }

    public boolean deleteVehicle(VehicleDetail vehicle) {

        if (null == vehicle || null == vehicle.getNumber() || 0 >= vehicle.getId()) {
            return false;
        }
        boolean success = false;
        try {
            final VehicleDetailService service = new VehicleDetailService();
            service.delete(vehicle.getId());
            this.vehicleDetails.remove(vehicle.getNumber());
            success = true;
        } catch (Exception e) {
            Log.e("Delete Vehicle ", e.toString());
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
            userService.update(user, user.getId());
        }
        return user;
    }

    public void deleteAppUser(AppUser user) {
        if (null != user) {
            final AppUserService userService = new AppUserService();
            userService.delete(user.getId());
            this.user = null;
        }
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
        sqLiteDatabase.execSQL(LogService.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(PositionService.DROP_TABLE);
        sqLiteDatabase.execSQL(AppUserService.DROP_TABLE);
        sqLiteDatabase.execSQL(VehicleDetailService.DROP_TABLE);
        sqLiteDatabase.execSQL(LogService.DROP_TABLE);
        onCreate(sqLiteDatabase);
    }

    private void lock() {
        wakeLock.acquire(WAKE_LOCK_TIMEOUT);
    }

    private void unlock() {
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    @Override
    public void onNetworkUpdate(boolean isOnline) {
        if (!isOnline) {
            //can do something.
        }
        this.isOnline = isOnline;
    }

    public boolean isOnline() {
        return isOnline;
    }
}
