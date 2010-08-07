package org.sport.tracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class RecordUI extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record);
        
        // set profile
        Bundle extras = getIntent().getExtras();
        if( extras.containsKey("profile") ) {
        	TextView profile_tv = (TextView) findViewById(R.id.tv_profile);
        	profile_tv.setText(extras.getCharSequence("profile"));
        	profile_tv.postInvalidate();
        }
        
        // add record start button listener
        Button record_stop = (Button) findViewById(R.id.bt_stop_record);
        record_stop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				recordStop();
			}
		});
    }
    
    public void recordStop(){
    	TextView profile_tv = (TextView) findViewById(R.id.tv_profile);
    	final String profile = (String) profile_tv.getText();
    	
    	// start recordinfo Activity
    	Intent recordinfo_intent = new Intent(RecordUI.this, RecordInfoUI.class);
    	recordinfo_intent.putExtra("profile", profile);
    	startActivity(recordinfo_intent);
    }
}
