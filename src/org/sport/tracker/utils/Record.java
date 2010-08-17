package org.sport.tracker.utils;

import java.util.LinkedList;
import java.util.List;

import android.location.Location;

public class Record {

	/**
	 * Record profile
	 */
	public final String profile;
	/**
	 * Distance recorded in meters
	 */
	public float distance;
	/**
	 * Avarage speed
	 */
	public float avarageSpeed;
	/**
	 * List with recorded waypoints
	 */
	public List<Waypoint> track;
	/**
	 * Record comment
	 */
	public String comment;
	
	public Record(String profile) {
		this.profile = profile;
		this.track = new LinkedList<Waypoint>();
		this.distance = 0;
		this.avarageSpeed = 0;
		this.comment = "";
	}
	
	public boolean addNewWaypoint(Waypoint waypoint) {
		Location lastLocation;
		if (track.size() > 1) {
			lastLocation = ((LinkedList<Waypoint>) track).getLast().getLocation();
		} else {
			lastLocation = waypoint.getLocation();
		}
		
		boolean added = track.add(waypoint);
		if (added) {
			distance += waypoint.getLocation().distanceTo(lastLocation);
			
			Location firstLocation = ((LinkedList<Waypoint>) track).getLast().getLocation();
			long recordedTime = lastLocation.getTime() - firstLocation.getTime();
			avarageSpeed = distance / (recordedTime * 1000);
		}
		return added;
	}
}
