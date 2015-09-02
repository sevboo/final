package com.example.project_idfind;

import android.app.Activity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.example.project_idfind.RecentUse.use_info;

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MonthUse extends ActionBarActivity {

	Spinner spinner;
	String selectYear;
	String result_Sum = null;
	TableLayout tableLayout;
	StringBuilder builder;
	TableRow tablerow;
	Button inquiryBtn;
	String[] recentInfo = new String[50];
	String mem_id = MenuActivity.memInfoArray[0];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.month_use);
		
		
		spinner = (Spinner) findViewById(R.id.spinner1);
		//tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		//inquiryBtn = (Button) findViewById(R.id.inquiryBtn);
		
		ArrayList<String> list = new ArrayList<String>();

		list.add("[년도를 선택하세요]");

		for (int i = Calendar.getInstance().get(Calendar.YEAR); i > 2000; i--) {
			list.add(i + "");
		}
		
		
		
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_dropdown_item, list);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				selectYear = spinner.getSelectedItem().toString();

				//tableLayout.setVisibility(android.view.View.INVISIBLE);

				Toast.makeText(MonthUse.this, "선택된 년도 : " + selectYear,
						Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
		
		
		
	}

	public void onClick(View v) {
		
		MonthUseAdapter UseAdapter = new MonthUseAdapter(this);
		ListView month_listview = (ListView)findViewById(R.id.month_use);
		month_listview.setAdapter(UseAdapter);
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
		// 스레드 가능
		.detectDiskReads().detectDiskWrites().detectNetwork()
		.penaltyLog().build());
		
		HttpPostData(); // 웹서버 연결 메소드

		Toast.makeText(MonthUse.this, "RESULT :" + result_Sum,
					Toast.LENGTH_LONG).show();

		StringTokenizer tokened = new StringTokenizer(result_Sum, ",");
		int numberOfToken = tokened.countTokens();
		
		for (int i = 0; i < numberOfToken; i++) {
			recentInfo[i] = tokened.nextToken(); // log 테이블에서 지정년도의 월수에 따른
													// 금액 합
			
			month_info month_use = new month_info((i+1)+"월",recentInfo[i]);
			UseAdapter.month_list.add(month_use);
				
		}
		
	}

	class month_info{
		public String div;
		public String pay;
		
		public month_info(String div,String pay){
			this.div=div;
			this.pay=pay;
		}
	}
	
	public void HttpPostData() {
		try {
			URL url = new URL(
					"https://cic.hongik.ac.kr/b289076/monthPaySum.php");

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
			buffer.append("selectYear=" + selectYear + "&mem_id=" + mem_id); //
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

			result_Sum = builder.toString();

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
