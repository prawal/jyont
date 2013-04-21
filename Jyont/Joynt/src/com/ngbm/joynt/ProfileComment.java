package com.ngbm.joynt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ngbm.joynt.adaptors.CommentAdaptor;
import com.ngbm.joynt.custom_classes.Comment;
import com.ngbm.joynt.custom_classes.Utility;
import com.ngbm.joynt.custom_ui.MySpinner;

public class ProfileComment extends Activity {
	ArrayList<Comment> commentList = null;
	private CommentAdaptor listAdapter = null;
	private ListView listComment;
	private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy hh:mma");
	private int loadComments = 1;
	private int showDelete = 0;
	String phoneNumber;
	private TextView dateTime;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.comment_list);

		((TextView) findViewById(R.id.header_name))
				.setText(R.string.commentTitle);

		((TextView) findViewById(R.id.header_left_button_text)).setText("Back");
		((TextView) findViewById(R.id.header_right_button_text))
				.setText("Edit");

		final LinearLayout btnEdit = ((LinearLayout) findViewById(R.id.header_right_button));
		phoneNumber = ScreenSplash.main.dbController.getPhoneNumberSetting()
				.replace("+", "").replace(" ", "");
		findViewById(R.id.header_left_button).setVisibility(0);
		findViewById(R.id.header_right_button).setVisibility(0);
		findViewById(R.id.header_left_button).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
								KeyEvent.KEYCODE_BACK));
						dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
								KeyEvent.KEYCODE_BACK));
					}
				});
		btnEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				btnEdit.setBackgroundResource(showDelete > 0 ? R.drawable.selector_navi_button
						: R.drawable.common_navibtn_done);
				((TextView) btnEdit.findViewById(R.id.header_right_button_text))
						.setText((showDelete > 0 ? R.string.edit
								: R.string.done));

				showDelete = showDelete > 0 ? 0 : 1;
				commentList = ScreenSplash.main.dbController.getComments(0);
				setAdapterToListview(commentList);

			}
		});
		listComment = ((ListView) findViewById(R.id.listComment));

		commentList = ScreenSplash.main.dbController.getComments(0);
		setAdapterToListview(commentList);
		final String comment = ((EditText) findViewById(android.R.id.edit))
				.getText().toString();
		EditText text = (EditText) findViewById(android.R.id.edit);

		text.setFilters(new InputFilter[] { new InputFilter() {
			public CharSequence filter(CharSequence src, int start, int end,
					Spanned dst, int dstart, int dend) {
				if (src.equals("")) { // for backspace
					return src;
				}
				if (src.toString().matches("[a-zA-Z ]+")) {
					return src;
				}
				return "";
			}
		} });
		findViewById(R.id.chathistory_send_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (comment.trim().equalsIgnoreCase("")) {
							if (Utility.isNetworkAvailable(ProfileComment.this)) {
								loadComments = 0;
								new MyTask().execute();
							} else {
								Toast.makeText(ProfileComment.this,
										"No Netwrok Connection!!!",
										Toast.LENGTH_SHORT).show();
							}
						}
					}
				});
		if (Utility.isNetworkAvailable(ProfileComment.this)) {

			new MyTask().execute();
		} else {
			Toast.makeText(ProfileComment.this, "No Netwrok Connection!!!",
					Toast.LENGTH_SHORT).show();
		}
	}

	// My AsyncTask start...

	class MyTask extends AsyncTask<Void, Void, Void> {

		MySpinner pDialog;

		@Override
		protected void onPreExecute() {
			pDialog = MySpinner.show(ProfileComment.this, null, "Wait...",
					true, true, null);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String veriUrl = ProfileComment.this
					.getString(R.string.phoneVarificationServiceUrlLive);
			try {
				if (loadComments == 0) {
					veriUrl = veriUrl
							+ "?method=UpdateComment&returntype=Json&phonenumber="
							+ phoneNumber + "&date=" + dateTime;
					List<NameValuePair> NVP = new ArrayList<NameValuePair>();
					NVP.add(new BasicNameValuePair("comment",
							((EditText) findViewById(android.R.id.edit))
									.getText().toString()));

					HttpPost httppost = new HttpPost(veriUrl);
					httppost.setEntity(new UrlEncodedFormEntity(NVP));
					response = httpclient.execute(httppost);
				} else {
					veriUrl = veriUrl
							+ "?method=GetUserComment&returntype=Json&phonenumber="
							+ ScreenSplash.main.dbController
									.getPhoneNumberSetting().replace("+", "")
									.replace(" ", "") + "&date=" + dateTime;
					HttpPost httppost = new HttpPost(veriUrl);
					// Execute HTTP Post Request
					response = httpclient.execute(httppost);
				}
				// Check the response
				if (response == null)
					return null;
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(
								response.getEntity().getContent(), "UTF-8"));
				StringBuilder builder = new StringBuilder();
				for (String line = null; (line = reader.readLine()) != null;) {
					builder.append(line).append("\n");
				}
				JSONTokener tokener = new JSONTokener(builder.toString());
				if (loadComments == 0) {
					JSONObject finalResult = new JSONObject(tokener);
					if (finalResult.getInt("id") > 0) {
						Comment objComment = new Comment();
						objComment.setId(finalResult.getInt("id"));
						objComment.setComment(finalResult.getString("comment"));
						objComment.setPosted_on(new Date(System
								.currentTimeMillis()));
						ScreenSplash.main.dbController
								.insertCommentItem(objComment);
						ScreenSplash.main.dbController
								.setStatusMsgSetting(finalResult
										.getString("comment"));
						commentList = ScreenSplash.main.dbController
								.getComments(0);
					}
					Log.d("Json Response", finalResult.toString());
				} else {
					JSONArray finalResult = new JSONArray(tokener);
					Log.d("Json Response", finalResult.toString());
					int len = finalResult.length();
					Comment objComment = null;
					for (int i = 0; i < len; i++) {
						objComment = new Comment();
						objComment.setId(finalResult.getJSONObject(i).getInt(
								"id"));
						objComment.setComment(finalResult.getJSONObject(i)
								.getString("comment"));
						objComment.setPosted_on(new Date(System
								.currentTimeMillis()));
						if (ScreenSplash.main.dbController.getComments(
								objComment.getId()).size() == 0) {
							ScreenSplash.main.dbController
									.insertCommentItem(objComment);
							if (i == 0)
								ScreenSplash.main.dbController
										.setStatusMsgSetting(finalResult
												.getJSONObject(i).getString(
														"comment"));
						}
					}
					commentList = ScreenSplash.main.dbController.getComments(0);
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			// Log.d("Contacts",arrContacts.toString());
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (null != pDialog && pDialog.isShowing()) {
				pDialog.dismiss();
			}
			if (null == commentList || commentList.size() == 0) {
				Toast.makeText(ProfileComment.this, "No Comment(s) found!!!",
						Toast.LENGTH_SHORT).show();
			} else {
				((EditText) findViewById(android.R.id.edit)).setText("");
				setAdapterToListview(commentList);
			}
			super.onPostExecute(result);
		}
	}

	public void setAdapterToListview(ArrayList<Comment> listForAdapter) {
		if (commentList.size() == 0) {
			findViewById(R.id.simple_profile_message).setVisibility(0);
		} else {
			findViewById(R.id.simple_profile_message).setVisibility(-1);
		}
		listAdapter = new CommentAdaptor(ProfileComment.this, listForAdapter,
				showDelete, phoneNumber,
				findViewById(R.id.simple_profile_message));
		listComment.setAdapter(listAdapter);
	}
}
