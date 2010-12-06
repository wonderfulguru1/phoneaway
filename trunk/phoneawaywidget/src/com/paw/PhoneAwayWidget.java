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

package com.paw;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.paw.handler.CallControl;
import com.paw.ui.PreferenceActivity;
import com.paw.ui.Status;

/**
 * Phone Away Widget
 * @author prasanta
 *
 */
public class PhoneAwayWidget extends AppWidgetProvider {


	private final String TAG = this.getClass().getSimpleName();
	RemoteViews remote;
	Context context;
	static CallControl callReceiver;
	
	@Override
	public void onEnabled(Context context) {
		Log.d(TAG, "onEnabled()");
		super.onEnabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG, "onReceive()");
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		
		Log.d(TAG, "onUpdate()");
		this.context = context;
		
		// Register Call Receiver
		 registerCallReceiver();
		
		// Load stored pref
		loadPref();
		
		
		// create the remote view
		remote = new RemoteViews(context.getPackageName(), R.layout.main);
		
		//Intent i = new Intent("com.my.receiver.MYBROADCAST");
		Intent i = new Intent(context, PreferenceActivity.class);
		i.setAction("com.paw.ui.ACTION_WIDGET_PREF_BTN");
		//PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
		PendingIntent pi = PendingIntent.getActivity(context, 0, i, 0);
		remote.setOnClickPendingIntent(R.id.status_layout, pi);
		updateRemoteView();
		/*
		 * Make sure you call below line otherwise Click function
		 * doesn't work
		 */
		appWidgetManager.updateAppWidget(new ComponentName(context, PhoneAwayWidget.class), remote);
	}

	/**
	 * TODO: unregister call receiver
	 */
	private void registerCallReceiver(){
		if(callReceiver != null){
			// Receiver is already registered
			return;
		}
		Log.i(TAG, "Register Receiver.........");
		IntentFilter inf = new IntentFilter("android.intent.action.PHONE_STATE");
		
		Log.i(TAG, "Call Receiver instance_New_________");
		callReceiver = new CallControl();
		context.getApplicationContext().registerReceiver(callReceiver, inf, "android.permission.READ_PHONE_STATE", null);
	}
	
	public void updateRemoteView(){
		if(Status.isAvailable){
			remote.setImageViewResource(R.id.status_icon, R.drawable.available);
			remote.setTextViewText(R.id.status, "[Available]");
		}
		else{
			remote.setImageViewResource(R.id.status_icon, R.drawable.away);
			remote.setTextViewText(R.id.status, Status.msg);
		}
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		Log.d(TAG, "onDeleted()");
		//unregisterCallReceiver();
		super.onDeleted(context, appWidgetIds);
	}

	@Override
	public void onDisabled(Context context) {
		Log.d(TAG, "onDisabled()");
		//unregisterCallReceiver();
		super.onDisabled(context);
	}
	
	private void loadPref(){
		SharedPreferences spref = PreferenceManager.getDefaultSharedPreferences(context);
		
		Status.isAvailable = spref.getBoolean(Status.SPREF_IS_AVAILABLE, true);
		Status.isSMSAllowed = spref.getBoolean(Status.SPREF_IS_SMS_ALLOWED, false);
		Status.msg = spref.getString(Status.SPREF_STATUS_MSG, Status.msg);
		Status.max_sms_allowed = spref.getInt(Status.SPREF_MAX_SMS_COUNT, Status.max_sms_allowed);
		Status.count_of_sms_sent = spref.getInt(Status.SPREF_SMS_SENT_COUNT, Status.count_of_sms_sent);
	}
}
