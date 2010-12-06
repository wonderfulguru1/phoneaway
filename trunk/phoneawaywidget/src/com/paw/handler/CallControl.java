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

package com.paw.handler;

import java.lang.reflect.Method;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.android.internal.telephony.ITelephony;
import com.paw.ui.Status;

/**
 * This class will interrupt incoming call
 * @author Prasanta Paul
 *
 */
public class CallControl extends BroadcastReceiver {

	private final String TAG = "CallControl";
	Context context;
	String incomingNumber;
	ITelephony telephonyService;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.v(TAG, "Incoming Call BroadCast received...");
		Log.v(TAG, "Intent: ["+ intent.getAction() +"]");
		this.context = context;
		Bundle b = intent.getExtras();
		
		Log.v(TAG, "Phone State: "+ b.getInt(TelephonyManager.EXTRA_STATE));
		
		incomingNumber = b.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
		
		if(!Status.isAvailable & isRinging()){
			// do this only when it is in Ringing_Mode and not when Call is canceled
			Toast.makeText(context, "I can't pick the call", Toast.LENGTH_LONG).show();
			cancelCall();
			sendSMS();
		}else{
			Log.v(TAG, "[No Call Handling]");
		}
	}

	private boolean isRinging(){
		TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			Log.v(TAG, "Get getTeleService...");
			Class c = Class.forName(tm.getClass().getName());
			Method m = c.getDeclaredMethod("getITelephony");
			m.setAccessible(true);
			telephonyService = (ITelephony) m.invoke(tm);
			
			return telephonyService.isRinging();
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG,
					"Error in accessing Telephony Manager: "+ e.toString());
			telephonyService = null;
		}
		
		return false;
	}
	
	private void cancelCall(){
		Log.d(TAG, "Ending call..."+ incomingNumber);
		try {
			if(telephonyService != null)
				telephonyService.endCall();
		} catch (Exception e) {
			e.printStackTrace();
			Log.d(TAG,
					"Error in accessing Telephony Manager: "+ e.toString());
		}
	}
	
	private void sendSMS(){
		Log.i(TAG, "Number of SMS sent: "+ Status.count_of_sms_sent);
		// TODO: Send SMS on a separate Thread using Handler
		if(Status.count_of_sms_sent >= Status.max_sms_allowed){
			Log.d(TAG, "Already sent Max number of SMS...");
			return;
		}
		Log.d(TAG, "Sending SMS...");
		Status.count_of_sms_sent++;
		// TODO: send SMS to the Incoming number
		SmsManager sms = SmsManager.getDefault(); 
		sms.sendTextMessage(incomingNumber, null, Status.msg, null, null); 
		storeSMSCount();
	}
	
	private void storeSMSCount(){
		SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = spref.edit();
		
		editor.putInt(Status.SPREF_SMS_SENT_COUNT, Status.count_of_sms_sent);
		editor.commit();
	}
}
