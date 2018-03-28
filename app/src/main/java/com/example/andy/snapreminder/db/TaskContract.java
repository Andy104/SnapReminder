package com.example.andy.snapreminder.db;

import android.provider.BaseColumns;

public class TaskContract {
    public static final String DEBUG_TAG = "SqLiteManager";
    public static final String DB_NAME = "com.example.andy.kalendarzo_przypominacz.db";
    public static final int DB_VERSION = 1;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE = "kalendarz";

        public static final String COL_TASK_TITLE = "tytul";

        public static final String COL_TASK_SUBTITLE = "opis";

        public static final String COL_TASK_HOUR = "godzina";
        public static final String COL_TASK_MINUTE = "minuta";

        public static final String COL_TASK_DAY = "dzien";
        public static final String COL_TASK_MONTH = "miesiac";
        public static final String COL_TASK_YEAR = "rok";

        public static final String COL_TASK_PATH = "sciezka";
        public static final String COL_TASK_CHECK = "powiadom";

        public static final String COL_TASK_INT_HOUR = "intHour";
        public static final String COL_TASK_INT_MINUTE = "intMinute";
        public static final String COL_TASK_INT_DAY = "intDay";
    }
}
