package com.ngbm.joynt.custom_classes;

import com.ngbm.joynt.interfaces.ICountry;

public class Country2 implements ICountry, Comparable<Country> {

	private String PhonePrefix;
	private String CountryName;

	public String getPhonePrefix() {
		return PhonePrefix;
	}

	public void setPhonePrefix(String PhonePrefix) {
		this.PhonePrefix = "+" + PhonePrefix;
	}

	public String getCountryName() {
		return CountryName;
	}

	public void setCountryName(String CountryName) {
		this.CountryName = CountryName;
	}
	public boolean isSectionItem() {
		return false;
	}
	@Override
	public int compareTo(Country another) {
		return this.getCountryName().compareTo(another.getCountryName());
	}
}
