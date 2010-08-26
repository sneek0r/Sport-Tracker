package org.sport.tracker.utils;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

public class WaypointsOverlay extends Overlay {

	public static final int DIRECTION_WIDTH = 0;
	public static final int DIRECTION_HEIGHT = 1;
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
		
		GeoPoint lastWayPoint = null;
		Point lastPoint = null;

		Point point = new Point();
		Path path = new Path();
//		Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.punkt);
		for (GeoPoint waypoint : waypoints) {
			mapView.getProjection().toPixels(waypoint, point);
//			canvas.drawBitmap(bmp, point.x, point.y - 50, null);
			
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
		canvas.save();
		
		if (waypoints.size() > 0) {
//			GeoPoint latMin = getMinGeoPoint(waypoints, DIRECTION_WIDTH);
//			GeoPoint lonMin = getMinGeoPoint(waypoints, DIRECTION_HEIGHT);
//			GeoPoint latMax = getMaxGeoPoint(waypoints, DIRECTION_WIDTH);
//			GeoPoint lonMax = getMaxGeoPoint(waypoints, DIRECTION_HEIGHT);
//			mapView.getController().zoomToSpan(latMax.getLatitudeE6() - latMin.getLatitudeE6(),
//					lonMax.getLongitudeE6() - lonMin.getLongitudeE6());
			mapView.getController().animateTo(waypoints.get(waypoints.size() / 2));
			mapView.getController().setZoom(17);
		}
		return true;
    }
	
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
	
	GeoPoint getCentralGeoPoint(List<GeoPoint> waypoints, int direction, int minValue, int maxValue) {
		
		int central = ((maxValue - minValue) / 2) + minValue;
		int centralRadius = ((central - minValue) / 3);
		int currentDist = Integer.MAX_VALUE;
		GeoPoint centralWp = null;
		switch (direction) {
		case DIRECTION_WIDTH:
			for (GeoPoint p : waypoints) {
				int dist = Math.abs(p.getLatitudeE6() - central);
				if (dist < centralRadius) {
					if ((currentDist - central) > dist ) {
						 centralWp = p;
						 currentDist = dist;
					}
				}
			}
			return centralWp;
			
		case DIRECTION_HEIGHT:
			for (GeoPoint p : waypoints) {
				int dist = Math.abs(p.getLongitudeE6() - central);
				if (dist < centralRadius) {
					if (centralWp != null && (currentDist - central) > dist ) {
						 centralWp = p;
						 currentDist = dist;
					}
				}
			}
			return centralWp;
			
		default:
			return null;
		}
	}

}
