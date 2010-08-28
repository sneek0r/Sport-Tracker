package org.sport.tracker;



import org.sport.tracker.utils.RecordDBHelper;
import org.sport.tracker.utils.WaypointDBHelper;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

public class RecordProvider extends ContentProvider {

	public static final Uri RECORD_CONTENT_URI = Uri.parse("content://org.sport.tracker/records");
	public static final Uri WAYPOINT_CONTENT_URI = Uri.parse("content://org.sport.tracker/waypoints");
	public static final int RECORDS = 0;
	public static final int RECORD_ID = 1;
	public static final int WAYPOINTS = 2;
	public static final int WAYPOINT_ID = 3;
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("org.sport.tracker", "records", RECORDS);
		uriMatcher.addURI("org.sport.tracker", "records/#", RECORD_ID);
		uriMatcher.addURI("org.sport.tracker", "waypoints/#", WAYPOINTS);
		uriMatcher.addURI("org.sport.tracker", "waypoints/#/#", WAYPOINT_ID);
	}

	private RecordDBHelper recordDbHelper;
	private WaypointDBHelper waypointDbHelber;
	
	@Override
	public boolean onCreate() {
		recordDbHelper = new RecordDBHelper(getContext(), RecordDBHelper.RECORDS_TABLE_NAME, null, 2);
		waypointDbHelber = new WaypointDBHelper(getContext(), WaypointDBHelper.WAYPOINT_TABLE_NAME, null, 2);
		return recordDbHelper.getWritableDatabase() == null ||
				waypointDbHelber.getWritableDatabase() == null ? false : true;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case RECORDS:
			return "vnd.android.cursor.dir/org.sport.tracker.utils.Record";
			
		case RECORD_ID:
			return "vnd.android.cursor.item/org.sport.tracker.utils.Record";
			
		case WAYPOINTS:
			return "vnd.android.cursor.dir/org.sport.tracker.utils.Waypoint";
			
		case WAYPOINT_ID:
			return "vnd.android.cursor.item/org.sport.tracker.utils.Waypoint";
			
		default:
			throw new IllegalArgumentException();
		}
	}
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {

		int count;
		switch (uriMatcher.match(uri)) {
			case RECORDS:
				count = recordDbHelper.delete(null, selection, selectionArgs);
				break;
				
			case RECORD_ID:
				count = recordDbHelper.delete(uri.getPathSegments().get(1), selection, selectionArgs);
				break;
				
			case WAYPOINTS:
				count = waypointDbHelber.delete(null, 
								WaypointDBHelper.KEY_RECORD_ID + " = " + 
								uri.getPathSegments().get(1) + 
								insertSelection(selection), selectionArgs);
				break;
				
			case WAYPOINT_ID:
				count = waypointDbHelber.delete(uri.getPathSegments().get(2), null, null);
				break;
				
			default:
				throw new IllegalArgumentException();
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		
		switch (uriMatcher.match(uri)) {
		case RECORDS:
			long recordId = recordDbHelper.insert(values);
			return Uri.withAppendedPath(RECORD_CONTENT_URI, Long.toString(recordId));
		
		case WAYPOINTS:
			String rId = uri.getPathSegments().get(1);
			if (!values.containsKey(WaypointDBHelper.KEY_RECORD_ID)) {
				values.put(WaypointDBHelper.KEY_RECORD_ID, rId);
			}
			long waypointId = waypointDbHelber.insert(values);
			return Uri.withAppendedPath(WAYPOINT_CONTENT_URI, rId+ "/" + Long.toString(waypointId));

		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		Cursor cursor;
		switch (uriMatcher.match(uri)) {
		case RECORDS:
			cursor = recordDbHelper.query(null, projection, selection, selectionArgs, sortOrder);
			break;
			
		case RECORD_ID:
			cursor = recordDbHelper.query(uri.getPathSegments().get(1), projection, null, null, sortOrder);
			break;
			
		case WAYPOINTS:
			String rId = uri.getPathSegments().get(1);
			if (TextUtils.isEmpty(selection)) selection = "";
			if (!selection.contains(WaypointDBHelper.KEY_RECORD_ID)) {
				selection = WaypointDBHelper.KEY_RECORD_ID + " = " + rId
							+ insertSelection(selection);
			}
			cursor = waypointDbHelber.query(null, projection, selection, selectionArgs, sortOrder);
			break;
			
		case WAYPOINT_ID:
			cursor = waypointDbHelber.query(uri.getPathSegments().get(2), projection, null, null, sortOrder);
			break;

		default:
			throw new IllegalArgumentException();
		}
		
		cursor.setNotificationUri(getContext().getContentResolver(), uri);
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		
		int count;
		switch (uriMatcher.match(uri)) {
		case RECORDS:
			count = recordDbHelper.update(null, values, selection, selectionArgs);
			break;
			
		case RECORD_ID:
			count = recordDbHelper.update(uri.getPathSegments().get(1), values, null, null);
			break;
			
		case WAYPOINTS:
			String rId = uri.getPathSegments().get(1);
			if (!values.containsKey(WaypointDBHelper.KEY_RECORD_ID)) {
				values.put(WaypointDBHelper.KEY_RECORD_ID, rId);
			}
			count = waypointDbHelber.update(null, values, selection, selectionArgs);
			break;
			
		case WAYPOINT_ID:
			count = waypointDbHelber.update(uri.getPathSegments().get(2), values, null, null);
			break;

		default:
			throw new IllegalArgumentException();
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
	
	String insertSelection(String selection) {
		return TextUtils.isEmpty(selection) ? "" : " AND (" + selection + ") ";
	}
}
