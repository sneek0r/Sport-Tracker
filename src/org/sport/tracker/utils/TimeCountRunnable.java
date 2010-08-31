package org.sport.tracker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.widget.TextView;

/**
 * Update time TextView with record time every sec.
 * 
 * @author Waldemar Smirnow
 *
 */
public class TimeCountRunnable implements Runnable {

	/**
	 * TextView to update.
	 */
	final TextView counterView;
	/**
	 * Record start time (millis).
	 */
	public final long startTime;
	/**
	 * Record end time (millis). Will be refreshed every update.
	 */
	public long endTime;
	/**
	 * Record running time (millis). Will be refreshed every update.
	 */
	public long currentTimeSpan = 0L;
	/**
	 * Date formatter (need to format time textview).
	 */
	SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");

	/**
	 * Constructor.
	 * @param view Textview to update
	 * @param startTime Record start time
	 */
	public TimeCountRunnable(TextView view, long startTime) {
		this.counterView = view;
		this.startTime = startTime;
		this.endTime = startTime;
	}

	/**
	 * Update textview with record running time every sec. 
	 */
	public void run() {
		endTime = new Date().getTime();
		currentTimeSpan = endTime - startTime;
		Date time = new Date(currentTimeSpan - 
				TimeZone.getDefault().getOffset(currentTimeSpan));
		
		counterView.setText(dateFormatter.format(time));
		counterView.postInvalidate();
		
		// execute himself on the UI Thread after 1sec
		counterView.postDelayed(this, 1000);
	}
}
