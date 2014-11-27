package me.anonim1133.ctf;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBConquered {
	private static String TAG = "ZACJA_DB_CONQ";
	private static String TABLE_NAME = "conquered";

	private SQLiteDatabase db;

	public DBConquered(SQLiteDatabase database){
		this.db = database;
	}

	public boolean add(int points, long date,  double longitude, double latitude){
		ContentValues values = new ContentValues();
		values.put("points", points);
		values.put("date", date);
		values.put("longitude", longitude);
		values.put("latitude", latitude);

		try{
			long value = db.insertOrThrow(TABLE_NAME, null, values);
			Log.i(TAG, "Dodajno conquered z id: " + value);
			if(value > 0) return true;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
		}

		return false;
	}

	public Cursor getLast(int limit) {
		Cursor cursor = db.query(TABLE_NAME , new String[] {"id", "points", "date",  "longitude", "latitude"}, null, null, null, null, "id DESC", String.valueOf(limit));

		return cursor;
	}
}