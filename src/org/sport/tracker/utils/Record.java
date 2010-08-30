package org.sport.tracker.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sport.tracker.RecordProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;

public class Record {
	Context context;
	public Uri recordUrl;
	public long recordId = 0;
	public final String profile;
	public long startTime;
	public long endTime;
	public float avarageSpeed = 0f;
	public float distance = 0f;
	List<Waypoint> waypoints;
	public String comment = "";
	
	public Record(Context context, String profile) {
		this.startTime = new Date().getTime();
		this.endTime = startTime;
		this.context = context;
		this.profile = profile;
		this.waypoints = new ArrayList<Waypoint>();
	}
	
	public boolean addWaypoint(Location location) {
		Waypoint wp = new Waypoint(recordId, location);
		return 0L != wp.insertDB(context) && waypoints.add(wp);
	}
	
	public final Uri insertDB() {
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(RecordDBHelper.KEY_PROFILE, profile);
		values.put(RecordDBHelper.KEY_START_TIME, startTime);
		recordUrl = resolver.insert(RecordProvider.RECORD_CONTENT_URI, values);
		recordId = Long.parseLong(recordUrl.getPathSegments().get(1));
		return recordUrl;
	}
	
	public int updateDB() {
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(RecordDBHelper.KEY_PROFILE, profile);
		values.put(RecordDBHelper.KEY_START_TIME, startTime);
		values.put(RecordDBHelper.KEY_END_TIME, endTime);
		values.put(RecordDBHelper.KEY_DISTANCE, distance);
		values.put(RecordDBHelper.KEY_AVARAGE_SPEED, avarageSpeed);
		values.put(RecordDBHelper.KEY_COMMENT, comment);
		return resolver.update(recordUrl, values, null, null);
	}
	
	public static Record queryDB(Context context, long recordId) {
		ContentResolver resolver = context.getContentResolver();
		Uri url = Uri.withAppendedPath(RecordProvider.RECORD_CONTENT_URI, ""+recordId);
		Cursor cursor =  resolver.query(url, null, null, null, null);
		
		if (cursor.getCount() != 1) throw new IllegalArgumentException();
		
		cursor.moveToFirst();
		
		final long id = cursor.getLong(cursor.getColumnIndex(RecordDBHelper.KEY_ID));
		final String profile = cursor.getString(cursor.getColumnIndex(RecordDBHelper.KEY_PROFILE));
		long startTime = cursor.getLong(cursor.getColumnIndex(RecordDBHelper.KEY_START_TIME));
		long endTime = cursor.getLong(cursor.getColumnIndex(RecordDBHelper.KEY_END_TIME));
		float avarageSpeed = cursor.getFloat(cursor.getColumnIndex(RecordDBHelper.KEY_AVARAGE_SPEED));
		float distance = cursor.getFloat(cursor.getColumnIndex(RecordDBHelper.KEY_DISTANCE));
		String comment = cursor.getString(cursor.getColumnIndex(RecordDBHelper.KEY_COMMENT));
		cursor.close();
		
		Record record = new Record(context, profile);
		record.recordId = id;
		record.recordUrl = url;
		record.startTime = startTime;
		record.endTime = endTime;
		record.avarageSpeed = avarageSpeed;
		record.distance = distance;
		record.comment = comment;
		record.waypoints = Waypoint.queryDB(context, recordId, 
				null, null, WaypointDBHelper.KEY_TIME);
		
		return record;
	}
	
	public static List<Record> queryDB(Context context,
			String selection, String[] selectionArgs, String sortOrder) {
		
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor = resolver.query(RecordProvider.RECORD_CONTENT_URI, 
				new String[] {
					RecordDBHelper.KEY_ID
				}, selection, selectionArgs, sortOrder);
		
		List<Record> records = new ArrayList<Record>();
		
		if (cursor.getCount() < 1) return records;
		cursor.moveToFirst();
		
		while (!cursor.isAfterLast()) {
			long recordId = cursor.getLong(cursor.getColumnIndex(RecordDBHelper.KEY_ID));
			records.add(Record.queryDB(context, recordId));
			cursor.moveToNext();
		}
		cursor.close();
		return records;
	}
	
	public static int deleteDB(Context context, long recordId) {
		ContentResolver resolver = context.getContentResolver();
		return resolver.delete(
				Uri.withAppendedPath(RecordProvider.RECORD_CONTENT_URI, ""+recordId),
				null, null);
	}

}
