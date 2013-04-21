package com.ngbm.joynt.phone_registration;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ngbm.joynt.R;
import com.ngbm.joynt.ScreenSplash;
import com.ngbm.joynt.custom_classes.Utility;
import com.ngbm.joynt.json_helper.UrlJsonAsyncTask;

public class TermOfUseActivity extends Activity {
	String phoneNumber;
	Dialog myDialog;
	Button myButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		setContentView(R.layout.term_of_use);
		

		((TextView) findViewById(R.id.header_name))
				.setText(R.string.termOfUseTitle);
		((TextView) findViewById(R.id.header_left_button_text)).setText("Back");

		findViewById(R.id.header_left_button).setVisibility(0);
		findViewById(R.id.header_right_button).setVisibility(-1);

		findViewById(R.id.header_left_button).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
								KeyEvent.KEYCODE_BACK));
						dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
								KeyEvent.KEYCODE_BACK));
					}
				});

		((WebView) findViewById(R.id.editAgreement)).loadData(
				getString(R.string.termOfUseAgreementText), "text/html",
				"utf-8");

		try {
			Bundle recdData = getIntent().getExtras();
			phoneNumber = recdData.getString("regData");
		} catch (Exception e) {
		}

		final Context context = this;
		myButton = (Button) findViewById(R.id.btnTermAgree);

		myButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog = new Dialog(context, R.style.Dialog_Progress);
				myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				myDialog.setContentView(R.layout.mydialog);
				myDialog.setTitle(R.string.termDialogTitle);
				((TextView) myDialog
						.findViewById(R.id.txtviewConfirmPhoneNumber))
						.setText(phoneNumber);
				myDialog.setCancelable(true);

				Button btnCancel = (Button)myDialog.findViewById(R.id.btn_cancel_term_dialog);
				btnCancel.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								myDialog.dismiss();

								Intent intent = new Intent(context,
										JoyntActivity.class);
								startActivity(intent);

							}
						});
				Button btnConfirm = (Button)myDialog.findViewById(R.id.btn_confirm_term_dialog);
				btnConfirm.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								myDialog.dismiss();
								String veriUrl = context
										.getString(R.string.phoneVarificationServiceUrlLive);
								veriUrl = veriUrl
										+ "?method=ValidatePhoneNumber&&returntype=Json&phonenumber="
										
										+ phoneNumber.replace("+", "").replace(" ", "-")
										.replace("-", "");
								Log.d("VerificationURl: +", veriUrl);
								if (Utility.isNetworkAvailable(context)) {
									MyTask task = new MyTask(context);
									task.setMessageLoading("Please Wait...");
									task.execute(veriUrl);
								} else {
									Toast.makeText(context,
											"No Netwrok Connection!!!",
											Toast.LENGTH_SHORT).show();
								}
							}
						});
				myDialog.show();
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

				Intent intent = new Intent(context, VerificationActivity.class);
				ScreenSplash.main.dbController
						.setPhoneNumberSetting(phoneNumber);
				ScreenSplash.main.dbController
						.setVerificationStatusSetting(json
								.getJSONObject("data").getString("status"));

				startActivity(intent);
			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}
}
