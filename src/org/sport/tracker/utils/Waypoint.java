package org.sport.tracker.utils;

import java.util.ArrayList;
import java.util.List;

import org.sport.tracker.RecordProvider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;

public class Waypoint {

	public final long recordId;
	public final double altitude;
	public final double latitude;
	public final double longtitude;
	public final float speed;
	public final float accuracy;
	public final long time;
	
	public long waypointId;
	public Uri waypointUrl;
	
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
	
	public Waypoint(long recordId, Location location) {
		this.recordId = recordId;
		this.accuracy = location.getAccuracy();
		this.latitude = location.getLatitude();
		this.longtitude = location.getLongitude();
		this.altitude = location.getAltitude();
		this.speed = location.getSpeed();
		this.time = location.getTime();
	}
	
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
				RecordProvider.WAYPOINT_CONTENT_URI, ""+recordId), values);
		waypointId = Long.parseLong(waypointUrl.getPathSegments().get(2));
		return waypointId;
	}
	
	public static Waypoint queryDB(Context context, long recordId, long waypointId) {
		
		ContentResolver resolver = context.getContentResolver();
		Uri url = Uri.withAppendedPath(
				RecordProvider.WAYPOINT_CONTENT_URI, ""+recordId+"/"+waypointId);
		
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
	
	public static List<Waypoint> queryDB(Context context, long recordId, 
			String selection, String[] selectionArgs, String sortOrder) {
		
		ContentResolver resolver = context.getContentResolver();
		Cursor cursor =  resolver.query(Uri.withAppendedPath(
				RecordProvider.WAYPOINT_CONTENT_URI, ""+recordId),
				null, selection, selectionArgs, sortOrder);
		
		List<Waypoint> waypoints = new ArrayList<Waypoint>();
		
		if (cursor.getCount() < 1) return waypoints;
		
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			long waypointId = cursor.getLong(cursor.getColumnIndex(WaypointDBHelper.KEY_ID));
			recordId = cursor.getLong(cursor.getColumnIndex(WaypointDBHelper.KEY_RECORD_ID));
			Uri waypoinUrl = Uri.withAppendedPath(RecordProvider.WAYPOINT_CONTENT_URI, ""+recordId+"/"+waypointId);
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
	
	public static int deleteDB(Context context, long recordId, long waypointId, 
			String where, String[] selectionArgs) {
		
		ContentResolver resolver = context.getContentResolver();
		return resolver.delete(Uri.withAppendedPath(
				RecordProvider.WAYPOINT_CONTENT_URI, ""+recordId+"/"+waypointId)
				, where, selectionArgs);
	}
}
