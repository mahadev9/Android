package com.example.mahadev.habittracker;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.mahadev.habittracker.data.HabitContract;
import com.example.mahadev.habittracker.data.HabitDbHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CatalogActivity extends AppCompatActivity {

    private static final String LOG_TAG = CatalogActivity.class.getSimpleName();

    private HabitDbHelper mHabitDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        mHabitDbHelper = new HabitDbHelper(this);

        insertHabit("Reading Novel", 60, "Finished Harry Potter and The Curse Child");
        insertHabit("Cycling", 30, "");
        insertHabit("Gaming on Laptop", 90, "Half through Far Cry 5");

        displayDatabaseInfo();
    }

    private void insertHabit(String habit, int duration, String comment) {

        Date date = new Date();
        SimpleDateFormat formated = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = formated.format(date);

        SQLiteDatabase db = mHabitDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(HabitContract.HabitEntry.COLUMN_HABIT, habit);
        values.put(HabitContract.HabitEntry.COLUMN_DATE, dateString);
        values.put(HabitContract.HabitEntry.COLUMN_DURATION, duration);
        values.put(HabitContract.HabitEntry.COLUMN_COMMENT, comment);

        long newRowId = db.insert(HabitContract.HabitEntry.TABLE_NAME, null, values);
    }

    private Cursor readHabit() {

        SQLiteDatabase db = mHabitDbHelper.getReadableDatabase();

        String[] projection = {
                HabitContract.HabitEntry._ID,
                HabitContract.HabitEntry.COLUMN_HABIT,
                HabitContract.HabitEntry.COLUMN_DATE,
                HabitContract.HabitEntry.COLUMN_DURATION,
                HabitContract.HabitEntry.COLUMN_COMMENT
        };

        Cursor cursor = db.query(
                HabitContract.HabitEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        return cursor;
    }

    private void displayDatabaseInfo() {

        Cursor cursor = readHabit();

        try {

            TextView display = (TextView) findViewById(R.id.habit_text_view);
            display.setText("There are " + cursor.getCount() + " rows in the Habit table.\n\n");
            display.append(HabitContract.HabitEntry._ID + " - " +
                    HabitContract.HabitEntry.COLUMN_HABIT + " - " +
                    HabitContract.HabitEntry.COLUMN_DATE + " - " +
                    HabitContract.HabitEntry.COLUMN_DURATION + " (in min) - " +
                    HabitContract.HabitEntry.COLUMN_COMMENT + "\n");

            int idColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry._ID);
            int habitColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_HABIT);
            int dateColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_DATE);
            int durationColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_DURATION);
            int commentColumnIndex = cursor.getColumnIndex(HabitContract.HabitEntry.COLUMN_COMMENT);

            while (cursor.moveToNext()) {

                int currentID = cursor.getInt(idColumnIndex);
                String currentHabit = cursor.getString(habitColumnIndex);
                String currentDate = cursor.getString(dateColumnIndex);
                int currentDuration = cursor.getInt(durationColumnIndex);
                String currentComment = cursor.getString(commentColumnIndex);

                display.append("\n" + currentID + " - " +
                        currentHabit + " - " +
                        currentDate + " - " +
                        currentDuration + " - " +
                        currentComment);

            }

        } finally {
            cursor.close();
        }
    }
}
