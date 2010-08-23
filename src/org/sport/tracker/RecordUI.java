package org.sport.tracker;

import org.sport.tracker.utils.RecordDBHelper;
import org.sport.tracker.utils.SportTrackerLocationListener;
import org.sport.tracker.utils.WaypointDBHelper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
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
		if (extras.containsKey("profile")) {
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
		locationListener = new SportTrackerLocationListener(this, extras.getString("profile"));
	}

	public void recordStop() {
		TextView profile_tv = (TextView) findViewById(R.id.tv_profile);
		final String profile = (String) profile_tv.getText();
////		Date startDate = new Date();
////		Date stopDate = startDate;
//		long recorded_time = 0;
//		if (locationListener.record.track.size() > 0) {
//			long startMillis = ((LinkedList<Waypoint>) locationListener.record.track).getFirst().time;
//			long stopMillis = ((LinkedList<Waypoint>) locationListener.record.track).getLast().time;
//			recorded_time = stopMillis - startMillis;
////			startDate = new Date(startMillis);
////			stopDate = new Date(stopMillis);
//		}
//
		
		locationListener.stopRecord();
		
		// start recordinfo Activity
		Intent recordinfo_intent = new Intent(RecordUI.this, RecordInfoUI.class);
		recordinfo_intent.putExtra("profile", profile);
		startActivity(recordinfo_intent);
		this.finish();
	}

	public void updateFields(Uri record, Uri waypoint) {
		if (locationListener.recordId < 0) 
			return;
		
		if (!this.isFinishing()) {
			ContentResolver resolver = getContentResolver();
			Cursor c = resolver.query(waypoint, new String[] { 
					WaypointDBHelper.KEY_LATITUDE,
					WaypointDBHelper.KEY_LONGTITUDE,
					WaypointDBHelper.KEY_ALTITUDE,
					WaypointDBHelper.KEY_ACCURACY,
					WaypointDBHelper.KEY_SPEED,
					WaypointDBHelper.KEY_TIME}, null, null, null);
			
			if (c.getColumnCount() != 6)
				return;
			
			c.moveToFirst();
			final double latitude = 	c.getDouble(0);
			final double longitude = 	c.getDouble(1);
			final double altitude = 	c.getDouble(2);
			final float accuracy = 		c.getFloat(3);
			final float speed = 		c.getFloat(4);
			c.close();
			
			c = resolver.query(record, new String[] {
					RecordDBHelper.KEY_DISTANCE,
					RecordDBHelper.KEY_AVARAGE_SPEED
				}, null, null, null);
			
			float distance = 		0;
			float avarageSpeed = 	0;
			
			if (c.getCount() > 0) {
				c.moveToFirst();
				distance = 		c.getFloat(0);
				avarageSpeed = 	c.getFloat(1);
			}
			c.close();

			TextView tv_latitude = (TextView) findViewById(R.id.tv_lat);
			tv_latitude.setText(Location.convert(latitude,
					Location.FORMAT_MINUTES));
			tv_latitude.postInvalidate();

			TextView tv_longtitude = (TextView) findViewById(R.id.tv_lon);
			tv_longtitude.setText(Location.convert(longitude,
					Location.FORMAT_MINUTES));
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
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		locationListener.stopRecord();
	}
}
