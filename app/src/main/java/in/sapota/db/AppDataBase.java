package in.sapota.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static in.sapota.db.Constants.DATABASE_NAME;
import static in.sapota.db.Constants.DATABASE_VERSION;

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
                vehicleDetails.put(detail.getNumber(), detail);
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

    public boolean addOrUpadate(VehicleDetail vehicle) {
        if (null == vehicle) {
            return false;
        }
        boolean success = true;
        try {
            final VehicleDetailService service = new VehicleDetailService();
            if (null == vehicleDetails.get(vehicle.getNumber())) {
                vehicle.setId(service.insert(vehicle));
            } else {
                if (null == vehicle.getId()) {
                    throw new Exception("Id can not be null for existing vehicles.");
                }
                service.update(vehicle);
            }
            this.vehicleDetails.put(vehicle.getNumber(), vehicle);
        } catch (Exception e) {
            success = false;
        }
        //TODO: on successful write, send it to server.
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
            this.vehicleDetails.remove(vehicle.getNumber());
        } catch (Exception e) {
            success = false;
        }
        //TODO: on successful write, send it to server.
        return success;
    }

    public AppUser getAppUser() {
        final AppUserService userService = new AppUserService();
        this.user = userService.select();
        return user;
    }

    public void addAppUser(AppUser user) {
        final AppUserService userService = new AppUserService();
        long id = userService.insert(user);
        user.setId(id);
        this.user = user;
    }

    public void updateAppUser(String mode) {
        if (null == user) {
            return;
        }
        final AppUserService userService = new AppUserService();
        this.user.setMode(mode);
        userService.update(user);
    }

    public String getUserMode() {
        if (null == user) {
            return null;
        }
        return this.user.getMode();
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
        return "";
    }
}
