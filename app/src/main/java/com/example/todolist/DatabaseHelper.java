package com.example.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "TaskDatabase";
    private static final String TABLE_TASKS = "Tasks";
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_END_DATE = "end_date";
    private static final String KEY_IS_COMPLETED = "is_completed";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_TASKS + " ("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + KEY_NAME + " TEXT, "
                + KEY_START_DATE + " TEXT, "
                + KEY_END_DATE + " TEXT, "
                + KEY_IS_COMPLETED + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }

    public long addTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, task.getName());
        values.put(KEY_START_DATE, task.getStartDate());
        values.put(KEY_END_DATE, task.getEndDate());
        values.put(KEY_IS_COMPLETED, task.isCompleted() ? 1 : 0);

        long result = db.insert(TABLE_TASKS, null, values);
        db.close();
        return result;
    }

    public List<Task> getAllTasks() {
        List<Task> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_TASKS, null);

        if (cursor.moveToFirst()) {
            do {
                Task task = new Task(
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_NAME)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(KEY_IS_COMPLETED)) == 1,
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_START_DATE)),
                        cursor.getString(cursor.getColumnIndexOrThrow(KEY_END_DATE)),
                        0 // Default progress is 0, can be managed separately
                );
                taskList.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return taskList;
    }

    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, task.getName());
        values.put(KEY_START_DATE, task.getStartDate());
        values.put(KEY_END_DATE, task.getEndDate());
        values.put(KEY_IS_COMPLETED, task.isCompleted() ? 1 : 0);

        int result = db.update(TABLE_TASKS, values, KEY_ID + "=?", new String[]{String.valueOf(task.getId())});
        db.close();
        return result;
    }

    public int deleteTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(TABLE_TASKS, KEY_ID + "=?", new String[]{String.valueOf(task.getId())});
        db.close();
        return result;
    }
}
