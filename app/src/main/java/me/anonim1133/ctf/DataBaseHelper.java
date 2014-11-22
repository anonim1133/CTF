package me.anonim1133.ctf;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "DBHelper";
	private static String DATABASE_NAME = "zacja.db";
	private static final int DATABASE_VERSION = 1;

	private Context c;
	private SQLiteDatabase db;
	private DBWifi wifi;

	private static String DATABASE_CREATE = "BEGIN TRANSACTION;\n" +
			"CREATE TABLE `wifi` (\n" +
			"\t`id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
			"\t`ssid`\tTEXT,\n" +
			"\t`signal`\tINTEGER DEFAULT '0',\n" +
			"\t`security`\tINTEGER DEFAULT '0',\n" +
			"\t`longitude`\tREAL DEFAULT '0',\n" +
			"\t`latitude`\tREAL DEFAULT '0'\n" +
			");\n" +
			"CREATE TABLE `conquered` (\n" +
			"\t`id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
			"\t`points`\tINTEGER DEFAULT '0',\n" +
			"\t`date`\tINTEGER DEFAULT '0',\n" +
			"\t`longitude`\tREAL DEFAULT '0',\n" +
			"\t`latitude`\tREAL DEFAULT '0'\n" +
			");\n" +
			"CREATE INDEX `long_index` ON `conquered` (`longitude` ASC);\n" +
			"CREATE INDEX `lat_index` ON `conquered` (`latitude` ASC);\n" +
			"CREATE INDEX `date_index` ON `conquered` (`date` DESC);\n" +
			"COMMIT;\n";

	public DataBaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.c = context;
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
    db.execSQL("DROP TABLE IF EXISTS wifi; DROP TABLE IF EXISTS conquered;");
    onCreate(db);
	}

	public void open() throws SQLException {
		db = getWritableDatabase();

	}

	public boolean addWifi(String ssid, int signal, int security, double longitude, double latitude){
		return wifi.add(ssid, signal, security, longitude, latitude);
	}

	public void sendWifi() {
		wifi.send();
	}
}


