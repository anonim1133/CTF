package me.anonim1133.ctf;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.sql.SQLException;

public class DataBaseHelper extends SQLiteOpenHelper {

	private static final String TAG = "DBHelper";
	private static String DATABASE_NAME = "zacja.db";
	private static final int DATABASE_VERSION = 1;

	private Context c;
	private SQLiteDatabase db;
	private DBWifi wifi;
	private DBConquered conquered;

	public DataBaseHelper(Context context) throws SQLException {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.c = context;

		Log.d(TAG, "konstruktor");
		this.open();

		wifi = new DBWifi(this.db);
		conquered = new DBConquered(this.db);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		String CREATE_CONQUERED_TABLE = "CREATE TABLE `conquered` ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `points` INTEGER DEFAULT '0', `date` INTEGER DEFAULT '0', `longitude` REAL DEFAULT '0', `latitude` REAL DEFAULT '0'); CREATE INDEX `long_index` ON `conquered` (`longitude` ASC);CREATE INDEX `lat_index` ON `conquered` (`latitude` ASC);CREATE INDEX `date_index` ON `conquered` (`date` DESC);";
		String CREATE_WIFI_TABLE = "CREATE TABLE `wifi` ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `ssid` TEXT, `signal` INTEGER DEFAULT '0', `security` INTEGER DEFAULT '0', `longitude` REAL DEFAULT '0', `latitude` REAL DEFAULT '0')";

		Log.d(TAG, "onCreate");
		database.execSQL(CREATE_CONQUERED_TABLE);
		database.execSQL(CREATE_WIFI_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
		Log.d(TAG, "OnUpgrade");
    db.execSQL("DROP TABLE IF EXISTS wifi; DROP TABLE IF EXISTS conquered;");
    onCreate(db);
	}

	public void open() throws SQLException {
		Log.d(TAG, "open");
		db = this.getWritableDatabase();

		Log.d(TAG, "PATH: " + db.getPath());
	}

	public boolean addWifi(String ssid, int signal, int security, double longitude, double latitude){
		return wifi.add(ssid, signal, security, longitude, latitude);
	}

	public void sendWifi() {
		wifi.send();
	}

	public  boolean addConquer(int points, String date, double longitude, double latitude){
		return conquered.add(points, date, longitude, latitude);
	}
}


