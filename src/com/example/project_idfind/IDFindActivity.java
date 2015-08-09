package com.example.project_idfind;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class IDFindActivity extends Activity {

	TextView setIdFind;
	String find_mem_id;
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.idfind);
	    
	    setIdFind=(TextView)findViewById(R.id.setIdFind);
	
	    Intent getintent = getIntent();
	    find_mem_id =getintent.getExtras().getString("find_mem_id");
	    
	    setIdFind.setText(find_mem_id);
	}
}
