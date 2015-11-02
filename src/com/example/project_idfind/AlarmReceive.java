package com.example.project_idfind;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class AlarmReceive extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("AlarmReceiver","onReceive()");
		
		long[] vibrate = {0, 100, 200, 300}; 
		NotificationManager nm = (NotificationManager)context.getSystemService(context.NOTIFICATION_SERVICE);
		Intent intentActivity = new Intent(context, MainActivity.class);
		PendingIntent pintent1 = PendingIntent.getActivity(context, 0, intentActivity, 0);
		
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
		mBuilder.setSmallIcon(R.drawable.app);
		mBuilder.setContentTitle("공지사항입니다");
		mBuilder.setContentText("오늘은 결제일입니다.\n자동으로 결제되오니 참고하시기 바랍니다.");
		mBuilder.setVibrate(vibrate);
		mBuilder.setContentIntent(pintent1);
			
		NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
		style.setSummaryText("and More +");
		style.setBigContentTitle("공지사항입니다");
		style.bigText("오늘은 결제일입니다.자동으로 결제되니 참고하시기 바랍니다.");
		mBuilder.setStyle(style);
		nm.notify(1, mBuilder.build());


	}


}
