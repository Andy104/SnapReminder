package com.example.andy.snapreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andy.snapreminder.db.TaskContract;
import com.example.andy.snapreminder.db.TaskDbHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private SQLiteDatabase db;
    private TaskDbHelper mHelper;
    private ListView mTaskListView;
    private CustomAdapter mAdapter;

    private AlarmManager alarmManager;
    private PendingIntent pIntent;

    private CalendarView calendar;

    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;

    private String getID;
    private Calendar cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_activity);

        calendar = (CalendarView) findViewById(R.id.calendar_main);
        mTaskListView = (ListView) findViewById(R.id.list_view);
        mHelper = new TaskDbHelper(this);

        Intent alarmIntent = new Intent(this, AlarmReciever.class);
        pIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        cal = Calendar.getInstance();
        selectedDay = cal.get(Calendar.DAY_OF_MONTH);
        selectedMonth = cal.get(Calendar.MONTH) + 1;
        selectedYear = cal.get(Calendar.YEAR);

        try {
            db = mHelper.getWritableDatabase();
            Log.d(TAG, "Database opened successfully in Write-Read mode.");
        } catch (SQLException e) {
            db = mHelper.getReadableDatabase();
            Log.d(TaskContract.DEBUG_TAG, "Error while opening database: " + e + "\n Read-only mode...");
            Toast.makeText(getApplicationContext(), "Read-only mode", Toast.LENGTH_LONG).show();
        }

        mTaskListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getID = null;

                TextView selectedTextView = (TextView) view.findViewById(R.id.numer);
                String currentID = String.valueOf(selectedTextView.getText());

                String where = TaskContract.TaskEntry._ID + " = " + currentID;
                Cursor cursor = db.query(TaskContract.TaskEntry.TABLE, null, where, null, null, null, null);
                if(cursor != null && cursor.moveToFirst()) {
                    getID = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry._ID));
                }

                try {
                    cursor.close();
                } catch (NullPointerException e) {
                    Log.d(TaskContract.DEBUG_TAG, "Cannot close cursor.");
                }

                Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
                detailIntent.putExtra("id", getID);
                detailIntent.putExtra("day", selectedDay);
                detailIntent.putExtra("month", selectedMonth);
                detailIntent.putExtra("year", selectedYear);
                startActivity(detailIntent);

                Log.d(TAG, "Clicked item " + position);
            }
        });

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                selectedYear = year;
                selectedMonth = month + 1;
                selectedDay = dayOfMonth;

                updateUI(selectedDay, selectedMonth, selectedYear);
            }
        });

        updateUI(selectedDay, selectedMonth, selectedYear);
    }

    @Override
    protected void onDestroy() {
        if (mAdapter != null) {
            db.close();
            mHelper.close();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void updateUI(int day, int month, int year) {
        ArrayList<SetOfData> taskList = new ArrayList<>();

        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int id_id = cursor.getColumnIndex(TaskContract.TaskEntry._ID);
            int id_title = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            int id_subtitle = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_SUBTITLE);
            int id_hour = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_HOUR);
            int id_minute = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_MINUTE);
            int id_day = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DAY);
            int id_month = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_MONTH);
            int id_year = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_YEAR);
            int id_path = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_PATH);

            NumberFormat f = new DecimalFormat("00");
            String time = f.format(cursor.getInt(id_hour)) + ":" + f.format(cursor.getInt(id_minute));
            String date = cursor.getInt(id_day) + "/" + cursor.getInt(id_month) + "/" + cursor.getInt(id_year);

            SetOfData datas = new SetOfData(cursor.getString(id_id), cursor.getString(id_title), cursor.getString(id_subtitle),
                    time, date, cursor.getString(id_path));

            //Log.d(TAG, String.valueOf("Cursor " + cursor.getInt(id_hour) + ":" + cursor.getInt(id_minute) + " - " + cursor.getInt(id_day) + "/" + cursor.getInt(id_month) + "/" + cursor.getInt(id_year)));

            if (cursor.getInt(id_day) == day && cursor.getInt(id_month) == month && cursor.getInt(id_year) == year ) {
                taskList.add(datas);
            }
        }
        //Log.d(TAG, String.valueOf("Calendar " + day + "/" + month + "/" + year));

        if (mAdapter == null) {
            mAdapter = new CustomAdapter(this, taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
    }

    public void addTask(View view) {
        ContentValues values = new ContentValues();

        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, "Tytuł");
        values.put(TaskContract.TaskEntry.COL_TASK_SUBTITLE, "Opis");
        values.put(TaskContract.TaskEntry.COL_TASK_HOUR, cal.get(Calendar.HOUR_OF_DAY));
        values.put(TaskContract.TaskEntry.COL_TASK_MINUTE, (cal.get(Calendar.MINUTE) + 1));
        values.put(TaskContract.TaskEntry.COL_TASK_DAY, selectedDay);
        values.put(TaskContract.TaskEntry.COL_TASK_MONTH, selectedMonth);
        values.put(TaskContract.TaskEntry.COL_TASK_YEAR, selectedYear);
        values.put(TaskContract.TaskEntry.COL_TASK_PATH, "");
        values.put(TaskContract.TaskEntry.COL_TASK_CHECK, 1);
        values.put(TaskContract.TaskEntry.COL_TASK_INT_DAY, 0);
        values.put(TaskContract.TaskEntry.COL_TASK_INT_HOUR, 0);
        values.put(TaskContract.TaskEntry.COL_TASK_INT_MINUTE, 0);

        long id = db.insert(TaskContract.TaskEntry.TABLE, null, values);
        Log.d(TAG, "newEvent " + id);

        Intent newIntent = new Intent(this, EditActivity.class);
        newIntent.putExtra("id", String.valueOf(id));
        newIntent.putExtra("nowe", 1);
        startActivity(newIntent);
    }
}
