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

/**
 * Record ORM to database.
 * 
 * @author Waldemar Smirnow
 *
 */
public class Record {
	/**
	 * Context.
	 */
	Context context;
	/**
	 * Record Url.
	 */
	public Uri recordUrl;
	/**
	 * Record ID.
	 */
	public long recordId = 0;
	/**
	 * Record profile.
	 */
	public final String profile;
	/**
	 * Record start time (millis).
	 */
	public long startTime;
	/**
	 * Record end time (millis).
	 */
	public long endTime;
	/**
	 * Record avarage speed (m/s).
	 */
	public float avarageSpeed = 0f;
	/**
	 * Record distance (m).
	 */
	public float distance = 0f;
	/**
	 * Record waypoints.
	 */
	List<Waypoint> waypoints;
	/**
	 * Record comment.
	 */
	public String comment = "";
	/**
	 * Last location, need to compute distance.
	 */
	Location lastLoc = null;
	
	/**
	 * Constructor.
	 * @param context Context
	 * @param profile Profile
	 */
	public Record(Context context, String profile) {
		this(context, profile, new Date().getTime());
	}
	
	/**
	 * Constructor.
	 * @param context Context.
	 * @param profile Profile
	 * @param time Start time
	 */
	public Record(Context context, String profile, long time) {
		
		this.context = context;
		this.profile = profile;
		this.startTime = time;
		this.endTime = startTime;
		this.waypoints = new ArrayList<Waypoint>();
	}
	
	/**
	 * Convert location to Waypoint, add it to waypoint list 
	 * and update other fields like distance and avarage speed.
	 * 
	 * @param location Location
	 * @return true if success
	 */
	public boolean addWaypoint(Location location) {
		Waypoint waypoint = new Waypoint(recordId, location);
		if(0L != waypoint.insertDB(context) && waypoints.add(waypoint)) {
			endTime = new Date().getTime();
			if(lastLoc != null){
				distance += lastLoc.distanceTo(location); 
				avarageSpeed = distance / ((endTime - startTime) / 1000);
			}
			lastLoc = location;
			return 1 == updateDB();
		} else return false;
	}
	
	/**
	 * Get waypoints count.
	 * @return waypoints count
	 */
	public int getWaypointsCount() {
		return waypoints.size();
	}
	
	/**
	 * Get waypoint at index.
	 * @param index Index
	 * @return waypoint at index
	 */
	public Waypoint getWaypoint(int index) {
		return waypoints.get(index);
	}
	
	/**
	 * Delete waypoint at index.
	 * @param index Index
	 * @return true if success
	 */
	public boolean deleteWaypoint(int index) {
		Waypoint wp = waypoints.get(index);
		return Waypoint.deleteDB(context, wp.recordId, wp.waypointId) == 1 &&
			waypoints.remove(wp);
	}
	
	/**
	 * Write data to database. Must be called after creation of Record.
	 * @return record url
	 */
	public final Uri insertDB() {
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(RecordDBHelper.KEY_PROFILE, profile);
		values.put(RecordDBHelper.KEY_START_TIME, startTime);
		recordUrl = resolver.insert(RecordProvider.RECORD_CONTENT_URI, values);
		recordId = Long.parseLong(recordUrl.getPathSegments().get(1));
		return recordUrl;
	}
	
	/**
	 * Update data on database. Can be called after insertBD method.
	 * @return updated rows
	 */
	public int updateDB() {
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(RecordDBHelper.KEY_PROFILE, profile);
		values.put(RecordDBHelper.KEY_START_TIME, startTime);
		values.put(RecordDBHelper.KEY_END_TIME, endTime);
		values.put(RecordDBHelper.KEY_DISTANCE, distance);
		values.put(RecordDBHelper.KEY_AVERAGE_SPEED, avarageSpeed);
		values.put(RecordDBHelper.KEY_COMMENT, comment);
		if (recordUrl == null) insertDB();
		return resolver.update(recordUrl, values, null, null);
	}
	
	/**
	 * Query an Record from database.
	 * @param context Context
	 * @param recordId record id
	 * @return Record with record id
	 * @throws IllegalArgumentException if record (with id) not exist
	 */
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
		float avarageSpeed = cursor.getFloat(cursor.getColumnIndex(RecordDBHelper.KEY_AVERAGE_SPEED));
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
	
	/**
	 * Query Records from database with selection.
	 * @param context Context
	 * @param selection Selection (see RecordDBHelper.KEY_*)
	 * @param selectionArgs Selection arguments
	 * @param sortOrder Sort order
	 * @return List with selected records (can be empty)
	 */
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
	
	/**
	 * Delete Record with id (all record waypoints will be delete to).
	 * @param context Context
	 * @param recordId record id
	 * @return true if success
	 */
	public static boolean deleteDB(Context context, long recordId) {
		ContentResolver resolver = context.getContentResolver();
		
		if (resolver.delete(
				Uri.withAppendedPath(RecordProvider.RECORD_CONTENT_URI, ""+recordId),
				null, null) == 1) {
			
			Waypoint.deleteDB(context, recordId, null, null);
			return true;
		} else return false;
	}

}
