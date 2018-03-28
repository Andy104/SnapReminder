package com.example.andy.snapreminder;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.andy.snapreminder.db.TaskContract;
import com.example.andy.snapreminder.db.TaskDbHelper;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;

public class EditActivity extends AppCompatActivity{
    private static final String TAG = "EditActivity";

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 1;
    private static final String IMAGE_DIRECTORY_NAME = "Snap reminder";

    private Uri uriSavedImage;
    private DatePickerDialog.OnDateSetListener mDatePicker;
    private TimePickerDialog.OnTimeSetListener mTimePicker;

    private SQLiteDatabase db;
    private TaskDbHelper mHelper;

    private TextView idZdarzenia;
    private EditText tytulZdarzenia;
    private EditText opisZdarzenia;
    private EditText dzienZdarzenia;
    private EditText godzinaZdarzenia;
    private EditText minutaZdarzenia;
    private TextView czasZdarzenia;
    private TextView dataZdarzenia;
    private ImageView podgladZdarzenia;
    private TextView sciezkaTekst;
    private Button newButton;

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
        setContentView(R.layout.edit_activity);

        mHelper = new TaskDbHelper(this);
        db = mHelper.getWritableDatabase();

        NumberFormat f = new DecimalFormat("00");

        idZdarzenia = (TextView) findViewById(R.id.editID);
        tytulZdarzenia = (EditText) findViewById(R.id.editTytul);
        opisZdarzenia = (EditText) findViewById(R.id.editOpis);
        dzienZdarzenia = (EditText) findViewById(R.id.editDay);
        godzinaZdarzenia = (EditText) findViewById(R.id.editHour);
        minutaZdarzenia = (EditText) findViewById(R.id.editMinute);
        czasZdarzenia = (TextView) findViewById(R.id.editCzas);
        dataZdarzenia = (TextView) findViewById(R.id.editData);
        podgladZdarzenia = (ImageView) findViewById(R.id.podglad);
        newButton = (Button) findViewById(R.id.update_btn);

        sciezkaTekst = (TextView) findViewById(R.id.editPath);

        Bundle extras = getIntent().getExtras();
        taskID = extras.getString("id");
        nowe = extras.getInt("nowe");

        if (nowe == 1) { newButton.setText("Utwórz"); }

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

        getTime = String.valueOf(f.format(getHour) + ":" + f.format(getMinute));
        getDate = String.valueOf(getDay + "/" + getMonth + "/" + getYear);

        if (getPath != null) {
            try {
                podgladZdarzenia.setVisibility(View.VISIBLE);

                Uri imageFile = Uri.parse(getPath);
                podgladZdarzenia.setImageURI(imageFile);
            } catch (NullPointerException e) {
                Log.d(TAG, "Błąd podglądu zdjęcia");
            }
        }

        try {
            cursor.close();
        } catch (NullPointerException e) {
            Log.d(TaskContract.DEBUG_TAG, "Cannot close cursor.");
        }

        idZdarzenia.setText(getID);
        tytulZdarzenia.setText(getTitle);
        opisZdarzenia.setText(getSubtitle);
        dzienZdarzenia.setText(String.valueOf(getIntervalDay));
        godzinaZdarzenia.setText(String.valueOf(getIntervalHour));
        minutaZdarzenia.setText(String.valueOf(getIntervalMinute));
        czasZdarzenia.setText(getTime);
        dataZdarzenia.setText(getDate);

        sciezkaTekst.setText(getPath);

        mDatePicker = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month += 1;
                getDay = dayOfMonth;
                getMonth = month;
                getYear = year;

                String date = dayOfMonth + "/" + month + "/" + year;
                dataZdarzenia.setText(date);
            }
        };

        mTimePicker = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                getHour = hourOfDay;
                getMinute = minute;

                NumberFormat f = new DecimalFormat("00");
                String time = f.format(hourOfDay) + ":" + f.format(minute);
                czasZdarzenia.setText(time);
            }
        };
    }

    @Override
    public void onBackPressed() {
        if (nowe == 1) {
            Intent detailIntent = new Intent(this, DetailActivity.class);
            detailIntent.putExtra("id", taskID);
            startActivity(detailIntent);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        db.close();
        mHelper.close();

        super.onDestroy();
    }

    public void updateDetail(View view) {
        where = TaskContract.TaskEntry._ID + " = " + taskID;
        Log.d(TAG, "where " + where);

        ContentValues values = new ContentValues();
        values.put(TaskContract.TaskEntry.COL_TASK_TITLE, String.valueOf(tytulZdarzenia.getText()));
        values.put(TaskContract.TaskEntry.COL_TASK_SUBTITLE, String.valueOf(opisZdarzenia.getText()));
        values.put(TaskContract.TaskEntry.COL_TASK_HOUR, getHour);
        values.put(TaskContract.TaskEntry.COL_TASK_MINUTE, getMinute);
        values.put(TaskContract.TaskEntry.COL_TASK_DAY, getDay);
        values.put(TaskContract.TaskEntry.COL_TASK_MONTH, getMonth);
        values.put(TaskContract.TaskEntry.COL_TASK_YEAR, getYear);
        values.put(TaskContract.TaskEntry.COL_TASK_PATH, String.valueOf(sciezkaTekst.getText()));
        values.put(TaskContract.TaskEntry.COL_TASK_CHECK, getChecked);
        values.put(TaskContract.TaskEntry.COL_TASK_INT_DAY, Integer.parseInt(String.valueOf(dzienZdarzenia.getText())));
        values.put(TaskContract.TaskEntry.COL_TASK_INT_HOUR, Integer.parseInt(String.valueOf(godzinaZdarzenia.getText())));
        values.put(TaskContract.TaskEntry.COL_TASK_INT_MINUTE, Integer.parseInt(String.valueOf(minutaZdarzenia.getText())));

        db.updateWithOnConflict(TaskContract.TaskEntry.TABLE, values, where, null, SQLiteDatabase.CONFLICT_REPLACE);

        Intent detailIntent = new Intent(getApplicationContext(), DetailActivity.class);
        detailIntent.putExtra("id", taskID);
        detailIntent.putExtra("nowe", nowe);
        startActivity(detailIntent);
    }

    public void cameraButton(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File imagesFolder = new File(Environment.getExternalStorageDirectory(), IMAGE_DIRECTORY_NAME);
        imagesFolder.mkdirs();
        File image = new File(imagesFolder, "image_" + taskID + ".jpg");
        Log.d(TAG, "image_" + String.valueOf(dataZdarzenia.getText()) + "_" + String.valueOf(czasZdarzenia.getText()) + ".jpg");
        uriSavedImage = Uri.fromFile(image);
        sciezkaTekst.setText(uriSavedImage.toString());
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);

        startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    int targetW = podgladZdarzenia.getWidth();
                    int targetH = podgladZdarzenia.getHeight();

                    Log.d(TAG, "W: " + targetW + " H: " + targetH);

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmapOptions.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(uriSavedImage.getPath(), bitmapOptions);
                    int photoW = bitmapOptions.outWidth;
                    int photoH = bitmapOptions.outHeight;

                    int sample = Math.min(photoW/targetW, photoH/targetH);

                    bitmapOptions.inJustDecodeBounds = false;
                    bitmapOptions.inSampleSize = sample;

                    Bitmap bitmap = BitmapFactory.decodeFile(uriSavedImage.getPath(), bitmapOptions);
                    podgladZdarzenia.setImageBitmap(bitmap);

                } catch (NullPointerException e) {
                    Log.d(TAG, "Błąd podglądu zdjęcia");
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "User cancelled image capture", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Sorry! Failed to capture image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void datePicker(View view) {
        Calendar cal = Calendar.getInstance();
        getYear = cal.get(Calendar.YEAR);
        getMonth = cal.get(Calendar.MONTH);
        getDay = cal.get(Calendar.DAY_OF_MONTH);
        //Log.d(TAG, "datePicker " + getDay + "/" + getMonth + "/" + getYear);

        DatePickerDialog dialog = new DatePickerDialog(this, R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, mDatePicker, getYear, getMonth, getDay);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();
    }

    public void timePicker(View view) {
        Calendar cal = Calendar.getInstance();
        getHour = cal.get(Calendar.HOUR_OF_DAY);
        getMinute = cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(this, R.style.Theme_AppCompat_DayNight_Dialog_MinWidth, mTimePicker, getHour, getMinute +1, true);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        dialog.show();
    }
}
