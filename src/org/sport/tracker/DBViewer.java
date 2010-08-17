package org.sport.tracker;

import java.util.Date;

import org.sport.tracker.utils.RecordDBHelper;
import org.sport.tracker.utils.WaypointDBHelper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

public class DBViewer extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db);
        
        TextView tv = (TextView) findViewById(R.id.tv);
        
        ContentResolver resolver = getContentResolver();
        ContentValues val = new ContentValues();
        val.put(RecordDBHelper.KEY_PROFILE, "test");
        val.put(RecordDBHelper.KEY_START_TIME, new Date().getTime());
        
        String text = "";
        Uri uri = resolver.insert(RecordProvider.RECORD_CONTENT_URI, val);
        text += uri.getPath() + "\n";
        
        Cursor c = resolver.query(RecordProvider.RECORD_CONTENT_URI, null, null, null, null);
        c.moveToFirst();
        do {
        	for (int col = 1; col < c.getColumnCount(); col++) {
        		text += c.getString(col) + "; ";
        	}
        	text += "\n";
        } while (c.moveToNext());
        c.close();
        
        resolver.delete(uri, null, null);
        
        val = new ContentValues();
        val.put(WaypointDBHelper.KEY_RECORD_ID, uri.getPathSegments().get(1));
        val.put(WaypointDBHelper.KEY_LATITUDE , 0.1);
        val.put(WaypointDBHelper.KEY_LONGTITUDE , 0.2);
        val.put(WaypointDBHelper.KEY_ALTITUDE , 0.3);
        val.put(WaypointDBHelper.KEY_ACCURACY, 0);
        val.put(WaypointDBHelper.KEY_TIME, new Date().getTime());
        
        uri = resolver.insert(RecordProvider.WAYPOINT_CONTENT_URI, val);
        text += "\n\n" + uri.getPath() + "\n";
        
        c = null;
        c = resolver.query(RecordProvider.WAYPOINT_CONTENT_URI, null, null, null, null);
        c.moveToFirst();
        do {
        	for (int col = 1; col < c.getColumnCount(); col++) {
        		text += c.getString(col) + "; ";
        	}
        	text += "\n";
        } while (c.moveToNext());
        c.close();
        
        resolver.delete(uri, "", new String[]{});
        
        tv.setText(text);
        tv.postInvalidate();
    }
}