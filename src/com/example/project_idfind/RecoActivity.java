package com.example.project_idfind;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;

import com.example.project_idfind.MainActivity;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOServiceConnectListener;


public abstract class RecoActivity extends Activity implements RECOServiceConnectListener {
	protected RECOBeaconManager mRecoManager;
	protected ArrayList<RECOBeaconRegion> mRegions;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mRecoManager = RECOBeaconManager.getInstance(getApplicationContext(), MainActivity.SCAN_RECO_ONLY, 
				MainActivity.ENABLE_BACKGROUND_RANGING_TIMEOUT);
		
		mRegions = this.generateBeaconRegion();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	private ArrayList<RECOBeaconRegion> generateBeaconRegion() {
		ArrayList<RECOBeaconRegion> regions = new ArrayList<RECOBeaconRegion>();
		
		RECOBeaconRegion recoRegion;
		recoRegion = new RECOBeaconRegion(MainActivity.RECO_UUID, "RECO Sample Region");
		regions.add(recoRegion);
		return regions;
	}
		protected abstract void start(ArrayList<RECOBeaconRegion> regions);
	protected abstract void stop(ArrayList<RECOBeaconRegion> regions);


}
