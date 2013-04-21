package com.ngbm.joynt.custom_classes;

import com.ngbm.joynt.interfaces.ICountry;

public class CountryGroup implements ICountry {
	private char sectionLetter;


	public char getSectionLetter() {
		return sectionLetter;
	}


	public void setSectionLetter(char sectionLetter) {
		this.sectionLetter = sectionLetter;
	}


	public boolean isSectionItem() {
		return true;
	}
}
