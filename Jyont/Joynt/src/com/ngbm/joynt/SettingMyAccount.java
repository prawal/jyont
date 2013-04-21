package com.ngbm.joynt;

import com.ngbm.joynt.phone_registration.JoyntActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class SettingMyAccount extends Activity{
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.myaccount);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.inc_header);

		((TextView) findViewById(R.id.header_name))
				.setText(R.string.myAccount);
		((TextView) findViewById(R.id.header_left_button_text)).setText("Back");

		findViewById(R.id.header_left_button).setVisibility(0);
		findViewById(R.id.header_right_button).setVisibility(-1);
		setContentView(R.layout.myaccount);
		final Context context = this;
		findViewById(R.id.header_left_button).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_BACK));
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_BACK));
			}
		});
	
		
		View viewButton = findViewById(R.id.settings_account_email);
		
		viewButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingMyAccount.this,
						EditEmailAddress.class);
				SettingMyAccount.this.startActivity(intent);
			}
		});

	View viewButton1 = findViewById(R.id.settings_account_password);
		
		viewButton1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SettingMyAccount.this,
						ChangePassword.class);
				SettingMyAccount.this.startActivity(intent);
			}
		});
	}
}

	
	
	
	
	
	
	
	
