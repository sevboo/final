package com.example.project_idfind;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class StartActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startactivity);
		Handler handler = new Handler(){
			@Override
			public void handleMessage(Message msg){
				super.handleMessage(msg);
				finish();
			}
		};
		handler.sendEmptyMessageDelayed(0, 1500);
		
	}
}
