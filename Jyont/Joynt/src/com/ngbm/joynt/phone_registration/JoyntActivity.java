package com.ngbm.joynt.phone_registration;

import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ngbm.joynt.BaseScreen;
import com.ngbm.joynt.Constants;
import com.ngbm.joynt.FriendList;
import com.ngbm.joynt.HomeActivity;
import com.ngbm.joynt.Main;
import com.ngbm.joynt.R;
import com.ngbm.joynt.ScreenSplash;
import com.ngbm.joynt.custom_classes.Utility;
import com.ngbm.joynt.joynt_services.PhoneVerivicationSms;
import com.ngbm.joynt.json_helper.UrlJsonAsyncTask;

public class JoyntActivity extends BaseScreen {
	
	

	Dialog myDialog = null;
	private static String TAG = JoyntActivity.class.getCanonicalName();

	PhoneVerivicationSms objVerification = null;
	
	public JoyntActivity() {
		super(SCREEN_TYPE.GENERAL_T, TAG);

	}
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		setContentView(R.layout.phone_reg);

		
		((TextView) findViewById(R.id.header_name))
				.setText(R.string.registorTitle);
		((TextView) findViewById(R.id.header_left_button_text)).setText("Back");

		findViewById(R.id.header_left_button).setVisibility(-1);
		findViewById(R.id.header_right_button).setVisibility(-1);

		 //TelephonyManager telephonyManager =
		 //(TelephonyManager)getSystemService(TELEPHONY_SERVICE);
		 //((EditText)
		 //findViewById(R.id.txtPhoneNumber)).setText(telephonyManager.getLine1Number());
		 //((EditText)
		 //findViewById(R.id.textCountrycode)).setText(telephonyManager.getSimCountryIso());
		try {
			Bundle recdData = getIntent().getExtras();
			String myVal = recdData.getString("selectedCountry");

			if (myVal != null || myVal != "") {

				String[] countryVal = myVal.split("-");
				Log.d("RegActivity", countryVal[1]);
				((EditText) findViewById(R.id.textCountry)).setText(
						countryVal[0] + "", TextView.BufferType.EDITABLE);
				((EditText) findViewById(R.id.textCountrycode)).setText(
						countryVal[1] + "", TextView.BufferType.EDITABLE);
			}
		} catch (Exception e) {
		}
		// if(ScreenSplash.main.dbController.isFirstRun()){
		// ScreenSplash.main.dbController.initializeSettings();
		// }
		final Context context = this;

		findViewById(R.id.buttonRegNext).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						String strNumber = ((EditText) findViewById(R.id.textCountrycode))
								.getText().toString()
								+ ((EditText) findViewById(R.id.txtPhoneNumber))
										.getText().toString().trim();
						if (strNumber.length() < 5) {
							showAlertView(context
									.getString(R.string.alertValidPhoneNumber),
									context);
						} else {
							if (ScreenSplash.main.dbController
									.getVerificationStatusSetting()
									.equalsIgnoreCase("NEW")) {
								String veriUrl = context
										.getString(R.string.phoneVarificationServiceUrlLive);
								
								veriUrl = veriUrl
										+ "?method=ValidatePhoneNumber&action=statuscheck&returntype=Json&phonenumber="
										+ strNumber.replace("+", "").replace(" ", "-").replace("-", "");
								Log.d("VerificationURl: +", veriUrl);
								if (Utility.isNetworkAvailable(context)) {
									MyTask task = new MyTask(context);
									task.setMessageLoading("Loading...");
									task.execute(veriUrl);
								} else {
									showAlertView("No Netwrok Connection!!!",
											context);

								}
							} else {
								Intent intent = new Intent(context,
										TermOfUseActivity.class);
								intent.putExtra(
										"regData",
										((EditText) findViewById(R.id.textCountrycode))
												.getText().toString()
												+ " "
												+ ((EditText) findViewById(R.id.txtPhoneNumber))
														.getText());

								startActivity(intent);
							}

						}
					}
				});
		findViewById(R.id.textCountry).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						final Intent intent = new Intent(context,
								CountryActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(intent);
					}
				});
		findViewById(R.id.textCountrycode).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						final Intent intent = new Intent(context,
								CountryActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(intent);
					}
				});

	}

	private class MyTask extends UrlJsonAsyncTask {
		public MyTask(Context context) {
			super(context);
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				Log.d("JsonData: +",
						json.getJSONObject("data").getString("phonenumber"));
			
				ScreenSplash.main.dbController
						.setPhoneNumberSetting(((EditText) findViewById(R.id.textCountrycode))
								.getText().toString()
								+ " "
								+ ((EditText) findViewById(R.id.txtPhoneNumber))
										.getText()
										+ "")
										;

				ScreenSplash.main.dbController
						.setVerificationStatusSetting(json
								.getJSONObject("data").getString("status"));

				ScreenSplash.main.dbController.setDisplayNameSetting(json
						.getJSONObject("data").getString("display_name"));
				ScreenSplash.main.dbController.setStatusMsgSetting(json
						.getJSONObject("data").getString("comment"));
				ScreenSplash.main.dbController.setAllowSearchSetting(json
						.getJSONObject("data").getString("searchsetting"));

				if (!ScreenSplash.main.dbController
						.getVerificationStatusSetting().equalsIgnoreCase("NEW")) {

					if (ScreenSplash.main.dbController
							.getVerificationStatusSetting().equalsIgnoreCase(
									Constants.PhoneNotVerified)) {
						final Intent intent = new Intent(context,
								VerificationActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(intent);

						finish();

					} else if (ScreenSplash.main.dbController
							.getVerificationStatusSetting().equalsIgnoreCase(
									Constants.PhoneVerificationCompleted)) {
						final Intent intent = new Intent(context,
								UseMyContact.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(intent);

						finish();

					} else if (ScreenSplash.main.dbController
							.getVerificationStatusSetting().equalsIgnoreCase(
									Constants.ContactStatusCompleted)) {
						final Intent intent = new Intent(context,
								DisplayNameAcivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(intent);

						finish();

					} else if (ScreenSplash.main.dbController
							.getVerificationStatusSetting().equalsIgnoreCase(
									Constants.PhoneRegistrationCompleted)) {
						Log.d(TAG, "PRC");
						final Intent intent = new Intent(context,
								HomeActivity.class);
						intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						context.startActivity(intent);
						getEngine().getConfigurationService().putBoolean(NgnConfigurationEntry.GENERAL_AUTOSTART, true);
						finish();
					
					}
				} else {
					Intent intent = new Intent(context, TermOfUseActivity.class);
					intent.putExtra(
							"regData",
							((EditText) findViewById(R.id.textCountrycode))
									.getText().toString()
									+ " "
									+ ((EditText) findViewById(R.id.txtPhoneNumber))
											.getText());

					startActivity(intent);
				}
			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}
	
	private void showAlertView(String message, Context context) {

		myDialog = new Dialog(context, R.style.cust_dialog);
		myDialog.setContentView(R.layout.myalert);
		myDialog.setTitle(R.string.alertTitle);
		((TextView) myDialog.findViewById(R.id.txtviewDialogMessage))
				.setText(message);
		myDialog.setCancelable(true);
		Button btnOk = (Button) myDialog.findViewById(R.id.btnOk);
		btnOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();
			}
		});
		myDialog.show();
	}

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