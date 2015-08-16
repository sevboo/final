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

import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class RecentUse extends ActionBarActivity {

	ArrayList<String> result_array = new ArrayList<String>();;
	TableLayout tableLayout;
	StringBuilder builder;
	TableRow tablerow;
	String[][] recentInfo = new String[50][50];
	String mem_id = MenuActivity.memInfoArray[0];

	int[] timeId = { R.id.time1, R.id.time2, R.id.time3, R.id.time4,R.id.time5, R.id.time6,
			R.id.time7, R.id.time8, R.id.time9, R.id.time10	 };
	int[] onoffdivId = { R.id.onoffdiv1, R.id.onoffdiv2, R.id.onoffdiv3, R.id.onoffdiv4,R.id.onoffdiv5, R.id.onoffdiv6,
			R.id.onoffdiv7, R.id.onoffdiv8, R.id.onoffdiv9, R.id.onoffdiv10	 };
	int[] chargeId = { R.id.charge1, R.id.charge2, R.id.charge3, R.id.charge4,R.id.charge5, R.id.charge6,
			R.id.charge7, R.id.charge8, R.id.charge9, R.id.charge10	 };
	
	TextView[] timeText = new TextView[timeId.length];
	TextView[] onoffdivText = new TextView[onoffdivId.length];
	TextView[] chargeText = new TextView[chargeId.length];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recent_use);
		tableLayout = (TableLayout) findViewById(R.id.tableLayout);
		
		tableLayout.setVisibility(android.view.View.VISIBLE);

		for (int i = 0; i < timeId.length; i++) {
			timeText[i] = (TextView) findViewById(timeId[i]);
			onoffdivText[i] = (TextView) findViewById(onoffdivId[i]);
			chargeText[i] = (TextView) findViewById(chargeId[i]);
		}

		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				// 스레드 가능
				.detectDiskReads().detectDiskWrites().detectNetwork()
				.penaltyLog().build());

		HttpPostData(); // 웹서버 연결 메소드

		for (int i=0; i< result_array.size(); i++) {
			//Toast.makeText(getApplication(), "result_array= "+result_array.get(i), Toast.LENGTH_LONG).show();
			StringTokenizer tokened = new StringTokenizer(result_array.get(i), ",");
			int numberOfToken = tokened.countTokens();
			

			for (int j = 0; j < numberOfToken; j++) {
				recentInfo[i][j] = tokened.nextToken(); // log 테이블에서 지정년도의 월수에  따른 금액 합
				//Toast.makeText(getApplication(), "recentInfo["+i+"]["+j+"]="+recentInfo[i][j], Toast.LENGTH_LONG).show();
			}
		}

			
		for (int i = 0; i < result_array.size(); i++) {
			onoffdivText[i].setText(recentInfo[i][0]);
			timeText[i].setText(recentInfo[i][1]);
			chargeText[i].setText(recentInfo[i][2]);
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
