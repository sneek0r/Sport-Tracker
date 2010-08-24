package org.sport.tracker.utils;

import java.util.Date;

import org.sport.tracker.RecordProvider;
import org.sport.tracker.RecordUI;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class SportTrackerLocationListener implements LocationListener {

	public Context context;
	public String profile;
	public long recordId = -1;
	public Uri recordUri;
	public boolean paused = false;
	public long startTime = -1;
	public long timeSpan = 0;

	public SportTrackerLocationListener(Context context, String profile) {
		this.context = context;
		this.profile = profile;
		LocationManager locManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 10,
				this);
	}

	@Override
	public void onLocationChanged(Location location) {

		if (paused) {
			startTime = new Date().getTime();
			return;
		}

		if (startTime != -1) {
			timeSpan = new Date().getTime() - startTime;
		}
		
		ContentResolver resolver = context.getContentResolver();
		ContentValues val = new ContentValues();
		if (recordId == -1l) {
			startTime = new Date().getTime();
			val.put(RecordDBHelper.KEY_PROFILE, profile);
			val.put(RecordDBHelper.KEY_START_TIME, startTime);
			recordUri = resolver.insert(RecordProvider.RECORD_CONTENT_URI, val);
			val.clear();
			recordId = Long.parseLong(recordUri.getPathSegments().get(1));
		}

		// get record distance
		Cursor c = resolver.query(recordUri,
				new String[] { RecordDBHelper.KEY_DISTANCE }, null, null, null);
		float distance = 0;
		if (c.getCount() > 0) {
			c.moveToFirst();
			distance = c.getFloat(0);
		}
		c.close();
		
		c = resolver.query(Uri.withAppendedPath(
				RecordProvider.WAYPOINT_CONTENT_URI, "" + recordId),
				new String[] { 
						WaypointDBHelper.KEY_LATITUDE,
						WaypointDBHelper.KEY_LONGTITUDE,
						WaypointDBHelper.KEY_ALTITUDE,
						WaypointDBHelper.KEY_TIME }, null, null,
				WaypointDBHelper.KEY_TIME);

		if (c.getCount() > 0) {
			c.moveToLast();
			Location lastLoc = new Location("GPS");
			lastLoc.setLatitude(c.getDouble(0));
			lastLoc.setLongitude(c.getDouble(1));
			lastLoc.setAltitude(c.getDouble(2));
			distance += location.distanceTo(lastLoc);
			val.clear();
			val.put(RecordDBHelper.KEY_DISTANCE, distance);
		}
		c.close();
		
		float avarageSpeed = (distance > 0) ? distance / (timeSpan / 1000) : 0;
		Log.d(getClass().toString(), "avaregeSpeed: " + avarageSpeed);
		val.put(RecordDBHelper.KEY_AVARAGE_SPEED, avarageSpeed);
		resolver.update(recordUri, val, null, null);
		
		
		val.clear();
		val.put(WaypointDBHelper.KEY_LATITUDE, location.getLatitude());
		val.put(WaypointDBHelper.KEY_LONGTITUDE, location.getLongitude());
		val.put(WaypointDBHelper.KEY_ALTITUDE, location.getAltitude());
		val.put(WaypointDBHelper.KEY_ACCURACY, location.getAccuracy());
		val.put(WaypointDBHelper.KEY_SPEED, location.getSpeed());
		val.put(WaypointDBHelper.KEY_TIME, new Date().getTime());
		
		// add waypoint
		Uri waypointUri = resolver.insert(Uri.withAppendedPath(
				RecordProvider.WAYPOINT_CONTENT_URI, "" + recordId), val);

		if (context instanceof RecordUI) {
			// notify UI
			((RecordUI) context).updateFields(recordUri, waypointUri);
		} else {
			Toast.makeText(context, "fields not updated", 1).show();
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle bundle) {
		// TODO Auto-generated method stub

	}

	public void stopRecord() {
		LocationManager locManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		locManager.removeUpdates(this);

		if (recordUri != null) {
			ContentValues val = new ContentValues();
			val.put(RecordDBHelper.KEY_END_TIME, new Date().getTime());
			context.getContentResolver().update(recordUri, val, null, null);
		}

		Toast.makeText(context, "Record stoped!", 3).show();
	}
}
