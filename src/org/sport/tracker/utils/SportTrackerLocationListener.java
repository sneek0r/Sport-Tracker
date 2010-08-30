package org.sport.tracker.utils;

import org.sport.tracker.RecordUI;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class SportTrackerLocationListener implements LocationListener {
	
	Context context;
	public Record record;
	
	public SportTrackerLocationListener(Context context, String profile, long time) {
		this.context = context;
		this.record = new Record(context, profile, time);
		this.record.insertDB();
		
		Criteria criteria = new Criteria();
		criteria.setPowerRequirement(Criteria.POWER_HIGH);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(true);
		criteria.setSpeedRequired(true);
		
		LocationManager manager = 
			(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		String provider = manager.getBestProvider(criteria, true);
		// TODO: enable provider if disabled!
		manager.requestLocationUpdates(provider, 3000, 10, this);
	}

	public long stopRecord(long time) {
		LocationManager manager = 
			(LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		
		manager.removeUpdates(this);
		record.endTime = time;
		record.updateDB();
		return record.recordId;
	}
	
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
