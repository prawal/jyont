package com.ngbm.joynt;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ngbm.joynt.cropper.CropOption;
import com.ngbm.joynt.cropper.CropOptionAdapter;
import com.ngbm.joynt.custom_classes.Base64;
import com.ngbm.joynt.custom_classes.ImageLoader;
import com.ngbm.joynt.custom_classes.Utility;
import com.ngbm.joynt.custom_ui.MonitoredActivity;
import com.ngbm.joynt.json_helper.UrlJsonAsyncTask;

public class SettingProfile extends MonitoredActivity implements
		OnClickListener {

	private static final int PICK_FROM_CAMERA = 1;
	private static final int PICK_FROM_FILE = 2;
	private static final int CROPED_IMAGE = 3;
	private Uri mImageCaptureUri;
	private String phoneNumber;
	private Dialog myDialog;
	private ImageView mImageView;
	private ProgressDialog dialog;

	private ContentResolver mContentResolver;
	private final Handler mHandler = new Handler();
	private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
	private Uri mSaveUri = null;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.settings_profile);
		
		((TextView) findViewById(R.id.header_name))
				.setText(R.string.profileTitle);
		((TextView) findViewById(R.id.header_left_button_text)).setText("Back");

		findViewById(R.id.header_left_button).setVisibility(0);
		findViewById(R.id.header_right_button).setVisibility(-1);

		this.LoadSettings();
		mContentResolver = getContentResolver();
		findViewById(R.id.header_left_button).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						Intent intent = new Intent(SettingProfile.this,
								FriendList.class);
						SettingProfile.this.startActivity(intent);
					}
				});

		/* Option menu */
		final String[] items = new String[] { "Take a Photo",
				"Select from Gallery", "Delete Profile Photo", "Cancel" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Progile Image Options");
		mSaveUri = Uri.fromFile(new File(Constants.PROFILE_IMAGE_SAVE_PATH,
				"profile_image.jpg"));
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					mImageCaptureUri = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory(), String
							.valueOf(System.currentTimeMillis()) + ".jpg"));

					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT,
							mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);

						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else if (item == 1) { // pick from file
					Intent intent = new Intent();

					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent,
							"Complete action using"), PICK_FROM_FILE);
				} else if (item == 2) {
					String veriUrl = SettingProfile.this
							.getString(R.string.phoneVarificationServiceUrlLive);
					veriUrl = veriUrl
							+ "?method=DeleteProfileImage&returntype=Json&phonenumber="
							+ phoneNumber.replace("+", "").replace(" ", "");
					Log.d("DeleteImageUrl: +", veriUrl);
					if (Utility.isNetworkAvailable(SettingProfile.this)) {
						DeleteProfileImageTask task = new DeleteProfileImageTask(
								SettingProfile.this);
						task.setMessageLoading("Loading...");
						task.execute(veriUrl);
					} else {
						showAlertView("No Netwrok Connection!!!",
								SettingProfile.this);
					}

				} else {
					dialog.dismiss();
				}
			}
		});

		final AlertDialog dialog = builder.create();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		File file = new File(mSaveUri.getPath());
		mImageView = ((ImageView) findViewById(R.id.settings_profile_photo));
		if (file.exists()) {	
			Bitmap myBitmap = BitmapFactory.decodeFile(mSaveUri.getPath());
			mImageView.setImageBitmap(myBitmap);
		}
		findViewById(R.id.profile_photo).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						dialog.show();
					}
				});
	}

	private class DeleteProfileImageTask extends UrlJsonAsyncTask {
		public DeleteProfileImageTask(Context context) {
			super(context);
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				Log.d("JsonData: +", json.toString());
				if (json.getString("message").equalsIgnoreCase("success")) {
					ImageCropUtil.startBackgroundJob(SettingProfile.this, null,
							"Deleting image", new Runnable() {
								public void run() {
									File file = new File(mSaveUri.getPath());
									if (file.exists())
										file.delete();

								}
							}, mHandler);
					ImageLoader imgLoader = new ImageLoader(SettingProfile.this);
					imgLoader.clearCache();
					mImageView.setImageResource(R.drawable.noimg_c2);
					Toast.makeText(context,
							"Profile image deleted successfully.",
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT)
						.show();
			} finally {
				super.onPostExecute(json);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case PICK_FROM_CAMERA:
			doCrop();
			break;

		case PICK_FROM_FILE:
			mImageCaptureUri = data.getData();
			doCrop();
			break;
		case CROPED_IMAGE:
			Bundle extras = data.getExtras();
			if (extras != null) {
				final Bitmap photo = extras.getParcelable("data");
				mImageView.setImageBitmap(photo);

				ImageCropUtil.startBackgroundJob(this, null, "Saving image",
						new Runnable() {
							public void run() {
								saveOutput(photo);
							}
						}, mHandler);
				new ImageUploadTask().execute(photo);
			}
			File f = new File(mImageCaptureUri.getPath());
			if (f.exists())
				f.delete();
			break;
		}
	}

	class ImageUploadTask extends AsyncTask<Bitmap, Void, String> {
		@Override
		protected void onPreExecute() {
			dialog = ProgressDialog.show(SettingProfile.this, "Uploading",
					"Please wait...", true);
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Bitmap... photo) {
			try {

				ByteArrayOutputStream bao = new ByteArrayOutputStream();
				photo[0].compress(Bitmap.CompressFormat.JPEG, 90, bao);
				byte[] ba = bao.toByteArray();
				String ba1 = Base64.encodeBytes(ba);
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("imagename", ba1));

				HttpClient httpclient = new DefaultHttpClient();
				String postUrl = getString(R.string.phoneVarificationServiceUrlLive)
						+ "?method=UpdateProfileImage&returntype=Json&phonenumber="
						+ phoneNumber.replace("+", "").replace(" ", "");
				Log.e("HttpUrl-ProfileImageSave", postUrl);
				HttpPost httppost = new HttpPost(postUrl);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(response.getEntity().getContent()));

				String sResponse = reader.readLine();
				Log.e("UploadImage-Json", sResponse);
				return sResponse;
			} catch (Exception e) {
				if (dialog.isShowing())
					dialog.dismiss();
				Log.e(e.getClass().getName(), e.getMessage(), e);
				return null;
			}
		}

		@Override
		protected void onProgressUpdate(Void... unsued) {

		}

		@Override
		protected void onPostExecute(String sResponse) {
			try {

				if (dialog.isShowing())
					dialog.dismiss();

				if (sResponse != null) {
					JSONObject JResponse = new JSONObject(sResponse);
					if (JResponse.getString("message").equalsIgnoreCase(
							"success")) {
						ImageLoader imgLoader = new ImageLoader(
								SettingProfile.this);
						imgLoader.clearCache();
						Toast.makeText(getApplicationContext(),
								"Photo uploaded successfully",
								Toast.LENGTH_SHORT).show();
					}
				}
			} catch (Exception e) {
				Toast.makeText(getApplicationContext(), e.getMessage(),
						Toast.LENGTH_LONG).show();
				Log.e(e.getClass().getName(), e.getMessage(), e);
			}
		}
	}

	private void saveOutput(Bitmap croppedImage) {
		if (mSaveUri != null) {
			File file = new File(Constants.PROFILE_IMAGE_SAVE_PATH);
			if (!file.exists())
				file.mkdirs();

			OutputStream outputStream = null;
			try {
				file = new File(mSaveUri.getPath());
				if (!file.exists())
					file.createNewFile();
				outputStream = mContentResolver.openOutputStream(mSaveUri);
				if (outputStream != null) {
					croppedImage.compress(mOutputFormat, 75, outputStream);
				}
			} catch (IOException ex) {
				// TODO: report error to caller
				Log.e("SettingProfile", "Cannot open file: " + mSaveUri, ex);
			} finally {
				ImageCropUtil.closeSilently(outputStream);
			}
		} else {
			Log.e("saveOutput", "not defined image url");
		} // croppedImage.recycle();
	}

	private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = getPackageManager().queryIntentActivities(
				intent, 0);

		int size = list.size();

		if (size == 0) {
			Toast.makeText(this, "Can not find image crop app",
					Toast.LENGTH_SHORT).show();

			return;
		} else {
			intent.setData(mImageCaptureUri);

			intent.putExtra("outputX", 200);
			intent.putExtra("outputY", 200);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);

			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName,
						res.activityInfo.name));

				startActivityForResult(i, CROPED_IMAGE);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = getPackageManager().getApplicationLabel(
							res.activityInfo.applicationInfo);
					co.icon = getPackageManager().getApplicationIcon(
							res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);

					co.appIntent
							.setComponent(new ComponentName(
									res.activityInfo.packageName,
									res.activityInfo.name));

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(
						getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle("Choose Crop App");
				builder.setAdapter(adapter,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								startActivityForResult(
										cropOptions.get(item).appIntent,
										CROPED_IMAGE);
							}
						});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

						if (mImageCaptureUri != null) {
							getContentResolver().delete(mImageCaptureUri, null,
									null);
							mImageCaptureUri = null;
						}
					}
				});

				AlertDialog alert = builder.create();

				alert.show();
			}
		}
	}

	private void LoadSettings() {
		View viewButton = findViewById(R.id.settings_profile_allow_search_by_id);
		((TextView) viewButton.findViewById(android.R.id.text1))
				.setText(R.string.setting_profile_allow_friend_search);
		viewButton = findViewById(R.id.settings_profile_name);
		viewButton.setOnClickListener(this);

		((TextView) viewButton.findViewById(android.R.id.text1))
				.setText(R.string.display_name);
		((TextView) viewButton.findViewById(android.R.id.text2))
				.setText(ScreenSplash.main.dbController
						.getDisplayNameSetting());
		/*viewButton = findViewById(R.id.settings_profile_phone);
		viewButton.setOnClickListener(this);
		((TextView) viewButton).setText(ScreenSplash.main.dbController
				.getPhoneNumberSetting());
*/
		viewButton = findViewById(R.id.settings_profile_comment);
		((TextView) viewButton.findViewById(android.R.id.text1))
				.setText(ScreenSplash.main.dbController
						.getStatusMsgSetting().equalsIgnoreCase("") ? "Not Set"
						: ScreenSplash.main.dbController
								.getStatusMsgSetting());
		viewButton.setOnClickListener(this);

		viewButton = findViewById(R.id.settings_profile_allow_search_by_id);
		((CheckBox) viewButton.findViewById(android.R.id.checkbox))
				.setChecked(ScreenSplash.main.dbController
						.getAllowSearchSetting().equalsIgnoreCase("yes") ? true
						: false);
		final Context context = this;
		phoneNumber = ScreenSplash.main.dbController
				.getPhoneNumberSetting();
		((CheckBox) viewButton.findViewById(android.R.id.checkbox))
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						String status = isChecked ? "Yes" : "No";
						String veriUrl = context
								.getString(R.string.phoneVarificationServiceUrlLive);
						veriUrl = veriUrl
								+ "?method=UpdateSearchSetting&returntype=Json&phonenumber="
								+ phoneNumber.replace("+", "").replace(" ", "")
								+ "&searchset=" + status;
						Log.d("VerificationURl: +", veriUrl);
						if (Utility.isNetworkAvailable(context)) {
							UpdateSearchSettingTask task = new UpdateSearchSettingTask(
									context);
							task.setMessageLoading("Loading...");
							task.execute(veriUrl);
						} else {
							showAlertView("No Netwrok Connection!!!", context);

						}

					}
				});
	}

	private class UpdateSearchSettingTask extends UrlJsonAsyncTask {
		public UpdateSearchSettingTask(Context context) {
			super(context);
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				Log.d("JsonData: +", json.getString("message").toString());
				if (json.getString("message").equalsIgnoreCase("success")) {
					ScreenSplash.main.dbController
							.setAllowSearchSetting(json
									.getString("searchsetting"));
				} else {
					showAlertView("Setting update failed. Please retry later.",
							context);
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

	protected void onResume() {
		super.onResume();
		this.LoadSettings();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.settings_profile_name) {
			Intent intent = new Intent(SettingProfile.this,
					SettingDisplayName.class);
			SettingProfile.this.startActivity(intent);
		} else if (v.getId() == R.id.settings_profile_comment) {
			Intent intent = new Intent(SettingProfile.this,
					ProfileComment.class);
			SettingProfile.this.startActivity(intent);
		}
	}
}
