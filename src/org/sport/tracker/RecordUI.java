package org.sport.tracker;

import java.util.LinkedList;

import org.sport.tracker.utils.Record;
import org.sport.tracker.utils.SportTrackerLocationListener;
import org.sport.tracker.utils.SportTrackerWaypoint;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RecordUI extends Activity {
	
	public SportTrackerLocationListener locationListener;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);
        
        // set profile
        Bundle extras = getIntent().getExtras();
        if( extras.containsKey("profile") ) {
        	TextView profile_tv = (TextView) findViewById(R.id.tv_profile);
        	profile_tv.setText(extras.getCharSequence("profile"));
        	profile_tv.postInvalidate();
        }
        
        // add record start button listener
        Button record_stop = (Button) findViewById(R.id.bt_stop_record);
        record_stop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				recordStop();
			}
		});
        
        // set LocationListener
        locationListener = new SportTrackerLocationListener(this, 
        			new Record(extras.getCharSequence("profile").toString()));
    }
    
    public void recordStop(){
    	TextView profile_tv = (TextView) findViewById(R.id.tv_profile);
    	final String profile = (String) profile_tv.getText();
    	
    	// start recordinfo Activity
    	Intent recordinfo_intent = new Intent(RecordUI.this, RecordInfoUI.class);
    	recordinfo_intent.putExtra("profile", profile);
    	startActivity(recordinfo_intent);
    }
    
    public void updateFields(Record record) {
    	Location lastLocation = ((LinkedList<SportTrackerWaypoint>) record.track).getLast().getLocation();
    	final double latitude = lastLocation.getLatitude();
    	final double longitude = lastLocation.getLongitude();
    	final double altitude = lastLocation.getAltitude();
    	final float accuracy = lastLocation.getAccuracy();
    	final float speed = lastLocation.getSpeed();
    	final float avarageSpeed = record.avarageSpeed;
    	final float distance = record.distance;
    	
    	TextView tv_latitude = (TextView) findViewById(R.id.tv_lat);
    	tv_latitude.setText(Location.convert(latitude, Location.FORMAT_MINUTES));
    	tv_latitude.postInvalidate();
    	
    	TextView tv_longtitude = (TextView) findViewById(R.id.tv_lon);
    	tv_longtitude.setText(Location.convert(longitude, Location.FORMAT_MINUTES));
    	tv_longtitude.postInvalidate();
    	
    	TextView tv_altitude = (TextView) findViewById(R.id.tv_alt);
    	tv_altitude.setText(Math.round(altitude) + " m");
    	tv_altitude.postInvalidate();
    	
    	TextView tv_accuracy = (TextView) findViewById(R.id.tv_acc);
    	tv_accuracy.setText(Math.round(accuracy) + " m");
    	tv_accuracy.postInvalidate();
    	
    	TextView tv_speed = (TextView) findViewById(R.id.tv_speed);
    	tv_speed.setText(Math.round(speed) + " m/s");
    	tv_speed.postInvalidate();
    	
    	TextView tv_avarage_speed = (TextView) findViewById(R.id.tv_avarage_speed);
    	tv_avarage_speed.setText(Math.round(avarageSpeed) + " m/s");
    	tv_avarage_speed.postInvalidate();
    	
    	TextView tv_distance = (TextView) findViewById(R.id.tv_distance);
    	tv_distance.setText(Math.round(distance) + " m");
    	tv_distance.postInvalidate();
    }
    
    @Override
    public void onDestroy() {
    	super.onDestroy();
     
    	locationListener.stopRecord();
    }
}
