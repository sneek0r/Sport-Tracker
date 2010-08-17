package org.sport.tracker.utils;

import android.location.Location;
import android.location.LocationManager;

public class Waypoint {
	
	public final double latitude;
	public final double longitude;
	public final double altitude;
	public final float 	accuracy;
	public final long 	time;
	public final float 	speed;
	
	public Waypoint(double latitude, double longtitude, 
								double altitude, float accuracy, 
								long time, float speed) {
		
		this.latitude = latitude;
		this.longitude = longtitude;
		this.altitude = altitude;
		this.accuracy = accuracy;
		this.time = time;
		this.speed = speed;
	}
	
	public Waypoint(Location location) {
		// get location stored data
		this.latitude = 	location.getLatitude();
		this.longitude = 	location.getLongitude();
		this.altitude = 	location.getLatitude();
		this.accuracy = 	location.getAccuracy();
		this.time = 		location.getTime();
		this.speed = 		location.getSpeed();
	}
	
	public Location getLocation() {
		// create location with own data
		Location location = new Location(LocationManager.GPS_PROVIDER);
		location.setLatitude(latitude);
		location.setLongitude(longitude);
		location.setAltitude(altitude);
		location.setAccuracy(accuracy);
		location.setTime(time);
		return location;
	}
}
