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
import java.util.StringTokenizer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Notification.Builder;
import android.app.PendingIntent.CanceledException;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.BigTextStyle;
import android.util.Log;

import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOBeaconRegionState;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECOMonitoringListener;
import com.perples.recosdk.RECORangingListener;
import com.perples.recosdk.RECOServiceConnectListener;

import com.example.project_idfind.GpsInfo;

public class BackgroundRangingService extends Service implements
		RECOMonitoringListener, RECORangingListener, RECOServiceConnectListener {
	StringBuilder builder;
	String result1,result2;
	String [] result_arr1 = new String[4];
	String [] result_arr2 = new String[4];
	GpsInfo gps;
	
	String now_time,now_time2;

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
	Boolean detection;
	ArrayList<String> result_array = new ArrayList<String>();
	String[][] recentInfo = new String[50][50];

	@Override
	public void onCreate() {
		Log.i("RECOBackgroundRangingService", "onCreate()");
		gps=new GpsInfo(BackgroundRangingService.this);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("RECOBackgroundRangingService", "onStartCommand");
		mRecoManager = RECOBeaconManager.getInstance(getApplicationContext(),
				MainActivity.SCAN_RECO_ONLY, false);
		
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
		
		/*
		if(mRangingAdapter.mRangedBeacons.get(0).getRssi()>=-60){
			this.popupNotification("-60db�̻� ���� ","major : "+mRangingAdapter.mRangedBeacons.get(0).getMajor()+
				"\n rssi : "+mRangingAdapter.mRangedBeacons.get(0).getRssi());
		}
		*/
		// rssi ũ�Ⱑ -70db ���� ū beacon�� ������ �˾����� ����(���� 1����)
		for (int i = 0; i < mRangingAdapter.mRangedBeacons.size(); i++) {
			Log.i("RECOBackgroundRangingService",
					"didRangeBeaconsInRegion() rssi- "
							+ mRangingAdapter.mRangedBeacons.get(0).getRssi()); 
			Log.i("RECOBackgroundRangingService",
					"didRangeBeaconsInRegion() major- "
							+ mRangingAdapter.mRangedBeacons.get(0).getMajor());
			try{
			if (mRangingAdapter.mRangedBeacons.get(i).getRssi() >= -70) {
				count++;
				
				if (count == 1) {
					major = String.valueOf(mRangingAdapter.mRangedBeacons.get(i).getMajor());
					
	                now_time = new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date(System.currentTimeMillis()));
	                now_time2 = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
					//���̵�, ��������, ����, �浵, ����ð�
	                lati = String.valueOf(gps.getLatitude()); //����
	                longi = String.valueOf(gps.getLongitude()); //�浵
	                
					InputData();
					
					StringTokenizer tokened1 = new StringTokenizer(result1,",");
					int numberOfToken1 = tokened1.countTokens();
					
					for(int j=0;j<numberOfToken1;j++){
						result_arr1[j]=tokened1.nextToken();
					}
					
					if(Integer.parseInt(result_arr1[2])==0){
						this.popupNotification("���� ����","�ð�:"+now_time2+"\n�̿�:"+result_arr1[1]+"\n���:"+result_arr1[3]+"\n��ġ:"+lati+","+longi);
						//this.getMessage("���� ����", "�ð�:"+now_time2+"\n�̿�:"+result_arr1[1]+"\n���:"+result_arr1[3]);
					}else if (Integer.parseInt(result_arr1[2])==1){
						this.popupNotification("���� ����","�ð�:"+now_time2+"\n�̿�:"+result_arr1[1]+"\n���:"+result_arr1[3]+"\n��ġ:"+lati+","+longi);
						//this.getMessage("���� ����", "�ð�:"+now_time2+"\n�̿�:"+result_arr1[1]+"\n���:"+result_arr1[3]);
						if(Integer.parseInt(major.substring(0, 1))==1){
							Log.i("RECOBackgroundRangingService","background() - bus on");
							while(true){
								detection=false;
								for (int k = 0; k < mRangingAdapter.mRangedBeacons.size(); k++) {
									if (mRangingAdapter.mRangedBeacons.get(k).getMajor() == Integer.parseInt(major)){
										detection=true;
									}
								}
								if(detection=false){
									OutputData();
									StringTokenizer tokened2 = new StringTokenizer(result2,",");
									int numberOfToken2 = tokened2.countTokens();
									
									for(int a=0;a<numberOfToken2;a++){
										result_arr2[a]=tokened2.nextToken();
									}
									this.popupNotification("���� ����","�ð�:"+now_time2+"\n�̿�:"+result_arr1[1]+"\n���:"+result_arr1[3]+"\n��ġ:"+lati+","+longi);
									//this.getMessage("���� ����", "�ð�:"+now_time2+"\n�̿�:"+result_arr1[1]+"\n���:"+result_arr1[3]);
									break;
								}
							}
						}
					}else{
						this.popupNotification("ȯ�� ����","�ð�:"+now_time2+"\n�̿�:"+result_arr1[1]+"\n���:"+result_arr1[3]+"\n��ġ:"+lati+","+longi);
						//this.getMessage("ȯ�� ����", "�ð�:"+now_time2+"\n�̿�:"+result_arr1[1]+"\n���:"+result_arr1[3]);
						}
					
					
					//this.popupNotification("-60db�̻� ���� ","major : "+mRangingAdapter.mRangedBeacons.get(0).getMajor()+
						//	"\n rssi : "+mRangingAdapter.mRangedBeacons.get(0).getRssi());
					//count = 100;
					/*
					new AlertDialog.Builder(this)
					.setTitle("ũ�Ⱑ -70db ���� ū Beacon�� ����")
					// x�˾�â Ÿ��Ʋ��
					.setMessage(
							"major : "+ mRangingAdapter.mRangedBeacons.get(i).getMajor()
							+ "\nrssi : "+ mRangingAdapter.mRangedBeacons.get(i).getRssi()) // �˾�â ����
					.setNeutralButton("�ݱ�",new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dlg, int sumthin) {
									// �ݱ� ��ư�� ������ �ƹ��͵� ���ϰ� �ݱ� ������ �׳� ���
								}
					}).show(); // �˾�â ������
	                */
				}
				else{
					stopRangingWithRegion(region);
				}
				
			}
			}catch(IndexOutOfBoundsException e){
				
			}
		}
	}
 
	
	private void getMessage(String title,String content){
		Intent popupintent = new Intent(getApplicationContext(),PopupActivity.class);
		popupintent.putExtra("on_off_div", title);
		popupintent.putExtra("content", content);
		PendingIntent pie= PendingIntent.getActivity(getApplicationContext(), 0, popupintent, PendingIntent.FLAG_ONE_SHOT);
		try {
		 pie.send();
		} catch (CanceledException e) {
		}
		
	}
	private void popupNotification(String title,String msg) {
		Log.i("RECOBackgroundRangingService", "popupNotification()");
		//String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.KOREA).format(new Date());
		long[] vibrate = {0, 100, 200, 300}; 
		NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Intent intent = new Intent(this,MainActivity.class);
		PendingIntent pintent1 = PendingIntent.getActivity(this, 0, intent, 0);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
		mBuilder.setSmallIcon(R.drawable.ic_launcher);
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
		/*
		Notification notification = new NotificationCompat.BigTextStyle(
				new NotificationCompat.Builder(getApplicationContext())
				.setContentTitle(title)
				.setContentText(msg)
				.setSmallIcon(R.drawable.ic_launcher)
				.setVibrate(vibrate)
				.setContentIntent(pintent1))
		.setSummaryText("and More+")
		.build();
		nm.notify(0, notification);*/
	}
	@Override
	public IBinder onBind(Intent intent) {
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

	public void InputData() {
		Log.i("RECOBackgroundRangingService", "InputData");
		try {
			URL url = new URL("https://cic.hongik.ac.kr/b289076/popupImport.php");// php ���ϼ��� �ʿ�
																					

			trustAllHosts(); // ssl socket ����

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

			// buffer:php�� ���� ����
			StringBuffer buffer = new StringBuffer();
			buffer.append("major=" + major+"&mem_id="+MenuActivity.memInfoArray[0]+"&lati="+lati+"&longi="+longi
					+"&now_time="+now_time);
			Log.i("InputData","major=" + major+"&mem_id="+MenuActivity.memInfoArray[0]+"&lati="+lati+"&longi="+longi
					+"&now_time="+now_time);
			// php�� ������
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
					connection.getOutputStream(), "EUC-KR"));
			writer.write(buffer.toString());
			writer.flush();
			connection.connect();
			// php���� �޾ƿ���
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "EUC-KR"));
			String resultStr;
			while ((resultStr = reader.readLine()) != null) {
				builder = new StringBuilder();
				builder.append(resultStr);
				result1 = builder.toString();
			}
			
			Log.i("HttpPost","php:"+result1);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {

		}
	}
	public void OutputData() {
		Log.i("RECOBackgroundRangingService", "OutputData");
		try {
			URL url = new URL("https://cic.hongik.ac.kr/b289076/popupExport.php");// php ���ϼ��� �ʿ�
																					

			trustAllHosts(); // ssl socket ����

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

			// buffer:php�� ���� ����
			StringBuffer buffer = new StringBuffer();
			buffer.append("major=" + major+"&mem_id="+MenuActivity.memInfoArray[0]+"&lati="+lati+"&longi="+longi
					+"&now_time="+now_time);
			Log.i("OutputData","major=" + major+"&mem_id="+MenuActivity.memInfoArray[0]+"&lati="+lati+"&longi="+longi
					+"&now_time="+now_time);
			// php�� ������
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
					connection.getOutputStream(), "EUC-KR"));
			writer.write(buffer.toString());
			writer.flush();
			connection.connect();
			// php���� �޾ƿ���
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