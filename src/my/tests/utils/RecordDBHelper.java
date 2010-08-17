package my.tests.utils;

import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.text.TextUtils;

public class RecordDBHelper extends SQLiteOpenHelper {

	public static final String RECORDS_TABLE_NAME = "records";
	static final String WPT_TABLE_NAME = "waypints";
	
	public static final String KEY_ID = "id";
	public static final String KEY_PROFILE = "profile";
	public static final String KEY_START_TIME = "starttime";
	public static final String KEY_END_TIME = "endtime";
	public static final String KEY_DISTANCE = "distance";
	public static final String KEY_AVARAGE_SPEED = "avaragespeed";
	public static final String KEY_COMMENT = "comment";
	
	
	final String TABLE_NAME;
	
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
				KEY_AVARAGE_SPEED + " REAL," +
				KEY_COMMENT + 		" TEXT)");
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
	
	public int delete(String id, String selection, String[] selectionArgs) {
		if (!TextUtils.isDigitsOnly(id))
			throw new IllegalArgumentException();
		
		String whereClause = "";
		if (!TextUtils.isEmpty(id))
			if (TextUtils.isDigitsOnly(id))
				whereClause += KEY_ID + " = " + id + " AND (" + selection  + ")";
			else 
				throw new IllegalArgumentException();
		else 
			whereClause = selection;
		
		return getWritableDatabase().delete(RECORDS_TABLE_NAME, 
				whereClause , selectionArgs);
	}
	
	public long insert(ContentValues values) {
		if (values == null)
			throw new IllegalArgumentException();
		
		if (!values.containsKey(KEY_PROFILE)) values.put(KEY_PROFILE, "none");
		if (!values.containsKey(KEY_START_TIME)) values.put(KEY_START_TIME, new Date().getTime());
		if (!values.containsKey(KEY_END_TIME)) values.put(KEY_END_TIME, new Date().getTime()+1);
		if (!values.containsKey(KEY_DISTANCE)) values.put(KEY_DISTANCE, 0.0);
		if (!values.containsKey(KEY_AVARAGE_SPEED)) values.put(KEY_AVARAGE_SPEED, 0.0);
		if (!values.containsKey(KEY_COMMENT)) values.put(KEY_COMMENT, "no comment");
		return getWritableDatabase().insert(RECORDS_TABLE_NAME, null, values);
	}
	
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
