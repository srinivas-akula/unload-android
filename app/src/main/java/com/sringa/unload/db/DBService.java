package com.sringa.unload.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DBService<T> {

    protected final IDataBase db;
    private Converter<T> converter;
    private final String tableName;

    public interface DatabaseHandler<T> {
        void onComplete(boolean success, T result);
    }

    private static abstract class DatabaseAsyncTask<X> extends AsyncTask<Void, Void, X> {

        private DatabaseHandler<X> handler;
        private RuntimeException error;

        public DatabaseAsyncTask(DatabaseHandler<X> handler) {
            this.handler = handler;
        }

        @Override
        protected X doInBackground(Void... params) {
            try {
                return executeMethod();
            } catch (RuntimeException error) {
                this.error = error;
                return null;
            }
        }

        protected abstract X executeMethod();

        @Override
        protected void onPostExecute(X result) {
            handler.onComplete(error == null, result);
        }
    }

    public DBService(Class type) {
        this.tableName = type.getSimpleName().toLowerCase();
        this.db = AppDataBase.INSTANCE;
    }

    public DBService(Class type, IDataBase db) {
        this.tableName = type.getSimpleName().toLowerCase();
        this.db = db;
    }

    protected void setConverter(Converter<T> converter) {
        this.converter = converter;
    }

    public long insert(T t) {
        final ContentValues values = converter.convertToValues(t);
        return db.insertOrThrow(tableName, null, values);
    }

    public void insertAsync(final T t, final DatabaseHandler<Void> handler) {

        new DatabaseAsyncTask<Void>(handler) {
            @Override
            protected Void executeMethod() {
                insert(t);
                return null;
            }
        }.execute();
    }

    public void delete(long id) {
        if (db.delete(tableName, "id = ?", new String[]{String.valueOf(id)}) != 1) {
            throw new SQLException();
        }
    }

    public void delete(String id) {
        if (db.delete(tableName, "id = ?", new String[]{id}) != 1) {
            throw new SQLException();
        }
    }

    public void deleteAsync(final long id, DatabaseHandler<Void> handler) {

        new DatabaseAsyncTask<Void>(handler) {
            @Override
            protected Void executeMethod() {
                delete(id);
                return null;
            }
        }.execute();
    }

    public T select() {

        T t = null;
        Cursor cursor = db.rawQuery(selectQuery(null, "1"), null);
        try {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                t = converter.convertToObject(cursor);
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
        return t;
    }

    public List<T> selectAll(String order, String limit) {

        final Cursor cursor = db.rawQuery(selectQuery(order, limit), null);
        try {
            if (cursor.getCount() != 0) {
                List<T> list = new ArrayList<>();
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    list.add(converter.convertToObject(cursor));
                    cursor.moveToNext();
                }
                return list;
            }
        } finally {
            cursor.close();
        }
        return Collections.emptyList();
    }

    public List<T> selectAll() {
        return selectAll(null, null);
    }

    public void selectAsync(DatabaseHandler<T> handler) {
        new DatabaseAsyncTask<T>(handler) {
            @Override
            protected T executeMethod() {
                return select();
            }
        }.execute();
    }

    public int update(T t, long id) {
        final ContentValues values = converter.convertToValues(t);
        return db.update(tableName, values, "id = ?", new String[]{String.valueOf(id)});
    }

    private String selectQuery(String order, String limit) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT * FROM ")
                .append(tableName)
                .append(" ORDER BY id ");
        if (null != order) {
            builder.append(order);
        }
        if (null != limit) {
            builder.append(" LIMIT ").append(limit);
        }
        return builder.toString();
    }
}
