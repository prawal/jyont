package com.ngbm.joynt.adaptors;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ngbm.joynt.R;
import com.ngbm.joynt.custom_classes.Friend;
import com.ngbm.joynt.custom_classes.FriendGroup;
import com.ngbm.joynt.custom_classes.ImageLoader;
import com.ngbm.joynt.interfaces.IFriend;

public class FriendsAdaptor extends ArrayAdapter<IFriend> {
	private LayoutInflater vi;
	private IFriend objItem;
	ViewHolderSectionName holderSection;
	ViewHolderName holderName;
	public ImageLoader imageLoader;
	private Context _context = null;

	// Initialize adapter
	public FriendsAdaptor(Context context, ArrayList<IFriend> contactList) {
		super(context, 0, contactList);
		_context = context;
		vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the current object
		objItem = getItem(position);

		if (objItem.isSectionItem()) {
			FriendGroup si = (FriendGroup) objItem;

			if (convertView == null
					|| !convertView.getTag().equals(holderSection)) {
				convertView = vi.inflate(R.layout.alphabet_separator, null);

				holderSection = new ViewHolderSectionName();
				convertView.setTag(holderSection);
			} else {
				holderSection = (ViewHolderSectionName) convertView.getTag();
			}

			holderSection.section = (TextView) convertView
					.findViewById(R.id.alphabet_letter);
			holderSection.section
					.setText(String.valueOf(si.getSectionHeader()));

		} else {
			Friend ei = (Friend) objItem;

			if (convertView == null || !convertView.getTag().equals(holderName)) {
				convertView = vi.inflate(R.layout.friend_list_row, null);

				holderName = new ViewHolderName();
				convertView.setTag(holderName);
			} else {
				holderName = (ViewHolderName) convertView.getTag();
			}
			((LinearLayout) convertView.findViewById(R.id.cellUser)).setTag(ei);

			holderName.displayname = (TextView) convertView
					.findViewById(R.id.titleDisplayName);
			holderName.phonenumber = (TextView) convertView
					.findViewById(R.id.titlePhoneNumber);
			holderName.comment = (TextView) convertView
					.findViewById(R.id.comment);
			holderName.imgprofile = (ImageView) convertView
					.findViewById(R.id.imgUser);

			((LinearLayout) convertView.findViewById(R.id.comment_area))
					.setTag(ei);
			if (holderName.displayname != null) {
				imageLoader.DisplayImage(ei.getPhoneNumber(),
						holderName.imgprofile, _context.getApplicationContext());
				holderName.displayname.setText(ei.getDisplayName());
				holderName.phonenumber.setText(ei.getPhoneNumber());
				if (ei.getComment() != null
						&& !ei.getComment().trim().equalsIgnoreCase("")) {
					holderName.comment.setText(ei.getComment());
					Log.d("holderName.comment true", ei.getComment());
				} else {
					holderName.comment.setText(ei.getComment());
					convertView.findViewById(R.id.wrapper).setVisibility(-1);
					Log.d("holderName.comment false", ei.getComment());
				}
			}
		}
		return convertView;

		// Log.d("CountryAdaptor", al.getCountryName());
	}

	@Override
	public boolean isEnabled(int position) {

		return false;
	}

	public static class ViewHolderName {
		public ImageView imgprofile;
		public TextView displayname;
		public TextView phonenumber;
		public TextView comment;
	}

	public static class ViewHolderSectionName {
		public TextView section;
	}
}
