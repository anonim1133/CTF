package me.anonim1133.ctf;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends FragmentActivity{

	private static String TAG = "MAP";

	private WifiManager mainWifi;
	private Location last_location;

    private GoogleMap mMap;
	private DataBaseHelper db;
	private ProgressBar progress;

	//private GpsHelper mGps;

	float distance_between_last_locations = 100f;
	long last_scan_time = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	    Log.d(MapsActivity.TAG, "onCreate");

	    //mGps = new GpsHelper(this);
	    try {
		    db = new DataBaseHelper(this);
	    } catch (SQLException e) {
		    Log.d(MapsActivity.TAG, "Błąd przy otwieraniu bazu: " + e.toString());
	    }

	    progress = (ProgressBar) findViewById(R.id.progressBar);

	    setUpMapIfNeeded();
    }

	private  void setUpWifi(){
		mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if(!mainWifi.isWifiEnabled()){
			mainWifi.setWifiEnabled(true);
		}
	}

	private void scanWifi(){
		mainWifi.startScan();

	}

	private void stopScanWifi(){
		//ToDo:
	}

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }

	    mMap.setMyLocationEnabled(true);
	    mMap.getUiSettings().setZoomControlsEnabled(false);
	    mMap.getUiSettings().setMyLocationButtonEnabled(true);

    }

	private void centerMapOnMyLocation() {

		mMap.setMyLocationEnabled(true);

		Location location = mMap.getMyLocation();

		if (location != null) {
			LatLng myLocation = new LatLng(location.getLatitude(),
					location.getLongitude());

			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, mMap.getMaxZoomLevel() - 5));
		}

	}

	public void onScan(View view) {
		Log.d(MapsActivity.TAG, "onScan");
		last_location = mMap.getMyLocation();

		//Czas od ostatniego skanowania
		long time_difference = System.currentTimeMillis() - last_scan_time;

		//Sprawdzenie, czy miało ono miejsce więcej niż 10s temu
		if(time_difference > 10000) {//10s
			last_scan_time = System.currentTimeMillis();

			setUpWifi();
			scanWifi();

			progress.setVisibility(View.VISIBLE);

			CountDownTimer cdt = new CountDownTimer(10000, 1000) {
				public void onTick(long millisUntilFinished) {
					progress.setProgress((int) ((10000 - millisUntilFinished) / 1000) + 1);
				}

				public void onFinish() {
					progress.setProgress(10);
					progress.setVisibility(View.GONE);
					onScanFinish(mainWifi.getScanResults());
				}
			}.start();

		}else{
			messageBox("Skanowanie już trwa", "Skanowanie można wykonać raz na 10s");
		}
	}

	public void onScanFinish(List<ScanResult> wifis){
		Location current_location = mMap.getMyLocation();
		Float distance = 0.0f;
		Boolean add = true;

		//Zliczyć punkty
		int points = 0;
		Iterator<ScanResult> iterator = wifis.iterator();
		while (iterator.hasNext()) {
			ScanResult result = iterator.next();
			int tmp_points = 100; //max za hotspot

			tmp_points -= result.level;
			if(!result.capabilities.matches("\\[WEP\\]|\\[ESS\\]|\\[WPA\\]")){
				//Siec bez zapezpieczen
				tmp_points += 10;

				db.addWifi(result.SSID, result.BSSID, result.level, 0, current_location.getLongitude(), current_location.getLatitude());
			}else{
				//Siec zabezpieczona
				db.addWifi(result.SSID, result.BSSID, result.level, 1, current_location.getLongitude(), current_location.getLatitude());
			}

			points = tmp_points/10;
		}

		//Sprawdzić czy gracz jest wciąż w tym samym miejscu w którym zaczął skanowanie
		distance = current_location.distanceTo(last_location);

		Log.d(TAG, "Dystans między ostatnią i obecną lokacją: " + distance);
		if(distance > distance_between_last_locations){
			add = false;
		}

		//Sprawdzic czy nie pokrywa się z ostatnimi dodanymi punktami
		Cursor conquers = db.getLastQonquers(5);
		conquers.moveToFirst();
		while (!conquers.isAfterLast()) {
			Location tmp_location = new Location("tmp");
			tmp_location.setLongitude(conquers.getDouble(3));
			tmp_location.setLatitude(conquers.getDouble(4));

			Log.d(TAG, "Dystans między obecną lokacją i jednym z miejsc: " + tmp_location.distanceTo(current_location));

			if(tmp_location.distanceTo(current_location) < distance_between_last_locations){
				add = false;
				break;
			}

			conquers.moveToNext();
		}

		if(add) {
			db.addConquer(100, "01-02-2003", current_location.getLongitude(), current_location.getLatitude());

			drawCircle(new LatLng(current_location.getLatitude(), current_location.getLongitude()));
		}else{
			messageBox("Błąd w podboju!", "Już podbiłeś to miejsce, bądź za bardzo oddaliłeś się od miejsca w którym zacząłeś skanowanie");
		}
	}

	public void drawAllCircles(){
		Cursor conquers = db.getLastQonquers(5);
		conquers.moveToFirst();
		while (!conquers.isAfterLast()) {
			Location tmp_location = new Location("tmp");
			tmp_location.setLongitude(conquers.getDouble(3));
			tmp_location.setLatitude(conquers.getDouble(4));

			drawCircle(new LatLng(tmp_location.getLatitude(), tmp_location.getLongitude()));

			conquers.moveToNext();
		}
	}

	public void drawCircle(LatLng where){
		mMap.addCircle(new CircleOptions()
				.center(where)
				.radius(distance_between_last_locations)
				.strokeColor(Color.parseColor("#1E90FF"))
				.fillColor(Color.parseColor("#331EC2FF")));
	}

	public void messageBox(String title, String message){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message)
				.setTitle(title);
		//builder.setView(R.layout.dialog_no_fix);

		AlertDialog dialog = builder.create();
		dialog.show();
	}

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
	    Log.d(MapsActivity.TAG, "onStart");
    }

	@Override
	public void onStop() {
		Log.d(MapsActivity.TAG, "onStop");
		// If the client is connected

		super.onStop();
	}

	@Override
	public void onPause() {
		Log.d(MapsActivity.TAG, "onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.d(MapsActivity.TAG, "onResume");
		super.onResume();

		setUpMapIfNeeded();
		drawAllCircles();
	}

}
