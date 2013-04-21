package com.ngbm.joynt;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class EditEmailAddress extends Activity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.edit_email_address);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.inc_header);

		((TextView) findViewById(R.id.header_name))
				.setText(R.string.editemail);
		((TextView) findViewById(R.id.header_left_button_text)).setText("Back");

		findViewById(R.id.header_left_button).setVisibility(0);
		findViewById(R.id.header_right_button).setVisibility(-1);
	
		final Context context = this;
		findViewById(R.id.header_left_button).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_BACK));
				dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_BACK));
			}
		});
		
	
	}
	











}