package com.example.project_idfind;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.project_idfind.PayActivity.pay_info;
import com.example.project_idfind.RecentUse.use_info;

public class PayUseAdapter extends BaseAdapter {

	public ArrayList<pay_info> pay_list;
	LayoutInflater mLayoutInflater3;
	//Context mContext;
	public PayUseAdapter(Context context) {
		super();
		pay_list = new ArrayList<pay_info>();
		mLayoutInflater3 = LayoutInflater.from(context);
	}	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return pay_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return pay_list.get(position);
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
			convertView = mLayoutInflater3.inflate(R.layout.pay_list, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.on_off_div = (TextView)convertView.findViewById(R.id.pay_div_list);
			viewHolder.time = (TextView)convertView.findViewById(R.id.pay_time_list);
			viewHolder.pay = (TextView)convertView.findViewById(R.id.pay_charge_list);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		pay_info use_list_info = pay_list.get(position);
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
