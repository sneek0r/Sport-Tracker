package org.sport.tracker;

import org.sport.tracker.utils.RecordDBHelper;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

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
        	profile = extras.getCharSequence("profile").toString();
        } else {
        	profile = "Other";
        }
        
        // fill profile text view
        TextView profile_tv = (TextView) findViewById(R.id.tv_profile);
    	profile_tv.setText(profile);
    	profile_tv.postInvalidate();
    	
    	ContentResolver resolver = getContentResolver();
    	Cursor c = resolver.query(RecordProvider.RECORD_CONTENT_URI, null, 
    			RecordDBHelper.KEY_PROFILE + " = '" + profile + "'",
    			null, RecordDBHelper.KEY_START_TIME);
    	
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
    	
    	// fill list view
    	c.moveToFirst();
    	ListView lv = (ListView) findViewById(R.id.lv_records);
    	CursorAdapter lv_adapter = new RecordsCursorAdapter(lv.getContext(), c);
    	lv.setAdapter(new RecordsCursorAdapter(lv.getContext(), c));
    	lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startRecordInfoActivity(id);
			}
		});
    	
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
    
    public void startRecordInfoActivity(long id) {
    	Intent recordInfo_intent = new Intent(StatisticUI.this, RecordInfoUI.class);
		recordInfo_intent.putExtra("id", id);
    	startActivity(recordInfo_intent);
    }
    
    public void onDestroy() {
    	super.onDestroy();
    }
}
