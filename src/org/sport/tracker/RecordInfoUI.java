package org.sport.tracker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class RecordInfoUI extends Activity {
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recordinfo);
        
        // set profile
        Bundle extras = getIntent().getExtras();
        if( extras.containsKey("profile") ) {
        	TextView profile_tv = (TextView) findViewById(R.id.tv_profile);
        	profile_tv.setText(extras.getCharSequence("profile"));
        	profile_tv.postInvalidate();
        }
    }
}
