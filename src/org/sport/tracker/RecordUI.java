package org.sport.tracker;

import java.util.Date;

import org.sport.tracker.utils.Record;
import org.sport.tracker.utils.SportTrackerLocationListener;
import org.sport.tracker.utils.TimeCountRunnable;
import org.sport.tracker.utils.Waypoint;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * Record activity, create and show new record.
 * 
 * @author Waldemar Smirnow
 *
 */
public class RecordUI extends Activity {

	/**
	 * LocationListener, update database and call updateFields method.
	 */
	public SportTrackerLocationListener locationListener;
	/**
	 * Selected profile.
	 */
	public String profile;
	/**
	 * Time counter Runnable, show record time on textview.
	 */
	Runnable timeCounter = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.record);

		// set profile
		Bundle extras = getIntent().getExtras();
		if (extras.containsKey("profile")) {
			profile = extras.getCharSequence("profile").toString();
		} else {
			profile = "Other";
		}
		
		// show profile on textview
		TextView profile_tv = (TextView) findViewById(R.id.tv_profile);
		profile_tv.setText(profile);
		profile_tv.postInvalidate();
		
		// add record stop button listener
		Button record_stop = (Button) findViewById(R.id.bt_stop_record);
		record_stop.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				recordStop();
			}
		});

		// let the time textview show record time
		final TextView tv_time = (TextView) findViewById(R.id.tv_time);
		long startTime = new Date().getTime();
		timeCounter = new TimeCountRunnable(tv_time, startTime);
		tv_time.post(timeCounter);
		
		// set LocationListener
		locationListener = new SportTrackerLocationListener(this, profile, startTime);
	}

	/**
	 * Stop record and start RecordInfoUI with recordId and endtime.
	 */
	public void recordStop() {
		final TextView tv_time = (TextView) findViewById(R.id.tv_time);
		final long endTime = ((TimeCountRunnable) timeCounter).endTime;
		tv_time.removeCallbacks(timeCounter);
		final long recordId = locationListener.stopRecord(endTime);
		
		// start recordinfo Activity
		Intent recordinfo_intent = new Intent(RecordUI.this, RecordInfoUI.class);
		recordinfo_intent.putExtra("id", recordId);
		startActivity(recordinfo_intent);
		this.finish();
	}

	/**
	 * Update record view fields.
	 * @param record recorded record
	 * @param waypoint last waypoint
	 */
	public void updateFields(Record record, Waypoint waypoint) {

		if (!this.isFinishing()) {
			
			final double latitude = 	waypoint.latitude;
			final double longitude = 	waypoint.longtitude;
			final double altitude = 	waypoint.altitude;
			final float accuracy = 		waypoint.accuracy;
			final float speed = 		waypoint.speed;
			float distance = 			record.distance;
			float avarageSpeed = 		record.avarageSpeed;
			
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
		locationListener.stopRecord(new Date().getTime());
	}
}
