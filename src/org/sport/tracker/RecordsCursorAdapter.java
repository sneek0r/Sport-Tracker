package org.sport.tracker;

import java.util.ArrayList;
import java.util.List;

import org.sport.tracker.utils.RecordDBHelper;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;

public class RecordsCursorAdapter<RecordListItem> extends CursorAdapter {
	
	public List<RecordListItem> items;
	
	public class RecordListItem {
		
		public long id = -1;
		public long startTime = -1;
		public long endTime = -1;
		public long totalTime = -1;
		public float distance = -1f;
		public float avarageSpeed = -1f;
		public String comment;
		
		public RecordListItem(long id, long startTime, long endTime, 
				float distance, float avarageSpeed, String comment) {
			
			if (startTime > endTime) throw new IllegalArgumentException();
			
			this.id = id;
			this.startTime = startTime;
			this.endTime = endTime;
			this.distance = distance;
			this.avarageSpeed = avarageSpeed;
			this.comment = comment;
			this.totalTime = endTime - startTime;
		}
		
		public String toString() {
			return 
			
			(startTime != -1 ? 
				Long.toString(startTime / 60 / 60 / 1000) + ":" +	// hours
				Long.toString(startTime / 60 / 1000) + ":" +		// munutes
				Long.toString(startTime / 1000) : ""					// secunds);
			) + (distance != -1 ? " " + Math.round(distance) + " m" : "");
		}
	}
	
	public RecordsCursorAdapter(Context context, Cursor c) {
		super(context, c);
		items = new ArrayList<RecordListItem>(c.getCount());
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		if (!cursor.isFirst()) cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			
			RecordListItem item = new RecordListItem(
					cursor.getLong(cursor.getColumnIndex(RecordDBHelper.KEY_ID)),
					cursor.getLong(cursor.getColumnIndex(RecordDBHelper.KEY_START_TIME)),
					cursor.getLong(cursor.getColumnIndex(RecordDBHelper.KEY_END_TIME)),
					cursor.getFloat(cursor.getColumnIndex(RecordDBHelper.KEY_DISTANCE)),
					cursor.getFloat(cursor.getColumnIndex(RecordDBHelper.KEY_AVARAGE_SPEED)),
					cursor.getString(cursor.getColumnIndex(RecordDBHelper.KEY_COMMENT)));
					
			items.add(item);
			cursor.moveToNext();
		}
		if (view instanceof AdapterView) 
			((AdapterView)view).setAdapter(this);
		else 
			Log.e(getClass().getName(), "View is not a AdapterView!");
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		ListView view = new ListView(context);
		bindView(view, context, cursor);
		parent.addView(view);
		return view;
	}

	public int getConunt() {
		return items.size();
	}
	
	public RecordListItem getItem(int pos) {
		return items.get(pos);
	}
	
	public long getItemId(int pos) {
		return items.get(pos).id;
	}
	
	public boolean isEmpty() {
		return items.size() == 0;
	}
}
