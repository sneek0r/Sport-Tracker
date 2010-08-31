package org.sport.tracker.utils;

import java.util.List;

import org.sport.tracker.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

/**
 * Map overlay. Show record track on map.
 * 
 * @author Waldemar Smirnow
 *
 */
public class WaypointsOverlay extends Overlay {

	/**
	 * Direction width (latitude).
	 */
	public static final int DIRECTION_WIDTH = 0;
	/**
	 * Direction height (longtitude).
	 */
	public static final int DIRECTION_HEIGHT = 1;
	
	/**
	 * Context.
	 */
	Context context;
	/**
	 * List with waypoints data.
	 */
	List<GeoPoint> waypoints;
	/**
	 * Track was drawn (=true).
	 */
	boolean drawn = false;
	
	/**
	 * Constructor.
	 * @param context Context
	 * @param waypoints Waypoints
	 */
	public WaypointsOverlay(Context context, List<GeoPoint> waypoints) {
		super();
		this.context = context;
		this.waypoints = waypoints;
	}
	
	@Override
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {

		if (waypoints.size() > 1) {
			if (shadow) {
				GeoPoint lastWayPoint = null;
				Point lastPoint = null;
				Point point = new Point();
				Path path = new Path();
				
				for (GeoPoint waypoint : waypoints) {
					mapView.getProjection().toPixels(waypoint, point);
					
					if (lastWayPoint == null) {
						lastWayPoint = waypoint;
						lastPoint = point;
						path.moveTo(lastPoint.x, lastPoint.y);
						continue;
					}
					path.lineTo(point.x, point.y);
				}
				
				Paint paint = new Paint();
				paint.setColor(Color.RED);
				paint.setStyle(Paint.Style.STROKE);
				paint.setStrokeWidth(3);
				paint.setAntiAlias(true);
				canvas.drawPath(path, paint);
				
				Bitmap flagStart = BitmapFactory.decodeResource(context.getResources(), R.drawable.flag_red);
				Bitmap flagEnd = BitmapFactory.decodeResource(context.getResources(), R.drawable.flag_green);
				
				mapView.getProjection().toPixels(waypoints.get(0), point);
				canvas.drawBitmap(flagStart, point.x, point.y - flagEnd.getHeight(), null);
		
				mapView.getProjection().toPixels(waypoints.get(waypoints.size()-1), point);
				canvas.drawBitmap(flagEnd, point.x, point.y - flagEnd.getHeight(), null);
			} else {
				// TODO paint flag shadow
			}
			super.draw(canvas, mapView, shadow);
		}
		
		if (!drawn && waypoints.size() > 0) {
			GeoPoint latMin = getMinGeoPoint(waypoints, DIRECTION_WIDTH);
			GeoPoint lonMin = getMinGeoPoint(waypoints, DIRECTION_HEIGHT);
			GeoPoint latMax = getMaxGeoPoint(waypoints, DIRECTION_WIDTH);
			GeoPoint lonMax = getMaxGeoPoint(waypoints, DIRECTION_HEIGHT);
			mapView.getController().animateTo(getCentralGeoPoint(latMin.getLatitudeE6(), 
					latMax.getLatitudeE6(), lonMin.getLongitudeE6(), lonMax.getLongitudeE6()));
			mapView.getController().zoomToSpan(
					(latMax.getLatitudeE6() - latMin.getLatitudeE6())+20,
					(lonMax.getLongitudeE6() - lonMin.getLongitudeE6())+20);
			drawn = true;
		}
		
    }
	
	/**
	 * Get geopoint with min value given by direction.
	 * @param waypoints Waypoints
	 * @param direction Direction (see WaypointsOverlay.DIRECTION_*)
	 * @return geopoint with min value
	 */
	static GeoPoint getMinGeoPoint(List<GeoPoint> waypoints, int direction) {
		int min = Integer.MAX_VALUE;
		GeoPoint minWp = null;
		switch (direction) {
		case DIRECTION_WIDTH:
			for (GeoPoint p : waypoints) {
				if (Math.min(p.getLatitudeE6(), min) == p.getLatitudeE6()) {
					minWp = p;
					min = minWp.getLatitudeE6();
				}
			}
			return minWp;
			
		case DIRECTION_HEIGHT:
			for (GeoPoint p : waypoints) {
				if (Math.min(p.getLongitudeE6(), min) == p.getLongitudeE6()) {
					minWp = p;
					min = minWp.getLongitudeE6();
				}
			}
			return minWp;
		
		default:
			return null;
		}
	}
	
	/**
	 * Get geopoint with max value given by direction.
	 * @param waypoints Waypoints
	 * @param direction Direction (see WaypointsOverlay.DIRECTION_*)
	 * @return waypoint with max value
	 */
	static GeoPoint getMaxGeoPoint(List<GeoPoint> waypoints, int direction) {
		int max = Integer.MIN_VALUE;
		GeoPoint maxWp = null;
		switch (direction) {
		case DIRECTION_WIDTH:
			for (GeoPoint p : waypoints) {
				if (Math.max(p.getLatitudeE6(), max) == p.getLatitudeE6()) {
					maxWp = p;
					max = maxWp.getLatitudeE6();
				}
			}
			return maxWp;
			
		case DIRECTION_HEIGHT:
			for (GeoPoint p : waypoints) {
				if (Math.max(p.getLongitudeE6(), max) == p.getLongitudeE6()) {
					maxWp = p;
					max = maxWp.getLongitudeE6();
				}
			}
			return maxWp;
		
		default:
			return null;
		}
	}
	
	/**
	 * Get central geopoint.
	 * @param minLatE6 Min Latitude
	 * @param maxLatE6 Max latitude
	 * @param minLonE6 Min longtitude
	 * @param maxLonE6 Max longtitude
	 * @return Central geopoint
	 */
	GeoPoint getCentralGeoPoint(int minLatE6, int maxLatE6, int minLonE6, int maxLonE6) {
		
		int centralLat = minLatE6 + ((maxLatE6 - minLatE6) / 2);
		int centralLon = minLonE6 + ((maxLonE6 - minLonE6) / 2);
		return new GeoPoint(centralLat, centralLon);
	}

}
