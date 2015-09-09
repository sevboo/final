package com.example.project_idfind;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

 
public class MenuActivity extends Activity{
	
	Button logoutBtn;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    static String memInfoArray[] = { "", "", "", "", "","","" }; // 아이디, 비밀번호, 이름, 이메일, 핸드폰번호,카드번호,카드유효기간
    HashMap<String, List<String>> listDataChild;
    Intent intent;
    TextView textCustomId,textSumCharge;
    
    Switch switch1;

	StringBuilder builder;
	String result;
	
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	private static final int REQUEST_ENABLE_BT = 1;
	
	@SuppressLint("NewApi")@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        textCustomId = (TextView) findViewById(R.id.textCustomId);
        textSumCharge = (TextView) findViewById(R.id.textSumCharge);
        
        Intent getintent = getIntent();
        memInfoArray[0] = getintent.getExtras().getString("checked_mem_id");
		memInfoArray[1] = getintent.getExtras().getString("checked_mem_pw");
		memInfoArray[2] = getintent.getExtras().getString("checked_mem_name");
		memInfoArray[3] = getintent.getExtras().getString("checked_mem_email");
		memInfoArray[4] = getintent.getExtras().getString("checked_mem_phone");
		memInfoArray[5] = getintent.getExtras().getString("checked_mem_cardnum");
		memInfoArray[6] = getintent.getExtras().getString("checked_mem_cardexpire");
		
		mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		
		if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {  //블루투스 안켜져있을때
			Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
		}
		
        textCustomId.setText("회원번호   "+memInfoArray[0]);
        
        //php를 걸쳐서 미결제 금액도 표시해줘야함
        NonPayData();
        textSumCharge.setText(result+"원");
        
        
        //background 서비스 시작
        intent = new Intent(this, BackgroundRangingService.class);

		startService(intent);
		
        // preparing list data
        prepareListData();
        
        
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
 
        // setting list adapter
        expListView.setAdapter(listAdapter);
        
        logoutBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent = new Intent(MenuActivity.this, MainActivity.class);
				Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.",Toast.LENGTH_LONG).show();
				intent.putExtra("login_button_div", "0");
				startActivity(intent);
				finish();
			}
		});
 
        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {
 
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                    int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });
 
        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
 
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });
 
        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
 
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();
            }
        });
 
        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {
 
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(),listDataHeader.get(groupPosition) + " : "
                   + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                
                if(groupPosition == 0 ){
                	if(childPosition == 0){
                		intent = new Intent(MenuActivity.this, InfoModify.class); //회원 정보 수정
                	}else if(childPosition == 1){
                		intent = new Intent(MenuActivity.this, CardModify.class); //카드 등록 및 수정
                	}
                }else if(groupPosition == 1 ){
                	if(childPosition == 0){
                		intent = new Intent(MenuActivity.this, PayUse.class); //결제 내역
                		intent = new Intent(MenuActivity.this, PayActivity.class); //결제하기
                	}
                }else if(groupPosition == 2 ){
                	if(childPosition == 0){
                		intent = new Intent(MenuActivity.this, RecentUse.class); //최근 내역
                	}else if(childPosition == 1){
                		intent = new Intent(MenuActivity.this, MonthUse.class); //월 사용 내역
                	}
                }else if(groupPosition == 3 ){
                	if(childPosition == 0){
                		intent = new Intent(MenuActivity.this, Notice.class); //공지 사항
                	}else if(childPosition == 1){
                		intent = new Intent(MenuActivity.this, GuidePage.class); //이용 안내
                	}else if(childPosition == 2){
                		intent = new Intent(MenuActivity.this, DeveloperInfo.class); //개발자 정보
                	}
                }                
                startActivity(intent);
                
                return false;
            }
        });
    }
    /*
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED || gps.isGetLocation == false) {
			//If the request to turn on bluetooth is denied, the app will be finished.
			//사용자가 블루투스 요청을 허용하지 않았을 경우, 어플리케이션은 종료됩니다.
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
    */
    @Override
	protected void onResume() {
		Log.i("MenuActivity", "onResume()");
		super.onResume();
		

		if(this.isBackgroundRangingServiceRunning(this)) {
			ToggleButton toggle = (ToggleButton)findViewById(R.id.background_toggle);
			toggle.setChecked(true);
		}
	}
    
    @Override
	protected void onDestroy() {
		Log.i("MainActivity", "onDestroy");
		super.onDestroy();
	}
    
    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
 
        // Adding child data
        listDataHeader.add("내 정보");
        listDataHeader.add("결제 정보");
        listDataHeader.add("사용 정보");
        listDataHeader.add("기타");
 
        // Adding child data
        List<String> myInfo = new ArrayList<String>();
        myInfo.add("회원 정보 수정");
        myInfo.add("카드 등록 및 수정");
 
        List<String> buyList = new ArrayList<String>();
        buyList.add("결제 내역");
        
        List<String> useList = new ArrayList<String>();
        useList.add("최근 내역");
        useList.add("월 사용 내역");
        
        List<String> etc = new ArrayList<String>();
        etc.add("공지 사항");
        etc.add("이용 안내");
        etc.add("개발자 정보");
        
        listDataChild.put(listDataHeader.get(0), myInfo); // Header, Child data
        listDataChild.put(listDataHeader.get(1), buyList);
        listDataChild.put(listDataHeader.get(2), useList);
        listDataChild.put(listDataHeader.get(3), etc);
    }
    
    public void onRangingToggleButtonClicked(View v) {
		ToggleButton toggle = (ToggleButton)v;
		if(toggle.isChecked()) {
			Log.i("MenuActivity", "onRangingToggleButtonClicked off to on");
			Intent intent = new Intent(this, BackgroundRangingService.class);
			//Log.i("MenuActivity", "lati"+lati+", longi"+longi);
			startService(intent);
			//stopService(new Intent(this, Noselect_BackgroundRangingService.class));
		} else {
			Log.i("MenuActivity", "onRangingToggleButtonClicked on to off");
			stopService(new Intent(this, BackgroundRangingService.class));
			//startService(new Intent(this,Noselect_BackgroundRangingService.class));
		}
	}

    private boolean isBackgroundRangingServiceRunning(Context context) {
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		for(RunningServiceInfo runningService : am.getRunningServices(Integer.MAX_VALUE)) {
			if(BackgroundRangingService.class.getName().equals(runningService.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
    public void NonPayData() {
		Log.i("RECOBackgroundRangingService", "OutputData");
		try {
			URL url = new URL("https://cic.hongik.ac.kr/b289076/NonPaySum.php");// php 파일수정 필요
																					

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
			buffer.append("mem_id="+memInfoArray[0]);
			Log.i("HttpPost","id:"+buffer.toString());
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
				result = builder.toString();
			}
			
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