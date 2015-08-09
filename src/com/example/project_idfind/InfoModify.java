package com.example.project_idfind;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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
import android.widget.TextView;
import android.widget.Toast;

public class InfoModify extends Activity {
	EditText editUserName, editUserPW, editUserPhone, editUserEmail;
	Button btnUpdateComplete, CancleBtn;
	String mem_name, password, mem_phone, mem_email;
	StringBuilder builder;
	String result;
	TextView textUserName;
	//String mem_id = MenuActivity.memInfoArray[0];
	
	// 이름 EditUserName,비밀번호 EditUserPW,핸드폰번호 EditUserPhone ,이메일 EditUserEmail

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.info_modify);
		
		editUserPW = (EditText) findViewById(R.id.EditUserPW);
		editUserPhone = (EditText) findViewById(R.id.EditUserPhone);
		editUserEmail = (EditText) findViewById(R.id.EditUserEmail);
		textUserName = (TextView) findViewById(R.id.textUserName);

		btnUpdateComplete = (Button) findViewById(R.id.ModifyBtn);
		CancleBtn = (Button) findViewById(R.id.CancleBtn);

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				// 스레드 가능
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		textUserName.setText("   "+MenuActivity.memInfoArray[2]);
		editUserPW.setText(MenuActivity.memInfoArray[1]);
		editUserPhone.setText(MenuActivity.memInfoArray[4]);
		editUserEmail.setText(MenuActivity.memInfoArray[3]);

		btnUpdateComplete.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				password = editUserPW.getText().toString();
				mem_phone = editUserPhone.getText().toString();
				mem_email = editUserEmail.getText().toString();

				UpdateMemData();

				MenuActivity.memInfoArray[1] = password;
				MenuActivity.memInfoArray[4] = mem_phone;
				MenuActivity.memInfoArray[3] = mem_email;
				
				finish();
			}
		});
		
		CancleBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish(); //현재 액티비티 종료	
			}
		});		
	}
	

	public void UpdateMemData() {
		try {
			URL url = new URL(
					"https://cic.hongik.ac.kr/b289076/memberModify.php");

			trustAllHosts();

			HttpsURLConnection https = (HttpsURLConnection) url
					.openConnection();
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
			connection.setRequestProperty("content-type",
					"application/x-www-form-urlencoded");

			StringBuffer buffer = new StringBuffer();
			buffer.append("mem_id=" + MenuActivity.memInfoArray[0] + "&password=" + password + "&mem_phone=" + mem_phone
					+ "&mem_email=" + mem_email);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
					connection.getOutputStream(), "EUC-KR"));
			writer.write(buffer.toString());
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "EUC-KR"));
			builder = new StringBuilder();
			builder.append(reader.readLine());

			result = builder.toString();
			
			Toast.makeText(InfoModify.this, "회원 정보가 수정되었습니다. ",	Toast.LENGTH_SHORT).show();

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
				// TODO Auto-generated method stub

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