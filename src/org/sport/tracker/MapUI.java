package org.sport.tracker;

import java.util.ArrayList;

import org.sport.tracker.utils.WaypointDBHelper;
import org.sport.tracker.utils.WaypointsOverlay;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;

public class MapUI extends MapActivity {

	long recordId;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.trackmap);
        
        Bundle extras = getIntent().getExtras();
        if( extras.containsKey("id") ) {
        	recordId = extras.getLong("id");
        	
        	ContentResolver resolver = getContentResolver();
        	Cursor c = resolver.query(Uri.withAppendedPath(RecordProvider.WAYPOINT_CONTENT_URI, ""+recordId), 
        			null, null, null, WaypointDBHelper.KEY_TIME);
        	
        	ArrayList<GeoPoint> points = new ArrayList<GeoPoint>(c.getCount());
        	if (c.getCount() < 1) {
        		Log.d(getClass().getName().toString(), "record with id "+ recordId + " has no waypoints");
        		return;
        	}
        	c.moveToFirst();
    		while (!c.isAfterLast()) {
    			GeoPoint point= new GeoPoint(
    					(int)(c.getDouble(c.getColumnIndex(WaypointDBHelper.KEY_LATITUDE)) * 1E6),
    					(int)(c.getDouble(c.getColumnIndex(WaypointDBHelper.KEY_LONGTITUDE)) * 1E6));
    			
    			points.add(point);
    			c.moveToNext();
    		}
    		c.close();
    		
    		MapView mapView= (MapView) findViewById(R.id.map_view);
    		mapView.setBuiltInZoomControls(true);
    		mapView.getOverlays().add(new WaypointsOverlay(this, points));
    		mapView.postInvalidate();
        }
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
