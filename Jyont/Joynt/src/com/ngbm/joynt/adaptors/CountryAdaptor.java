package com.ngbm.joynt.adaptors;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ngbm.joynt.R;
import com.ngbm.joynt.custom_classes.Country;
import com.ngbm.joynt.custom_classes.CountryGroup;
import com.ngbm.joynt.interfaces.ICountry;

public class CountryAdaptor extends ArrayAdapter<ICountry> {
	private LayoutInflater vi;
	private ICountry objItem;
	ViewHolderSectionName holderSection;
	ViewHolderName holderName;

	// Initialize adapter
	public CountryAdaptor(Context context, ArrayList<ICountry> countryList) {
		super(context, 0, countryList);
		vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// Get the current object
		objItem = getItem(position);

		if (objItem.isSectionItem()) {
			CountryGroup si = (CountryGroup) objItem;

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
					.setText(String.valueOf(si.getSectionLetter()));
			holderSection.section = (TextView) convertView
					.findViewById(R.id.groupID);
			holderSection.section
					.setText(String.valueOf(si.getSectionLetter()));

		} else {
			Country ei = (Country) objItem;

			if (convertView == null || !convertView.getTag().equals(holderName)) {
				convertView = vi.inflate(R.layout.country_row, null);

				holderName = new ViewHolderName();
				convertView.setTag(holderName);
			} else {
				holderName = (ViewHolderName) convertView.getTag();
			}

			holderName.name = (TextView) convertView
					.findViewById(R.id.titleCountryName);
			holderName.prefix = (TextView) convertView
					.findViewById(R.id.titlePrefixCode);
			if (holderName.name != null) {
				holderName.name.setText(ei.getCountryName());
				holderName.prefix.setText(ei.getPhonePrefix());
			}
		}
		return convertView;

		// Log.d("CountryAdaptor", al.getCountryName());
	}

	public static class ViewHolderName {
		public TextView name;
		public TextView prefix;
	}

	public static class ViewHolderSectionName {
		public TextView section;
	}
}
