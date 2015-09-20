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
import java.util.StringTokenizer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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
import com.perples.recosdk.RECOServiceConnectListener;

public class BackgroundMonitoringService extends Service implements RECOMonitoringListener, RECOServiceConnectListener{
	StringBuilder builder;
	String result2,lastmajor;
	String lati,longi,now_time,now_time2;
	GpsInfo gps;
	String [] result_arr2 = new String[4];
	
	private long mScanDuration = 1000L;
	private long mSleepDuration = (1 / 4) * 1000L;
	private long mRegionExpirationTime = 3 * 1000L;
	private int mNotificationID = 9999;
	private RECOBeaconManager mRecoManager;
	private ArrayList<RECOBeaconRegion> mRegions;
	int major;
		@Override
	public void onCreate() {
		Log.i("RECOBackgroundMonitoringService", "onCreate()");
		gps=new GpsInfo(BackgroundMonitoringService.this);
		super.onCreate();
	}
		@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("RECOBackgroundMonitoringService", "onStartCommand()");
		mRecoManager = RECOBeaconManager.getInstance(getApplicationContext(), MainActivity.SCAN_RECO_ONLY, 
				MainActivity.ENABLE_BACKGROUND_RANGING_TIMEOUT);
		major=intent.getIntExtra("major", 0);
		this.bindRECOService();
		return START_STICKY;
	}
		@Override
	public void onDestroy() {
		Log.i("RECOBackgroundMonitoringService", "onDestroy()");
		this.tearDown();
		super.onDestroy();
	}
	@Override
	public void onTaskRemoved(Intent rootIntent) {
		Log.i("RECOBackgroundMonitoringService", "onTaskRemoved()");
		super.onTaskRemoved(rootIntent);
	}
		private void bindRECOService() {
		Log.i("RECOBackgroundMonitoringService", "bindRECOService()");
		
		mRegions = new ArrayList<RECOBeaconRegion>();
		this.generateBeaconRegion();
		
		mRecoManager.setMonitoringListener(this);
		mRecoManager.bind(this);
	}
	private void generateBeaconRegion() {
		Log.i("RECOBackgroundMonitoringService", "generateBeaconRegion()-"+major);
		
		RECOBeaconRegion recoRegion;
		
		recoRegion = new RECOBeaconRegion(MainActivity.RECO_UUID, major,"on");
		recoRegion.setRegionExpirationTimeMillis(mRegionExpirationTime);
		mRegions.add(recoRegion);
	}
	private void startMonitoring() {
		Log.i("RECOBackgroundMonitoringService", "startMonitoring()");
		
		mRecoManager.setScanPeriod(mScanDuration);
		mRecoManager.setSleepPeriod(mSleepDuration);
		
		for(RECOBeaconRegion region : mRegions) {
			try {
				mRecoManager.startMonitoringForRegion(region);
			} catch (RemoteException e) {
				Log.e("RECOBackgroundMonitoringService", "RemoteException has occured while executing RECOManager.startMonitoringForRegion()");
				e.printStackTrace();
			} catch (NullPointerException e) {
				Log.e("RECOBackgroundMonitoringService", "NullPointerException has occured while executing RECOManager.startMonitoringForRegion()");
				e.printStackTrace();
			}
		}
	}
	private void stopMonitoring() {
		Log.i("RECOBackgroundMonitoringService", "stopMonitoring()");
		
		for(RECOBeaconRegion region : mRegions) {
			try {
				mRecoManager.stopMonitoringForRegion(region);
			} catch (RemoteException e) {
				Log.e("RECOBackgroundMonitoringService", "RemoteException has occured while executing RECOManager.stopMonitoringForRegion()");
				e.printStackTrace();
			} catch (NullPointerException e) {
				Log.e("RECOBackgroundMonitoringService", "NullPointerException has occured while executing RECOManager.stopMonitoringForRegion()");
				e.printStackTrace();
			}
		}
	}
	private void tearDown() {
		Log.i("RECOBackgroundMonitoringService", "tearDown()");
		this.stopMonitoring();
		
		try {
			mRecoManager.unbind();
		} catch (RemoteException e) {
			Log.e("RECOBackgroundMonitoringService", "RemoteException has occured while executing unbind()");
			e.printStackTrace();
		}
	}
		@Override
	public void onServiceConnect() {
		Log.i("RECOBackgroundMonitoringService", "onServiceConnect()");
		this.startMonitoring();
		//Write the code when RECOBeaconManager is bound to RECOBeaconService
	}
	@Override
	public void didDetermineStateForRegion(RECOBeaconRegionState state, RECOBeaconRegion region) {
		Log.i("RECOBackgroundMonitoringService", "didDetermineStateForRegion()");
		//Write the code when the state of the monitored region is changed
	}
	@Override
	public void didEnterRegion(RECOBeaconRegion region, Collection<RECOBeacon> beacons) {
		//Get the region and found beacon list in the entered region
		Log.i("RECOBackgroundMonitoringService", "didEnterRegion() - " + region.getUniqueIdentifier());
		//this.popupNotification("Inside of " + region.getUniqueIdentifier());
		//Write the code when the device is enter the region
	}
		@Override
	public void didExitRegion(RECOBeaconRegion region) {
		Log.i("RECOBackgroundMonitoringService", "didExitRegion() - " + region.getUniqueIdentifier());
		//this.popupNotification("Outside of " + region.getUniqueIdentifier());
		//Write the code when the device is exit the region
		now_time = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date(System.currentTimeMillis()));
        now_time2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
        lati = String.valueOf(gps.getLatitude()); //위도
        longi = String.valueOf(gps.getLongitude()); //경도
		/*GetLastMajor();
		
		if(Integer.parseInt(lastmajor)==region.getMajor()){
			if(Integer.parseInt(region.getMajor().toString().substring(0, 1))==1){
				OutputData();
				Log.i("RECOBackgroundRangingService","background() - bus off");
				Toast.makeText(getApplicationContext(), "bus off 되었습니다.",Toast.LENGTH_LONG).show();
				
				StringTokenizer tokened2 = new StringTokenizer(result2,",");
				int numberOfToken2 = tokened2.countTokens();
				
				for(int a=0;a<numberOfToken2;a++){
					result_arr2[a]=tokened2.nextToken();
				}
				this.popupNotification("하차 정보","시간:"+now_time+"\n이용:"+result_arr2[1]+"\n요금:"+result_arr2[3]+"\n위치:"+lati+","+longi);
				
			}else if(Integer.parseInt(region.getMajor().toString().substring(0, 1))==2){
				Toast.makeText(getApplicationContext(), "subway off 되었습니다.",Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(getApplicationContext(), "error",Toast.LENGTH_LONG).show();
			}
			Intent intent = new Intent(this, BackgroundRangingService.class);
			startService(intent);
			onDestroy();
		}*/
		//&&major==region.getMajor()
		if(major!=0&&major==region.getMajor()){
			Log.i("RECOBackgroundRangingService","major 비교()-"+region.getMajor().toString().substring(0, 1));
			if(region.getMajor().toString().substring(0, 1).equals("1")){
				OutputData();
				Log.i("RECOBackgroundRangingService","background() - bus off");
				Toast.makeText(getApplicationContext(), "bus off 되었습니다.",Toast.LENGTH_LONG).show();
				
				StringTokenizer tokened2 = new StringTokenizer(result2,",");
				int numberOfToken2 = tokened2.countTokens();
				
				for(int a=0;a<numberOfToken2;a++){
					result_arr2[a]=tokened2.nextToken();
				}
				this.popupNotification("하차 정보","시간:"+now_time+"\n이용:"+result_arr2[1]+"\n요금:"+result_arr2[3]+"\n위치:"+lati+","+longi);
				
			}else if(region.getMajor().toString().substring(0, 1).equals("2")){
				Toast.makeText(getApplicationContext(), "subway off 되었습니다.",Toast.LENGTH_LONG).show();
			}else{
				Toast.makeText(getApplicationContext(), "error",Toast.LENGTH_LONG).show();
			}
			Intent intent = new Intent(this, BackgroundRangingService.class);
			startService(intent);
			onDestroy();
		}
	}
	@Override
	public void didStartMonitoringForRegion(RECOBeaconRegion region) {
		Log.i("RECOBackgroundMonitoringService", "didStartMonitoringForRegion() - " + region.getUniqueIdentifier());
		//Write the code when starting monitoring the region is started successfully
	}
	private void popupNotification(String title,String msg) {
		Log.i("RECOBackgroundRangingService", "popupNotification()");
		//String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.KOREA).format(new Date());
		long[] vibrate = {0, 100, 200, 300}; 
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Intent intent = new Intent(this,MainActivity.class);
		PendingIntent pintent1 = PendingIntent.getActivity(this, 0, intent, 0);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setSmallIcon(R.drawable.app);
		mBuilder.setContentTitle(title);
		mBuilder.setContentText(msg);
		mBuilder.setVibrate(vibrate);
		mBuilder.setContentIntent(pintent1);
		
		NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
		style.setSummaryText("and More +");
		style.setBigContentTitle(title);
		style.bigText(msg);
		mBuilder.setStyle(style);
		nm.notify(0, mBuilder.build());
	}
	public void OutputData() {
		Log.i("RECOBackgroundRangingService", "OutputData");
		try {
			URL url = new URL("https://cic.hongik.ac.kr/b289076/popupExport.php");// php 파일수정 필요
																					

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
			Log.i("OutputData","major=" + major+"&mem_id="+MenuActivity.memInfoArray[0]+"&lati="+lati+"&longi="+longi
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
			String resultStr;
			while ((resultStr = reader.readLine()) != null) {
				builder = new StringBuilder();
				builder.append(resultStr);
				result2 = builder.toString();
			}
			
			Log.i("HttpPost","php:"+result2);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}
	/*
	public void GetLastMajor() {
		Log.i("RECOBackgroundRangingService", "OutputData");
		try {
			URL url = new URL("https://cic.hongik.ac.kr/b289076/getlastMajor.php");// php 파일수정 필요
																					

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

			connection.connect();
			// php에서 받아오기
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "EUC-KR"));
			String resultStr;
			while ((resultStr = reader.readLine()) != null) {
				builder = new StringBuilder();
				builder.append(resultStr);
				lastmajor = builder.toString();
			}
			
			Log.i("HttpPost","php:"+lastmajor);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}*/
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
	@Override
	public IBinder onBind(Intent intent) {
		// This method is not used
		return null;
	}
		@Override
	public void onServiceFail(RECOErrorCode errorCode) {
		//Write the code when the RECOBeaconService is failed.
		//See the RECOErrorCode in the documents.
		return;
	}
		@Override
	public void monitoringDidFailForRegion(RECOBeaconRegion region, RECOErrorCode errorCode) {
		//Write the code when the RECOBeaconService is failed to monitor the region.
		//See the RECOErrorCode in the documents.
		return;
	}
}