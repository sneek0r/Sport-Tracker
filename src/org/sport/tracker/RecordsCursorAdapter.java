package org.sport.tracker;

import java.util.Date;

import org.sport.tracker.utils.RecordDBHelper;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class RecordsCursorAdapter extends CursorAdapter {
	
	public RecordsCursorAdapter(Context context, Cursor cursor) {
		super(context, cursor);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		TextView tv_start_time = (TextView) view.findViewById(R.id.tv_record_date);
		TextView tv_distance = (TextView) view.findViewById(R.id.tv_record_distance);
		
		tv_start_time.setText(new Date(cursor.getLong(cursor.getColumnIndex(RecordDBHelper.KEY_START_TIME))).toLocaleString());
		tv_distance.setText(""+Math.round(cursor.getFloat(cursor.getColumnIndex(RecordDBHelper.KEY_DISTANCE))));
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.record_row, parent, false);
		bindView(view, context, cursor);
		return view;
	}
}
