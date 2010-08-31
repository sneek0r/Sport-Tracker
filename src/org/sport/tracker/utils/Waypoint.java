package org.sport.tracker.utils;

import java.util.ArrayList;
import java.util.List;

import org.sport.tracker.SportTrackerProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;

/**
 * Waypoint ORM to database.
 * 
 * @author Waldemar Smirnow
 *
 */
public class Waypoint {

	/**
	 * Record id.
	 */
	public final long recordId;
	/**
	 * Altitude.
	 */
	public final double altitude;
	/**
	 * Latitude.
	 */
	public final double latitude;
	/**
	 * Longtitude.
	 */
	public final double longtitude;
	/**
	 * Speed.
	 */
	public final float speed;
	/**
	 * Accuracy.
	 */
	public final float accuracy;
	/**
	 * Time.
	 */
	public final long time;
	/**
	 * Waypoint id.
	 */
	public long waypointId;
	/**
	 * Waypoint url.
	 */
	public Uri waypointUrl;
	
	/**
	 * Constructor.
	 * @param recordId Record id
	 * @param accuracy Accuracy
	 * @param latitude Latitude
	 * @param longtitude Longtitude
	 * @param altitude Altitude
	 * @param speed Speed
	 * @param time Time
	 */
	public Waypoint(long recordId,  float accuracy, 
			double latitude, double longtitude, 
			double altitude, float speed, long time) {
		
		this.recordId = recordId;
		this.accuracy = accuracy;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.altitude = altitude;
		this.speed = speed;
		this.time = time;
	}
	
	/**
	 * Constructor.
	 * @param recordId Record id
	 * @param location Location
	 */
	public Waypoint(long recordId, Location location) {
		this.recordId = recordId;
		this.accuracy = location.getAccuracy();
		this.latitude = location.getLatitude();
		this.longtitude = location.getLongitude();
		this.altitude = location.getAltitude();
		this.speed = location.getSpeed();
		this.time = location.getTime();
	}
	
	/**
	 * Write data to database. Must be called after creation of waypoint.
	 * @param context Context
	 * @return Waypoint id
	 */
	public long insertDB(Context context) {
		ContentResolver resolver = context.getContentResolver();
		ContentValues values = new ContentValues();
		values.put(WaypointDBHelper.KEY_RECORD_ID, recordId);
		values.put(WaypointDBHelper.KEY_ALTITUDE, altitude);
		values.put(WaypointDBHelper.KEY_LATITUDE, latitude);
		values.put(WaypointDBHelper.KEY_LONGTITUDE, longtitude);
		values.put(WaypointDBHelper.KEY_SPEED, speed);
		values.put(WaypointDBHelper.KEY_ACCURACY, accuracy);
		values.put(WaypointDBHelper.KEY_TIME, time);
		waypointUrl = resolver.insert(Uri.withAppendedPath(
				SportTrackerProvider.WAYPOINT_CONTENT_URI, ""+recordId), values);
		waypointId = Long.parseLong(waypointUrl.getPathSegments().get(2));
		return waypointId;
	}
	
	/**
	 * Query an waypoint from database. 
	 * @param context Context
	 * @param recordId Record id
	 * @param waypointId Waypoint id
	 * @return Waypoint (with waypoint id)
	 */
	public static Waypoint queryDB(Context context, long recordId, long waypointId) {
		
		ContentResolver resolver = context.getContentResolver();
		Uri url = Uri.withAppendedPath(
				SportTrackerProvider.WAYPOINT_CONTENT_URI, ""+recordId+"/"+waypointId);
		
		Cursor cursor = resolver.query(url, null, null, null, null);
		
		if (cursor.getCount() != 1) throw new IllegalArgumentException();
		
		cursor.moveToFirst();
		final double altitude = cursor.getDouble(cursor.getColumnIndex(WaypointDBHelper.KEY_ALTITUDE));
		final double latitude = cursor.getDouble(cursor.getColumnIndex(WaypointDBHelper.KEY_LATITUDE));
		final double longtitude = cursor.getDouble(cursor.getColumnIndex(WaypointDBHelper.KEY_LONGTITUDE));
		final float speed = cursor.getFloat(cursor.getColumnIndex(WaypointDBHelper.KEY_SPEED));
		final float accuracy = cursor.getFloat(cursor.getColumnIndex(WaypointDBHelper.KEY_ACCURACY));
		final long time = cursor.getLong(cursor.getColumnIndex(WaypointDBHelper.KEY_TIME));
		cursor.close();
		
		Waypoint wp = new Waypoint(recordId, accuracy, latitude, longtitude, altitude, speed, time);
		wp.waypointId = waypointId;
		wp.waypointUrl = url;
		return wp;
	}
	
	/**
	 * Query waypoints from database with selection.
	 * @param context Context
	 * @param recordId Record id
	 * @param selection Selection (see WaypointDBHelper.KEY_*)
	 * @param selectionArgs Selection arguments
	 * @param sortOrder Sort order
	 * @return Waypoints from selection (can be empty)
	 */
	public static List<Waypoint> queryDB(Context context, long recordId, 
			String selection, String[] selectionArgs, String sortOrder) {
		
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor =  resolver.query(Uri.withAppendedPath(
				SportTrackerProvider.WAYPOINT_CONTENT_URI, ""+recordId),
				null, selection, selectionArgs, sortOrder);
		
		List<Waypoint> waypoints = new ArrayList<Waypoint>();
		
		if (cursor.getCount() < 1) {
			cursor.close();
			return waypoints;
		}
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			long waypointId = cursor.getLong(cursor.getColumnIndex(WaypointDBHelper.KEY_ID));
			recordId = cursor.getLong(cursor.getColumnIndex(WaypointDBHelper.KEY_RECORD_ID));
			Uri waypoinUrl = Uri.withAppendedPath(SportTrackerProvider.WAYPOINT_CONTENT_URI, ""+recordId+"/"+waypointId);
			final double altitude = cursor.getDouble(cursor.getColumnIndex(WaypointDBHelper.KEY_ALTITUDE));
			final double latitude = cursor.getDouble(cursor.getColumnIndex(WaypointDBHelper.KEY_LATITUDE));
			final double longtitude = cursor.getDouble(cursor.getColumnIndex(WaypointDBHelper.KEY_LONGTITUDE));
			final float speed = cursor.getFloat(cursor.getColumnIndex(WaypointDBHelper.KEY_SPEED));
			final float accuracy = cursor.getFloat(cursor.getColumnIndex(WaypointDBHelper.KEY_ACCURACY));
			final long time = cursor.getLong(cursor.getColumnIndex(WaypointDBHelper.KEY_TIME));
			
			
			Waypoint wp = new Waypoint(recordId, accuracy, latitude, longtitude, altitude, speed, time);
			wp.waypointId = waypointId;
			wp.waypointUrl = waypoinUrl;
			cursor.moveToNext();
			waypoints.add(wp);
			
			cursor.moveToNext();
		}
		cursor.close();
		return waypoints;
	}
	
	/**
	 * Delete waypoint with id.
	 * @param context Context
	 * @param recordId Record id
	 * @param waypointId Waypoint id
	 * @return Rows deleted
	 */
	public static int deleteDB(Context context, long recordId, long waypointId) {
		
		ContentResolver resolver = context.getContentResolver();
		return resolver.delete(Uri.withAppendedPath(
				SportTrackerProvider.WAYPOINT_CONTENT_URI, ""+recordId+"/"+waypointId),
				null, null);
	}
	
	/**
	 * Delete waypoints with selection. 
	 * @param context Context
	 * @param recordId Record id
	 * @param where Selection (see WaypointDBHelper.KEY_*)
	 * @param selectionArgs Selection arguments
	 * @return rows deleted
	 */
	public static int deleteDB(Context context, long recordId, String where, String[] selectionArgs) {
		
		ContentResolver resolver = context.getContentResolver();
		return resolver.delete(Uri.withAppendedPath(
				SportTrackerProvider.WAYPOINT_CONTENT_URI, ""+recordId),
				where, selectionArgs);
	}
}
