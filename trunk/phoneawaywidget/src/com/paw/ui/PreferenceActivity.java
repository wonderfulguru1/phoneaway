/*
 * Copyright (C) 2010 Prasanta Paul, http://prasanta-paul.blogspot.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.paw.ui;


import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RemoteViews;

import com.paw.PhoneAwayWidget;
import com.paw.R;

/**
 * Preference UI
 * @author Prasanta Paul
 *
 */
public class PreferenceActivity extends Activity implements OnClickListener {
	
	private final String  TAG = this.getClass().getSimpleName();
	private RadioButton avail, sms_notify;
	private EditText status, sms_max_count;
	private Button updt,clrtxt;
	private boolean stat;
	private boolean sms_state;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pref);
        
        // load stored preference
        loadPref();
        
        avail = (RadioButton) findViewById(R.id.pref_avl_radio);
        avail.setOnClickListener(this);
        avail.setChecked(Status.isAvailable);
        stat = avail.isChecked();
        Log.i(TAG, "Initial Value of avail "+stat);
        
        sms_notify = (RadioButton) findViewById(R.id.pref_sms_radio);
        sms_notify.setChecked(Status.isSMSAllowed);
        sms_notify.setEnabled(!Status.isAvailable);
        sms_state = sms_notify.isChecked();
        sms_notify.setOnClickListener(this);
        
        sms_max_count = (EditText) findViewById(R.id.pref_sms_max_count_edit);
        sms_max_count.setEnabled(Status.isSMSAllowed);
        
        status = (EditText) findViewById(R.id.pref_edit);
        status.setOnClickListener(this);
        status.setEnabled(!Status.isAvailable);
        
        updt = (Button) findViewById(R.id.pref_update_btn);
        updt.setOnClickListener(this);
        clrtxt = (Button) findViewById(R.id.pref_clear_btn);
        clrtxt.setOnClickListener(this);
    }


	public void onClick(View v) 
	{
		if(v==updt)
		{
			Status.isAvailable = avail.isChecked();
			if(Status.isAvailable){
				Status.msg = "[Available]";
			}
			else{
				Status.count_of_sms_sent = 0;
				Status.isSMSAllowed = sms_notify.isChecked();
				Status.msg = status.getText().toString();
				try{
					Status.max_sms_allowed = Integer.parseInt(sms_max_count.getText().toString());
				}catch(Exception ex){}
			}
			
			// Store user selection
			storePref();
			
			updateRemoteView();
			// TODO: Update DB
			finish();
			return;
		}
		else if(v==clrtxt)
		{
			status.setText("");
		}
		else if(v==avail)
		{
			if(stat==true)
			{
				// Enable updation of status
				avail.setChecked(false);
				stat = avail.isChecked();
				status.setEnabled(true);
				// make SMS options read only
				sms_notify.setChecked(true);
				sms_state = true;
				sms_notify.setEnabled(true);
				sms_max_count.setEnabled(true);
			}
			else
			{
				// Disable updation of status
				avail.setChecked(true);
				stat = avail.isChecked();
				status.setEnabled(false);
				// make SMS options editable
				sms_notify.setChecked(false);
				sms_state = false;
				sms_notify.setEnabled(false);
				sms_max_count.setEnabled(false);
			}
		}
		else if(v == sms_notify){
			Log.v(TAG, "Check_State="+ sms_notify.isChecked());
			if(sms_state){
				sms_state = !sms_state;
				sms_notify.setChecked(sms_state);
				sms_max_count.setEnabled(false);
			}else{
				sms_state = !sms_state;
				sms_notify.setChecked(sms_state);
				sms_max_count.setEnabled(true);
			}
		}
	}
	
	private void updateRemoteView(){
		RemoteViews remote =  new RemoteViews(this.getPackageName(), R.layout.main);
		if(Status.isAvailable){
			remote.setImageViewResource(R.id.status_icon, R.drawable.available);
			remote.setTextViewText(R.id.status, "[Available]");
		}
		else{
			remote.setImageViewResource(R.id.status_icon, R.drawable.away);
			remote.setTextViewText(R.id.status, Status.msg);
		}
		/*
		 * Make sure you call below line otherwise Click function
		 * doesn't work
		 */
		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this.getApplicationContext());
		appWidgetManager.updateAppWidget(new ComponentName(this.getApplicationContext(), PhoneAwayWidget.class), remote);
	}
	
	private void storePref() {
		SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = spref.edit();
		
		editor.putBoolean(Status.SPREF_IS_AVAILABLE, Status.isAvailable);
		editor.putBoolean(Status.SPREF_IS_SMS_ALLOWED, Status.isSMSAllowed);
		editor.putString(Status.SPREF_STATUS_MSG, Status.msg);
		editor.putInt(Status.SPREF_MAX_SMS_COUNT, Status.max_sms_allowed);
		editor.putInt(Status.SPREF_SMS_SENT_COUNT, Status.count_of_sms_sent);
		
		editor.commit();
	}
	
	private void loadPref(){
		SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(this);
		
		Status.isAvailable = spref.getBoolean(Status.SPREF_IS_AVAILABLE, true);
		Status.isSMSAllowed = spref.getBoolean(Status.SPREF_IS_SMS_ALLOWED, false);
		Status.msg = spref.getString(Status.SPREF_STATUS_MSG, Status.msg);
		Status.max_sms_allowed = spref.getInt(Status.SPREF_MAX_SMS_COUNT, Status.max_sms_allowed);
		Status.count_of_sms_sent = spref.getInt(Status.SPREF_SMS_SENT_COUNT, Status.count_of_sms_sent);
	}
}
