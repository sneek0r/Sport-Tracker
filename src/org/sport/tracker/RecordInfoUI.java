package org.sport.tracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.sport.tracker.utils.Record;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * RecordInfo activity, to show record data.
 * 
 * @author Waldemar Smirnow
 *
 */
public class RecordInfoUI extends Activity {
	
	/**
	 * Record ID.
	 */
	long recordId = 0;
	/**
	 * Record to show.
	 */
	Record record;
	/**
	 * Delete Record dialog id.
	 */
	static final int DELETE_DIALOG_ID = 0;
	/**
	 * Comment dialog id.
	 */
	static final int COMMENT_DIALOG_ID = 1;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recordinfo);
        
        // set profile
        Bundle extras = getIntent().getExtras();
        if( extras.containsKey("id") ) {
        	recordId = extras.getLong("id");
        	try {
        		record = Record.queryDB(this, recordId);
        	} catch (IllegalArgumentException e) {
        		Log.d(getClass().getName().toString(), "record with id " + recordId + " does not exist");
        		return;
        	}
        	
        	// get data
    		String profile = 		record.profile;
    		long startTime = 		record.startTime;
    		long endTime = 			record.endTime;
    		float distance =		record.distance;
    		float avarageSpeed = 	record.averageSpeed;
    		String comment = 		record.comment;
        	
    		// fill profile textview
        	TextView profile_tv = (TextView) findViewById(R.id.tv_profile);
        	profile_tv.setText(profile);
        	profile_tv.postInvalidate();

        	// fill recorded time textview
        	TextView time_tv = (TextView) findViewById(R.id.tv_time);
        	long timeSpan = endTime - startTime;
        	Date time = new Date(timeSpan-TimeZone.getDefault().getOffset(timeSpan));
        	SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss");
        	time_tv.setText(dateFormatter.format(time));
        	time_tv.postInvalidate();
            
        	// fill distance textview
        	TextView distance_tv = (TextView) findViewById(R.id.tv_distance);
        	distance_tv.setText(Math.round(distance) + " m");
        	distance_tv.postInvalidate();
        
        	// fill avarage speed textview
        	TextView avarage_speed_tv = (TextView) findViewById(R.id.tv_average_speed);
        	avarage_speed_tv.setText(Math.round(avarageSpeed) + " m/s");
        	avarage_speed_tv.postInvalidate();
        	
        	// fill comment textview
//        	TextView comment_tv = (TextView) findViewById(R.id.tv_comment);
//        	comment_tv.setText(comment);
//        	comment_tv.postInvalidate();
        	
        	// add handler, to delete record
        	Button bt_delete_reord = (Button) findViewById(R.id.bt_delete_record);
        	bt_delete_reord.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					showDialog(DELETE_DIALOG_ID);
				}
			});
        	
        	// add handler, to show record on map
        	Button bt_show_map = (Button) findViewById(R.id.bt_show_on_map);
        	bt_show_map.setOnClickListener( new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					startMapActivity();
				}
			});
        }
    }
    
    /**
     * Delete record and close activity.
     */
    public void deleteReord() {
    	if (Record.deleteDB(this, recordId)) {
    		this.finish();
    	}
    }
    
    public boolean saveComment(String comment) {
    	record.comment = comment;
    	return record.updateDB() == 1;
    }
	
    /**
     * Start MapUI activity with shown record (id).
     */
	public void startMapActivity() {
		Intent mapIntent = new Intent(RecordInfoUI.this, MapUI.class);
		mapIntent.putExtra("id", recordId);
		startActivity(mapIntent);
	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		final Dialog dialog;
		switch (id) {
		case DELETE_DIALOG_ID:
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getString(R.string.sure_to_delete_record))
			       .setCancelable(true)
			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			           public void onClick(DialogInterface dialog, int id) {
			                RecordInfoUI.this.deleteReord();
			           }
			       });
			dialog = builder.create();
			break;
			
		case COMMENT_DIALOG_ID:
			dialog = new Dialog(this);
			dialog.setContentView(R.layout.comment_dialog);
			dialog.setTitle(R.string.comment_dialog);
			dialog.setCancelable(true);
			Button bt_cancel = (Button) findViewById(R.id.bt_cancel);
			bt_cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					dialog.cancel();
				}
			});
			
			Button bt_save = (Button) findViewById(R.id.bt_comment_save);
			bt_save.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Editable comment = ((EditText)findViewById(R.id.ev_enter_comment)).getText();
					if (RecordInfoUI.this.saveComment(comment.toString())) {
						dialog.cancel();
					} else {
						Toast.makeText(RecordInfoUI.this, "Can't save comment, please try again!", 3);
					}
				}
			});
			break;
			
		default:
			throw new IllegalArgumentException();
		}
		return dialog;
	}

}
