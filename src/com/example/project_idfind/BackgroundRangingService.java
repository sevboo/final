package com.example.project_idfind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOBeaconRegionState;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECOMonitoringListener;
import com.perples.recosdk.RECORangingListener;
import com.perples.recosdk.RECOServiceConnectListener;

public class BackgroundRangingService extends Service implements
		RECOMonitoringListener, RECORangingListener, RECOServiceConnectListener {
	StringBuilder builder;
	String result;
	GpsInfo gps=new GpsInfo(BackgroundRangingService.this);
	
	String now_time;

	private long mScanDuration = (1 / 2) * 1000L;
	private long mSleepDuration = (1 / 4) * 1000L;
	private long mRegionExpirationTime = 3 * 1000L;
	private int mNotificationID = 9999;
	private RECOBeaconManager mRecoManager;
	private ArrayList<RECOBeaconRegion> mRegions;
	private RangingListAdapter mRangingAdapter;
	
	String lati;
	String longi;
	
	int count = 0;
	String major;

	@Override
	public void onCreate() {
		Log.i("RECOBackgroundRangingService", "onCreate()");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("RECOBackgroundRangingService", "onStartCommand");
		mRecoManager = RECOBeaconManager.getInstance(getApplicationContext(),
				MainActivity.SCAN_RECO_ONLY, false);
		
		lati = intent.getExtras().getString("lati");
		longi = intent.getExtras().getString("longi");
		Log.i("RECOBackgroundRangingService", "1111lati"+lati+", longi"+longi);
		this.bindRECOService();
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		Log.i("RECOBackgroundRangingService", "onDestroy()");
		count=0;
		this.tearDown();
		super.onDestroy();
	}

	@Override
	public void onTaskRemoved(Intent rootIntent) {
		Log.i("RECOBackgroundRangingService", "onTaskRemoved()");
		super.onTaskRemoved(rootIntent);
	}

	private void bindRECOService() {
		Log.i("RECOBackgroundRangingService", "bindRECOService()");

		mRegions = new ArrayList<RECOBeaconRegion>();
		this.generateBeaconRegion();

		mRecoManager.setMonitoringListener(this);
		mRecoManager.setRangingListener(this);
		mRecoManager.bind(this);
	}

	private void generateBeaconRegion() {
		Log.i("RECOBackgroundRangingService", "generateBeaconRegion()");

		RECOBeaconRegion recoRegion;

		recoRegion = new RECOBeaconRegion(MainActivity.RECO_UUID,
				"RECO Sample Region");
		recoRegion.setRegionExpirationTimeMillis(this.mRegionExpirationTime);
		mRegions.add(recoRegion);
	}

	private void startMonitoring() {
		Log.i("RECOBackgroundRangingService", "startMonitoring()");

		mRecoManager.setScanPeriod(this.mScanDuration);
		mRecoManager.setSleepPeriod(this.mSleepDuration);

		for (RECOBeaconRegion region : mRegions) {
			try {
				mRecoManager.startMonitoringForRegion(region);
			} catch (RemoteException e) {
				Log.e("RECOBackgroundRangingService",
						"RemoteException has occured while executing RECOManager.startMonitoringForRegion()");
				e.printStackTrace();
			} catch (NullPointerException e) {
				Log.e("RECOBackgroundRangingService",
						"NullPointerException has occured while executing RECOManager.startMonitoringForRegion()");
				e.printStackTrace();
			}
		}
	}

	private void stopMonitoring() {
		Log.i("RECOBackgroundRangingService", "stopMonitoring()");

		for (RECOBeaconRegion region : mRegions) {
			try {
				mRecoManager.stopMonitoringForRegion(region);
			} catch (RemoteException e) {
				Log.e("RECOBackgroundRangingService",
						"RemoteException has occured while executing RECOManager.stopMonitoringForRegion()");
				e.printStackTrace();
			} catch (NullPointerException e) {
				Log.e("RECOBackgroundRangingService",
						"NullPointerException has occured while executing RECOManager.stopMonitoringForRegion()");
				e.printStackTrace();
			}
		}
	}

	private void startRangingWithRegion(RECOBeaconRegion region) {
		Log.i("RECOBackgroundRangingService", "startRangingWithRegion()");
		try {
			mRecoManager.startRangingBeaconsInRegion(region);
		} catch (RemoteException e) {
			Log.e("RECOBackgroundRangingService",
					"RemoteException has occured while executing RECOManager.startRangingBeaconsInRegion()");
			e.printStackTrace();
		} catch (NullPointerException e) {
			Log.e("RECOBackgroundRangingService",
					"NullPointerException has occured while executing RECOManager.startRangingBeaconsInRegion()");
			e.printStackTrace();
		}
	}

	private void stopRangingWithRegion(RECOBeaconRegion region) {
		Log.i("RECOBackgroundRangingService", "stopRangingWithRegion()");

		try {
			mRecoManager.stopRangingBeaconsInRegion(region);
		} catch (RemoteException e) {
			Log.e("RECOBackgroundRangingService",
					"RemoteException has occured while executing RECOManager.stopRangingBeaconsInRegion()");
			e.printStackTrace();
		} catch (NullPointerException e) {
			Log.e("RECOBackgroundRangingService",
					"NullPointerException has occured while executing RECOManager.stopRangingBeaconsInRegion()");
			e.printStackTrace();
		}
	}

	private void tearDown() {
		Log.i("RECOBackgroundRangingService", "tearDown()");
		this.stopMonitoring();

		try {
			mRecoManager.unbind();
		} catch (RemoteException e) {
			Log.e("RECOBackgroundRangingService",
					"RemoteException has occured while executing unbind()");
			e.printStackTrace();
		}
	}

	@Override
	public void onServiceConnect() {
		Log.i("RECOBackgroundRangingService", "onServiceConnect()");
		this.startMonitoring();
		// Write the code when RECOBeaconManager is bound to RECOBeaconService
	}

	@Override
	public void didDetermineStateForRegion(RECOBeaconRegionState state,
			RECOBeaconRegion region) {
		Log.i("RECOBackgroundRangingService", "didDetermineStateForRegion()");
		// Write the code when the state of the monitored region is changed
	}

	@Override
	public void didEnterRegion(RECOBeaconRegion region,
			Collection<RECOBeacon> beacons) {
		// Get the region and found beacon list in the entered region
		Log.i("RECOBackgroundRangingService",
				"didEnterRegion() - " + region.getUniqueIdentifier());
		// this.popupNotification("Inside of " + region.getUniqueIdentifier());
		// Write the code when the device is enter the region

		this.startRangingWithRegion(region); // start ranging to get beacons
												// inside of the region
		// from now, stop ranging after 10 seconds if the device is not exited

	}

	@Override
	public void didExitRegion(RECOBeaconRegion region) {
		Log.i("RECOBackgroundRangingService",
				"didExitRegion() - " + region.getUniqueIdentifier());
		// this.popupNotification("Outside of " + region.getUniqueIdentifier());
		// Write the code when the device is exit the region

		this.stopRangingWithRegion(region); // stop ranging because the device
											// is outside of the region from now
	}

	@Override
	public void didStartMonitoringForRegion(RECOBeaconRegion region) {
		Log.i("RECOBackgroundRangingService",
				"didStartMonitoringForRegion() - "
						+ region.getUniqueIdentifier());
		// Write the code when starting monitoring the region is started
		// successfully

	}

	@Override
	public void didRangeBeaconsInRegion(Collection<RECOBeacon> beacons,
			RECOBeaconRegion region) {
		Log.i("RECOBackgroundRangingService", "didRangeBeaconsInRegion() - "
				+ region.getUniqueIdentifier() + " with " + beacons.size()
				+ " beacons");
		mRangingAdapter = new RangingListAdapter(this);
		mRangingAdapter.updateAllBeacons(beacons);
		mRangingAdapter.notifyDataSetChanged();
		Log.i("RECOBackgroundRangingService",
				"didRangeBeaconsInRegion() rssi- "
						+ mRangingAdapter.mRangedBeacons.get(0).getRssi()); 
		Log.i("RECOBackgroundRangingService",
				"didRangeBeaconsInRegion() major- "
						+ mRangingAdapter.mRangedBeacons.get(0).getMajor());
		/*
		if(mRangingAdapter.mRangedBeacons.get(0).getRssi()>=-60){
			this.popupNotification("-60db이상 정보 ","major : "+mRangingAdapter.mRangedBeacons.get(0).getMajor()+
				"\n rssi : "+mRangingAdapter.mRangedBeacons.get(0).getRssi());
		}
		*/
		// rssi 크기가 -70db 보다 큰 beacon의 정보를 팝업으로 띄우기(최초 1번만)
		for (int i = 0; i < mRangingAdapter.mRangedBeacons.size(); i++) {
			if (mRangingAdapter.mRangedBeacons.get(i).getRssi() >= -70) {
				count++;
				if (count == 1) {
					major = String.valueOf(mRangingAdapter.mRangedBeacons.get(i).getMajor());

	                now_time = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date(System.currentTimeMillis()));
					
					//아이디, 메이져값, 위도, 경도, 현재시간
					
	                
					HttpPostData();
					this.popupNotification("-60db이상 정보 ","major : "+mRangingAdapter.mRangedBeacons.get(0).getMajor()+
							"\n rssi : "+mRangingAdapter.mRangedBeacons.get(0).getRssi());
					count = 100;
					/*
					new AlertDialog.Builder(this)
					.setTitle("크기가 -70db 보다 큰 Beacon의 정보")
					// x팝업창 타이틀바
					.setMessage(
							"major : "+ mRangingAdapter.mRangedBeacons.get(i).getMajor()
							+ "\nrssi : "+ mRangingAdapter.mRangedBeacons.get(i).getRssi()) // 팝업창 내용
					.setNeutralButton("닫기",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dlg, int sumthin) {
									// 닫기 버튼을 누르면 아무것도 안하고 닫기 때문에 그냥 비움
								}
					}).show(); // 팝업창 보여줌
	                */
				}
				else{
					stopRangingWithRegion(region);
				}
				
			}
		}
	}

	private void popupNotification(String title,String msg) {
		Log.i("RECOBackgroundRangingService", "popupNotification()");
		String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.KOREA).format(new Date());
		long[] vibrate = {0, 100, 200, 300}; 
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NotificationCompat.Builder builder = new NotificationCompat.Builder(this).setSmallIcon(R.drawable.ic_launcher)
																				.setContentTitle(title)
																				.setContentText(msg);
		NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
		builder.setStyle(inboxStyle);
		builder.setVibrate(vibrate);
		//builder.setSound(file://sdcard/notification/ringer.mp3);
		nm.notify(mNotificationID, builder.build());
		mNotificationID = (mNotificationID - 1) % 1000 + 9000;
	}
	@Override
	public IBinder onBind(Intent intent) {
		// This method is not used
		return null;
	}

	@Override
	public void onServiceFail(RECOErrorCode errorCode) {
		// Write the code when the RECOBeaconService is failed.
		// See the RECOErrorCode in the documents.
		return;
	}

	@Override
	public void monitoringDidFailForRegion(RECOBeaconRegion region,
			RECOErrorCode errorCode) {
		// Write the code when the RECOBeaconService is failed to monitor the
		// region.
		// See the RECOErrorCode in the documents.
		return;
	}

	@Override
	public void rangingBeaconsDidFailForRegion(RECOBeaconRegion region,
			RECOErrorCode errorCode) {
		// Write the code when the RECOBeaconService is failed to range beacons
		// in the region.
		// See the RECOErrorCode in the documents.
		return;
	}

	public void HttpPostData() {
		Log.i("RECOBackgroundRangingService", "httppostdata");
		try {
			URL url = new URL("https://cic.hongik.ac.kr/b289076/popupImport.php");// php 파일수정 필요
																					

			trustAllHosts(); // ssl socket 적용

			HttpsURLConnection https = (HttpsURLConnection) url
					.openConnection();
			https.setHostnameVerifier(new HostnameVerifier() {

				@Override
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			});

			HttpURLConnection connection = https;

			connection.setDefaultUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("content-type",
					"application/x-www-form-urlencoded");

			// buffer:php로 보낼 구문
			StringBuffer buffer = new StringBuffer();
			buffer.append("major=" + major+"&mem_id="+MenuActivity.memInfoArray[0]+"&lati="+lati+"&longi="+longi
					+"&now_time="+now_time);
			Log.i("HttpPost","major=" + major+"&mem_id="+MenuActivity.memInfoArray[0]+"&lati="+lati+"&longi="+longi
					+"&now_time="+now_time);
			// php로 보내기
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
					connection.getOutputStream(), "EUC-KR"));
			writer.write(buffer.toString());
			writer.flush();
			connection.connect();
			// php에서 받아오기
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "EUC-KR"));
			builder = new StringBuilder();
			builder.append(reader.readLine());

			result = builder.toString();
			Log.i("HttpPost","php:"+result);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}

	private static void trustAllHosts() {
		// Create a trust manager that does not validate certificate chains
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[] {};
			}

			@Override
			public void checkClientTrusted(
					java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
			}

			@Override
			public void checkServerTrusted(
					java.security.cert.X509Certificate[] chain, String authType)
					throws java.security.cert.CertificateException {
				// TODO Auto-generated method stub

			}
		} };

		// Install the all-trusting trust manager
		try {
			SSLContext sc = SSLContext.getInstance("TLS");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}