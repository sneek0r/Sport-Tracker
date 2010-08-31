package org.sport.tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;

/**
 * Main activity.
 * 
 * @author Waldemar Smirnow
 *
 */
public class LauncherUI extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
                
        // add record start button listener
        Button record_start = (Button) findViewById(R.id.bt_start_record);
        record_start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				recordStart();
			}
		});
        
        // add show statistic button listener
        Button show_statistic = (Button) findViewById(R.id.bt_show_statistic);
        show_statistic.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showStatistic();
			}
		});
    }
    
    /**
     * Start RecordUI activity with selected profile.
     */
    public void recordStart() {
    	// get selected profile
    	Spinner profile_spinner = (Spinner) findViewById(R.id.sp_profile);
        final String selected_profile = (String) profile_spinner.getSelectedItem();
        
        // start record Activity
    	Intent record_intent = new Intent(LauncherUI.this, RecordUI.class);
    	record_intent.putExtra("profile", selected_profile);
    	startActivity(record_intent);
    }
    
    /**
     * Start StatisticUI activity with selected profile.
     */
    public void showStatistic() {
    	// get selected profile
    	Spinner profile_spinner = (Spinner) findViewById(R.id.sp_profile);
        final String selected_profile = (String) profile_spinner.getSelectedItem();
        
        // start statistic Activity
    	Intent statistic_intent = new Intent(LauncherUI.this, StatisticUI.class);
    	statistic_intent.putExtra("profile", selected_profile);
    	startActivity(statistic_intent);
    }
}