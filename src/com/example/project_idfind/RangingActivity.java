package com.example.project_idfind;

import java.util.ArrayList;
import java.util.Collection;
import java.util.StringTokenizer;

import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.ListView;

import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECORangingListener;
/**
 * RECORangingActivity class is to range regions in the foreground.
 * 
 * RECORangingActivity 클래스는 foreground 상태에서 ranging을 수행합니다. 
 */
public class RangingActivity extends RecoActivity implements RECORangingListener{
	private RangingListAdapter mRangingListAdapter;
	protected ListView mRegionListView;
		@Override
	protected void onCreate(Bundle savedInstanceState) {		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ranging_list);
		//RECORangingListener 를 설정합니다. (필수)
		mRecoManager.setRangingListener(this);
		mRecoManager.setScanPeriod(500);
		mRecoManager.setSleepPeriod(500);
		mRecoManager.bind(this);
	}
	@Override
	protected void onResume() {
		super.onResume();		
		mRangingListAdapter = new RangingListAdapter(this);
		mRegionListView = (ListView)findViewById(R.id.ranging_list);
		mRegionListView.setAdapter(mRangingListAdapter);
	}
		@Override
	protected void onDestroy() {
		super.onDestroy();	
		this.stop(mRegions);
		this.unbind();
	}
		private void unbind() {
		try {
			mRecoManager.unbind();
		} catch (RemoteException e) {
			Log.i("RECORangingActivity", "Remote Exception");
			e.printStackTrace();
		}
	}
		@Override
	public void onServiceConnect() {
		Log.i("RECORangingActivity", "onServiceConnect()");
		mRecoManager.setDiscontinuousScan(MainActivity.DISCONTINUOUS_SCAN);
		this.start(mRegions);
		//Write the code when RECOBeaconManager is bound to RECOBeaconService
	}
	@Override
	public void didRangeBeaconsInRegion(Collection<RECOBeacon> recoBeacons, RECOBeaconRegion recoRegion) {
		Log.i("RECORangingActivity", "didRangeBeaconsInRegion() region: " + recoRegion.getUniqueIdentifier() + ", number of beacons ranged: " + recoBeacons.size());
		mRangingListAdapter.updateAllBeacons(recoBeacons);
		mRangingListAdapter.notifyDataSetChanged();
		
		//Write the code when the beacons in the region is received
	}
	
		@Override
	protected void start(ArrayList<RECOBeaconRegion> regions) {		
		for(RECOBeaconRegion region : regions) {
			try {
				mRecoManager.startRangingBeaconsInRegion(region);
			} catch (RemoteException e) {
				Log.i("RECORangingActivity", "Remote Exception");
				e.printStackTrace();
			} catch (NullPointerException e) {
				Log.i("RECORangingActivity", "Null Pointer Exception");
				e.printStackTrace();
			}
		}
	}
	@Override
	protected void stop(ArrayList<RECOBeaconRegion> regions) {
		for(RECOBeaconRegion region : regions) {
			try {
				mRecoManager.stopRangingBeaconsInRegion(region);
			} catch (RemoteException e) {
				Log.i("RECORangingActivity", "Remote Exception");
				e.printStackTrace();
			} catch (NullPointerException e) {
				Log.i("RECORangingActivity", "Null Pointer Exception");
				e.printStackTrace();
			}
		}
	}
		@Override
	public void onServiceFail(RECOErrorCode errorCode) {
		return;
	}
		@Override
	public void rangingBeaconsDidFailForRegion(RECOBeaconRegion region, RECOErrorCode errorCode) {
		return;
	}
}