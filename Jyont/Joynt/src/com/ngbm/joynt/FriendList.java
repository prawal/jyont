package com.ngbm.joynt;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnConfigurationService;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.ngn.utils.NgnUriUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ngbm.joynt.adaptors.FriendsAdaptor;
import com.ngbm.joynt.custom_classes.Friend;
import com.ngbm.joynt.custom_classes.FriendGroup;
import com.ngbm.joynt.custom_classes.Utility;
import com.ngbm.joynt.custom_ui.MySpinner;
import com.ngbm.joynt.custom_ui.ToolBarBaseActivity;
import com.ngbm.joynt.interfaces.IFriend;
import com.ngbm.joynt.joynt_services.ScreenService;

public class FriendList extends BaseScreen implements TextWatcher {

	private ListView listFriend;
	private FriendsAdaptor listAdapter = null;
	ArrayList<IFriend> itemsSection = new ArrayList<IFriend>();
	AlertDialog alert = null;
	ArrayList<Friend> friendList;
	ArrayList<Friend> filterArray = new ArrayList<Friend>();
	Dialog myDialog = null;
	EditText mySearch;
	String searchString;
	Friend objProfile;
	String contactJsonString, phoneNumber;
	final boolean IsEdit = false;

	Context mContext = null;

	private static String TAG = HomeActivity.class.getCanonicalName();
	private final INgnSipService mSipService;

	public FriendList() {
		super(SCREEN_TYPE.FRIEND_LIST, TAG);
		mSipService = getEngine().getSipService();

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.friend_list);

		((TextView) findViewById(R.id.header_name))
				.setText(R.string.friendListTitle);
		((TextView) findViewById(R.id.header_left_button_text)).setText("Edit");

		findViewById(R.id.header_left_button).setVisibility(0);
		findViewById(R.id.header_right_button).setVisibility(-1);

		findViewById(R.id.header_left_button).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {

					}
				});
		mContext = this;
	}

	private void LoadFriends() {
		listFriend = (ListView) findViewById(R.id.listFriends);
		mySearch = (EditText) findViewById(R.id.input_search_query);
		phoneNumber = ScreenSplash.main.dbController.getPhoneNumberSetting()
				.replace("+", "").replace(" ", "-");
		mySearch.addTextChangedListener(this);

		contactJsonString = getContacts();
		if (ScreenSplash.main.dbController.getFriends(0).size() <= 1) {
			if (Utility.isNetworkAvailable(mContext)) {
				new GetAllFriends().execute();
			} else {
				Toast.makeText(mContext, "No Netwrok Connection!!!",
						Toast.LENGTH_SHORT).show();
			}
		} else {
			ArrayList<Friend> finalResult = ScreenSplash.main.dbController
					.getFriends(0);
			friendList = new ArrayList<Friend>();
			if (finalResult != null) {
				// Log.d("Friend Count", finalResult.size() + "");
				Friend objFriend = null;

				int len = finalResult.size();
				for (int i = 0; i < len; i++) {
					objFriend = new Friend();
					objFriend.setDisplayName(finalResult.get(i)
							.getDisplayName());
					objFriend.setPhoneNumber(finalResult.get(i)
							.getPhoneNumber());
					objFriend.setComment(finalResult.get(i).getComment());
					objFriend.setGroupName(finalResult.get(i).getGroupName());
					Log.d("Comment", finalResult.get(i).getComment());

					if (objFriend.getGroupName().equalsIgnoreCase("Profile")) {
						objFriend.setComment(ScreenSplash.main.dbController
								.getStatusMsgSetting());
						objProfile = objFriend;

					} else {
						friendList.add(objFriend);
					}
				}
				setAdapterToListview(friendList);
			}
		}
	}

	private String getContacts() {
		JSONArray arrContacts = new JSONArray();
		try {
			Cursor phones = getContentResolver().query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					null, null, null);
			while (phones.moveToNext()) {
				// String name =
				// phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				String phoneNumber = phones
						.getString(phones
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				JSONObject obj = new JSONObject();
				obj.put("phonenumber", phoneNumber);
				arrContacts.put(obj);
			}
			JSONObject obj = new JSONObject();
			obj.put("phonenumber", phoneNumber);
			arrContacts.put(obj);
			phones.close();
			Log.d("Json", arrContacts.toString());
		} catch (Exception e) {
			return null;
		}

		return arrContacts.toString();
	}

	public void btnBubbleClick(View v) {
		if (((Friend) v.getTag())
				.getPhoneNumber()
				.replace(" ", "")
				.replace("+", "")
				.equalsIgnoreCase(
						ScreenSplash.main.dbController.getPhoneNumberSetting()
								.replace(" ", "").replace("+", ""))) {
			Intent intent = new Intent(this, ProfileComment.class);
			startActivity(intent);
		}
	}

	private Uri mSaveUri = null;

	public void onCellUserClick(View v) {
		Log.d("Json", ((Friend) v.getTag()).getDisplayName());
		myDialog = new Dialog(this, R.style.Dialog_Progress);
		myDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		Log.d("View Num", ((Friend) v.getTag()).getPhoneNumber());
		Log.d("Saved Num",
				ScreenSplash.main.dbController.getPhoneNumberSetting());
		if (((Friend) v.getTag())
				.getPhoneNumber()
				.replace(" ", "")
				.replace("+", "")
				.equalsIgnoreCase(
						ScreenSplash.main.dbController.getPhoneNumberSetting()
								.replace(" ", "").replace("+", ""))) {
			myDialog.setContentView(R.layout.profile_detail_dialog);

			((TextView) myDialog.findViewById(R.id.profiledetaildialog_name))
					.setText(((Friend) v.getTag()).getDisplayName());
			((TextView) myDialog
					.findViewById(R.id.profiledetaildialog_status_msg))
					.setText(((Friend) v.getTag()).getComment());

			mSaveUri = Uri.fromFile(new File(Constants.PROFILE_IMAGE_SAVE_PATH,
					"profile_image.jpg"));
			File file = new File(mSaveUri.getPath());
			if (file.exists()) {
				ImageView mImageView = ((ImageView) myDialog
						.findViewById(R.id.profiledetaildialog_thumbnail));
				Bitmap myBitmap = BitmapFactory.decodeFile(mSaveUri.getPath());
				mImageView.setImageBitmap(myBitmap);
			}

			if (((Friend) v.getTag()).getComment().equalsIgnoreCase("")) {
				myDialog.findViewById(R.id.profiledetaildialog_status_area)
						.setVisibility(-1);
			}
			myDialog.findViewById(R.id.profiledetaildialog_status_area).setTag(
					((Friend) v.getTag()));
			final Context contaxt = this;
			myDialog.findViewById(R.id.profiledetaildialog_edit_profile)
					.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							myDialog.dismiss();
							Intent intent = new Intent(contaxt,
									SettingProfile.class);
							contaxt.startActivity(intent);
							finish();
						}
					});
			myDialog.findViewById(R.id.profiledetaildialog_edit_comment)
					.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							myDialog.dismiss();
							Intent intent = new Intent(contaxt,
									ProfileComment.class);
							contaxt.startActivity(intent);
						}
					});
			myDialog.findViewById(R.id.profiledetaildialog_status_area)
					.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							myDialog.dismiss();
							Intent intent = new Intent(contaxt,
									ProfileComment.class);
							startActivity(intent);
						}
					});
		} else {
			myDialog.setContentView(R.layout.friends_detail_dialog);
			((TextView) myDialog.findViewById(R.id.frienddetaildialog_name))
					.setText(((Friend) v.getTag()).getDisplayName());
			((TextView) myDialog
					.findViewById(R.id.frienddetaildialog_status_msg))
					.setText(((Friend) v.getTag()).getComment());
			if (((Friend) v.getTag()).getComment().equalsIgnoreCase("")) {
				myDialog.findViewById(R.id.profiledetaildialog_status_area)
						.setVisibility(-1);
			}

			myDialog.findViewById(R.id.free_call_btn).setOnClickListener(
					new OnClickListener() {
						public void onClick(View v) {
							myDialog.dismiss();
							if (mSipService.isRegistered()) {
								ScreenAV.makeCall(NgnUriUtils
										.makeValidSipUri(String.format(
												"sip:%s@%s", "anilngbm",
												HomeActivity.SIP_DOMAIN)),
										NgnMediaType.Audio);
								

							}
						}
					});
		}
		myDialog.setTitle(null);

		myDialog.setCancelable(true);
		ImageView btnClose = (ImageView) myDialog.findViewById(R.id.imgClose);
		btnClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				myDialog.dismiss();

			}
		});
		myDialog.show();
	}

	// My AsyncTask start...

	class GetAllFriends extends AsyncTask<Void, Void, Void> {

		MySpinner pDialog;

		@Override
		protected void onPreExecute() {
			pDialog = MySpinner.show(mContext, null, "Wait...", true, true,
					null);
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			HttpClient httpclient = new DefaultHttpClient();
			String veriUrl = mContext
					.getString(R.string.phoneVarificationServiceUrlLive);
			veriUrl = veriUrl
					+ "?method=GetContact&returntype=Json&phonenumber="
					+ phoneNumber.replace("+", "").replace(" ", "-")
							.replace("-", "");
			Log.d("Contact List", veriUrl);
			HttpPost httppost = new HttpPost(veriUrl);
			try {
				List<NameValuePair> NVP = new ArrayList<NameValuePair>();
				NVP.add(new BasicNameValuePair("jsondata", contactJsonString));
				httppost.setEntity(new UrlEncodedFormEntity(NVP));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);

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
				JSONArray finalResult = new JSONArray(tokener);
				Log.d("Json Response", finalResult.toString());
				// POST DONE

				friendList = new ArrayList<Friend>();

				if (finalResult != null) {

					Friend objFriend = null;
					int len = finalResult.length();
					for (int i = 0; i < len; i++) {
						objFriend = new Friend();
						objFriend.setDisplayName(finalResult.getJSONObject(i)
								.getString("displayname"));
						objFriend.setPhoneNumber("+"
								+ finalResult.getJSONObject(i).getString(
										"phonenumber"));
						objFriend.setComment(finalResult.getJSONObject(i)
								.getString("comment"));
						Log.d("ProfileMine", ScreenSplash.main.dbController
								.getPhoneNumberSetting());
						if (ScreenSplash.main.dbController
								.getPhoneNumberSetting()
								.replace("+", "")
								.replace(" ", "")
								.equalsIgnoreCase(
										finalResult.getJSONObject(i).getString(
												"phonenumber"))) {
							objFriend.setGroupName("Profile");
							objProfile = objFriend;
						} else {
							objFriend.setGroupName("Friends");
							friendList.add(objFriend);

						}
						if (ScreenSplash.main.dbController.getFriends(
								finalResult.getJSONObject(i).getInt(
										"phonenumber")).size() == 0) {
							ScreenSplash.main.dbController
									.insertFriend(objFriend);
						}
					}

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

			if (null == friendList || friendList.size() == 0) {
				Toast.makeText(mContext, R.string.friendAlertMessage,
						Toast.LENGTH_SHORT).show();
			}
			setAdapterToListview(friendList);

			super.onPostExecute(result);
		}
	}

	@Override
	public void afterTextChanged(Editable arg0) {
		filterArray.clear();
		Friend _objFriend = new Friend();
		_objFriend.setDisplayName(ScreenSplash.main.dbController
				.getDisplayNameSetting());
		_objFriend.setPhoneNumber(ScreenSplash.main.dbController
				.getPhoneNumberSetting());
		_objFriend.setGroupName("Profile");
		filterArray.add(_objFriend);
		searchString = mySearch.getText().toString().trim()
				.replaceAll("\\s", "");
		if (searchString != null)
			if (friendList.size() > 0 && searchString.length() > 0) {
				for (Friend objFriend : friendList) {
					if (objFriend.getDisplayName().toLowerCase()
							.startsWith(searchString.toLowerCase())
							&& objFriend.getGroupName().equalsIgnoreCase(
									"Friends")) {

						filterArray.add(objFriend);
					}
				}
				setAdapterToListview(filterArray);
			} else {
				filterArray.clear();
				setAdapterToListview(friendList);
			}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	public void setAdapterToListview(ArrayList<Friend> listForAdapter) {

		itemsSection.clear();

		if (null != listForAdapter) {
			if (listForAdapter.size() > 0)
				Collections.sort(listForAdapter);
			listForAdapter.add(0, objProfile);

			String checkChar = " ";
			Log.d("AdptItems", listForAdapter.size() + "");
			for (int index = 0; index < listForAdapter.size(); index++) {

				Friend objItem = (Friend) listForAdapter.get(index);
				if (objItem != null) {
					Log.d("FriendListAdaptorItem", "null");

					String firstChar = objItem.getGroupName();

					if (" " != checkChar) {
						if (checkChar != firstChar) {
							FriendGroup objSectionItem = new FriendGroup();
							objSectionItem.setSectionHeader(firstChar);
							itemsSection.add(objSectionItem);
						}
					} else {
						FriendGroup objSectionItem = new FriendGroup();
						objSectionItem.setSectionHeader(firstChar);
						itemsSection.add(objSectionItem);
					}
					checkChar = firstChar;
					itemsSection.add(objItem);
				}
			}
		} else {
			showAlertView(mContext.getString(R.string.friendAlertMessage),
					mContext);
		}

		if (null == listAdapter) {
			listAdapter = new FriendsAdaptor(mContext, itemsSection);
			listFriend.setAdapter(listAdapter);
		} else {
			listAdapter.notifyDataSetChanged();
		}
	}

	protected void onResume() {
		super.onResume();
		this.LoadFriends();
	}

	private void showAlertView(String message, Context context) {

		myDialog = new Dialog(context, R.style.cust_dialog);
		myDialog.setContentView(R.layout.myalert);
		myDialog.setTitle(R.string.countryAlertTitle);
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
