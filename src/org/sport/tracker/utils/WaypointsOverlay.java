package org.sport.tracker.utils;

import java.util.List;

import android.content.Context;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.Overlay;

public class WaypointsOverlay extends Overlay {

	Context context;
	List<GeoPoint> waypoints;
	
	public WaypointsOverlay(Context context, List<GeoPoint> waypoints) {
		super();
		this.context = context;
		this.waypoints = waypoints;
	}
}
