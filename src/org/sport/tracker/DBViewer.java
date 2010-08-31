package org.sport.tracker;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

/**
 * Test view to show database content.
 * 
 * @author Waldemar Smirnow
 */
public class DBViewer extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db);
        
        TextView tv = (TextView) findViewById(R.id.tv);
        tv.setMovementMethod(ScrollingMovementMethod.getInstance());
        
        ContentResolver resolver = getContentResolver();
        Cursor c = resolver.query(SportTrackerProvider.RECORD_CONTENT_URI, null, null, null, null);
        String text = "Records:\n";
        
        c.moveToFirst();
        while (!c.isAfterLast()) {
        	for (int i = 0; i < c.getColumnCount(); i++) {
        		text += c.getColumnName(i) + ": " + c.getString(i) + ", ";
        	}
        	text += "\n";
        	c.moveToNext();
        }
        c.close();
        
        c = resolver.query(Uri.withAppendedPath(SportTrackerProvider.WAYPOINT_CONTENT_URI, "0"), null, null, null, null);
        text += "\nWaypoints:\n";
        
        c.moveToFirst();
        while (!c.isAfterLast()) {
        	for (int i = 0; i < c.getColumnCount(); i++) {
        		text += c.getColumnName(i) + ": " + c.getString(i) + ", ";
        	}
        	text += "\n";
        	c.moveToNext();
        }
        c.close();
        
        tv.setText(text);
        tv.postInvalidate();
    }
}