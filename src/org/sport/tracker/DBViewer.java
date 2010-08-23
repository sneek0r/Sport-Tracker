package org.sport.tracker;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
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
        Cursor c = resolver.query(RecordProvider.RECORD_CONTENT_URI, null, null, null, null);
        String text = "Records:\n";
        
        c.moveToFirst();
        while (!c.isAfterLast()) {
        	for (int i = 0; i < c.getColumnCount(); i++) {
        		text += c.getColumnName(i) + ": " + c.getString(i) + ", ";
        	}
        	text += "\n";
        }
        c.close();
        
        c = resolver.query(RecordProvider.WAYPOINT_CONTENT_URI, null, null, null, null);
        text = "Waypoints:\n";
        
        c.moveToFirst();
        while (!c.isAfterLast()) {
        	for (int i = 0; i < c.getColumnCount(); i++) {
        		text += c.getColumnName(i) + ": " + c.getString(i) + ", ";
        	}
        	text += "\n";
        }
        c.close();
        
        tv.setText(text);
        tv.postInvalidate();
    }
}