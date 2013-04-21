package com.ngbm.joynt.database;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.ngbm.joynt.custom_classes.Comment;
import com.ngbm.joynt.custom_classes.Friend;

public class DatabaseController extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "Joynt.db";
	private static final int DATABASE_VERSION = 1;
	private static final String USER_DATA = "create table settings (settings text primary key, "
			+ "value text null);";
	private static final String COMMENT_DATA = "create table comment (id int primary key,"
			+ "comment text null, posted_on string null);";
	private static final String FRIEND_LIST = "create table friend_list (id int primary key, "
			+ "display_name text null, phone_number text null, email_id text null, comment text null, group_name text null, is_blocked bool false);";

	public DatabaseController(Context context) {

		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(USER_DATA);
		database.execSQL(FRIEND_LIST);
		database.execSQL(COMMENT_DATA);
		//Log.d("onCreate", "Create");

	}

	// Method is called during an upgrade of the database, e.g. if you increase
	// the database version
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(this.getClass().getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS settings");
		database.execSQL("DROP TABLE IF EXISTS friend_list");
		database.execSQL("DROP TABLE IF EXISTS comment");
		onCreate(database);
	}

	public void initializeSettings() {
		try {
			SQLiteDatabase db = getWritableDatabase();
			ContentValues cv = new ContentValues();

			// for the settings table (integer settings)
			cv.put("settings", "phone_number");
			cv.put("value", "");
			db.insert("settings", null, cv);
			cv.clear();

			cv.put("settings", "display_name");
			cv.put("value", "");
			db.insert("settings", null, cv);
			cv.clear();

			cv.put("settings", "verification_status");
			cv.put("value", "NEW");
			db.insert("settings", null, cv);
			cv.clear();

			cv.put("settings", "use_contact_list");
			cv.put("value", "false");
			db.insert("settings", null, cv);
			cv.clear();

			cv.put("settings", "status_msg");
			cv.put("value", "");
			db.insert("settings", null, cv);
			cv.clear();

			cv.put("settings", "allow_search");
			cv.put("value", "false");
			db.insert("settings", null, cv);
			cv.clear();

			db.close();
		} catch (Exception ex) {
			Log.e("initializeSettings", "ERROR: " + ex.getMessage());
		}
	}

	public void setStatusMsgSetting(String value) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put("value", value);
		//Log.d("setStatusMsgSetting", "SET TO " + value);
		db.update("settings", cv, "settings = \"status_msg\"", null);
		db.close();
	}

	public String getStatusMsgSetting() {
		try {
			SQLiteDatabase db = getReadableDatabase();
			Cursor cursor = db.query("settings", null,
					"settings = \"status_msg\"", null, null, null, null);
			String nTmp = "";
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					//Log.d("getStatusMsgSetting", "Inside move first");
					do {
						nTmp = cursor.getString(1);
					} while (cursor.moveToNext());
				}
			}
			db.close();
			cursor.close();
			return nTmp;
		} catch (Exception e) {
			Log.d("getStatusMsgSetting", e.getMessage());
			return "";
		}
	}

	public void setAllowSearchSetting(String value) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put("value", value);
		//Log.d("setAllowSearchSetting", "SET TO " + value);
		db.update("settings", cv, "settings = \"allow_search\"", null);
		db.close();
	}

	public String getAllowSearchSetting() {
		try {
			SQLiteDatabase db = getReadableDatabase();
			Cursor cursor = db.query("settings", null,
					"settings = \"allow_search\"", null, null, null, null);
			String nTmp = "";
			if (cursor != null) {
				if (cursor.moveToFirst()) {
					//Log.d("getAllowSearchSetting", "Inside move first");
					do {
						nTmp = cursor.getString(1);
					} while (cursor.moveToNext());
				}
			}
			db.close();
			cursor.close();
			return nTmp;
		} catch (Exception e) {
			Log.d("getAllowSearchSetting", e.getMessage());
			return "false";
		}
	}

	public void setPhoneNumberSetting(String value) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put("value", value);
		//Log.d("setPhoneNumberSetting", "SET TO " + value);
		db.update("settings", cv, "settings = \"phone_number\"", null);
		db.close();
	}

	public String getPhoneNumberSetting() {
		try {
			SQLiteDatabase db = getReadableDatabase();
			Cursor cursor = db.query("settings", null,
					"settings = \"phone_number\"", null, null, null, null);

			String nTmp = "";
			if (cursor != null) {

				if (cursor.moveToFirst()) {
					//Log.d("getPhoneNumberSetting", "Inside move first");
					do {
						nTmp = cursor.getString(1);
					} while (cursor.moveToNext());
				}
			}
			db.close();
			cursor.close();
			return nTmp;
		} catch (Exception e) {
			Log.d("getPhoneNumberSetting", e.getMessage());
			return "";
		}
	}

	public void setDisplayNameSetting(String value) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put("value", value);
		//Log.e("setDisplayNameSetting", "SET TO " + value);

		db.update("settings", cv, "settings = \"display_name\"", null);
		db.close();
	}

	public String getDisplayNameSetting() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("settings", null,
				"settings = \"display_name\"", null, null, null, null);
		String nTmp = "";
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				nTmp = cursor.getString(1);
			}
		}
		db.close();
		cursor.close();
		return nTmp;

	}

	public void setVerificationStatusSetting(String value) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put("value", value);
		//Log.e("setVerificationStatusSetting", "SET TO " + value);

		db.update("settings", cv, "settings = \"verification_status\"", null);
		db.close();
	}

	public String getVerificationStatusSetting() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("settings", null,
				"settings = \"verification_status\"", null, null, null, null);
		String nTmp = "";
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				nTmp = cursor.getString(1);
			}
		}
		db.close();
		cursor.close();
		return nTmp;

	}

	public void setUseContactListSetting(String value) {
		SQLiteDatabase db = getWritableDatabase();
		ContentValues cv = new ContentValues();

		cv.put("value", value);
		//Log.e("use_contact_list", "SET TO " + value);

		db.update("settings", cv, "settings = \"use_contact_list\"", null);
		db.close();
	}

	public String getUseContactListSetting() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query("settings", null,
				"settings = \"use_contact_list\"", null, null, null, null);
		String nTmp = "";
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				nTmp = cursor.getString(1);
			}
		}
		db.close();
		cursor.close();
		return nTmp;

	}

	/*
	 * Adds comment to database
	 */
	public void insertCommentItem(Comment obj) {
		try {
			SQLiteDatabase db = getWritableDatabase();
			ContentValues cv = new ContentValues();

			cv.put("id", obj.getId());
			cv.put("comment", obj.getComment());
			cv.put("posted_on", obj.getPosted_on().toString());
			db.insert("comment", null, cv);
			db.close();
			Log.e("insertCommentItem", "Comment Inserted: " + obj.getComment());
		} catch (Exception e) {
			Log.e("insertCommentItem", "Error: " + e.getMessage());
		}
	}

	/*
	 * Adds comment to database
	 */
	public void deleteCommentItem(Comment obj) {
		try {
			SQLiteDatabase db = getWritableDatabase();

			db.delete("comment", "id = " + obj.getId(), null);
			db.close();
			Log.e("deleteCommentItem", "Comment deleted: " + obj.getComment());
		} catch (Exception e) {
			Log.e("deleteCommentItem", "Error: " + e.getMessage());
		}
	}

	/*
	 * Retrieves shelf item entries from the database and stores it in an
	 * ArrayList
	 */
	@SuppressWarnings("deprecation")
	public ArrayList<Comment> getComments(int id) {
		ArrayList<Comment> commentItems = new ArrayList<Comment>();
		SQLiteDatabase db = getReadableDatabase();

		String strQuery = "select id, comment, posted_on from comment ";
		if (id > 0) {
			strQuery = strQuery + "where id= " + id;
		}
		strQuery = strQuery + " order by posted_on desc";

		Cursor cursor = db.rawQuery(strQuery, null);
		Comment objComment = null;
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					objComment = new Comment();
					objComment.setId(cursor.getInt(0));
					objComment.setComment(cursor.getString(1));
					objComment.setPosted_on(new Date(cursor.getString(2)));
					commentItems.add(objComment);
				} while (cursor.moveToNext());
			}
		}
		cursor.close();
		db.close();
		return commentItems;
	}

	/*
	 * Retrieves shelf item entries from the database and stores it in an
	 * ArrayList
	 */
	public ArrayList<Friend> getFriends(long phone_number) {
		ArrayList<Friend> friendsItems = new ArrayList<Friend>();
		SQLiteDatabase db = getReadableDatabase();
		String strQuery = "select id, display_name, phone_number, email_id,comment, group_name, is_blocked from friend_list ";
		if (phone_number > 0) {
			strQuery = strQuery + "where phone_number= \"+" + phone_number + "\"";
		}
		strQuery = strQuery + " order by display_name asc";
		//Log.d("FriendQuery", strQuery);
		Cursor cursor = db.rawQuery(strQuery, null);
		Friend objFriend = null;
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					//Log.d("FriendFound", cursor.getString(1));
					objFriend = new Friend();
					objFriend.setId(cursor.getInt(0));
					objFriend.setDisplayName(cursor.getString(1));
					objFriend.setPhoneNumber(cursor.getString(2));
					objFriend.setEmailAddress(cursor.getString(3));
					objFriend.setComment(cursor.getString(4));
					objFriend.setGroupName(cursor.getString(5));
					objFriend.setIs_blocked(Boolean.parseBoolean(cursor
							.getString(6)));
					friendsItems.add(objFriend);
				} while (cursor.moveToNext());
			}
		}
		cursor.close();
		db.close();
		return friendsItems;
	}

	/*
	 * Adds friend to database
	 */
	public void insertFriend(Friend obj) {
		try {
			SQLiteDatabase db = getWritableDatabase();
			ContentValues cv = new ContentValues();

			cv.put("display_name", obj.getDisplayName());
			cv.put("phone_number", obj.getPhoneNumber());
			cv.put("email_id", obj.getEmailAddress());
			cv.put("is_blocked", obj.getIs_blocked());
			cv.put("group_name", obj.getGroupName());
			cv.put("comment", obj.getComment());
			db.insert("friend_list", null, cv);
			db.close();
			Log.e("insertFriend", "Friend Inserted: " + obj.getDisplayName());
		} catch (Exception e) {
			Log.e("insertFriend", "Error: " + e.getMessage());
		}
	}

	public void updateFriend(Friend obj) {
		try {
			SQLiteDatabase db = getWritableDatabase();
			ContentValues cv = new ContentValues();

			cv.put("display_name", obj.getDisplayName());
			cv.put("phone_number", obj.getPhoneNumber());
			cv.put("email_id", obj.getEmailAddress());
			cv.put("is_blocked", obj.getIs_blocked());
			cv.put("group_name", obj.getGroupName());
			cv.put("comment", obj.getComment());
			db.update("friend_list", cv,
					"phone_number = \"" + obj.getPhoneNumber() + "\"", null);
			db.close();
			Log.e("updateFriend", "Friend Updated: " + obj.getDisplayName());
		} catch (Exception e) {
			Log.e("updateFriend", "Error: " + e.getMessage());
		}
	}

	/*
	 * Checks if the program is newly installed
	 */
	public boolean isFirstRun() {
		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.rawQuery(
				"SELECT value FROM settings where settings=\"phone_number\"",
				null);
		String strTmp = null;
		if (cursor != null) {
			if (cursor.moveToFirst()) {
				do {
					strTmp = cursor.getString(0);
				} while (cursor.moveToNext());
			}
		}

		cursor.close();
		db.close();

		if (strTmp == null || strTmp.equals("NEW")) {
			return true;
		} else {
			return false;
		}
	}
}
