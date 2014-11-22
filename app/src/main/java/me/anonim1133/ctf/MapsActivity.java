package me.anonim1133.ctf;

import android.content.Context;
import android.content.pm.PackageManager;
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

public class MapsActivity extends FragmentActivity{

	private WifiManager mainWifi;
	private Location last_location;

    private GoogleMap mMap;
	private DataBaseHelper db;
	private ProgressBar progress;

	//private GpsHelper mGps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	    Log.d("MAP", "onCreate");

	    //mGps = new GpsHelper(this);
	    try {
		    db = new DataBaseHelper(this);
	    } catch (SQLException e) {
		    Log.d("MAP", "Błąd przy otwieraniu bazu: " + e.toString());
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
		Log.d("MAP", "onScan");

		setUpWifi();
		scanWifi();

		progress.setVisibility(View.VISIBLE);

		CountDownTimer cdt = new CountDownTimer(10000, 1000) {
			public void onTick(long millisUntilFinished) {
				progress.setProgress((int) ((10000-millisUntilFinished)/1000));
			}

			public void onFinish() {
				progress.setProgress(10);
				Log.d("MAP", mainWifi.getScanResults().toString());
				progress.setVisibility(View.GONE);
				onScanFinish();
			}
		}.start();
		last_location = mMap.getMyLocation();
	}

	public void onScanFinish(){
		Location current_location = mMap.getMyLocation();
		Float distance = 0.0f;

		distance = current_location.distanceTo(last_location);

		if(distance < 100f){
			db.addConquer(100, "01-02-2003", current_location.getLongitude(), current_location.getLatitude());
		}
	}

    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
	    Log.d("MAP", "onStart");
    }

	@Override
	public void onStop() {
		Log.d("MAP", "onStop");
		// If the client is connected

		super.onStop();
	}

	@Override
	public void onPause() {
		Log.d("MAP", "onPause");
		super.onPause();
	}

	@Override
	protected void onResume() {
		Log.d("MAP", "onResume");
		super.onResume();
		setUpMapIfNeeded();
	}

}
