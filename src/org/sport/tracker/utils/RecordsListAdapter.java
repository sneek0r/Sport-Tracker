package org.sport.tracker.utils;

import java.util.Date;
import java.util.List;

import org.sport.tracker.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Record ListAdapter. Fill List entries with record data.
 * 
 * @author Waldemar Smirnow
 *
 */
public class RecordsListAdapter extends ArrayAdapter<Record> {

	/**
	 * Context.
	 */
	Context context;
	/**
	 * List with (record) data.
	 */
	List<Record> records;
	
	/**
	 * Constructor.
	 * @param context Context
	 * @param textViewResourceId List row layout
	 * @param records List with records to show
	 */
	public RecordsListAdapter(Context context, 
			int textViewResourceId, List<Record> records) {
		
		super(context, textViewResourceId, records);
		this.context = context;
		this.records = records;
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) 
            	context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.record_row, null);
		}
		
		Record record = records.get(position);
		TextView tv_date = (TextView) convertView.findViewById(R.id.tv_record_date);
		tv_date.setText(new Date(record.startTime).toLocaleString());
		tv_date.postInvalidate();
		
		TextView tv_distance = (TextView) convertView.findViewById(R.id.tv_record_distance);
		tv_distance.setText(Math.round(record.distance) + " m");
		tv_distance.postInvalidate();
		
		return convertView;
	}
}
