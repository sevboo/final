package com.example.project_idfind;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.project_idfind.PayUse.pay_use_info;


public class PayUseAdapter extends BaseAdapter {

	public ArrayList<pay_use_info> pay_use_list;
	LayoutInflater mLayoutInflater4;
	//Context mContext;
	public PayUseAdapter(Context context) {
		super();
		pay_use_list = new ArrayList<pay_use_info>();
		mLayoutInflater4 = LayoutInflater.from(context);
	}	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return pay_use_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return pay_use_list.get(position);
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
			convertView = mLayoutInflater4.inflate(R.layout.pay_use_list, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.pay_day = (TextView)convertView.findViewById(R.id.pay_day_list);
			viewHolder.pay_charge = (TextView)convertView.findViewById(R.id.paycharge_list);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		pay_use_info list_info = pay_use_list.get(position);
		//Log.i("adapter", use_list_info.time );
		viewHolder.pay_day.setText(list_info.day);
		viewHolder.pay_charge.setText(list_info.pay);

		return convertView;
	}
	
	static class ViewHolder {
		TextView pay_day;
		TextView pay_charge;
	}

}
