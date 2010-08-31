package org.sport.tracker.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.os.Handler;
import android.widget.TextView;

public class TimeCounteRunnable implements Runnable {

	Handler handler;
	final TextView counterView;
	public final long startTime;
	public long endTime;
	public long currentTimeSpan = 0L;
	SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");

	public TimeCounteRunnable(TextView view, long startTime) {
		this.counterView = view;
		this.startTime = startTime;
		this.endTime = startTime;
	}

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
