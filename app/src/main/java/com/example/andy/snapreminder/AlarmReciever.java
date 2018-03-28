package com.example.andy.snapreminder;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.andy.snapreminder.db.TaskContract;
import com.example.andy.snapreminder.db.TaskDbHelper;

import java.util.Calendar;

public class AlarmReciever extends BroadcastReceiver {
    private static final String TAG = "AlarmReciever";

    private SQLiteDatabase db;
    private TaskDbHelper mHelper;

    NotificationManager notificationManager;

    private Bundle extras;
    private String taskID;

    private int newHour;
    private int newMinute;
    private int newDay;
    private int newMonth;
    private int newYear;

    @Override
    public void onReceive(Context context, Intent intent) {
        extras = intent.getExtras();
        if (extras != null) {
            taskID = extras.getString("id");
        }
        Log.d(TAG, "onReceive " + taskID);
        //Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();

        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        month += 1;
        int year = cal.get(Calendar.YEAR);

        mHelper = new TaskDbHelper(context);
        try {
            db = mHelper.getReadableDatabase();
        } catch (SQLException e) {
            Log.d(TaskContract.DEBUG_TAG, "Error while opening database: " + e);
            Toast.makeText(context , "Read-only mode", Toast.LENGTH_LONG).show();
        }

        newYear = 0;
        newMonth = 0;
        newDay = 0;
        newMinute = 0;
        newHour = 0;

        Log.d(TAG, "Calendar: " + hour + ":" + minute + " - " + day + "/" + month + "/" + year);

        String where = TaskContract.TaskEntry._ID + " = " + taskID;
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE, null, where, null, null, null, null);
        while (cursor.moveToNext()) {
            int id_id = cursor.getColumnIndex(TaskContract.TaskEntry._ID);
            int id_title = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            int id_subtitle = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_SUBTITLE);
            int id_hour = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_HOUR);
            int id_minute = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_MINUTE);
            int id_day = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_DAY);
            int id_month = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_MONTH);
            int id_year = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_YEAR);
            int int_minute = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_INT_MINUTE);
            int int_hour = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_INT_HOUR);
            int int_day = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_INT_DAY);
            int id_check = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_CHECK);

            if (cursor.getInt(id_hour) == hour && cursor.getInt(id_minute) == minute && cursor.getInt(id_day) == day && cursor.getInt(id_month) == month && cursor.getInt(id_year) == year) {
                Log.d(TAG, "In while-loop, get Cursor  " + cursor.getInt(id_hour) + ":" + cursor.getInt(id_minute) + " - " + cursor.getInt(id_day) + "/" + cursor.getInt(id_month) + "/" + cursor.getInt(id_year));

                newMinute += cursor.getInt(id_minute) + cursor.getInt(int_minute);
                if (newMinute >= 60) { newHour += 1; newMinute -= 60; }
                newHour += cursor.getInt(id_hour) + cursor.getInt(int_hour);
                if (newHour >= 24) { newDay += 1; newHour -= 24; }
                newDay += cursor.getInt(id_day) + cursor.getInt(int_day);
                newMonth += cursor.getInt(id_month);
                switch (newMonth) {
                    case 1:
                        if (newDay > 31) { newDay -= 31; newMonth += 1; }
                        break;
                    case 2:
                        if (newDay > 28) { newDay -= 28; newMonth += 1; }
                        break;
                    case 3:
                        if (newDay > 31) { newDay -= 31; newMonth += 1; }
                        break;
                    case 4:
                        if (newDay > 30) { newDay -= 30; newMonth += 1; }
                        break;
                    case 5:
                        if (newDay > 31) { newDay -= 31; newMonth += 1; }
                        break;
                    case 6:
                        if (newDay > 30) { newDay -= 30; newMonth += 1; }
                        break;
                    case 7:
                        if (newDay > 31) { newDay -= 31; newMonth += 1; }
                        break;
                    case 8:
                        if (newDay > 30) { newDay -= 30; newMonth += 1; }
                        break;
                    case 9:
                        if (newDay > 31) { newDay -= 31; newMonth += 1; }
                        break;
                    case 10:
                        if (newDay > 30) { newDay -= 30; newMonth += 1; }
                        break;
                    case 11:
                        if (newDay > 31) { newDay -= 31; newMonth += 1; }
                        break;
                    case 12:
                        if (newDay > 30) { newDay -= 30; newMonth += 1; }
                        break;
                }
                if (newMonth >= 12) { newMonth -= 12; newYear += 1; }
                newYear += cursor.getInt(id_year);

                ContentValues values = new ContentValues();
                values.put(TaskContract.TaskEntry.COL_TASK_HOUR, newHour);
                values.put(TaskContract.TaskEntry.COL_TASK_MINUTE, newMinute);
                values.put(TaskContract.TaskEntry.COL_TASK_DAY, newDay);
                values.put(TaskContract.TaskEntry.COL_TASK_MONTH, newMonth);
                values.put(TaskContract.TaskEntry.COL_TASK_YEAR, newYear);
                db.update(TaskContract.TaskEntry.TABLE, values, where, null);

                if (Integer.parseInt(cursor.getString(id_check)) == 1)
                {
                    Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
                            .setContentTitle(cursor.getString(id_title))
                            .setContentText(cursor.getString(id_subtitle))
                            .setTicker("Alert")
                            .setColor(Color.GREEN)
                            .setVibrate(new long[]{100, 200, 300, 400, 500})
                            .setSound(soundUri)
                            .setSmallIcon(R.drawable.icon_person);

                    Intent detailNotifyIntent = new Intent(context, DetailActivity.class);
                    detailNotifyIntent.putExtra("id", cursor.getString(id_id));

                    TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);

                    taskStackBuilder.addParentStack(MainActivity.class);
                    taskStackBuilder.addNextIntent(detailNotifyIntent);

                    PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                    notifBuilder.setContentIntent(pendingIntent);

                    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(Integer.parseInt(cursor.getString(id_id)), notifBuilder.build());
                }
            }
        }

        cursor.close();
        db.close();
    }
}