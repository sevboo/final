package com.example.project_idfind;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import com.perples.recosdk.RECOBeacon;
public class RangingListAdapter extends BaseAdapter {
	public ArrayList<RECOBeacon> mRangedBeacons;
	//Context mContext;
	public RangingListAdapter(Context context) {
		super();
		mRangedBeacons = new ArrayList<RECOBeacon>();
		LayoutInflater.from(context);
	}
	
	public void updateBeacon(RECOBeacon beacon) {
		synchronized (mRangedBeacons) {
			if(mRangedBeacons.contains(beacon)) {
				mRangedBeacons.remove(beacon);
			}
			mRangedBeacons.add(beacon);
		}
	}
	
	public void updateAllBeacons(Collection<RECOBeacon> beacons) {
		synchronized (beacons) {
			mRangedBeacons = new ArrayList<RECOBeacon>(beacons);
		}	
	}
	
	public void clear() {
		mRangedBeacons.clear();
	}
	
	@Override
	public int getCount() {
		return mRangedBeacons.size();
	}
	@Override
	public Object getItem(int position) {
		return mRangedBeacons.get(position);
	}
	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}
}
