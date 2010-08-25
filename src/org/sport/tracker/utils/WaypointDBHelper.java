package org.sport.tracker.utils;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.text.TextUtils;

public class WaypointDBHelper extends SQLiteOpenHelper {

	public static final String WAYPOINT_TABLE_NAME = "waypoints";
	
	public static final String KEY_ID = "_id";
	public static final String KEY_RECORD_ID = "recordID";
	public static final String KEY_LATITUDE = "latitude";
	public static final String KEY_LONGTITUDE = "longtitude";
	public static final String KEY_ALTITUDE = "altitude";
	public static final String KEY_ACCURACY = "accuracy";
	public static final String KEY_TIME = "time";
	public static final String KEY_SPEED = "speed";
	
	final String TABLE_NAME;
	
	public WaypointDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		
		super(context, name, factory, version);
		this.TABLE_NAME = name;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				KEY_ID + 		" INTEGER PRIMARY KEY, " +
				KEY_RECORD_ID + " INTEGER NOT NULL, " +
				KEY_LATITUDE +	" REAL, " +
				KEY_LONGTITUDE +" REAL, " +
				KEY_ALTITUDE +	" REAL, " +
				KEY_ACCURACY +	" REAL, " +
				KEY_TIME +		" INTEGER, " + 
				KEY_SPEED +		" REAL)");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	
	public int delete(String id, String selection, String[] selectionArgs) {
		if (!TextUtils.isEmpty(id) && !TextUtils.isDigitsOnly(id))
			throw new IllegalArgumentException();
		
		if (!TextUtils.isEmpty(id))
			return getWritableDatabase().delete(TABLE_NAME, KEY_ID + " = " + id, null);
		else 
			return getWritableDatabase().delete(TABLE_NAME, selection , selectionArgs);
	}
	
	public long insert(ContentValues values) {
		if (TextUtils.isEmpty(values.getAsString(KEY_RECORD_ID)) || !TextUtils.isDigitsOnly(values.getAsString(KEY_RECORD_ID)))
			throw new IllegalArgumentException();
		
		if (!values.containsKey(KEY_LATITUDE)) values.put(KEY_LATITUDE, 0.0);
		if (!values.containsKey(KEY_LONGTITUDE)) values.put(KEY_LONGTITUDE, 0.0);
		if (!values.containsKey(KEY_ALTITUDE)) values.put(KEY_ALTITUDE, 0.0);
		if (!values.containsKey(KEY_ACCURACY)) values.put(KEY_ACCURACY, 0);
		if (!values.containsKey(KEY_TIME)) values.put(KEY_TIME, new Date().getTime());
		if (!values.containsKey(KEY_SPEED)) values.put(KEY_SPEED, 0.0);
		
		return getWritableDatabase().insert(TABLE_NAME, null, values);
	}
	
	public Cursor query(String waypointId, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		if (!TextUtils.isEmpty(waypointId) && !TextUtils.isDigitsOnly(waypointId))
			throw new IllegalArgumentException();
		
		if (!TextUtils.isEmpty(waypointId)) {
			return getReadableDatabase().query(TABLE_NAME, projection, KEY_ID + " = " + waypointId, 
				null, null, null, sortOrder); 
		} else 		
			return getReadableDatabase().query(TABLE_NAME, projection, 
				selection, selectionArgs, null, null, sortOrder);
	}
	
	public int update(String waypointId, ContentValues values, String selection,
			String[] selectionArgs) {

		if (!TextUtils.isEmpty(waypointId) && !TextUtils.isDigitsOnly(waypointId))
			throw new IllegalArgumentException();
		
		if (!TextUtils.isEmpty(waypointId)) {
			return getWritableDatabase().update(TABLE_NAME, values, KEY_ID + " = " + waypointId, null);
		} else {
			return getWritableDatabase().update(TABLE_NAME, values, selection, selectionArgs);
		}
	}

}
