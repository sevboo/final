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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class FindActivity extends Activity {
	
	EditText UserName_id,UserEmail_id,UserEmail_pw,ID,SSUM;
	Button IDFindBtn,PWFindBtn;
	String user_id,user_name,user_ssnum,user_email_id,user_email_pw;
	StringBuilder builder;
	String result_ID, result_PW;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.idpwfind);
	    
	    UserName_id = (EditText) findViewById(R.id.userName_id);
	    UserEmail_id = (EditText) findViewById(R.id.userEmail_id);
	    UserEmail_pw = (EditText) findViewById(R.id.userEmail_pw);
	    ID = (EditText) findViewById(R.id.userID_pw);
	    SSUM = (EditText) findViewById(R.id.ssNum_pw);
	    
	    IDFindBtn = (Button) findViewById(R.id.idFindButton);
	    PWFindBtn = (Button) findViewById(R.id.pwFindButton);
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //스레드 가능
    	.detectDiskReads()
    	.detectDiskWrites()
    	.detectNetwork()
    	.penaltyLog()
    	.build());

		IDFindBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				user_name = UserName_id.getText().toString();
				user_email_id = UserEmail_id.getText().toString();
			
				FindIDData(); //웹서버 연동 부분
				
				StringTokenizer tokened = new StringTokenizer(result_ID,",");
				int numberOfToken = tokened.countTokens();
				String[] memberInfo_ID = new String[6];
				
				for(int i=0 ; i<numberOfToken; i++){
					memberInfo_ID[i]=tokened.nextToken(); //customer 테이블의 정보 받아오기
				} 
				
				if(!memberInfo_ID[0].equals("findID")){
					Toast.makeText(getBaseContext(), "정보가 맞지 않습니다.", Toast.LENGTH_LONG).show();}				
				else{
					Intent intent = new Intent(FindActivity.this, IDFindActivity.class);
					intent.putExtra("find_mem_id", memberInfo_ID[1]);
					startActivity(intent);   
				}
			}
		});
		
		PWFindBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				user_ssnum = SSUM.getText().toString();
				user_email_pw = UserEmail_pw.getText().toString();
				user_id = ID.getText().toString();
			
				FindPWData(); //웹서버 연동 부분
				
				if(!result_PW.equals("findPW")){
					Toast.makeText(getBaseContext(), "정보가 맞지 않습니다.", Toast.LENGTH_LONG).show();}				
				else{
					Toast.makeText(FindActivity.this, "이메일을 보냈습니다.", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	public void FindIDData(){
		try{
			URL url = new URL("https://cic.hongik.ac.kr/b289076/idFind.php");
			
			trustAllHosts();
			
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
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("mem_name=" + user_name + "&mem_email="	+ user_email_id);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(),"EUC-KR"));
			writer.write(buffer.toString());
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"EUC-KR"));
			builder = new StringBuilder();
			builder.append(reader.readLine());

			result_ID = builder.toString();
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			
		}
	}
	
	public void FindPWData(){
		try{
			URL url = new URL("https://cic.hongik.ac.kr/b289076/pwFind.php");
			
			trustAllHosts();
			
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
			
			StringBuffer buffer = new StringBuffer();
			buffer.append("mem_ssnum=" + user_ssnum +"&mem_email=" + user_email_pw + "&mem_id=" + user_id);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(),"EUC-KR"));
			writer.write(buffer.toString());
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(),"EUC-KR"));
			builder = new StringBuilder();
			builder.append(reader.readLine());

			result_PW = builder.toString();
			
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
}
