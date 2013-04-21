package com.ngbm.joynt;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater.Filter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ngbm.joynt.custom_classes.Friend;
import com.ngbm.joynt.json_helper.UrlJsonAsyncTask;

public class SettingDisplayName extends Activity {
	String phoneNumber;
	Dialog myDialog;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_profile_field);
		((TextView) findViewById(R.id.header_name)).setText(R.string.displayNameTitle);
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
		final EditText textDisplayName = (EditText) findViewById(android.R.id.edit);
		textDisplayName.setFilters(new InputFilter[] {
			    new InputFilter() {
			        public CharSequence filter(CharSequence src, int start,
			                int end, Spanned dst, int dstart, int dend) {
			            if(src.equals("")){ // for backspace
			                return src;
			            }
			            if(src.toString().matches("[a-zA-Z ]+")){
			                return src;
			            }
			            return "";
			        }
			    }
			});

		textDisplayName.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				((TextView) findViewById(android.R.id.hint)).setText(" ("
						+ ((EditText) textDisplayName).length() + "/20)")
						;
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});

		final Context context = this;
		phoneNumber = ScreenSplash.main.dbController
				.getPhoneNumberSetting();

		findViewById(android.R.id.button1).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						if (((EditText) findViewById(android.R.id.edit))
								.length() > 1) {
							String veriUrl = context
									.getString(R.string.phoneVarificationServiceUrlLive);
							veriUrl = veriUrl
									+ "?method=UpdateDisplayName&returntype=Json&phonenumber="
									+ phoneNumber.replace("+", "").replace(" ",
											"")
									+ "&displayname="
									+ ((EditText) findViewById(android.R.id.edit))
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
				//Log.d("JsonData: +", json.getString("status").toString());
				ScreenSplash.main.dbController
						.setDisplayNameSetting(((EditText) findViewById(android.R.id.edit))
								.getText().toString());
				ArrayList<Friend> lstFriends  = ScreenSplash.main.dbController.getFriends(Long.parseLong(phoneNumber.replace("+", "").replace(" ","")));
				if(lstFriends.size() >0){
					
					Friend objFriend = lstFriends.get(0);
					objFriend.setDisplayName(ScreenSplash.main.dbController.getDisplayNameSetting());
					ScreenSplash.main.dbController.updateFriend(objFriend);
				}
				Toast.makeText(context,
						R.string.display_name_update_success_msg,
						Toast.LENGTH_SHORT).show();
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
}
