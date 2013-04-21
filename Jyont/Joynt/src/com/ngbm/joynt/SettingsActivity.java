package com.ngbm.joynt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.ngbm.joynt.custom_ui.ToolBarBaseActivity;

public class SettingsActivity extends ToolBarBaseActivity implements OnClickListener {
	private static String TAG = SettingsActivity.class.getCanonicalName();
	
	public SettingsActivity() {
		super(SCREEN_TYPE.SETTINGS_T, TAG);
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.settings, this);
		

		((TextView) findViewById(R.id.header_name))
				.setText(R.string.settingsTitle);
		((TextView) findViewById(R.id.header_left_button_text))
		.setText("Edit");
		
		findViewById(R.id.header_left_button)
				.setVisibility(-1);
		findViewById(R.id.header_right_button)
				.setVisibility(-1);
		
		View viewButton = findViewById(R.id.settings_profile);
		
		((TextView)viewButton.findViewById(android.R.id.text1)).setText("Profile");
		viewButton.setOnClickListener(this);
		
		viewButton = findViewById(R.id.settings_myaccount);
		((TextView)viewButton.findViewById(android.R.id.text1)).setText("My Account");
		viewButton.setOnClickListener(this);
		
		viewButton = findViewById(R.id.settings_chat_room);
		((TextView)viewButton.findViewById(android.R.id.text1)).setText("Chat Rooms");
		viewButton.setOnClickListener(this);
		
		viewButton = findViewById(R.id.settings_notification);
		((TextView)viewButton.findViewById(android.R.id.text1)).setText("Notifications");
		((TextView)viewButton.findViewById(android.R.id.text2)).setText("ON");
		viewButton.setOnClickListener(this);
		
		viewButton = findViewById(R.id.settings_voicecall);
		((TextView)viewButton.findViewById(android.R.id.text1)).setText("Voice Call");
		((TextView)viewButton.findViewById(android.R.id.text2)).setText("OFF");
		viewButton.setOnClickListener(this);
		
		viewButton = findViewById(R.id.settings_privacy);
		((TextView)viewButton.findViewById(android.R.id.text1)).setText("Privacy Settings");
		viewButton.setOnClickListener(this);
		
		viewButton = findViewById(R.id.settings_news);
		((TextView)viewButton.findViewById(android.R.id.text1)).setText("News");
		((TextView)viewButton.findViewById(android.R.id.text2)).setText("0");
		viewButton.setOnClickListener(this);
		
		viewButton = findViewById(R.id.settings_help);
		((TextView)viewButton.findViewById(android.R.id.text1)).setText("Help");
		viewButton.setOnClickListener(this);
		
		viewButton = findViewById(R.id.settings_about_us);
		((TextView)viewButton.findViewById(android.R.id.text1)).setText("About JOYNT");
		viewButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.settings_profile){
			Intent intent = new Intent(SettingsActivity.this, SettingProfile.class);
			SettingsActivity.this.startActivity(intent);
		}
		
		Toast.makeText(this, ((TextView)v.findViewById(android.R.id.text1)).getText(), Toast.LENGTH_SHORT)
		.show();
		
	}
}
