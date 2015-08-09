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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CardModify extends Activity {

	EditText editCardNum1, editCardNum2, editCardNum3, editCardNum4,
			editExpirationDateMonth, editExpirationDateYear;
	String mem_id = MenuActivity.memInfoArray[0];
	Button cardModifyButton;
	Intent intent;
	StringBuilder builder;
	String cardNum1, cardNum2, cardNum3, cardNum4, expirationDateMonth,
			expirationDateYear, result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_enroll);

		editCardNum1 = (EditText) findViewById(R.id.cardNum1);
		editCardNum2 = (EditText) findViewById(R.id.cardNum2);
		editCardNum3 = (EditText) findViewById(R.id.cardNum3);
		editCardNum4 = (EditText) findViewById(R.id.cardNum4);
		editExpirationDateMonth = (EditText) findViewById(R.id.expirationDateMonth);
		editExpirationDateYear = (EditText) findViewById(R.id.expirationDateYear);

		cardModifyButton = (Button) findViewById(R.id.cardModifyButton);

		cardModifyButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cardNum1 = editCardNum1.getText().toString();
				cardNum2 = editCardNum2.getText().toString();
				cardNum3 = editCardNum3.getText().toString();
				cardNum4 = editCardNum4.getText().toString();
				expirationDateMonth = editExpirationDateMonth.getText()
						.toString();
				expirationDateYear = editExpirationDateYear.getText()
						.toString();

				UpdateCardData();

			}
		});

	}

	public void UpdateCardData() {
		try {
			URL url = new URL("https://cic.hongik.ac.kr/b289076/cardEnroll.php");

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
			buffer.append("mem_id=" + mem_id + "&card_num1=" + cardNum1
					+ "&card_num2=" + cardNum2 + "&card_num3=" + cardNum3
					+ "&card_num4=" + cardNum4 + "&expirationDateMonth="
					+ expirationDateMonth + "&expirationDateYear="
					+ expirationDateYear);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(
					connection.getOutputStream(), "EUC-KR"));
			writer.write(buffer.toString());
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					connection.getInputStream(), "EUC-KR"));
			builder = new StringBuilder();
			builder.append(reader.readLine());

			result = builder.toString();

			Toast.makeText(CardModify.this, "카드 정보가 수정되었습니다. ",
					Toast.LENGTH_SHORT).show();

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
