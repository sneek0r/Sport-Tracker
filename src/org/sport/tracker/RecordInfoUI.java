package org.sport.tracker;

import org.sport.tracker.utils.Record;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.maps.MapActivity;

public class RecordInfoUI extends MapActivity {
	
	long recordId = -1;
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
        	
    		String profile = 		record.profile;
    		long startTime = 		record.startTime;
    		long endTime = 			record.endTime;
    		float distance =		record.distance;
    		float avarageSpeed = 	record.avarageSpeed;
    		String comment = 		record.comment;
        	
        	TextView profile_tv = (TextView) findViewById(R.id.tv_profile);
        	profile_tv.setText(profile);
        	profile_tv.postInvalidate();

        	TextView time_tv = (TextView) findViewById(R.id.tv_time);
        	long timeSpan = endTime - startTime;
        	time_tv.setText(
        			Long.toString(timeSpan / 60 / 60 / 1000) + ":" +	// hours
        			Long.toString(timeSpan / 60 / 1000) + ":" +			// munutes
        			Long.toString(timeSpan / 1000));					// secunds
        	time_tv.postInvalidate();
            
        	TextView distance_tv = (TextView) findViewById(R.id.tv_distance);
        	distance_tv.setText(Math.round(distance) + " m");
        	distance_tv.postInvalidate();
        
        	TextView avarage_speed_tv = (TextView) findViewById(R.id.tv_avarage_speed);
        	avarage_speed_tv.setText(Math.round(avarageSpeed) + " m/s");
        	avarage_speed_tv.postInvalidate();
        	
        	Button button_showMap = (Button) findViewById(R.id.bt_show_on_map);
        	button_showMap.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startMapActivity();
				}
			});
        }
    }

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
	
	public void startMapActivity() {
		Intent mapIntent = new Intent(RecordInfoUI.this, MapUI.class);
		mapIntent.putExtra("id", recordId);
		startActivity(mapIntent);
	}
}
