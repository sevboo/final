package com.example.project_idfind;
 
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.R.string;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECORangingListener;
 
public class MenuActivity extends Activity {
	
	Button logoutBtn;
    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    static String memInfoArray[] = { "", "", "", "", "" }; // ���̵�, ��й�ȣ, �̸�, �̸���, �ڵ�����ȣ
    HashMap<String, List<String>> listDataChild;
    Intent intent;
    TextView textCustomId,textSumCharge;
    
    Switch switch1;
  
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        
        // get the listview
        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        logoutBtn = (Button) findViewById(R.id.logoutBtn);
        textCustomId = (TextView) findViewById(R.id.textCustomId);
        textSumCharge = (TextView) findViewById(R.id.textSumCharge);
        
        Intent getintent = getIntent();
        memInfoArray[0] = getintent.getExtras().getString("checked_mem_id");
		memInfoArray[1] = getintent.getExtras().getString("checked_mem_pw");
		memInfoArray[2] = getintent.getExtras().getString("checked_mem_name");
		memInfoArray[3] = getintent.getExtras().getString("checked_mem_email");
		memInfoArray[4] = getintent.getExtras().getString("checked_mem_phone");
 
        textCustomId.setText("ȸ����ȣ   "+memInfoArray[0]);
        
        // preparing list data
        prepareListData();
<<<<<<< HEAD
        
        //php�� ���ļ� �̰��� �ݾ׵� ǥ���������
=======
>>>>>>> 7f9d7ec7abefdcb7a4bf4e85374f658cb5b7f617
 
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
 
        // setting list adapter
        expListView.setAdapter(listAdapter);
        
        logoutBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent = new Intent(MenuActivity.this, MainActivity.class);
				Toast.makeText(getApplicationContext(), "�α׾ƿ� �Ǿ����ϴ�.",Toast.LENGTH_LONG).show();
				startActivity(intent);
			}
		});
 
        // Listview Group click listener
        expListView.setOnGroupClickListener(new OnGroupClickListener() {
 
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                    int groupPosition, long id) {
                // Toast.makeText(getApplicationContext(),
                // "Group Clicked " + listDataHeader.get(groupPosition),
                // Toast.LENGTH_SHORT).show();
                return false;
            }
        });
 
        // Listview Group expanded listener
        expListView.setOnGroupExpandListener(new OnGroupExpandListener() {
 
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Expanded",
                        Toast.LENGTH_SHORT).show();
            }
        });
 
        // Listview Group collasped listener
        expListView.setOnGroupCollapseListener(new OnGroupCollapseListener() {
 
            @Override
            public void onGroupCollapse(int groupPosition) {
                Toast.makeText(getApplicationContext(),
                        listDataHeader.get(groupPosition) + " Collapsed",
                        Toast.LENGTH_SHORT).show();
            }
        });
 
        // Listview on child click listener
        expListView.setOnChildClickListener(new OnChildClickListener() {
 
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(),listDataHeader.get(groupPosition) + " : "
                   + listDataChild.get(listDataHeader.get(groupPosition)).get(childPosition), Toast.LENGTH_SHORT).show();
                
                if(groupPosition == 0 ){
                	if(childPosition == 0){
                		intent = new Intent(MenuActivity.this, InfoModify.class); //ȸ�� ���� ����
                	}else if(childPosition == 1){
                		intent = new Intent(MenuActivity.this, CardModify.class); //ī�� ��� �� ����
                	}
                }else if(groupPosition == 1 ){
                	if(childPosition == 0){
                		intent = new Intent(MenuActivity.this, PayUse.class); //���� ����
                	}
                }else if(groupPosition == 2 ){
                	if(childPosition == 0){
                		intent = new Intent(MenuActivity.this, RecentUse.class); //�ֱ� ����
                	}else if(childPosition == 1){
                		intent = new Intent(MenuActivity.this, MonthUse.class); //�� ��� ����
                	}
                }else if(groupPosition == 3 ){
                	if(childPosition == 0){
                		intent = new Intent(MenuActivity.this, Notice.class); //���� ����
                	}else if(childPosition == 1){
                		intent = new Intent(MenuActivity.this, GuidePage.class); //�̿� �ȳ�
                	}else if(childPosition == 2){
                		intent = new Intent(MenuActivity.this, DeveloperInfo.class); //������ ����
                	}
                }                
                startActivity(intent);
                
                return false;
            }
        });
    }
    @Override
	protected void onResume() {
		Log.i("MenuActivity", "onResume()");
		super.onResume();
	    
		if(this.isBackgroundRangingServiceRunning(this)) {
			switch1 = (Switch)findViewById(R.id.switch1);
			switch1.setChecked(true);
		}
	}

    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
 
        // Adding child data
        listDataHeader.add("�� ����");
        listDataHeader.add("���� ����");
        listDataHeader.add("��� ����");
        listDataHeader.add("��Ÿ");
 
        // Adding child data
        List<String> myInfo = new ArrayList<String>();
        myInfo.add("ȸ�� ���� ����");
        myInfo.add("ī�� ��� �� ����");
 
        List<String> buyList = new ArrayList<String>();
        buyList.add("���� ����");
        
        List<String> useList = new ArrayList<String>();
        useList.add("�ֱ� ����");
        useList.add("�� ��� ����");
        
        List<String> etc = new ArrayList<String>();
        etc.add("���� ����");
        etc.add("�̿� �ȳ�");
        etc.add("������ ����");
        
        listDataChild.put(listDataHeader.get(0), myInfo); // Header, Child data
        listDataChild.put(listDataHeader.get(1), buyList);
        listDataChild.put(listDataHeader.get(2), useList);
        listDataChild.put(listDataHeader.get(3), etc);
    }
    
    public void onRangingToggleButtonClicked(View v) {
		ToggleButton toggle = (ToggleButton)v;
		if(toggle.isChecked()) {
			Log.i("MainActivity", "onRangingToggleButtonClicked off to on");
			Intent intent = new Intent(this, BackgroundRangingService.class);
			startService(intent);
		} else {
			Log.i("MainActivity", "onRangingToggleButtonClicked on to off");
			stopService(new Intent(this, BackgroundRangingService.class));
			startService(new Intent(this,Noselect_BackgroundRangingService.class));
		}
	}
    private boolean isBackgroundRangingServiceRunning(Context context) {
		ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
		for(RunningServiceInfo runningService : am.getRunningServices(Integer.MAX_VALUE)) {
			if(BackgroundRangingService.class.getName().equals(runningService.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}