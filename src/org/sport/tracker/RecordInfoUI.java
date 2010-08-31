package org.sport.tracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.sport.tracker.utils.Record;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * RecordInfo activity, to show record data.
 * 
 * @author Waldemar Smirnow
 *
 */
public class RecordInfoUI extends Activity {
	
	/**
	 * Record ID.
	 */
	long recordId = 0;
	/**
	 * Record to show.
	 */
	Record record;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recordinfo);
        
        // set profile
        Bundle extras = getIntent().getExtras();
        if( extras.containsKey("id") ) {
        	recordId = extras.getLong("id");
        	try {
        		record = Record.queryDB(this, recordId);
        	} catch (IllegalArgumentException e) {
        		Log.d(getClass().getName().toString(), "record with id " + recordId + " does not exist");
        		return;
        	}
        	
        	// get data
    		String profile = 		record.profile;
    		long startTime = 		record.startTime;
    		long endTime = 			record.endTime;
    		float distance =		record.distance;
    		float avarageSpeed = 	record.averageSpeed;
    		String comment = 		record.comment;
        	
    		// fill profile textview
        	TextView profile_tv = (TextView) findViewById(R.id.tv_profile);
        	profile_tv.setText(profile);
        	profile_tv.postInvalidate();

        	// fill recorded time textview
        	TextView time_tv = (TextView) findViewById(R.id.tv_time);
        	long timeSpan = endTime - startTime;
        	Date time = new Date(timeSpan-TimeZone.getDefault().getOffset(timeSpan));
        	SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
        	time_tv.setText(dateFormatter.format(time));
        	time_tv.postInvalidate();
            
        	// fill distance textview
        	TextView distance_tv = (TextView) findViewById(R.id.tv_distance);
        	distance_tv.setText(Math.round(distance) + " m");
        	distance_tv.postInvalidate();
        
        	// fill avarage speed textview
        	TextView avarage_speed_tv = (TextView) findViewById(R.id.tv_average_speed);
        	avarage_speed_tv.setText(Math.round(avarageSpeed) + " m/s");
        	avarage_speed_tv.postInvalidate();
        	
        	// fill comment textview
//        	TextView comment_tv = (TextView) findViewById(R.id.tv_comment);
//        	comment_tv.setText(comment);
//        	comment_tv.postInvalidate();
        	
        	// add handler, to delete record
        	Button bt_delete_reord = (Button) findViewById(R.id.bt_delete_record);
        	bt_delete_reord.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					deleteReord();
				}
			});
        	
        	// add handler, to show record on map
        	Button bt_show_map = (Button) findViewById(R.id.bt_show_on_map);
        	bt_show_map.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startMapActivity();
				}
			});
        }
    }
    
    /**
     * Delete record and close activity.
     */
    public void deleteReord() {
    	if (Record.deleteDB(this, recordId)) {
    		this.finish();
    	}
    }
	
    /**
     * Start MapUI activity with shown record (id).
     */
	public void startMapActivity() {
		Intent mapIntent = new Intent(RecordInfoUI.this, MapUI.class);
		mapIntent.putExtra("id", recordId);
		startActivity(mapIntent);
	}
}
