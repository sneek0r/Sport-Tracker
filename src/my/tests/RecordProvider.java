package my.tests;

import my.tests.utils.RecordDBHelper;
import my.tests.utils.WaypointDBHelper;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class RecordProvider extends ContentProvider {

	public static final Uri RECORD_CONTENT_URI = Uri.parse("content://my.tests.provider/records");
	public static final Uri WAYPOINT_CONTENT_URI = Uri.parse("content://my.tests.provider/waypoints");
	public static final int RECORDS = 0;
	public static final int RECORD_ID = 1;
	public static final int WAYPOINTS = 2;
	public static final int WAYPOINT_ID = 3;
	private static final UriMatcher uriMatcher;
	static {
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI("my.tests.provider", "records", RECORDS);
		uriMatcher.addURI("my.tests.provider", "records/#", RECORD_ID);
		uriMatcher.addURI("my.tests.provider", "waypoints/", WAYPOINTS);
		uriMatcher.addURI("my.tests.provider", "waypoints/#", WAYPOINT_ID);
	}

	private RecordDBHelper recordDbHelper;
	private WaypointDBHelper waypointDbHelber;
	
	@Override
	public boolean onCreate() {
		recordDbHelper = new RecordDBHelper(getContext(), RecordDBHelper.RECORDS_TABLE_NAME, null, 1);
		waypointDbHelber = new WaypointDBHelper(getContext(), WaypointDBHelper.WAYPOINT_TABLE_NAME, null, 1);
		return recordDbHelper.getWritableDatabase() == null ||
				waypointDbHelber.getWritableDatabase() == null ? false : true;
	}
	
	@Override
	public String getType(Uri uri) {
		switch (uriMatcher.match(uri)) {
		case RECORDS:
			return "vnd.android.cursor.dir/my.tests.record";
			
		case RECORD_ID:
			return "vnd.android.cursor.item/my.tests.record";
			
		case WAYPOINTS:
			return "vnd.android.cursor.dir/my.tests.record.waypoint";
			
		case WAYPOINT_ID:
			return "vnd.android.cursor.item/my.tests.record.waypoint";
			
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
				count = waypointDbHelber.delete(null, selection, selectionArgs);
				break;
				
			case WAYPOINT_ID:
				count = waypointDbHelber.delete(uri.getPathSegments().get(1), null, null);
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
			long waypointId = waypointDbHelber.insert(values);
			return Uri.withAppendedPath(WAYPOINT_CONTENT_URI, Long.toString(waypointId));

		default:
			throw new IllegalArgumentException();
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		
		
		String recordId = null;
		Cursor cursor;
		switch (uriMatcher.match(uri)) {
		case RECORDS:
			cursor = recordDbHelper.query(null, projection, selection, selectionArgs, sortOrder);
			break;
			
		case RECORD_ID:
			cursor = recordDbHelper.query(uri.getPathSegments().get(1), projection, null, null, sortOrder);
			break;
			
		case WAYPOINTS:
			cursor = waypointDbHelber.query(null, projection, selection, selectionArgs, sortOrder);
			break;
			
		case WAYPOINT_ID:
			cursor = waypointDbHelber.query(uri.getPathSegments().get(1), projection, null, null, sortOrder);
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
			count = waypointDbHelber.update(null, values, selection, selectionArgs);
			break;
			
		case WAYPOINT_ID:
			count = waypointDbHelber.update(uri.getPathSegments().get(1), values, null, null);
			break;

		default:
			throw new IllegalArgumentException();
		}
		
		getContext().getContentResolver().notifyChange(uri, null);
		return count;
	}
}
