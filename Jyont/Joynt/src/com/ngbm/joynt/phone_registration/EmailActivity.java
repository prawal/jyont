
package com.ngbm.joynt.phone_registration;
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
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ngbm.joynt.R;
import com.ngbm.joynt.ScreenSplash;
import com.ngbm.joynt.custom_classes.Utility;
import com.ngbm.joynt.json_helper.UrlJsonAsyncTask;




public class EmailActivity extends Activity {
	String phoneNumber;
	String email;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	

	
		setContentView(R.layout.email);
		
		((TextView) findViewById(R.id.header_name))
		.setText(R.string.registorT);
	
		((TextView) findViewById(R.id.header_left_button_text)).setText("Back");

		findViewById(R.id.header_left_button).setVisibility(1);
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

	
		TextView text1 = (TextView)findViewById(R.id.changeNumber);
		text1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						

						Intent intent = new Intent(context,
								JoyntActivity.class);
						startActivity(intent);

					}
				});
		findViewById(R.id.buttonEmail).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						String strID =  ((EditText) findViewById(R.id.emailAddress))
										.getText().toString().trim();
						
								if (strID.length()<5){
									showAlertView(context
											.getString(R.string.alertValidEmailID),
											context);
								}else{
									
								phoneNumber = ScreenSplash.main.dbController
											.getPhoneNumberSetting().toString();
								
							
									String veriUrl1	 = context
											.getString(R.string.phoneVarificationServiceUrlLive);

						/*
						 * 		String strNumber = 
								((EditText) findViewById(R.id.txtPhoneNumber))
										.getText().toString().trim();
						 */
						veriUrl1 = veriUrl1
								+"?method=ValidatePhoneNumber&action=email&returntype=Json&email=" 
								+ strID
								+ "&phonenumber="
								+ phoneNumber.replace("+", "").replace(" ", "-")
								.replace("-", "")
								;
								
						Log.d("VerificationURl: +", veriUrl1);
						if (Utility.isNetworkAvailable(context)) {
							SMSTask2 task = new SMSTask2(context);
							task.setMessageLoading("Loading...");
							task.execute(veriUrl1);
						} else {
							showAlertView("No Netwrok Connection!!!",
									context);

						}
					}
				
				}}
				);
		
	
				
		phoneNumber = ScreenSplash.main.dbController
				.getPhoneNumberSetting().toString();
		((TextView) findViewById(R.id.labelPhoneNumber)).setText(phoneNumber);
		
		
		findViewById(R.id.buttonVeriCodeResned).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						String veriUrl = context
								.getString(R.string.phoneVarificationServiceUrlLive);
						veriUrl = veriUrl
								+ "?method=ValidatePhoneNumber&&returntype=Json&phonenumber="
								+ phoneNumber.replace("+", "").replace(" ", "-");
						Log.d("VerificationURl: +", veriUrl);

						SMSTask task = new SMSTask(context);
						task.setMessageLoading("Please Wait...");
						task.execute(veriUrl);
					}
				});
		
	}private class SMSTask extends UrlJsonAsyncTask {
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
						EmailActivity.this);

			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}
	private class SMSTask2 extends UrlJsonAsyncTask {
		public SMSTask2(Context context) {
			super(context);
		}
		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				Log.d("JsonData: +",
						json.getJSONObject("data").getString("phonenumber")
								.toString());
				
					
				showAlertView("Verification code sent successfully",
						EmailActivity.this);

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
