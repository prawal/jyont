package com.ngbm.joynt.phone_registration;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ngbm.joynt.Constants;
import com.ngbm.joynt.R;
import com.ngbm.joynt.ScreenSplash;
import com.ngbm.joynt.json_helper.UrlJsonAsyncTask;

public class VerificationActivity extends Activity {
	String phoneNumber;
	String strID;
	String email;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		setContentView(R.layout.code_verification);
		

		((TextView) findViewById(R.id.header_name))
				.setText(R.string.phoneVerificationTitle);
		((TextView) findViewById(R.id.header_left_button_text)).setText("Back");

		findViewById(R.id.header_left_button).setVisibility(-1);
		findViewById(R.id.header_right_button).setVisibility(-1);

		((TextView) findViewById(R.id.labelPhoneVerificationEnter))
				.setText(Html
						.fromHtml(getString(R.string.phoneVerificationEnter)));
		((TextView) findViewById(R.id.labelResendVerificationNumber))
				.setText(Html
						.fromHtml(getString(R.string.phoneVerificationSmsNotReceived)));
		
		

		findViewById(R.id.header_left_button).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
								KeyEvent.KEYCODE_BACK));
						dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
								KeyEvent.KEYCODE_BACK));
					}
				});
		final Context context = this;
		findViewById(R.id.buttonVeriNext).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						if (((EditText) findViewById(R.id.txtVerificationNumber))
								.length() >= 4) {
							try {
								String strVarificationCode = ((EditText) findViewById(R.id.txtVerificationNumber))
										.getText().toString();
								
								String veriUrl = context
										.getString(R.string.phoneVarificationServiceUrlLive);
						
								veriUrl = veriUrl
										+ "?method=ValidateVerificationCode&&returntype=Json&phonenumber="
										+ phoneNumber.replace("+", "").replace(" ", "-")
										.replace("-", "")
										+ "&email="
										+ email
										+ "&verificationcode="
										+ strVarificationCode
										;
									

								Log.d("VerificationURl: +", veriUrl);

								VTask task = new VTask(context);
								task.setMessageLoading("Verifying...");
								task.execute(veriUrl);

							} catch (Exception e) {
								Log.d("Verification Update", e.getMessage());
							}

						} else {
							showAlertView(
									context.getString(R.string.phoneVerificationAlertMessage),
									context);
						}
					}
				});
		
		
		TextView text1 = (TextView)findViewById(R.id.changeNumber);
		text1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						

						Intent intent = new Intent(context,
								JoyntActivity.class);
						startActivity(intent);

					}
				});
		
		Button option = (Button)findViewById(R.id.buttonOption);
		option.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						

						Intent intent = new Intent(context,
								EmailActivity.class);
						startActivity(intent);

					}
				});
	
		
		phoneNumber = ScreenSplash.main.dbController
				.getPhoneNumberSetting().toString();
		((TextView) findViewById(R.id.labelPhoneNumber)).setText(phoneNumber);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(true);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}


	private class SMSTask extends UrlJsonAsyncTask {
		public SMSTask(Context context) {
			super(context);
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				Log.d("JsonData: +",
						json.getJSONObject("data").getString("phonenumber")
								.toString());
				showAlertView("Verification code sent successfully",
						VerificationActivity.this);

			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}
	private class VTask extends UrlJsonAsyncTask {
		public VTask(Context context) {
			super(context);
		}

		@Override
		protected void onPostExecute(JSONObject json) {

			try {
				Log.d("JsonData: ", json.getString("status"));

				if (json.getString("status").equalsIgnoreCase(
						Constants.PhoneVerificationCompleted)) {
					ScreenSplash.main.dbController
							.setVerificationStatusSetting(json
									.getString("status"));
					Intent intent = new Intent(context, UseMyContact.class);
					startActivity(intent);
				} else {
					showAlertView(
							"Invalid varification code entered. Please try again.",
							context);
					((EditText) findViewById(R.id.txtVerificationNumber))
							.setText("");
				}

			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}

	Dialog myDialog = null;

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
