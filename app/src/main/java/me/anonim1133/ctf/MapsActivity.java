package me.anonim1133.ctf;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class MapsActivity extends FragmentActivity{

	private WifiManager mainWifi;
	private Location last_location;

    private GoogleMap mMap;
	private DataBaseHelper db;
	private ProgressBar progress;

	//private GpsHelper mGps;

	private static String TAG = "MAP";

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
		//ToDo: Sprawdzić najpierw czy jest gpsFix na mapie.
		Log.d(MapsActivity.TAG, "onScan");

		setUpWifi();
		scanWifi();

		progress.setVisibility(View.VISIBLE);

		CountDownTimer cdt = new CountDownTimer(10000, 1000) {
			public void onTick(long millisUntilFinished) {
				progress.setProgress((int) ((10000-millisUntilFinished)/1000));
			}

			public void onFinish() {
				progress.setProgress(10);
				progress.setVisibility(View.GONE);
				onScanFinish(mainWifi.getScanResults());
			}
		}.start();
		last_location = mMap.getMyLocation();
	}

	public void onScanFinish(List<ScanResult> wifis){
		//TODO: Sprawdzić czas ostatniego skanowania, nie może być mniejszy niż 10s
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
		if(distance > 100f){
			add = false;
		}

		//Sprawdzic czy nie pokrywa się z ostatnimi dodanymi punktami

		if(add)
		db.addConquer(100, "01-02-2003", current_location.getLongitude(), current_location.getLatitude());
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
	}

}
