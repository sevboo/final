package com.example.project_idfind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends FragmentActivity {
	EditText ID,Password;
	Button btnLogin,btnFind,btnNewjoin;
	CheckBox autoLogin,idSave;
	String mem_id,mem_pwd;
	Intent intent;
	StringBuilder builder;
	String result;
	
	//�ڵ��α��� ���� ����
	SharedPreferences setting;
	SharedPreferences.Editor editor;
	
	public static String RECO_UUID = "24DDF411-8CF1-440C-87CD-E368DAF9C93E";
	public static final boolean SCAN_RECO_ONLY = true;
	public static final boolean ENABLE_BACKGROUND_RANGING_TIMEOUT = true;
	public static final boolean DISCONTINUOUS_SCAN = false;
	
	GpsInfo gps;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        
        startActivity(new Intent(this,StartActivity.class));
        
        ID = (EditText) findViewById(R.id.inID);
		Password = (EditText) findViewById(R.id.inPW);
		btnLogin = (Button) findViewById(R.id.btnLogin);
		btnFind = (Button) findViewById(R.id.btnFind);
		btnNewjoin = (Button) findViewById(R.id.btnNewJoin);
		autoLogin = (CheckBox)findViewById(R.id.autologin);	
		idSave = (CheckBox)findViewById(R.id.idsave);
		//�ڵ��α��� ����
		setting = getSharedPreferences("setting", 0);
		editor= setting.edit();
		
		//��Ȯ�� ��ġ������ ���� �α���ȭ�鿡�� ����.
		gps = new GpsInfo(MainActivity.this);
		if(!gps.isGPSEnabled){
			gps.showSettingsAlert();
		}
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //������ ����
    	.detectDiskReads()
    	.detectDiskWrites()
    	.detectNetwork()
    	.penaltyLog()
    	.build());
		
		alarm_on();
		
		btnLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent = new Intent(MainActivity.this, MenuActivity.class);
				mem_id=ID.getText().toString();
				mem_pwd=Password.getText().toString();
				
								
				HttpPostData(); //������ ���� �޼ҵ�
				
				StringTokenizer tokened = new StringTokenizer(result,",");
				int numberOfToken = tokened.countTokens();
				String[] memberInfo = new String[50];
				
				for(int i=0 ; i<numberOfToken; i++){
					memberInfo[i]=tokened.nextToken(); //customer ���̺��� ���� �޾ƿ���
				} 
				//Log.i("MainActivity",memberInfo[0]);
				
				if(!memberInfo[0].equals("login")){
					Toast.makeText(getBaseContext(), "���̵�� ��й�ȣ�� Ȯ���� �ּ���.", Toast.LENGTH_LONG).show();}				
				else{
					Toast.makeText(MainActivity.this, "�α��� �Ǿ����ϴ�.", Toast.LENGTH_LONG).show();
					intent.putExtra("checked_mem_id", memberInfo[1]);
					intent.putExtra("checked_mem_pw", memberInfo[2]);
					intent.putExtra("checked_mem_name", memberInfo[3]);
					intent.putExtra("checked_mem_email", memberInfo[4]);
					intent.putExtra("checked_mem_phone", memberInfo[5]);
					intent.putExtra("checked_mem_cardnum",memberInfo[6]);
					intent.putExtra("checked_mem_cardexpire",memberInfo[7]);
					
					startActivity(intent);
					finish();
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
		autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					String Id = ID.getText().toString();
					String PW = Password.getText().toString();
					
					editor.putString("Id", Id);
					editor.putString("PW", PW);
					editor.putBoolean("Auto_Login_enabled", true);
					editor.commit();
				}else{
					editor.clear();
					editor.commit();
				}
			}
		});
		
		idSave.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					String Id = ID.getText().toString();
					
					editor.putString("Id", Id);
					editor.putBoolean("Id_Save_enabled", true);
					editor.commit();
				}else{
					editor.clear();
					editor.commit();
				}
			}
		});
		
		//�ڵ��α��� üũ����-yes�϶�
		if(setting.getBoolean("Auto_Login_enabled", false)){
			Intent getintent = getIntent();
	        String button_onoff_div = "0";

	        //Toast.makeText(getApplicationContext(), getintent.getExtras().getString("login_button_div"), Toast.LENGTH_LONG).show();
	        if(getintent.getExtras()==null){
	        	ID.setText(setting.getString("Id", ""));
				Password.setText(setting.getString("PW", ""));
				autoLogin.setChecked(true);
				btnLogin.performClick();
	        }else{
	        	ID.setText(setting.getString("Id", ""));
				Password.setText(setting.getString("PW", ""));
				autoLogin.setChecked(true);
				//btnLogin.performClick();
	        }	
		}
		if(setting.getBoolean("Id_Save_enabled", false)){
			Intent getintent = getIntent();
	        String button_onoff_div = "0";

	        //Toast.makeText(getApplicationContext(), getintent.getExtras().getString("login_button_div"), Toast.LENGTH_LONG).show();
	        if(getintent.getExtras()==null){
	        	ID.setText(setting.getString("Id", ""));
				idSave.setChecked(true);

	        }else{
	        	ID.setText(setting.getString("Id", ""));
				idSave.setChecked(true);
				//btnLogin.performClick();
	        }	
		}
		
    }
    
    public void HttpPostData(){
		try{
			URL url = new URL("https://cic.hongik.ac.kr/b289076/loginCheck.php");
			
			trustAllHosts(); //ssl socket ����

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
			
            //buffer:php�� ���� ����
			StringBuffer buffer = new StringBuffer();
			buffer.append("mem_id=" + mem_id + "&mem_pwd=" + mem_pwd);
			//php�� ������
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(),"EUC-KR"));
			writer.write(buffer.toString()); 
			writer.flush();
			connection.connect();
			//php���� �޾ƿ���
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
	
	public void alarm_on(){
		Log.i("MainActivity", "alarm_on()");
		
		AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(MainActivity.this,AlarmReceive.class);
		PendingIntent sender = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
		Calendar calendar = Calendar.getInstance();
		int currentYear=calendar.get(Calendar.YEAR);
		int currentMonth=calendar.get(Calendar.MONTH);
		int currentDate=calendar.get(Calendar.DATE);
		
		calendar.set(currentYear, currentMonth, 25, 12, 00 , 00);
		
		if (currentMonth == Calendar.JANUARY || currentMonth == Calendar.MARCH || currentMonth == Calendar.MAY || currentMonth == Calendar.JULY 
	            || currentMonth == Calendar.AUGUST || currentMonth == Calendar.OCTOBER || currentMonth == Calendar.DECEMBER){
			am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 31, sender);
	    }
	    if (currentMonth == Calendar.APRIL || currentMonth == Calendar.JUNE || currentMonth == Calendar.SEPTEMBER 
	            || currentMonth == Calendar.NOVEMBER){
	    	Log.i("MainActivity","setRepeating");
	    	am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 30, sender);
	        }


	    if  (currentMonth == Calendar.FEBRUARY){
	        GregorianCalendar cal = (GregorianCalendar) GregorianCalendar.getInstance();    
	            if(cal.isLeapYear(currentYear)){
	            	am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 29, sender);
	            }
	            else{ 
	            	am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 28, sender);
	            }
	    }
	}
}
