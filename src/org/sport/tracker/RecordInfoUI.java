package org.sport.tracker;

import org.sport.tracker.utils.RecordDBHelper;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class RecordInfoUI extends Activity {
	
	long recordId = -1;
	Uri recordUri;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recordinfo);
        
        // set profile
        Bundle extras = getIntent().getExtras();
        if( extras.containsKey("id") ) {
        	recordId = extras.getLong("id");
        	Log.d(getClass().toString(),"recordId: " + recordId);
        	
        	recordUri = Uri.parse(RecordProvider.RECORD_CONTENT_URI + "/" + recordId);
        	ContentResolver resolver = getContentResolver();
        	String[] projection = new String [] {
            		RecordDBHelper.KEY_PROFILE,
            		RecordDBHelper.KEY_START_TIME,
            		RecordDBHelper.KEY_END_TIME,
            		RecordDBHelper.KEY_DISTANCE,
            		RecordDBHelper.KEY_AVARAGE_SPEED,
            		RecordDBHelper.KEY_COMMENT
            	};
        	Cursor c = resolver.query(recordUri, projection, null, null, null);
        	
        	if (c.getCount() < 1 || c.getColumnCount() != projection.length) return;
        		
    		c.moveToFirst();
    		int coll = 0;
    		String profile = 		c.getString(coll++);
    		long startTime = 		c.getLong(coll++);
    		long endTime = 			c.getLong(coll++);
    		float distance =		c.getFloat(coll++);
    		float avarageSpeed = 	c.getFloat(coll++);
    		String comment = 		c.getString(coll++);
    		c.close();
        	
        	TextView profile_tv = (TextView) findViewById(R.id.tv_profile);
        	profile_tv.setText(profile);
        	profile_tv.postInvalidate();

//            if( extras.containsKey("time") ) {
//            	TextView time_tv = (TextView) findViewById(R.id.tv_time);
//            	Long time = (Long)extras.get("time");
//            	time_tv.setText(
//            			Long.toString(time / 60 / 60 / 1000) + ":" +	// hours
//            			Long.toString(time / 60 / 1000) + ":" +			// munutes
//            			Long.toString(time / 1000));					// secunds
//            	time_tv.postInvalidate();
//            }
            
        	TextView distance_tv = (TextView) findViewById(R.id.tv_distance);
        	distance_tv.setText(distance + " m");
        	distance_tv.postInvalidate();
        
        	TextView avarage_speed_tv = (TextView) findViewById(R.id.tv_avarage_speed);
        	avarage_speed_tv.setText(avarageSpeed + " m/s");
        	avarage_speed_tv.postInvalidate();
        }
    }
}
