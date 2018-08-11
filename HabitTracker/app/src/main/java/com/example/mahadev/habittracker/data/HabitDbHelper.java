package com.example.mahadev.habittracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HabitDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = HabitDbHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "habit.db";

    public static final int DATABASE_VERSION = 1;

    public HabitDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String SQL_CREATE_HABIT_TABLE = "CREATE TABLE " + HabitContract.HabitEntry.TABLE_NAME + "(" +
                HabitContract.HabitEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                HabitContract.HabitEntry.COLUMN_HABIT + " TEXT NOT NULL, " +
                HabitContract.HabitEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                HabitContract.HabitEntry.COLUMN_DURATION + " INTEGER DEFAULT 0, " +
                HabitContract.HabitEntry.COLUMN_COMMENT + " TEXT);";

        Log.i(LOG_TAG, "HabitDbHelper onCreate Method");

        sqLiteDatabase.execSQL(SQL_CREATE_HABIT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

    }
}
