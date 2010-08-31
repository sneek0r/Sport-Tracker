package org.sport.tracker;

import java.text.SimpleDateFormat;
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

/**
 * Statistic activity, show total time, avarage speed and  all records from selected profile.
 *  
 * @author Waldemar Smirnow
 *
 */
public class StatisticUI extends Activity {
	
	/**
	 * Record profile.
	 */
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

    	// get all records with selected profile
    	List<Record> records = Record.queryDB(this, RecordDBHelper.KEY_PROFILE + " = '" + profile + "'", 
    			null, RecordDBHelper.KEY_START_TIME);
    	
    	if (records.size() < 1) return;
    	// there are records on this profile
    	// collect data
    	long totalTime = 0;
    	float distance = 0.0f;
    	float averageSpeed = 0.0f;
    	for (Record record : records) {
    		totalTime += record.endTime - record.startTime;
    		distance += record.distance;
    		averageSpeed += record.averageSpeed;
    	}
    	averageSpeed /= records.size();
    	
    	// fill list view with records and set callback
    	ListView lv = (ListView) findViewById(R.id.lv_records);
    	lv.setAdapter(new RecordsListAdapter(lv.getContext(), R.layout.record_row, records));
    	lv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Record record = (Record) parent.getAdapter().getItem(position);
				startRecordInfoActivity(record.recordId);
			}
		});
    	
    	// fill total time textview
    	TextView tv_time = (TextView) findViewById(R.id.tv_total_time);
    	Date time = new Date(totalTime-TimeZone.getDefault().getOffset(totalTime));
    	SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
    	tv_time.setText(dateFormatter.format(time));
    	tv_time.postInvalidate();
    	
    	// fill distance textview
    	TextView tv_distance = (TextView) findViewById(R.id.tv_total_distance);
    	tv_distance.setText(Math.round(distance) + " m");
    	tv_distance.postInvalidate();
    	
    	// fill average speed textview
    	TextView tv_speed = (TextView) findViewById(R.id.tv_total_average_speed);
    	tv_speed.setText(Math.round(averageSpeed) + " m/s");
    	tv_speed.postInvalidate();
    }
    
    /**
     * Callback for list items to start RecordInfoUI for selected record.
     * @param id record id
     */
    public void startRecordInfoActivity(long id) {
    	Intent recordInfo_intent = new Intent(StatisticUI.this, RecordInfoUI.class);
		recordInfo_intent.putExtra("id", id);
    	startActivity(recordInfo_intent);
    }
}
