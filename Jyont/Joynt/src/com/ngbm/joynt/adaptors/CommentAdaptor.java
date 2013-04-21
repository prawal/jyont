package com.ngbm.joynt.adaptors;

import java.util.ArrayList;

import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ngbm.joynt.R;
import com.ngbm.joynt.ScreenSplash;
import com.ngbm.joynt.custom_classes.Comment;
import com.ngbm.joynt.custom_classes.ImageLoader;
import com.ngbm.joynt.custom_classes.Utility;
import com.ngbm.joynt.json_helper.UrlJsonAsyncTask;

public class CommentAdaptor extends ArrayAdapter<Comment> {
	private LayoutInflater vi;
	private Comment objItem;
	ViewHolderName holderName;
	private int _showDelete = 0;
	ArrayList<Comment> comments;
	Context _context;
	Comment _objcomment = null;
	ImageLoader imageLoader;
	String phoneNumber;
	View _objNocomments;
	// Initialize adapter
	public CommentAdaptor(Context context, ArrayList<Comment> commentList,
			int showDelete, String phone, View message) {
		super(context, 0, commentList);
		vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		_showDelete = showDelete;
		comments = commentList;
		_context = context;
		imageLoader = new ImageLoader(context);
		phoneNumber = phone;
		_objNocomments = message;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the current object
		objItem = getItem(position);

		Comment ei = (Comment) objItem;

		if (convertView == null || !convertView.getTag().equals(holderName)) {
			convertView = vi.inflate(R.layout.comment_list_row, null);

			holderName = new ViewHolderName();
			convertView.setTag(holderName);
		} else {
			holderName = (ViewHolderName) convertView.getTag();
		}
		((LinearLayout) convertView.findViewById(R.id.comment_area)).setTag(ei);

		holderName.comment = (TextView) convertView.findViewById(R.id.comment);
		holderName.date = (TextView) convertView.findViewById(R.id.date_time);
		holderName.imgprofile = (ImageView) convertView
				.findViewById(R.id.imgUser);
		if (ei.getComment() != null
				&& !ei.getComment().trim().equalsIgnoreCase("")) {
			imageLoader.DisplayImage(phoneNumber,
					holderName.imgprofile, _context.getApplicationContext());
			holderName.comment.setText(ei.getComment());
			holderName.date.setText(ei.getPosted_on().toString());
			Log.d("holderName.comment true", ei.getComment());
		} else {
			holderName.comment.setText(ei.getComment());
			convertView.findViewById(R.id.wrapper).setVisibility(-1);
			Log.d("holderName.comment false", ei.getComment());
		}
		if (_showDelete > 0) {
			Button btnDelete = (Button) convertView
					.findViewById(R.id.btnDelete);
			btnDelete.setVisibility(0);
			btnDelete.setTag(ei);
			btnDelete.setOnClickListener(new AdapterView.OnClickListener() {

				@Override
				public void onClick(View v) {
					_objcomment = ((Comment) v.getTag());

					if (Utility.isNetworkAvailable(_context)) {
						String veriUrl = _context
								.getString(R.string.phoneVarificationServiceUrlLive);
						veriUrl = veriUrl
								+ "?method=DeleteComment&returntype=Json&id="
								+ _objcomment.getId();
						DeleteTask task = new DeleteTask(_context);
						task.setMessageLoading("Deleting...");
						task.execute(veriUrl);
					} else {
						showAlertView("No Netwrok Connection!!!", _context);

					}

				}
			});
		}
		return convertView;

		// Log.d("CountryAdaptor", al.getCountryName());
	}

	Dialog myDialog;

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

	private class DeleteTask extends UrlJsonAsyncTask {
		public DeleteTask(Context context) {
			super(context);
		}

		@Override
		protected void onPostExecute(JSONObject json) {
			try {
				Log.d("JsonData: +", json.getString("message"));
				if (json.getString("message").equalsIgnoreCase("success")) {
					comments.remove(_objcomment);
					ScreenSplash.main.dbController
							.deleteCommentItem(_objcomment);
					ArrayList<Comment> commTemp = ScreenSplash.main.dbController.getComments(0);
					if(commTemp.size()>0){
						_objNocomments.setVisibility(-1);
						ScreenSplash.main.dbController.setStatusMsgSetting(commTemp.get(0).getComment());
					}else{
						_objNocomments.setVisibility(0);
						ScreenSplash.main.dbController.setStatusMsgSetting("");
						
					}
					notifyDataSetChanged();
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
	public boolean isEnabled(int position) {

		return false;
	}

	public static class ViewHolderName {
		public ImageView imgprofile;
		public TextView comment;
		public TextView date;
	}
}
