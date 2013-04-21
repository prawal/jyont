package com.ngbm.joynt.custom_classes;

import com.ngbm.joynt.interfaces.IFriend;

public class FriendGroup implements IFriend {
	private String sectionHeader;


	public String getSectionHeader() {
		return sectionHeader;
	}


	public void setSectionHeader(String sectionHeader) {
		this.sectionHeader = sectionHeader;
	}


	public boolean isSectionItem() {
		return true;
	}
}
