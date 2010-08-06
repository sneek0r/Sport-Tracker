package org.sport.tracker;

import org.sport.tracker.utils.ProfilesOnItemSelectedListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Spinner;

public class Launcher extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        
        // no more needed
        Spinner profile_spinner = (Spinner) findViewById(R.id.sp_profile);
        profile_spinner.setOnItemSelectedListener(new ProfilesOnItemSelectedListener());
        
        // add record start button listener
        Button record_start = (Button) findViewById(R.id.bt_start_record);
        record_start.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				recordStart();
			}
		});
    }
    
    public void recordStart() {
    	// get selected profile
    	Spinner profile_spinner = (Spinner) findViewById(R.id.sp_profile);
        final String selected_profile = (String) profile_spinner.getSelectedItem();
        
        // start record Activity
    	Intent record_intent = new Intent(Launcher.this, RecordUI.class);
    	record_intent.putExtra("profile", selected_profile);
    	startActivity(record_intent);
    }
}