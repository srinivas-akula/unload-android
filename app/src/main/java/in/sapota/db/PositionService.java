package in.sapota.db;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.Date;

public class PositionService extends DBService<Position> implements Converter<Position> {

    public static final String CREATE_TABLE = "CREATE TABLE position (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "deviceId TEXT," +
            "time INTEGER," +
            "latitude REAL," +
            "longitude REAL," +
            "altitude REAL," +
            "speed REAL," +
            "course REAL," +
            "accuracy REAL," +
            "battery REAL)";

    public static final String DROP_TABLE = "DROP TABLE IF EXISTS position;";

    public PositionService() {
        super(Position.class);
        setConverter(this);
    }

    @Override
    public ContentValues convertToValues(Position position) {

        ContentValues values = new ContentValues();
        values.put("deviceId", position.getDeviceId());
        values.put("time", position.getTime().getTime());
        values.put("latitude", position.getLatitude());
        values.put("longitude", position.getLongitude());
        values.put("altitude", position.getAltitude());
        values.put("speed", position.getSpeed());
        values.put("course", position.getCourse());
        values.put("accuracy", position.getCourse());
        values.put("battery", position.getBattery());
        return values;
    }

    @Override
    public Position convertToObject(Cursor cursor) {

        Position position = new Position();
        position.setId(cursor.getLong(cursor.getColumnIndex("id")));
        position.setDeviceId(cursor.getString(cursor.getColumnIndex("deviceId")));
        position.setTime(new Date(cursor.getLong(cursor.getColumnIndex("time"))));
        position.setLatitude(cursor.getDouble(cursor.getColumnIndex("latitude")));
        position.setLongitude(cursor.getDouble(cursor.getColumnIndex("longitude")));
        position.setAltitude(cursor.getDouble(cursor.getColumnIndex("altitude")));
        position.setSpeed(cursor.getDouble(cursor.getColumnIndex("speed")));
        position.setCourse(cursor.getDouble(cursor.getColumnIndex("course")));
        position.setAccuracy(cursor.getDouble(cursor.getColumnIndex("accuracy")));
        position.setBattery(cursor.getDouble(cursor.getColumnIndex("battery")));
        return position;
    }
}
