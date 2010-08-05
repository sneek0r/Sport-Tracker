package org.sport.tracker;

import org.sport.tracker.utils.ProfilesOnItemSelectedListener;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class Launcher extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher);
        
        Spinner profile_spinner = (Spinner) findViewById(R.id.sp_profile);
        profile_spinner.setOnItemSelectedListener(new ProfilesOnItemSelectedListener());
    }
}