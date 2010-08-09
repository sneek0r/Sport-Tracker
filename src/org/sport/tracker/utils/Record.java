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
	public List<SportTrackerWaypoint> track;
	/**
	 * Record comment
	 */
	public String comment;
	
	public Record(String profile) {
		this.profile = profile;
		this.track = new LinkedList<SportTrackerWaypoint>();
	}
	
	public boolean addNewWaypoint(SportTrackerWaypoint waypoint) {
		Location lastLocation;
		if (track.size() > 1) {
			lastLocation = ((LinkedList<SportTrackerWaypoint>) track).getLast().getLocation();
		} else {
			lastLocation = waypoint.getLocation();
		}
		
		boolean added = track.add(waypoint);
		if (added) {
			distance += waypoint.getLocation().distanceTo(lastLocation);
			
			Location firstLocation = ((LinkedList<SportTrackerWaypoint>) track).getLast().getLocation();
			long recordedTime = lastLocation.getTime() - firstLocation.getTime();
			avarageSpeed = distance / (recordedTime * 1000);
		}
		return added;
	}
}
