package in.sapota.db;

import android.database.Cursor;

public interface Converter<T> {

    android.content.ContentValues convertToValues(T t);

    T convertToObject(Cursor cursor);
}
