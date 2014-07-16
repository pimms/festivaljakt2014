package com.stien.festivaljakt.slottsfjell;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class TagDatabase extends SQLiteOpenHelper {
	private static final String TABLE_NAME = "TagDatabase";
	private static final String ID_KEY = "id";
	private static final String ID_NAME = "name";


	public TagDatabase(Context context) {
		super(context, TABLE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		String query = "CREATE TABLE " + TABLE_NAME + "( "
				+ ID_KEY + " INTEGER PRIMARY KEY, "
				+ ID_NAME + " TEXT NOT NULL "
				+ ") ";
		sqLiteDatabase.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
		// yolo
	}

	public void insertName(TagUploader.UploadResponse response) {
		ContentValues values = new ContentValues();
		values.put(ID_NAME, response.tagOwnerName);

		SQLiteDatabase db = getWritableDatabase();
		db.insert(TABLE_NAME, null, values);
		db.close();
	}

	ArrayList<String> getNames() {
		ArrayList<String> names = new ArrayList<String>();

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(false, TABLE_NAME, new String[]{ID_NAME}, null, null, null, null, null, "50");

		if (cursor != null) {
			cursor.moveToFirst();

			while (!cursor.isAfterLast()) {
				String name = cursor.getString(0);
				names.add(name);
				cursor.moveToNext();
			}

			cursor.close();
		}

		db.close();
		return names;
	}
}
