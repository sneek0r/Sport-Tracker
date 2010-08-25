package org.sport.tracker;

import org.sport.tracker.utils.RecordDBHelper;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class StatisticUI extends Activity {
	
	public String profile;
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic);
        
        // set profile
        Bundle extras = getIntent().getExtras();
        if( extras.containsKey("profile") ) {
        	profile = (String) extras.getCharSequence("profile");
        }
        
        // fill profile text view
        TextView profile_tv = (TextView) findViewById(R.id.tv_profile);
    	profile_tv.setText(profile);
    	profile_tv.postInvalidate();
    	
    	ContentResolver resolver = getContentResolver();
    	Cursor c = resolver.query(RecordProvider.RECORD_CONTENT_URI, null, null, null, RecordDBHelper.KEY_START_TIME);
    	
    	if (c.getCount() < 1) return;
    	c.moveToFirst();
    	long totalTime = 0;
    	float distance = 0.0f;
    	float avarageSpeed = 0.0f;
    	int records = 0;
    	while (!c.isAfterLast()) {
    		records++;
    		long startTime = c.getLong(c.getColumnIndex(RecordDBHelper.KEY_START_TIME));
    		long endTime = c.getLong(c.getColumnIndex(RecordDBHelper.KEY_END_TIME));
    		totalTime += Math.abs(endTime - startTime);
    		distance += Math.abs(c.getFloat(c.getColumnIndex(RecordDBHelper.KEY_DISTANCE)));
    		avarageSpeed += Math.abs(c.getFloat(c.getColumnIndex(RecordDBHelper.KEY_AVARAGE_SPEED)));
    		c.moveToNext();
    	}
    	if (records != 0) avarageSpeed /= records;
//    	c.moveToFirst();
    	c.close();
    	
    	c = resolver.query(RecordProvider.RECORD_CONTENT_URI, new String[] {
    			RecordDBHelper.KEY_START_TIME,
    			RecordDBHelper.KEY_DISTANCE,
    			RecordDBHelper.KEY_ID
    	}, null, null, RecordDBHelper.KEY_START_TIME);
    	
    	c.moveToFirst();
    	// fill listview
    	ListView lv = (ListView) findViewById(R.id.lv_records);
//    	new RecordsCursorAdapter(this, c).bindView(lv, this, c);
    	lv.setAdapter(new SimpleCursorAdapter(this, R.id.record_row, c, new String[] {
    			RecordDBHelper.KEY_START_TIME,
    			RecordDBHelper.KEY_DISTANCE,
    			RecordDBHelper.KEY_ID
    	}, new int[] {
    			R.id.tv_record_date,
    			R.id.tv_record_distance,
    			R.id.tv_record_id
    	}));
    	c.close();
    	Log.d(getClass().getName(), ""+lv.getAdapter().getCount());
    	
    	TextView tv_time = (TextView) findViewById(R.id.tv_total_time);
    	tv_time.setText(
    			Long.toString(totalTime / 60 / 60 / 1000) + ":" +	// hours
    			Long.toString(totalTime / 60 / 1000) + ":" +		// munutes
    			Long.toString(totalTime / 1000));					// secunds);
    	tv_time.postInvalidate();
    	
    	TextView tv_distance = (TextView) findViewById(R.id.tv_total_distance);
    	tv_distance.setText(Math.round(distance) + " m");
    	tv_distance.postInvalidate();
    	
    	TextView tv_speed = (TextView) findViewById(R.id.tv_total_avarage_speed);
    	tv_speed.setText(Math.round(avarageSpeed) + " m/s");
    	tv_speed.postInvalidate();
    }
}
