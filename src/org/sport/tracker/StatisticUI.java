package org.sport.tracker;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.sport.tracker.utils.Record;
import org.sport.tracker.utils.RecordDBHelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

    	List<Record> records = Record.queryDB(this, RecordDBHelper.KEY_PROFILE + " = '" + profile + "'", 
    			null, RecordDBHelper.KEY_START_TIME);
    	
    	if (records.size() < 1) return;

    	long totalTime = 0;
    	float distance = 0.0f;
    	float avarageSpeed = 0.0f;
    	List<Map<String,String>> recordsViewList = new ArrayList<Map<String,String>>();
    	for (Record record : records) {
    		totalTime += record.endTime - record.startTime;
    		distance += record.distance;
    		avarageSpeed += record.avarageSpeed;
    		Map<String,String> recordMap = new HashMap<String, String>();
    		recordMap.put(RecordDBHelper.KEY_ID, ""+record.recordId);
    		recordMap.put(RecordDBHelper.KEY_START_TIME, new Date(record.startTime).toLocaleString());
    		recordMap.put(RecordDBHelper.KEY_DISTANCE, ""+Math.round(record.distance)+" m");
    		recordsViewList.add(recordMap);
    	}
    	avarageSpeed /= records.size();
    	
    	
    	
    	// fill list view
    	ListView lv = (ListView) findViewById(R.id.lv_records);
    	lv.setAdapter(new SimpleAdapter(this, recordsViewList, R.layout.record_row, new String[] {
    			RecordDBHelper.KEY_START_TIME,
    			RecordDBHelper.KEY_DISTANCE,
    			RecordDBHelper.KEY_ID
    	}, new int[] {
    			R.id.tv_record_date,
    			R.id.tv_record_distance
    	}));
    	lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				startRecordInfoActivity(id);
			}
		});
    	
    	TextView tv_time = (TextView) findViewById(R.id.tv_total_time);
    	Date time = new Date(totalTime-TimeZone.getDefault().getOffset(totalTime));
    	tv_time.setText(time.getHours()+":"+time.getMinutes()+":"+time.getSeconds());
//    	tv_time.setText(
//    			Long.toString(totalTime / 60 / 60 / 1000) + ":" +	// hours
//    			Long.toString(totalTime / 60 / 1000) + ":" +		// munutes
//    			Long.toString(totalTime / 1000));					// secunds);
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
