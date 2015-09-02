package com.example.project_idfind;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.project_idfind.MonthUse.month_info;


public class MonthUseAdapter extends BaseAdapter{

	public ArrayList<month_info> month_list;
	LayoutInflater mLayoutInflater2;
	
	public MonthUseAdapter(Context context) {
		super();
		month_list = new ArrayList<month_info>();
		mLayoutInflater2 = LayoutInflater.from(context);
	}	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return month_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return month_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if(convertView == null) {
			convertView = mLayoutInflater2.inflate(R.layout.month_use_list, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.on_off_div = (TextView)convertView.findViewById(R.id.mdiv_list);
			viewHolder.pay = (TextView)convertView.findViewById(R.id.mpay_list);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		month_info use_list_info = month_list.get(position);
		
		Log.i(this.getClass().getName(), use_list_info.div);
		Log.i(this.getClass().getName(), use_list_info.pay);
		
		viewHolder.on_off_div.setText(use_list_info.div);
		viewHolder.pay.setText(use_list_info.pay);

		return convertView;
	}
	
	static class ViewHolder {
		TextView on_off_div;
		TextView pay;
	}
}
