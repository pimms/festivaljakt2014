package com.stien.festivaljakt.slottsfjell;


import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class UserPreferences {

	public static SharedPreferences getDefaultSharedPreferences() {
		return PreferenceManager.getDefaultSharedPreferences(ScanApplication.sharedApplicationContext());
	}


	public static String getUserName() {
		return getDefaultSharedPreferences().getString("username", null);
	}

	public static void setUserName(String username) {
		SharedPreferences.Editor edit = getDefaultSharedPreferences().edit();
		edit.putString("username", username);
		edit.commit();
	}


	public static String getUserTag() {
		return getDefaultSharedPreferences().getString("usertag", null);
	}

	public static void setUserTag(String usertag) {
		SharedPreferences.Editor edit = getDefaultSharedPreferences().edit();
		edit.putString("usertag", usertag);
		edit.commit();
	}


	public static int getUserScore() {
		return getDefaultSharedPreferences().getInt("score", 0);
	}

	public static void setUserScore(int score) {
		SharedPreferences.Editor edit = getDefaultSharedPreferences().edit();
		edit.putInt("score", score);
		edit.commit();
	}
}
