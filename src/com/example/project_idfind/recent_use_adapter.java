package com.example.project_idfind;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.project_idfind.RecentUse.use_info;

public class recent_use_adapter extends BaseAdapter {

	public ArrayList<use_info> use_list;
	LayoutInflater mLayoutInflater;
	//Context mContext;
	public recent_use_adapter(Context context) {
		super();
		use_list = new ArrayList<use_info>();
		mLayoutInflater = LayoutInflater.from(context);
	}	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return use_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return use_list.get(position);
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
			convertView = mLayoutInflater.inflate(R.layout.recent_use_list, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.on_off_div = (TextView)convertView.findViewById(R.id.div_list);
			viewHolder.time = (TextView)convertView.findViewById(R.id.time_list);
			viewHolder.pay = (TextView)convertView.findViewById(R.id.pay_list);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		use_info use_list_info = use_list.get(position);
		//Log.i("adapter", use_list_info.time );
		viewHolder.on_off_div.setText(use_list_info.div);
		viewHolder.time.setText(use_list_info.time);
		viewHolder.pay.setText(use_list_info.pay);

		return convertView;
	}
	
	static class ViewHolder {
		TextView on_off_div;
		TextView time;
		TextView pay;
	}

}
