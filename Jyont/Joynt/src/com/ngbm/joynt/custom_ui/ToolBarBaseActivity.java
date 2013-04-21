package com.ngbm.joynt.custom_ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.ngbm.joynt.BaseScreen;
import com.ngbm.joynt.FriendList;
import com.ngbm.joynt.HomeActivity;
import com.ngbm.joynt.R;
import com.ngbm.joynt.SettingsActivity;

public class ToolBarBaseActivity extends BaseScreen {
	LinearLayout linBase;
	Context context;
	RadioButton radioButton;
	
	public ToolBarBaseActivity(SCREEN_TYPE type, String id) {
		super(type, id);

	}
	
	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.base_layout);
		
		radioButton = (RadioButton) findViewById(R.id.btnHome);
		radioButton
				.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListener);
		radioButton = (RadioButton) findViewById(R.id.btnFriends);
		
		radioButton
				.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListener);
		radioButton = (RadioButton) findViewById(R.id.btnChats);
		radioButton
				.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListener);
		radioButton = (RadioButton) findViewById(R.id.btnShare);
		radioButton
				.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListener);
		radioButton = (RadioButton) findViewById(R.id.btnSettings);
		radioButton
				.setOnCheckedChangeListener(btnNavBarOnCheckedChangeListener);
		linBase = (LinearLayout) findViewById(R.id.content);
	}

	@Override
	public void setContentView(int id) {

		LayoutInflater inflater = (LayoutInflater) getBaseContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		inflater.inflate(id, linBase);

	}
	public void setContentView(int id, Context _context) {

		setContentView(id);
		context = _context;
		if(context.getClass().getName().equalsIgnoreCase(FriendList.class.getName())){
			radioButton = (RadioButton) findViewById(R.id.btnFriends);
			radioButton.setChecked(true);
		}else if(context.getClass().getName().equalsIgnoreCase(SettingsActivity.class.getName())){
			radioButton = (RadioButton) findViewById(R.id.btnSettings);
			radioButton.setChecked(true);
			
			findViewById(R.id.settings_profile);
		}

	}
	private CompoundButton.OnCheckedChangeListener btnNavBarOnCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			if (isChecked) {
				if(buttonView.getText().equals("Home"))
				{
					Intent intent = new Intent(context, HomeActivity.class);
					startActivity(intent);
				}
				else if(buttonView.getText().equals("Friends") && !context.getClass().getName().equalsIgnoreCase(FriendList.class.getName())){
					Intent intent = new Intent(context, FriendList.class);
					startActivity(intent);
				}
				else if(buttonView.getText().equals("Settings") && !context.getClass().getName().equalsIgnoreCase(SettingsActivity.class.getName())){
					Intent intent = new Intent(context, SettingsActivity.class);
					startActivity(intent);
				}
			}
		}
	};
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		View v = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);

		if (v instanceof EditText) {
			View w = getCurrentFocus();
			int scrcoords[] = new int[2];
			w.getLocationOnScreen(scrcoords);
			float x = event.getRawX() + w.getLeft() - scrcoords[0];
			float y = event.getRawY() + w.getTop() - scrcoords[1];
			if (event.getAction() == MotionEvent.ACTION_UP
					&& (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w
							.getBottom())) {

				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
						.getWindowToken(), 0);
			}
		}
		return ret;
	}
}
