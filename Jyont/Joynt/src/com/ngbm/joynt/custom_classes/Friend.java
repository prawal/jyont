package com.ngbm.joynt.custom_classes;

import com.ngbm.joynt.interfaces.*;


public class Friend implements IFriend, Comparable<Friend>{

	private int id;
	private boolean is_blocked;
	private String phoneNumber;
	private String emailAddress;
	private String displayName;
	private String comment;
	
	private String groupName;
	
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmailAddress() {
		return emailAddress;
	}
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}
	@Override
	public boolean isSectionItem() {

		return false;
	}
	@Override
	public int compareTo(Friend another) {
		return this.getDisplayName().compareTo(another.getDisplayName());
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public boolean getIs_blocked() {
		return is_blocked;
	}
	public void setIs_blocked(boolean is_blocked) {
		this.is_blocked = is_blocked;
	}
}
