package com.stien.festivaljakt.slottsfjell;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TagDatabase extends SQLiteOpenHelper {
	/* Ignore this name when the server returns it. #poorSolutions */
	private static final String NO_REGISTERED_NAME = "__!!__##__";

	private static final String TABLE_NAME = "TagDatabase";
	private static final String ID_KEY = "id";
	private static final String ID_NAME = "name";
	private static final String ID_TIME = "time";


	public TagDatabase(Context context) {
		super(context, TABLE_NAME, null, 1);
	}

	@Override
	public void onCreate(SQLiteDatabase sqLiteDatabase) {
		String query = "CREATE TABLE " + TABLE_NAME + "( "
				+ ID_KEY + " INTEGER PRIMARY KEY, "
				+ ID_NAME + " TEXT NOT NULL, "
				+ ID_TIME + " INTEGER "
				+ ") ";
		sqLiteDatabase.execSQL(query);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
		// yolo
	}

	public void insertName(TagUploader.UploadResponse response) {
		if (response.tagOwnerName.equals(NO_REGISTERED_NAME)) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(ID_NAME, response.tagOwnerName);
		values.put(ID_TIME, System.currentTimeMillis() / 1000L);

		SQLiteDatabase db = getWritableDatabase();
		db.insert(TABLE_NAME, null, values);
		db.close();
	}

	ArrayList<Tag> getTags() {
		ArrayList<Tag> tags = new ArrayList<Tag>();

		SQLiteDatabase db = getReadableDatabase();
		Cursor cursor = db.query(false, TABLE_NAME, new String[]{ID_NAME,ID_TIME}, null, null, null, null, null, "50");

		if (cursor != null) {
			cursor.moveToFirst();
			long now = System.currentTimeMillis() / 1000L;

			while (!cursor.isAfterLast()) {
				String name = cursor.getString(0);
				int time = cursor.getInt(1);

				tags.add(new Tag(name, now - time));
				cursor.moveToNext();
			}

			cursor.close();
		}

		db.close();
		return tags;
	}
}
