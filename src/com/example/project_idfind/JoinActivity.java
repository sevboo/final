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

import com.example.project_idfind.MenuActivity;
import com.example.project_idfind.R;
import com.example.project_idfind.JoinActivity;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class JoinActivity extends Activity {

	EditText UserName,UserPW,UserBirthYear,UserBirthMonth,UserBirthDay,UserPhone,UserEmail,UserCardNum,UserCardExp;
	Button JoinBtn,CancleBtn;
	String user_id,user_name,user_password,user_birth_year,user_birth_month,user_birth_day,user_phone,user_email,user_card_num,user_card_expire;
	StringBuilder builder;
	String result;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.join);
        
	    UserName = (EditText) findViewById(R.id.UserName);
	    UserPW = (EditText) findViewById(R.id.UserPW);
	    UserBirthYear = (EditText) findViewById(R.id.UserBirthYear);
	    UserBirthMonth = (EditText) findViewById(R.id.UserBirthMonth);
	    UserBirthDay = (EditText) findViewById(R.id.UserBirthDay);
	    UserPhone = (EditText) findViewById(R.id.UserPhone);
	    UserEmail = (EditText) findViewById(R.id.UserEmail);
	    UserCardNum = (EditText) findViewById(R.id.UserCardNum);
	    UserCardExp = (EditText) findViewById(R.id.UserCardExp);
	   
	    JoinBtn = (Button) findViewById(R.id.JoinBtn);
	    CancleBtn = (Button) findViewById(R.id.CancleBtn);
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder() //������ ����
    	.detectDiskReads()
    	.detectDiskWrites()
    	.detectNetwork()
    	.penaltyLog()
    	.build());

		JoinBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				user_name = UserName.getText().toString();
				user_password = UserPW.getText().toString();
				user_birth_year = UserBirthYear.getText().toString();
				user_birth_month = UserBirthMonth.getText().toString();
				user_birth_day = UserBirthDay.getText().toString();
				user_phone = UserPhone.getText().toString();
				user_email = UserEmail.getText().toString();
				user_card_num = UserCardNum.getText().toString();
				user_card_expire = UserCardExp.getText().toString();
			
				JoinMemData(); //������ ���� �κ�
				
				Toast.makeText(JoinActivity.this, "ȸ�������� �Ϸ�Ǿ����ϴ�. ", Toast.LENGTH_SHORT).show();
			}
		});
	
	   
	}
	
	public void JoinMemData(){
		try{
			URL url = new URL("https://cic.hongik.ac.kr/b289076/newMember.php");
			
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
			buffer.append("user_name=" + user_name + "&user_password=" + user_password
					+ "&user_birth_year=" + user_birth_year +"&user_birth_month=" + user_birth_month +
					"&user_birth_day=" + user_birth_day +"&user_phone=" + user_phone+ "&user_email="
					+ user_email+ "&user_card_num=" + user_card_num	+ "&user_card_expire=" + user_card_expire);
			PrintWriter writer = new PrintWriter(new OutputStreamWriter(connection.getOutputStream(),"EUC-KR"));
			writer.write(buffer.toString());
			writer.flush();
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

}
