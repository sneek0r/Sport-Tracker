package org.sport.tracker.utils;

import org.sport.tracker.RecordUI;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * LocationListener. Create an record and fill it with Waypoint data from android location service.
 * 
 * @author Waldemar Smirnow
 *
 */
public class SportTrackerLocationListener implements LocationListener {
	
	/**
	 * Context.
	 */
	Context context;
	/**
	 * Record.
	 */
	public Record record;
	
	/**
	 * Constructor. Create an Record and request for location updates.
	 * @param context Context
	 * @param profile Profile
	 * @param time Start time
	 */
	public SportTrackerLocationListener(Context context, String profile, long time) {
		this.context = context;
		this.record = new Record(context, profile, time);
		this.record.insertDB();
		
		// search best provider
		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(true);
		criteria.setSpeedRequired(true);
		
		// request location updates
		LocationManager manager = 
			(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		String provider = manager.getBestProvider(criteria, true);
		// TODO: enable provider if disabled!
		manager.requestLocationUpdates(provider, 3000, 10, this);
	}

	/**
	 * Stop location updates and "close" record.
	 * @param time End time
	 * @return Record id
	 */
	public long stopRecord(long time) {
		LocationManager manager = 
			(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		manager.removeUpdates(this);
		record.endTime = time;
		record.updateDB();
		return record.recordId;
	}
	
	/**
	 * Create new waypoint and update record.
	 */
	@Override
	public void onLocationChanged(Location location) {
		
		if (record.addWaypoint(location)) {
			if (context instanceof RecordUI) {
				((RecordUI) context).updateFields(
						record, record.getWaypoint(record.getWaypointsCount()-1));
			}
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
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

}
