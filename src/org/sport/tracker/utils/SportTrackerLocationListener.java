package org.sport.tracker.utils;

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
import android.widget.Toast;

public class SportTrackerLocationListener implements LocationListener {

	public Context context;
	public String profile;
	public long recordId = -1;
	public Uri recordUri;

	public SportTrackerLocationListener(Context context, String profile) {
		this.context = context;
		this.profile = profile;
		LocationManager locManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 10, this);
	}

	@Override
	public void onLocationChanged(Location location) {

		ContentResolver resolver = context.getContentResolver();
		ContentValues val = new ContentValues();
		if (recordId == -1l) {
			val.put(RecordDBHelper.KEY_PROFILE, profile);
			val.put(RecordDBHelper.KEY_START_TIME, location.getTime());
			recordUri = resolver.insert(RecordProvider.RECORD_CONTENT_URI, val);
			val.clear();
			recordId = Long.parseLong(recordUri.getPathSegments().get(1));
		}
		
		val.put(WaypointDBHelper.KEY_LATITUDE, location.getLatitude());
		val.put(WaypointDBHelper.KEY_LONGTITUDE, location.getLongitude());
		val.put(WaypointDBHelper.KEY_ALTITUDE, location.getAltitude());
		val.put(WaypointDBHelper.KEY_ACCURACY, location.getAccuracy());
		val.put(WaypointDBHelper.KEY_SPEED, location.getSpeed());
		val.put(WaypointDBHelper.KEY_TIME, location.getTime());

		// add waypoint
		Uri waypointUri = resolver.insert(Uri.withAppendedPath(RecordProvider.WAYPOINT_CONTENT_URI,	""+recordId), val);
		// get record distance
		Cursor c = resolver.query(recordUri, new String[] {
				RecordDBHelper.KEY_DISTANCE
			}, null, null, null);
		float distance = 0;
		if (c.getCount() > 0) {
			c.moveToFirst();
			distance = c.getFloat(0);
		}
		c.close();
		c = resolver.query(Uri.withAppendedPath(RecordProvider.WAYPOINT_CONTENT_URI,	""+recordId), 
				new String[] {
					WaypointDBHelper.KEY_LATITUDE,
					WaypointDBHelper.KEY_LONGTITUDE,
					WaypointDBHelper.KEY_ALTITUDE
				}, null, null, WaypointDBHelper.KEY_TIME);
		
		if (c.getCount() > 0) {
			c.moveToFirst();
			Location firstLoc = new Location("GPS");
			firstLoc.setLatitude(c.getDouble(0));
			firstLoc.setLongitude(c.getDouble(1));
			firstLoc.setAltitude(c.getDouble(2));
			distance += location.distanceTo(firstLoc);
			val.clear();
			val.put(RecordDBHelper.KEY_DISTANCE, distance);
			// update distance
			int updated = resolver.update(Uri.withAppendedPath(RecordProvider.RECORD_CONTENT_URI, "/"+recordId), val, null, null);
			if (updated != 1)
				Toast.makeText(context, "distance update updated rows: "+updated, 1);
		}
		c.close();
		
		if (context instanceof RecordUI) {
			// notify UI
			((RecordUI)context).updateFields(recordUri, waypointUri);
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
		Toast.makeText(context, "Record stoped!", 3).show();
	}
}
