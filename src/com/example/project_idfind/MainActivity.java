package com.example.project_idfind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

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
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;


public class MainActivity extends ActionBarActivity {
	EditText ID,Password;
	Button btnLogin,btnFind,btnNewjoin;
	String mem_id,mem_pwd;
	Intent intent;
	StringBuilder builder;
	String result;
	
	public static String RECO_UUID = "24DDF411-8CF1-440C-87CD-E368DAF9C93E";
	public static final boolean SCAN_RECO_ONLY = true;
	public static final boolean ENABLE_BACKGROUND_RANGING_TIMEOUT = true;
	public static final boolean DISCONTINUOUS_SCAN = false;
	private static final int REQUEST_ENABLE_BT = 1;
	private BluetoothManager mBluetoothManager;
	private BluetoothAdapter mBluetoothAdapter;
	
    @SuppressLint("NewApi") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = mBluetoothManager.getAdapter();
		
		if(mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
			Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
		}
		
        ID = (EditText) findViewById(R.id.inID);
		Password = (EditText) findViewById(R.id.inPW);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnFind = (Button) findViewById(R.id.btnFind);
		btnNewjoin = (Button) findViewById(R.id.btnNewJoin);
		
		Intent getintent = getIntent();
		
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //스레드 가능
    	.detectDiskReads()
    	.detectDiskWrites()
    	.detectNetwork()
    	.penaltyLog()
    	.build());

		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(MainActivity.this, MenuActivity.class);
				mem_id=ID.getText().toString();
				mem_pwd=Password.getText().toString();
				
				//Log.i("MainActivity",mem_id);
				//Log.i("MainActivity",mem_pwd);
				
				HttpPostData(); //웹서버 연결 메소드
				
				StringTokenizer tokened = new StringTokenizer(result,",");
				int numberOfToken = tokened.countTokens();
				String[] memberInfo = new String[50];
				
				for(int i=0 ; i<numberOfToken; i++){
					memberInfo[i]=tokened.nextToken(); //customer 테이블의 정보 받아오기
				} 
				//Log.i("MainActivity",memberInfo[0]);
				
				if(!memberInfo[0].equals("login")){
					Toast.makeText(getBaseContext(), "아이디와 비밀번호를 확인해 주세요.", Toast.LENGTH_LONG).show();}				
				else{
					Toast.makeText(MainActivity.this, "로그인 되었습니다.", Toast.LENGTH_LONG).show();
					intent.putExtra("checked_mem_id", memberInfo[1]);
					intent.putExtra("checked_mem_pw", memberInfo[2]);
					intent.putExtra("checked_mem_name", memberInfo[3]);
					intent.putExtra("checked_mem_email", memberInfo[4]);
					intent.putExtra("checked_mem_phone", memberInfo[5]);
					
					startActivity(intent);
				}
			}
		});
		btnNewjoin.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent = new Intent(MainActivity.this, JoinActivity.class);
				startActivity(intent);
			}
		});
		
		btnFind.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent = new Intent(MainActivity.this, FindActivity.class);
				startActivity(intent);
				
			}
		});
    }
    
    public void HttpPostData(){
		try{
			URL url = new URL("https://cic.hongik.ac.kr/b289076/loginCheck.php");
			
			trustAllHosts(); //ssl socket 적용

            HttpsURLConnection https = (HttpsURLConnection) url.openConnection();
            https.setHostnameVerifier(new HostnameVerifier() {

				@Override
				public boolean verify(String hostname, SSLSession session) {
					// TODO Auto-generated method stub
					return true;
				}
            });

            HttpURLConnection connection = https;			

            connection.setDefaultUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "application/x-www-form-urlencoded");
			
            //buffer:php로 보낼 구문
			StringBuffer buffer = new StringBuffer();
			buffer.append("mem_id=" + mem_id + "&mem_pwd=" + mem_pwd);
			//php로 보내기
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(),"EUC-KR"));
			writer.write(buffer.toString()); 
			writer.flush();
			connection.connect();
			//php에서 받아오기
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"EUC-KR"));
			builder = new StringBuilder();
			builder.append(reader.readLine());

			result = builder.toString();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			
		}
	}
    
    private static void trustAllHosts() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[]{};
            }

            @Override
            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }

            @Override
            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] chain,
                    String authType)
                    throws java.security.cert.CertificateException {
                // TODO Auto-generated method stub

            }
        }};

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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
			//If the request to turn on bluetooth is denied, the app will be finished.
			//사용자가 블루투스 요청을 허용하지 않았을 경우, 어플리케이션은 종료됩니다.
			finish();
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
		@Override
	protected void onResume() {
		Log.i("MainActivity", "onResume()");
		super.onResume();
		
		/*if(this.isBackgroundMonitoringServiceRunning(this)) {
			ToggleButton toggle = (ToggleButton)findViewById(R.id.monitoring_toggle);
			toggle.setChecked(true);
		}
		
		if(this.isBackgroundRangingServiceRunning(this)) {
			ToggleButton toggle = (ToggleButton)findViewById(R.id.background_toggle);
			toggle.setChecked(true);
		}*/
	}
		@Override
	protected void onDestroy() {
		Log.i("MainActivity", "onDestroy");
		super.onDestroy();
	}
	public void onMonitoringToggleButtonClicked(View v) {
		/*ToggleButton toggle = (ToggleButton)v;
		if(toggle.isChecked()) {
			Log.i("MainActivity", "onMonitoringToggleButtonClicked off to on");
			Intent intent = new Intent(this, BackgroundMonitoringService.class);
			startService(intent);
		} else {
			Log.i("MainActivity", "onMonitoringToggleButtonClicked on to off");
			stopService(new Intent(this, BackgroundMonitoringService.class));
		}*/
	}
	public void onRangingToggleButtonClicked(View v) {
		/*ToggleButton toggle = (ToggleButton)v;
		if(toggle.isChecked()) {
			Log.i("MainActivity", "onRangingToggleButtonClicked off to on");
			Intent intent = new Intent(this, BackgroundRangingService.class);
			startService(intent);
		} else {
			Log.i("MainActivity", "onRangingToggleButtonClicked on to off");
			stopService(new Intent(this, BackgroundRangingService.class));
		}*/
	}
	public void onButtonClicked(View v) {
		/*Button btn = (Button)v;
		if(btn.getId() == R.id.rangingButton) {
			final Intent intent = new Intent(this, RangingActivity.class);
			startActivity(intent);
		} else if(btn.getId() == R.id.popupButton) {
			final Intent intent = new Intent(this, PopupActivity.class);
			startActivity(intent);
		}*/
	}
}
