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
import java.util.StringTokenizer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import android.widget.Toast;

public class RecentUse extends ActionBarActivity {

	ArrayList<String> result_array = new ArrayList<String>();
	StringBuilder builder;
	String[][] recentInfo = new String[50][50];
	String mem_id = MenuActivity.memInfoArray[0];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recent_use);
		//ArrayList<use_info> use_list = new ArrayList<use_info>();
		recent_use_adapter UseAdapter = new recent_use_adapter(this);
		use_info temp_use = null;
		ListView recent_listview = (ListView)findViewById(R.id.recent_use);
		recent_listview.setAdapter(UseAdapter);	
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// 스레드 가능
		.detectDiskReads().detectDiskWrites().detectNetwork()
		.penaltyLog().build());

		HttpPostData(); // 웹서버 연결 메소드
		
		for (int i=0; i< result_array.size(); i++) {
			StringTokenizer tokened = new StringTokenizer(result_array.get(i), ",");
			int numberOfToken = tokened.countTokens();
			

			for (int j = 0; j < numberOfToken; j++) {
				recentInfo[i][j] = tokened.nextToken(); // log 테이블에서 지정년도의 월수에  따른 금액 합
				
			}
		}
		for (int i = 0; i < result_array.size(); i++) {
			String temp_div=recentInfo[i][0];
			if(temp_div.equalsIgnoreCase("0")){
				temp_use = new use_info("하차",recentInfo[i][1],recentInfo[i][2]);
			}else if(temp_div.equalsIgnoreCase("1")){
				temp_use = new use_info("승차",recentInfo[i][1],recentInfo[i][2]);
			}else if(temp_div.equalsIgnoreCase("2")){
				temp_use = new use_info("환승",recentInfo[i][1],recentInfo[i][2]);
			}
			UseAdapter.use_list.add(temp_use);
		}
		
		
		
	}
	class use_info{
		public String div;
		public String time;
		public String pay;
		
		public use_info(String div,String time,String pay){
			this.div=div;
			this.time=time;
			this.pay=pay;
		}
	}
	public void HttpPostData() {
		try {
			URL url = new URL(
					"https://cic.hongik.ac.kr/b289076/recentUse.php");

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

			// buffer:php로 보낼 구문
			StringBuffer buffer = new StringBuffer();
			buffer.append("mem_id=" + mem_id);
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
				result_array.add(builder.toString());
				
			}

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
