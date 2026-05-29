package com.ti.nfcdemo.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class ReadingStore extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "bladder_readings.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_SESSIONS = "sessions";
    private static final String TABLE_READINGS = "readings";

    public ReadingStore(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_SESSIONS + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "created_at INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE " + TABLE_READINGS + " (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "session_id INTEGER NOT NULL, " +
                "timestamp_millis INTEGER NOT NULL, " +
                "sample_index INTEGER NOT NULL, " +
                "raw_hex TEXT NOT NULL, " +
                "reference_adc INTEGER NOT NULL, " +
                "thermistor_adc INTEGER NOT NULL, " +
                "resistance_kohm REAL NOT NULL, " +
                "volume_percent REAL NOT NULL, " +
                "display_mode TEXT NOT NULL, " +
                "FOREIGN KEY(session_id) REFERENCES " + TABLE_SESSIONS + "(_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_READINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSIONS);
        onCreate(db);
    }

    public long createSession() {
        ContentValues values = new ContentValues();
        values.put("created_at", System.currentTimeMillis());
        return getWritableDatabase().insert(TABLE_SESSIONS, null, values);
    }

    public long insertReading(Reading reading) {
        ContentValues values = new ContentValues();
        values.put("session_id", reading.sessionId);
        values.put("timestamp_millis", reading.timestampMillis);
        values.put("sample_index", reading.sampleIndex);
        values.put("raw_hex", reading.rawHex);
        values.put("reference_adc", reading.referenceAdc);
        values.put("thermistor_adc", reading.thermistorAdc);
        values.put("resistance_kohm", reading.resistanceKohm);
        values.put("volume_percent", reading.volumePercent);
        values.put("display_mode", reading.displayMode);
        return getWritableDatabase().insert(TABLE_READINGS, null, values);
    }

    public int getSessionSampleCount(long sessionId) {
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_READINGS + " WHERE session_id = ?",
                new String[]{String.valueOf(sessionId)});
        try {
            if (cursor.moveToFirst()) {
                return cursor.getInt(0);
            }
            return 0;
        } finally {
            cursor.close();
        }
    }

    public List<Reading> getReadingsForSession(long sessionId) {
        ArrayList<Reading> readings = new ArrayList<Reading>();
        Cursor cursor = getReadableDatabase().query(
                TABLE_READINGS,
                new String[]{"_id", "session_id", "timestamp_millis", "sample_index", "raw_hex",
                        "reference_adc", "thermistor_adc", "resistance_kohm", "volume_percent", "display_mode"},
                "session_id = ?",
                new String[]{String.valueOf(sessionId)},
                null,
                null,
                "sample_index ASC");
        try {
            while (cursor.moveToNext()) {
                Reading reading = new Reading();
                reading.id = cursor.getLong(0);
                reading.sessionId = cursor.getLong(1);
                reading.timestampMillis = cursor.getLong(2);
                reading.sampleIndex = cursor.getInt(3);
                reading.rawHex = cursor.getString(4);
                reading.referenceAdc = cursor.getLong(5);
                reading.thermistorAdc = cursor.getLong(6);
                reading.resistanceKohm = cursor.getDouble(7);
                reading.volumePercent = cursor.getDouble(8);
                reading.displayMode = cursor.getString(9);
                readings.add(reading);
            }
        } finally {
            cursor.close();
        }
        return readings;
    }

    public void clearSession(long sessionId) {
        getWritableDatabase().delete(TABLE_READINGS, "session_id = ?", new String[]{String.valueOf(sessionId)});
    }

    public static class Reading {
        public long id;
        public long sessionId;
        public long timestampMillis;
        public int sampleIndex;
        public String rawHex;
        public long referenceAdc;
        public long thermistorAdc;
        public double resistanceKohm;
        public double volumePercent;
        public String displayMode;
    }
}
