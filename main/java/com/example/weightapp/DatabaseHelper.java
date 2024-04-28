package com.example.weightapp;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserManager.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_USER = "user";
    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_NAME = "user_name";
    private static final String COLUMN_USER_PASSWORD = "password";

    private static final String TABLE_WEIGHTS = "weights";
    public static final String COLUMN_WEIGHT_ID = "weight_id";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_DATE = "date";

    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_NAME + " TEXT,"
            + COLUMN_USER_PASSWORD + " TEXT" + ")";

    private static final String CREATE_WEIGHT_TABLE = "CREATE TABLE " + TABLE_WEIGHTS + "("
            + COLUMN_WEIGHT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_ID + " INTEGER,"
            + COLUMN_WEIGHT + " TEXT,"
            + COLUMN_DATE + " TEXT,"
            + "FOREIGN KEY(" + COLUMN_USER_ID + ") REFERENCES " + TABLE_USER + "(" + COLUMN_USER_ID + "))";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;
    private String DROP_WEIGHT_TABLE = "DROP TABLE IF EXISTS " + TABLE_WEIGHTS;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_WEIGHT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_WEIGHT_TABLE);
        onCreate(db);
    }

    public void addUser(String name, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_NAME, name);
        values.put(COLUMN_USER_PASSWORD, password);
        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public boolean checkUser(String username, String password) {
        String[] columns = {
                COLUMN_USER_ID
        };
        SQLiteDatabase db = this.getReadableDatabase();
        String selection = COLUMN_USER_NAME + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query(TABLE_USER,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        return cursorCount > 0;
    }
    public int getUserId(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COLUMN_USER_ID };
        String selection = COLUMN_USER_NAME + " = ? AND " + COLUMN_USER_PASSWORD + " = ?";
        String[] selectionArgs = { username, password };

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int userId = -1;  // Default to -1, indicating failure

        // Check if COLUMN_USER_ID exists in the cursor
        int columnIndex = cursor.getColumnIndex(COLUMN_USER_ID);
        if (columnIndex != -1) {
            if (cursor.moveToFirst()) {
                userId = cursor.getInt(columnIndex);  // Use the valid column index
            }
        }

        cursor.close();
        db.close();
        return userId;
    }
    // Add method to insert weight
    public void addWeight(int userId, String weight, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID, userId);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_DATE, date);
        db.insert(TABLE_WEIGHTS, null, values);
        db.close();
    }

    // Add method to update weight
    public void updateWeight(int weightId, String weight) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_WEIGHT, weight);
        db.update(TABLE_WEIGHTS, values, COLUMN_WEIGHT_ID + " = ?", new String[]{String.valueOf(weightId)});
        db.close();
    }

    // Add method to delete weight
    public void deleteWeight(int weightId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WEIGHTS, COLUMN_WEIGHT_ID + " = ?", new String[]{String.valueOf(weightId)});
        db.close();
    }

    // Fetch latest weight for user
    public String getLatestWeight(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String weight = "No weight recorded"; // Default message if no record is found
        // Query to select the weight from the weights table for the given user, ordered by date descending and limited to 1
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_WEIGHT + " FROM " + TABLE_WEIGHTS +
                        " WHERE " + COLUMN_USER_ID + " = ? ORDER BY " + COLUMN_DATE + " DESC LIMIT 1",
                new String[]{String.valueOf(userId)});

        // Get the index of the column and check if it's valid
        int columnIndex = cursor.getColumnIndex(COLUMN_WEIGHT);
        if (columnIndex != -1 && cursor.moveToFirst()) {
            weight = cursor.getString(columnIndex); // Use the valid column index to get the latest weight
        }

        cursor.close(); // Close the cursor to release resources
        db.close(); // Close the database to release resources
        return weight;
    }

    // Fetch all weights for user
    public Cursor getAllWeights(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_WEIGHTS + " WHERE " + COLUMN_USER_ID + " = ? ORDER BY " + COLUMN_DATE + " DESC",
                new String[]{String.valueOf(userId)});
    }


}