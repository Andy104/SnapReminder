package com.example.andy.snapreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andy.snapreminder.db.TaskContract;
import com.example.andy.snapreminder.db.TaskDbHelper;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    private SQLiteDatabase db;
    private TaskDbHelper mHelper;

    private AlarmManager alarmManager;
    private PendingIntent pIntent;

    private TextView idZdarzenia;
    private TextView tytulZdarzenia;
    private TextView dzienZdarzenia;
    private TextView godzinaZdarzenia;
    private TextView minutaZdarzenia;
    private TextView opisZdarzenia;
    private TextView czasZdarzenia;
    private TextView dataZdarzenia;
    private ImageView obrazZdarzenia;
    private CheckBox checkBox;
    private TextView tekstObrazu;

    private String taskID;
    private String where;
    private int nowe;

    private String getID;
    private String getTitle;
    private String getSubtitle;
    private int getHour;
    private int getMinute;
    private int getDay;
    private int getMonth;
    private int getYear;
    private String getTime;
    private String getDate;
    private String getPath;
    private int getChecked;
    private int getIntervalMinute;
    private int getIntervalHour;
    private int getIntervalDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        mHelper = new TaskDbHelper(this);
        db = mHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        taskID = extras.getString("id");
        nowe = extras.getInt("nowe");

        final Intent alarmIntent = new Intent(getApplicationContext(), AlarmReciever.class);
        alarmIntent.putExtra("id", taskID);
        pIntent = PendingIntent.getBroadcast(this, Integer.parseInt(taskID), alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (nowe == 1) {
            alarmStart();
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainIntent);
        }

        idZdarzenia = (TextView) findViewById(R.id.detailId);
        tytulZdarzenia = (TextView) findViewById(R.id.detailTytul);
        opisZdarzenia = (TextView) findViewById(R.id.detailOpis);
        dzienZdarzenia = (TextView) findViewById(R.id.detailDay);
        godzinaZdarzenia = (TextView) findViewById(R.id.detailHour);
        minutaZdarzenia = (TextView) findViewById(R.id.detailMinute);
        czasZdarzenia = (TextView) findViewById(R.id.detailCzas);
        dataZdarzenia = (TextView) findViewById(R.id.detailData);
        obrazZdarzenia = (ImageView) findViewById(R.id.detailObraz);
        checkBox = (CheckBox) findViewById(R.id.checkNotify);
        tekstObrazu = (TextView) findViewById(R.id.obrazekText);

        String where = TaskContract.TaskEntry._ID + " = " + taskID;
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE, null, where, null, null, null, null);
        if(cursor != null && cursor.moveToFirst()) {
            getID = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry._ID));
            getTitle = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE));
            getSubtitle = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_SUBTITLE));
            getHour = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_HOUR));
            getMinute = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_MINUTE));
            getDay = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DAY));
            getMonth = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_MONTH));
            getYear = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_YEAR));
            getPath = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_PATH));
            getChecked = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_CHECK));
            getIntervalMinute = cursor.getInt((cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_INT_MINUTE)));
            getIntervalHour = cursor.getInt((cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_INT_HOUR)));
            getIntervalDay = cursor.getInt((cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_INT_DAY)));
        }

        try {
            cursor.close();
        } catch (NullPointerException e) {
            Log.d(TaskContract.DEBUG_TAG, "Cannot close cursor.");
        }

        NumberFormat f = new DecimalFormat("00");
        getTime = String.valueOf(f.format(getHour) + ":" + f.format(getMinute));
        getDate = String.valueOf(getDay + "/" + getMonth + "/" + getYear);

        idZdarzenia.setText(getID);
        tytulZdarzenia.setText(getTitle);
        opisZdarzenia.setText(getSubtitle);
        dzienZdarzenia.setText(String.valueOf(getIntervalDay));
        godzinaZdarzenia.setText(String.valueOf(getIntervalHour));
        minutaZdarzenia.setText(String.valueOf(getIntervalMinute));
        czasZdarzenia.setText(getTime);
        dataZdarzenia.setText(getDate);
        tekstObrazu.setText(getPath);

        if (getChecked == 1) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        try {
            Uri imageFile = Uri.parse(getPath);
            obrazZdarzenia.setImageURI(imageFile);
        } catch (NullPointerException e) {
            Log.d(TAG, "Błąd podglądu zdjęcia");
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String where = TaskContract.TaskEntry._ID + " = " + taskID;

                Log.d(TAG, "isChecked " + String.valueOf(isChecked));

                if (isChecked) {
                    ContentValues values = new ContentValues();
                    values.put(TaskContract.TaskEntry.COL_TASK_CHECK, 1);
                    db.update(TaskContract.TaskEntry.TABLE, values, where, null);

                    //Toast.makeText(getApplicationContext(), "Started", Toast.LENGTH_SHORT).show();

                    Log.d(TAG, getID + " " + getTitle + " " + getSubtitle + " " + getTime + " " + getDay + "/" + getMonth + "/" + getYear + " " + getPath);
                } else {
                    ContentValues values = new ContentValues();
                    values.put(TaskContract.TaskEntry.COL_TASK_CHECK, 0);
                    db.update(TaskContract.TaskEntry.TABLE, values, where, null);

                    Log.d(TAG, "Alarm zatrzymany");
                    //Toast.makeText(getApplicationContext(), "Ended", Toast.LENGTH_SHORT).show();
                }
                Cursor cursor = db.query(TaskContract.TaskEntry.TABLE, null, where, null, null, null, null);
                if(cursor != null && cursor.moveToFirst()) {
                    getID = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry._ID));
                    getTitle = cursor.getString(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE));
                    getChecked = cursor.getInt(cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_CHECK));
                }
                Log.d(TAG, "Updated onCheckedChanged: " + getID + " " + getTitle + " " + getChecked);
            }
        });
    }

    @Override
    protected void onDestroy() {
        db.close();
        mHelper.close();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        startActivity(mainIntent);
    }

    public void editTask(View view) {
        where = TaskContract.TaskEntry._ID + " = " + String.valueOf(idZdarzenia.getText());

        Intent editIntent = new Intent(getApplicationContext(), EditActivity.class);
        editIntent.putExtra("new", false);
        editIntent.putExtra("id", taskID);
        startActivity(editIntent);
    }

    public void deleteTask(View view) {
        alarmStop();

        where = TaskContract.TaskEntry._ID + " = " + String.valueOf(idZdarzenia.getText());

        db.delete(TaskContract.TaskEntry.TABLE, where, null);
        Log.d(TAG, "Deleted");

        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainIntent);
    }

    public void alarmStart() {
        Calendar calendar = Calendar.getInstance();
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.YEAR, getYear);
        calendar.set(Calendar.MONTH, getMonth -1);
        calendar.set(Calendar.DAY_OF_MONTH, getDay);
        calendar.set(Calendar.HOUR_OF_DAY, getHour);
        calendar.set(Calendar.MINUTE, getMinute);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 1000 * 60, pIntent);
    }

    public void alarmStop() {
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pIntent);
    }
}
