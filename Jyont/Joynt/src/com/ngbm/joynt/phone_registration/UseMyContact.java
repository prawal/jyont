package com.ngbm.joynt.phone_registration;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.ngbm.joynt.Constants;
import com.ngbm.joynt.R;
import com.ngbm.joynt.ScreenSplash;
import com.ngbm.joynt.json_helper.UrlJsonAsyncTask;

public class UseMyContact extends Activity {
	String phoneNumber;
	String contactStatus = "false";

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		
		setContentView(R.layout.use_contact);
	
		((TextView) findViewById(R.id.header_name))
				.setText(R.string.useContactTitle);
		((TextView) findViewById(R.id.header_left_button_text)).setText("Back");

		findViewById(R.id.header_left_button).setVisibility(-1);
		findViewById(R.id.header_right_button).setVisibility(-1);

		((WebView) findViewById(R.id.editText1))
				.loadData(getString(R.string.useContactContantRows),
						"text/html", "utf-5");

		findViewById(R.id.header_left_button).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
								KeyEvent.KEYCODE_BACK));
						dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
								KeyEvent.KEYCODE_BACK));
					}
				});
		phoneNumber = ScreenSplash.main.dbController.getPhoneNumberSetting();
		final Context context = this;
		findViewById(R.id.btnUseContactFirstBotton).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						contactStatus = "true";
						String veriUrl = context
								.getString(R.string.phoneVarificationServiceUrlLive);
						veriUrl = veriUrl
								+ "?method=UseMyContactList&returntype=Json&phonenumber="
								+ phoneNumber.replace("+", "").replace(" ", "")
								+ "&usecontact=1";
						Log.d("VerificationURl: +", veriUrl);
						ContactTask task = new ContactTask(context);
						task.setMessageLoading("Updating...");
						task.execute(veriUrl);

					}
				});
		findViewById(R.id.btnUseContactSecondBotton).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						contactStatus = "false";
						String veriUrl = context
								.getString(R.string.phoneVarificationServiceUrlLive);
						veriUrl = veriUrl
								+ "?method=UseMyContactList&returntype=Json&phonenumber="
								+ phoneNumber.replace("+", "").replace(" ", "")
								+ "&usecontact=1";
						Log.d("VerificationURl: +", veriUrl);
						ContactTask task = new ContactTask(context);
						task.setMessageLoading("Updating...");
						task.execute(veriUrl);
					}
				});
		try {
			Bundle recdData = getIntent().getExtras();
			phoneNumber = recdData.getString("regData");
		} catch (Exception e) {
		}
	}

	private class ContactTask extends UrlJsonAsyncTask {
		public ContactTask(Context context) {
			super(context);
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				Log.d("JsonData: +",
						json.getString("phonenumber")
								.toString());

				ScreenSplash.main.dbController
						.setUseContactListSetting(contactStatus);
				ScreenSplash.main.dbController
				.setVerificationStatusSetting(Constants.ContactStatusCompleted);
				Intent intent = new Intent(context, DisplayNameAcivity.class);
				startActivity(intent);

			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
