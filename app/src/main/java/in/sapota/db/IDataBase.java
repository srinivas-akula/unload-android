package in.sapota.db;

import android.content.ContentValues;
import android.database.Cursor;

public interface IDataBase {

    public long insertOrThrow(String table, String nullColumnHack, ContentValues values);

    public int delete(String table, String whereClause, String[] whereArgs);

    public Cursor rawQuery(String sql, String[] selectionArgs);

    public int update(String table, ContentValues values, String whereClause, String[] whereArgs);
}
