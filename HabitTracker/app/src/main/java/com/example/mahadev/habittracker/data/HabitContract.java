package com.example.mahadev.habittracker.data;

import android.provider.BaseColumns;

public final class HabitContract {

    private HabitContract() {
    }

    public static abstract class HabitEntry implements BaseColumns {

        public static final String TABLE_NAME = "habit";
        public static final String _ID = "id";
        public static final String COLUMN_HABIT = "habit";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_COMMENT = "comment";

    }
}
