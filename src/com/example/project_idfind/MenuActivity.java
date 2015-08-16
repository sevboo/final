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
    static String memInfoArray[] = { "", "", "", "", "" }; // 아이디, 비밀번호, 이름, 이메일, 핸드폰번호
    HashMap<String, List<String>> listDataChild;
    Intent intent;
    TextView textCustomId,textSumCharge;
    
    Switch switch1;
  
    GpsInfo gps;
    String lati;
	String longi;
	
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
 
        textCustomId.setText("회원번호   "+memInfoArray[0]);
        gps = new GpsInfo(MenuActivity.this);
        // GPS 사용유무 가져오기
        if (gps.isGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
             
            lati = String.valueOf(latitude); //위도
            longi = String.valueOf(longitude); //경도
             
            Toast.makeText(
                    getApplicationContext(),
                    "당신의 위치 - \n위도: " + latitude + "\n경도: " + longitude,
                    Toast.LENGTH_LONG).show();
        } else {
            // GPS 를 사용할수 없으므로
            gps.showSettingsAlert();
        }
        
        // preparing list data
        prepareListData();
        
        //php를 걸쳐서 미결제 금액도 표시해줘야함
 
        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);
 
        // setting list adapter
        expListView.setAdapter(listAdapter);
        
        logoutBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				intent = new Intent(MenuActivity.this, MainActivity.class);
				Toast.makeText(getApplicationContext(), "로그아웃 되었습니다.",Toast.LENGTH_LONG).show();
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
                		intent = new Intent(MenuActivity.this, InfoModify.class); //회원 정보 수정
                	}else if(childPosition == 1){
                		intent = new Intent(MenuActivity.this, CardModify.class); //카드 등록 및 수정
                	}
                }else if(groupPosition == 1 ){
                	if(childPosition == 0){
                		intent = new Intent(MenuActivity.this, PayUse.class); //결제 내역
                	}
                }else if(groupPosition == 2 ){
                	if(childPosition == 0){
                		intent = new Intent(MenuActivity.this, RecentUse.class); //최근 내역
                	}else if(childPosition == 1){
                		intent = new Intent(MenuActivity.this, MonthUse.class); //월 사용 내역
                	}
                }else if(groupPosition == 3 ){
                	if(childPosition == 0){
                		intent = new Intent(MenuActivity.this, Notice.class); //공지 사항
                	}else if(childPosition == 1){
                		intent = new Intent(MenuActivity.this, GuidePage.class); //이용 안내
                	}else if(childPosition == 2){
                		intent = new Intent(MenuActivity.this, DeveloperInfo.class); //개발자 정보
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
			ToggleButton toggle = (ToggleButton)findViewById(R.id.background_toggle);
			toggle.setChecked(true);
		}
	}
    
    @Override
	protected void onDestroy() {
		Log.i("MainActivity", "onDestroy");
		super.onDestroy();
	}
    
    /*
     * Preparing the list data
     */
    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();
 
        // Adding child data
        listDataHeader.add("내 정보");
        listDataHeader.add("결제 정보");
        listDataHeader.add("사용 정보");
        listDataHeader.add("기타");
 
        // Adding child data
        List<String> myInfo = new ArrayList<String>();
        myInfo.add("회원 정보 수정");
        myInfo.add("카드 등록 및 수정");
 
        List<String> buyList = new ArrayList<String>();
        buyList.add("결제 내역");
        
        List<String> useList = new ArrayList<String>();
        useList.add("최근 내역");
        useList.add("월 사용 내역");
        
        List<String> etc = new ArrayList<String>();
        etc.add("공지 사항");
        etc.add("이용 안내");
        etc.add("개발자 정보");
        
        listDataChild.put(listDataHeader.get(0), myInfo); // Header, Child data
        listDataChild.put(listDataHeader.get(1), buyList);
        listDataChild.put(listDataHeader.get(2), useList);
        listDataChild.put(listDataHeader.get(3), etc);
    }
    
    public void onRangingToggleButtonClicked(View v) {
		ToggleButton toggle = (ToggleButton)v;
		if(toggle.isChecked()) {
			Log.i("MenuActivity", "onRangingToggleButtonClicked off to on");
			Intent intent = new Intent(this, BackgroundRangingService.class);
			
			intent.putExtra("lati", lati);
			intent.putExtra("longi", longi);
			Log.i("MenuActivity", "lati"+lati+", longi"+longi);
			startService(intent);
			//stopService(new Intent(this, Noselect_BackgroundRangingService.class));
		} else {
			Log.i("MenuActivity", "onRangingToggleButtonClicked on to off");
			stopService(new Intent(this, BackgroundRangingService.class));
			//startService(new Intent(this,Noselect_BackgroundRangingService.class));
		}
	}
    public void onButtonClicked(View v) {
		Button btn = (Button)v;
		if(btn.getId() == R.id.rangingButton) {
			final Intent intent = new Intent(this, RangingActivity.class);
			startActivity(intent);
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