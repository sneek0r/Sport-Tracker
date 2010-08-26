package org.sport.tracker.utils;

import java.util.List;

import org.sport.tracker.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class WaypointsOverlay extends Overlay {

	Context context;
	List<GeoPoint> waypoints;
	
	public WaypointsOverlay(Context context, List<GeoPoint> waypoints) {
		super();
		this.context = context;
		this.waypoints = waypoints;
	}
	
	@Override
    public boolean draw(Canvas canvas, MapView mapView, boolean shadow, long when) {
		super.draw(canvas, mapView, shadow); 
		
		
		for (GeoPoint waypoint : waypoints) {
			Point point = new Point();
			mapView.getProjection().toPixels(waypoint, point);
			Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.punkt);
			canvas.drawBitmap(bmp, point.x, point.y - 50, null);
			
		}
		
		if (waypoints.size() > 0) mapView.getController().animateTo(waypoints.get(0));
		return true;
    }

}
