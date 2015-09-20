package com.example.project_idfind;

import java.util.ArrayList;
import java.util.Collection;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.perples.recosdk.RECOBeacon;
public class RangingListAdapter extends BaseAdapter {
	public ArrayList<RECOBeacon> mRangedBeacons;
	private LayoutInflater mLayoutInflater;
	
	//Context mContext;
	public RangingListAdapter(Context context) {
		super();
		mRangedBeacons = new ArrayList<RECOBeacon>();
		mLayoutInflater = LayoutInflater.from(context);
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
			Log.i("RangingAdapter", "updateAllBeacons()");
			mRangedBeacons = new ArrayList<RECOBeacon>(beacons);
		}	
	}
	public boolean BeaconDetect(String major){
		boolean detection;
		detection=false;
		for (int k = 0; k < mRangedBeacons.size(); k++) {
			if (mRangedBeacons.get(k).getMajor() == Integer.parseInt(major)){
				detection=true;
			}
		}
		//Log.i("RECOBackgroundRangingService","background() - detecting"+detection);
		return detection;
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
		ViewHolder viewHolder;
		
		if(convertView == null) {
			convertView = mLayoutInflater.inflate(R.layout.beacon_ranging_list, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.recoProximityUuid = (TextView)convertView.findViewById(R.id.ProximityUuid);
			viewHolder.recoMajor = (TextView)convertView.findViewById(R.id.recoMajor);
			viewHolder.recoMinor = (TextView)convertView.findViewById(R.id.recoMinor);
			viewHolder.recoTxPower = (TextView)convertView.findViewById(R.id.recoTxPower);
			viewHolder.recoRssi = (TextView)convertView.findViewById(R.id.recoRssi);
			viewHolder.recoProximity = (TextView)convertView.findViewById(R.id.recoProximity);
			viewHolder.recoAccuracy = (TextView)convertView.findViewById(R.id.recoAccuracy);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		RECOBeacon recoBeacon = mRangedBeacons.get(position);
		
		String proximityUuid = recoBeacon.getProximityUuid();
		
		viewHolder.recoProximityUuid.setText(String.format("%s-%s-%s-%s-%s", proximityUuid.substring(0, 8), 
				proximityUuid.substring(8, 12), proximityUuid.substring(12, 16), 
				proximityUuid.substring(16, 20), proximityUuid.substring(20) ));
		viewHolder.recoMajor.setText(recoBeacon.getMajor() + "");
		viewHolder.recoMinor.setText(recoBeacon.getMinor() + "");
		viewHolder.recoTxPower.setText(recoBeacon.getTxPower() + "");
		viewHolder.recoRssi.setText(recoBeacon.getRssi() + "");
		viewHolder.recoProximity.setText(recoBeacon.getProximity() + "");
		viewHolder.recoAccuracy.setText(String.format("%.2f", recoBeacon.getAccuracy()));
		
		/*Intent intent = new Intent(mContext, PopupActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("rssi", recoBeacon.getRssi());
		mContext.startActivity(intent);*/
		
		return convertView;
	}
	
	static class ViewHolder {
		TextView recoProximityUuid;
		TextView recoMajor;
		TextView recoMinor;
		TextView recoTxPower;
		TextView recoRssi;
		TextView recoProximity;
		TextView recoAccuracy;
	}
}
