/* Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, Doubango Telecom.
*
* Contact: Mamadou Diop <diopmamadou(at)doubango(dot)org>
*	
* This file is part of imsdroid Project (http://code.google.com/p/imsdroid)
*
* imsdroid is free software: you can redistribute it and/or modify it under the terms of 
* the GNU General Public License as published by the Free Software Foundation, either version 3 
* of the License, or (at your option) any later version.
*	
* imsdroid is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
* See the GNU General Public License for more details.
*	
* You should have received a copy of the GNU General Public License along 
* with this program; if not, write to the Free Software Foundation, Inc., 
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package com.ngbm.joynt;

import org.doubango.ngn.utils.NgnConfigurationEntry;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import com.ngbm.joynt.database.DatabaseController;
import com.ngbm.joynt.phone_registration.DisplayNameAcivity;
import com.ngbm.joynt.phone_registration.JoyntActivity;
import com.ngbm.joynt.phone_registration.UseMyContact;
import com.ngbm.joynt.phone_registration.VerificationActivity;

public class ScreenSplash extends BaseScreen {
	private static String TAG = ScreenSplash.class.getCanonicalName();
	
	private BroadcastReceiver mBroadCastRecv;
	public static ScreenSplash main;
	public DatabaseController dbController = new DatabaseController(this);
	
	public ScreenSplash() {
		super(SCREEN_TYPE.SPLASH_T, TAG);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.screen_splash);
		main = this;
		Log.d("Launcher Activity", dbController.getVerificationStatusSetting());
		if (dbController.isFirstRun()) {
			dbController.initializeSettings();
			final Intent intent = new Intent(this, JoyntActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			this.startActivity(intent);

		} else {
			if (dbController.getVerificationStatusSetting().equalsIgnoreCase(
					Constants.PhoneNotVerified)) {
				final Intent intent = new Intent(this,
						VerificationActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.startActivity(intent);


			} else if (dbController.getVerificationStatusSetting()
					.equalsIgnoreCase(Constants.PhoneVerificationCompleted)) {
				final Intent intent = new Intent(this, UseMyContact.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.startActivity(intent);

			} else if (dbController.getVerificationStatusSetting()
					.equalsIgnoreCase(Constants.ContactStatusCompleted)) {
				final Intent intent = new Intent(this, DisplayNameAcivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				this.startActivity(intent);

			} else if (dbController.getVerificationStatusSetting()
					.equalsIgnoreCase("NEW")) {
				mScreenService.show(JoyntActivity.class);
//				final Intent intent = new Intent(this, JoyntActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				this.startActivity(intent);
			}
		}
		mBroadCastRecv = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				final String action = intent.getAction();
				Log.d(TAG, "onReceive()");
				
				if(NativeService.ACTION_STATE_EVENT.equals(action)){
					if(intent.getBooleanExtra("started", false)){
						mScreenService.show(HomeActivity.class);
						getEngine().getConfigurationService().putBoolean(NgnConfigurationEntry.GENERAL_AUTOSTART, true);
						finish();
					}
				}
			}
		};
		final IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(NativeService.ACTION_STATE_EVENT);
	    registerReceiver(mBroadCastRecv, intentFilter);
	}
	
	@Override
	protected void onDestroy() {
		if(mBroadCastRecv != null){
			unregisterReceiver(mBroadCastRecv);
		}
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		final Engine engine = getEngine();
			
		final Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				if(!engine.isStarted()){
					Log.d(TAG, "Starts the engine from the splash screen");
					engine.start();
				}
			}
		});
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();
	}
}