package com.ngbm.joynt.phone_registration;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
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
import com.ngbm.joynt.HomeActivity;
import com.ngbm.joynt.R;
import com.ngbm.joynt.ScreenSplash;
import com.ngbm.joynt.json_helper.UrlJsonAsyncTask;

public class DisplayNameAcivity extends Activity {
	Dialog myDialog = null;
	String phoneNumber;
	protected InputFilter filter;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.display_name);

	

		((TextView) findViewById(R.id.header_name))
				.setText(R.string.displayNameTitle);
		((TextView) findViewById(R.id.header_left_button_text)).setText("Back");

		findViewById(R.id.header_left_button).setVisibility(0);
		findViewById(R.id.header_right_button).setVisibility(-1);
		
		EditText textDisplayName = (EditText) findViewById(R.id.textDisplayName);
		
	//textDisplayName.setFilters(new InputFilter[]{filter});
	
		textDisplayName.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				((TextView) findViewById(R.id.labelCharCount)).setText(" ("
						+ ((EditText) findViewById(R.id.textDisplayName))
								.length() + "/20)")
								;
			}
	/*	    InputFilter filter = new InputFilter() { 
		        public CharSequence filter(CharSequence source, int start, int end, 
		Spanned dest, int dstart, int dend) { 
		                for (int i = start; i < end; i++) { 
		                        if (!Character.isLetter(source.charAt(i))) { 
		                                return ""; 
		                        } 
		                } 
		                return null; 
		        } 
		}; 
*/

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}



			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
		if (ScreenSplash.main.dbController.getVerificationStatusSetting()
				.equalsIgnoreCase(Constants.ContactStatusCompleted)) {
			findViewById(R.id.header_left_button).setVisibility(-1);
		} else {
			findViewById(R.id.header_left_button).setVisibility(0);
		}
		findViewById(R.id.header_right_button).setVisibility(-1);
		findViewById(R.id.header_left_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(DisplayNameAcivity.this,
								UseMyContact.class);
						startActivity(intent);
						finish();
					}
				});
		final Context context = this;
		phoneNumber = ScreenSplash.main.dbController
				.getPhoneNumberSetting();

		findViewById(R.id.buttonReg).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (((EditText) findViewById(R.id.textDisplayName)).length() > 1) {
					String veriUrl = context
							.getString(R.string.phoneVarificationServiceUrlLive);
					veriUrl = veriUrl
							+ "?method=UpdateDisplayName&returntype=Json&phonenumber="
							+ phoneNumber.replace("+", "").replace(" ", "")
							+ "&displayname="
							+ ((EditText) findViewById(R.id.textDisplayName))
									.getText().toString();
					Log.d("VerificationURl: +", veriUrl);
					DisplayNameTask task = new DisplayNameTask(context);
					task.setMessageLoading("Updating...");
					task.execute(veriUrl);
				} else {
					showAlertView(
							context.getString(R.string.displayNameAlertMessage),
							context);
				}
			}
		});
	}

	private class DisplayNameTask extends UrlJsonAsyncTask {
		public DisplayNameTask(Context context) {
			super(context);
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				Log.d("JsonData: +", json.getString("status").toString());

				ScreenSplash.main.dbController
						.setDisplayNameSetting(((EditText) findViewById(R.id.textDisplayName))
								.getText().toString());
				ScreenSplash.main.dbController
						.setVerificationStatusSetting(json.getString("status"));
				Intent intent = new Intent(context, HomeActivity.class);
				startActivity(intent);
				finish();

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
