package org.sport.tracker;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.sport.tracker.utils.Record;
import org.sport.tracker.utils.RecordDBHelper;
import org.sport.tracker.utils.RecordsListAdapter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
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

    	List<Record> records = Record.queryDB(this, RecordDBHelper.KEY_PROFILE + " = '" + profile + "'", 
    			null, RecordDBHelper.KEY_START_TIME);
    	
    	if (records.size() < 1) return;

    	long totalTime = 0;
    	float distance = 0.0f;
    	float avarageSpeed = 0.0f;
    	for (Record record : records) {
    		totalTime += record.endTime - record.startTime;
    		distance += record.distance;
    		avarageSpeed += record.avarageSpeed;
    	}
    	avarageSpeed /= records.size();
    	
    	// fill list view
    	ListView lv = (ListView) findViewById(R.id.lv_records);
    	lv.setAdapter(new RecordsListAdapter(lv.getContext(), R.layout.record_row, records));
    	lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Record record = (Record) parent.getAdapter().getItem(position);
				startRecordInfoActivity(record.recordId);
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
