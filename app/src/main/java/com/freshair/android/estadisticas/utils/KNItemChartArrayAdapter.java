package com.freshair.android.estadisticas.utils;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.freshair.android.estadisticas.ItemPerMonthActivity;
import com.freshair.android.estadisticas.R;
import com.freshair.android.estadisticas.dtos.KNItemChart;

public class KNItemChartArrayAdapter extends ArrayAdapter<KNItemChart> {
	
	private LayoutInflater mInflater;
	private ItemPerMonthActivity myContext;

	
	public KNItemChartArrayAdapter(Context context, int textViewResourceId,
			List<KNItemChart> objects) {
		super(context, textViewResourceId, objects);
		myContext = (ItemPerMonthActivity) context;
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		// TODO Auto-generated method stub
		//View v = super.getView(position, convertView, parent); 
		View v = null;
		final int pos = position;
		KNItemChart item;
		try {
			v = mInflater.inflate(R.layout.row_item, parent, false);
		} catch (Exception e) {
			e.getMessage();
		}
		item = this.getItem(position);
		TextView text = v.findViewById(R.id.item_day);
		text.setText(item.getDay());
		text = v.findViewById(R.id.item_hour);
		text.setText("(" + item.getHourMin() + ")");
		text = v.findViewById(R.id.item_value);
		text.setText(item.getValue());
		ImageView btn = v.findViewById(R.id.btnRemove);
        btn.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
             	myContext.eliminarItemDialog(pos);
             }
        }); 
        btn = v.findViewById(R.id.btnEdit);
        btn.setOnClickListener(new View.OnClickListener() {
             public void onClick(View v) {
             	myContext.openEditItemChart(pos);
             }
        });     
		return v;
	
	}

}
