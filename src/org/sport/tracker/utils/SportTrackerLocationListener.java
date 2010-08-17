package org.sport.tracker.utils;

import org.sport.tracker.RecordUI;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;


public class SportTrackerLocationListener implements LocationListener {

	public Context context;
	public Record record;
	
	public SportTrackerLocationListener(Context context, Record record) {
		this.context = context;
		this.record = record;
		LocationManager locManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 5, this);
	}
	
	@Override
	public void onLocationChanged(Location location) {
		
		if (record != null) {
			record.addNewWaypoint(new Waypoint(location));
		}
		
		if (context instanceof RecordUI) {
			((RecordUI)context).updateFields(record);
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
		LocationManager locManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
		locManager.removeUpdates(this);
		Toast.makeText(context, "Record stoped!", 3).show();
	}
}
