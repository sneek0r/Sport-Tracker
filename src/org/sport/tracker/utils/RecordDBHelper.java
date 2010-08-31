package org.sport.tracker.utils;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.text.TextUtils;

/**
 * Record database helper. Provide Record CRUD functions.
 * 
 * @author Waldemar Smirnow
 *
 */
public class RecordDBHelper extends SQLiteOpenHelper {

	/**
	 * Static records table name (should be used for constructor).
	 */
	public static final String RECORDS_TABLE_NAME = "records";
	/**
	 * Column name for record id.
	 */
	public static final String KEY_ID = "_id";
	/**
	 * Column name for record profile.
	 */
	public static final String KEY_PROFILE = "profile";
	/**
	 * Column name for record start time.
	 */
	public static final String KEY_START_TIME = "starttime";
	/**
	 * Column name for record end time.
	 */
	public static final String KEY_END_TIME = "endtime";
	/**
	 * Column name for record distance.
	 */
	public static final String KEY_DISTANCE = "distance";
	/**
	 * Column name for record average speed.
	 */
	public static final String KEY_AVERAGE_SPEED = "averagespeed";
	/**
	 * Column name for record comment.
	 */
	public static final String KEY_COMMENT = "comment";
	
	/**
	 * Records table name.
	 */
	final String TABLE_NAME;
	
	/**
	 * Constructor.
	 * @param context Context
	 * @param name Table name (see RecordDBHelper.RECORDS_TABLE_NAME)
	 * @param factory Cursor factory
	 * @param version Database version
	 */
	public RecordDBHelper(Context context, String name, CursorFactory factory, int version) {
		super(context, name, factory, version);
		this.TABLE_NAME = name;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (" +
				KEY_ID + 			" INTEGER PRIMARY KEY, " +
				KEY_PROFILE + 		" TEXT NOT NULL," +
				KEY_START_TIME + 	" INTEGER," +
				KEY_END_TIME + 		" INTEGER," +
				KEY_DISTANCE +		" REAL," +
				KEY_AVERAGE_SPEED + " REAL," +
				KEY_COMMENT + 		" TEXT)");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	
	/**
	 * Delete record with id or selection. 
	 * @throws IllegalArgumentException if id is not empty and is not a digit
	 * @param id Record id
	 * @param selection Selection
	 * @param selectionArgs Selection arguments
	 * @return Deleted rows
	 */
	public int delete(String id, String selection, String[] selectionArgs) {
		if (!TextUtils.isDigitsOnly(id))
			throw new IllegalArgumentException();
		
		String whereClause = "";
		if (!TextUtils.isEmpty(id))
			if (TextUtils.isDigitsOnly(id))
				whereClause += KEY_ID + " = " + id;
			else 
				throw new IllegalArgumentException();
		else 
			whereClause = selection;
		
		return getWritableDatabase().delete(RECORDS_TABLE_NAME, 
				whereClause , selectionArgs);
	}
	
	/**
	 * Insert record into database. 
	 * @throws IllegalArgumentException if record data is null
	 * @param values Record data
	 * @return Record id
	 */
	public long insert(ContentValues values) {
		if (values == null)
			throw new IllegalArgumentException();
		
		if (!values.containsKey(KEY_PROFILE)) values.put(KEY_PROFILE, "none");
		if (!values.containsKey(KEY_START_TIME)) values.put(KEY_START_TIME, new Date().getTime());
		if (!values.containsKey(KEY_END_TIME)) values.put(KEY_END_TIME, new Date().getTime()+1);
		if (!values.containsKey(KEY_DISTANCE)) values.put(KEY_DISTANCE, 0.0);
		if (!values.containsKey(KEY_AVERAGE_SPEED)) values.put(KEY_AVERAGE_SPEED, 0.0);
		if (!values.containsKey(KEY_COMMENT)) values.put(KEY_COMMENT, "no comment");
		return getWritableDatabase().insert(RECORDS_TABLE_NAME, null, values);
	}
	
	/**
	 * Query record(s) with id or selection.
	 * @throws IllegalArgumentException if id is not empty and is not a digit
	 * @param id Record id
	 * @param projection Projection 
	 * @param selection Selection
	 * @param selectionArgs Selection arguments
	 * @param sortOrder Sort order
	 * @return Cursor to record(s)
	 */
	public Cursor query(String id, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		if (!TextUtils.isEmpty(id) && !TextUtils.isDigitsOnly(id))
			throw new IllegalArgumentException();

		if (!TextUtils.isEmpty(id)) {
			return getReadableDatabase().query(TABLE_NAME, projection, KEY_ID + " = " + id, 
				null, null, null, sortOrder); 
		} else 		
			return getReadableDatabase().query(TABLE_NAME, projection, 
				selection, selectionArgs, null, null, sortOrder);
	}
	
	/**
	 * Update Record with id or selection.
	 * @throws IllegalArgumentException if id is not empty and is not a digit
	 * @param id Record id
	 * @param values Record data
	 * @param selection Selection
	 * @param selectionArgs Selection arguments
	 * @return updated rows
	 */
	public int update(String id, ContentValues values, String selection,
			String[] selectionArgs) {
		
		if (!TextUtils.isEmpty(id) && !TextUtils.isDigitsOnly(id))
			throw new IllegalArgumentException();

		if (!TextUtils.isEmpty(id))
			return getWritableDatabase().update(TABLE_NAME, values, KEY_ID + " = " + id, null);
		else 
			return getWritableDatabase().update(TABLE_NAME, values, selection, selectionArgs);
	}
}
