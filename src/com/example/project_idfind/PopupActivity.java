package com.example.project_idfind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class PopupActivity extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.popup);
		Intent getintent = getIntent();
		//getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED|
		//		WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD|WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		TextView title = (TextView)findViewById(R.id.on_off_div);
		TextView content = (TextView)findViewById(R.id.content);
		Button ok = (Button)findViewById(R.id.popup_ok);
		Button close = (Button)findViewById(R.id.popup_close);
		
		title.setText(getintent.getExtras().getString("on_off_div"));
		content.setText(getintent.getExtras().getString("content"));
		
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(PopupActivity.this,MenuActivity.class);
				startActivity(intent);
				finish();
			}
	    });
		close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
	    });
	}
	
}
