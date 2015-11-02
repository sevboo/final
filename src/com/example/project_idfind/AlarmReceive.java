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
		mBuilder.setContentTitle("���������Դϴ�");
		mBuilder.setContentText("������ �������Դϴ�.\n�ڵ����� �����ǿ��� �����Ͻñ� �ٶ��ϴ�.");
		mBuilder.setVibrate(vibrate);
		mBuilder.setContentIntent(pintent1);
			
		NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
		style.setSummaryText("and More +");
		style.setBigContentTitle("���������Դϴ�");
		style.bigText("������ �������Դϴ�.�ڵ����� �����Ǵ� �����Ͻñ� �ٶ��ϴ�.");
		mBuilder.setStyle(style);
		nm.notify(1, mBuilder.build());


	}


}
