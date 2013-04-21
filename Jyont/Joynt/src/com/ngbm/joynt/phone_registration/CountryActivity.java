package com.ngbm.joynt.phone_registration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ngbm.joynt.R;
import com.ngbm.joynt.adaptors.CountryAdaptor;
import com.ngbm.joynt.custom_classes.Country;
import com.ngbm.joynt.custom_classes.CountryGroup;
import com.ngbm.joynt.interfaces.ICountry;

public class CountryActivity extends Activity implements
		TextWatcher {
	private ListView listCountry;

	private CountryAdaptor listAdapter = null;
	ArrayList<ICountry> itemsSection = new ArrayList<ICountry>();
	AlertDialog alert = null;
	ArrayList<Country> countryList;
	ArrayList<Country> filterArray = new ArrayList<Country>();

	EditText mySearch;
	String searchString;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.country_list);

		((TextView) findViewById(R.id.header_name))
				.setText(R.string.countryTitle);
		((TextView) findViewById(R.id.header_left_button_text)).setText("Back");

		findViewById(R.id.header_left_button).setVisibility(1);
		findViewById(R.id.header_right_button).setVisibility(-1);

		mySearch = (EditText) findViewById(R.id.input_search_query);
		mySearch.setInputType(android.text.InputType.TYPE_CLASS_TEXT
				| android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
		mySearch.addTextChangedListener(this);

		findViewById(R.id.header_left_button).setOnClickListener(
				new OnClickListener() {
					public void onClick(View v) {
						dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
								KeyEvent.KEYCODE_BACK));
						dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_UP,
								KeyEvent.KEYCODE_BACK));
					}
				});

		listCountry = (ListView) findViewById(R.id.listCountry);
		countryList = new ArrayList<Country>();
		try {

			InputStream inputStream = getResources().openRawResource(
					R.raw.countries);
			if (inputStream != null) {
				InputStreamReader inputreader = new InputStreamReader(
						inputStream);
				BufferedReader r = new BufferedReader(inputreader);
				String strLine;
				while ((strLine = r.readLine()) != null) {
					// Log.d("Country", strLine);
					String[] strCountry = strLine.split("-");
					Country _objCountry = new Country();
					_objCountry.setPhonePrefix(strCountry[1].replaceAll("\\s",
							""));
					_objCountry.setCountryName(strCountry[2]);
					countryList.add(_objCountry);
					// Log.d("Country prefix: +", strCountry[1]);
				}

				setAdapterToListview(countryList);
				final Context context = this;
				listCountry.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (view.getTag().getClass().getSimpleName()
								.equals("ViewHolderName")) {
							String countryName = ((TextView) ((LinearLayout) view)
									.findViewById(R.id.titleCountryName))
									.getText().toString();
							String countryPrefixCode = ((TextView) ((LinearLayout) view)
									.findViewById(R.id.titlePrefixCode))
									.getText().toString();

							// Toast.makeText(getBaseContext(), countryName,
							// Toast.LENGTH_LONG).show();

							Intent intent = new Intent(context,
									JoyntActivity.class);
							intent.putExtra("selectedCountry", countryName
									+ "-" + countryPrefixCode);

							startActivity(intent);
							finish();
						}
					}
				});
			}

		} catch (Exception e) {
			Log.d("CountryException", e.getMessage());
		}

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

			// Log.d("Activity",
			// "Touch event "+event.getRawX()+","+event.getRawY()+" "+x+","+y+" rect "+w.getLeft()+","+w.getTop()+","+w.getRight()+","+w.getBottom()+" coords "+scrcoords[0]+","+scrcoords[1]);
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

	// Textwatcher's ovveride methods

	@Override
	public void afterTextChanged(Editable s) {
		filterArray.clear();
		searchString = mySearch.getText().toString().trim()
				.replaceAll("\\s", "");

		if (countryList.size() > 0 && searchString.length() > 0) {
			for (Country name : countryList) {
				if (name.getCountryName().toLowerCase()
						.startsWith(searchString.toLowerCase())) {

					filterArray.add(name);
				}
			}
			setAdapterToListview(filterArray);
		} else {
			filterArray.clear();
			setAdapterToListview(countryList);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	// Here Data is Filtered!!!
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	public void setAdapterToListview(ArrayList<Country> listForAdapter) {

		itemsSection.clear();

		if (null != listForAdapter && listForAdapter.size() != 0) {

			Collections.sort(listForAdapter);

			char checkChar = ' ';

			for (int index = 0; index < listForAdapter.size(); index++) {

				Country objItem = (Country) listForAdapter.get(index);

				char firstChar = objItem.getCountryName().charAt(0);

				if (' ' != checkChar) {
					if (checkChar != firstChar) {
						CountryGroup objSectionItem = new CountryGroup();
						objSectionItem.setSectionLetter(firstChar);
						itemsSection.add(objSectionItem);
					}
				} else {
					CountryGroup objSectionItem = new CountryGroup();
					objSectionItem.setSectionLetter(firstChar);
					itemsSection.add(objSectionItem);
				}

				checkChar = firstChar;

				itemsSection.add(objItem);
			}
		} else {
			showAlertView(this.getString(R.string.countryAlertMessage)
					+ searchString, this);
		}

		if (null == listAdapter) {
			listAdapter = new CountryAdaptor(CountryActivity.this, itemsSection);
			listCountry.setAdapter(listAdapter);
			// listCountry1.setAdapter(listAdapter);
		} else {
			listAdapter.notifyDataSetChanged();
		}

	}

	Dialog myDialog = null;

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
