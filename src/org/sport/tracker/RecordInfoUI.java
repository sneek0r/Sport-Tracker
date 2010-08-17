package org.sport.tracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class RecordInfoUI extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recordinfo);
        
        // set profile
        Bundle extras = getIntent().getExtras();
        if( extras.containsKey("profile") ) {
        	TextView profile_tv = (TextView) findViewById(R.id.tv_profile);
        	profile_tv.setText(extras.getCharSequence("profile"));
        	profile_tv.postInvalidate();
        }
        
        if( extras.containsKey("time") ) {
        	TextView time_tv = (TextView) findViewById(R.id.tv_time);
        	Long time = (Long)extras.get("time");
        	time_tv.setText(
        			Long.toString(time / 60 / 60 / 1000) + ":" +	// hours
        			Long.toString(time / 60 / 1000) + ":" +			// munutes
        			Long.toString(time / 1000));					// secunds
        	time_tv.postInvalidate();
        }
        
        if( extras.containsKey("distance") ) {
        	TextView distance_tv = (TextView) findViewById(R.id.tv_distance);
        	Float distance = (Float)extras.get("distance");
        	distance_tv.setText(distance + " m");
        	distance_tv.postInvalidate();
        }
        
        if( extras.containsKey("avarage_speed") ) {
        	TextView avarage_speed_tv = (TextView) findViewById(R.id.tv_avarage_speed);
        	Float avarageSpeed = (Float)extras.get("avarage_speed");
        	avarage_speed_tv.setText(avarageSpeed + " m/s");
        	avarage_speed_tv.postInvalidate();
        }
    }
}
