package com.ngbm.joynt.custom_classes;

import java.util.Date;

public class Comment {
	private int id;
	private int user_id;
	private String comment;
	private Date posted_on;
	
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Date getPosted_on() {
		return posted_on;
	}
	public void setPosted_on(Date date) {
		this.posted_on = date;
	}
}
