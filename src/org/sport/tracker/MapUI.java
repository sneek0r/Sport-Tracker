package org.sport.tracker;

import java.util.ArrayList;
import java.util.List;

import org.sport.tracker.utils.Waypoint;
import org.sport.tracker.utils.WaypointDBHelper;
import org.sport.tracker.utils.WaypointsOverlay;

import android.os.Bundle;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

/**
 * Map activity to show record waypoints on map.
 * 
 * @author Waldemar Smirnow
 *
 */
public class MapUI extends MapActivity {

	/**
	 * Record id.
	 */
	long recordId;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.trackmap);
        
        // get record to show waypoints
        Bundle extras = getIntent().getExtras();
        if( extras.containsKey("id") ) {
        	recordId = extras.getLong("id");
        	
        	List<Waypoint> waypoints = Waypoint.queryDB(this, recordId, null, null, WaypointDBHelper.KEY_TIME);
        	ArrayList<GeoPoint> points = new ArrayList<GeoPoint>(waypoints.size());
        	
        	// convert Waypoint's to GeoPoint's
        	for (Waypoint wp : waypoints) {
        		GeoPoint geopoint = new GeoPoint(
        				(int) (wp.latitude * 1E6),
        				(int) (wp.longtitude * 1E6));
        		
        		points.add(geopoint);
        	}
    		
        	// fill map with waypoints
    		MapView mapView= (MapView) findViewById(R.id.map_view);
    		mapView.setBuiltInZoomControls(true);
    		mapView.getOverlays().add(new WaypointsOverlay(mapView.getContext(), points));
        }
	}
	
    /**
     * No route shown (always return false).
     */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
