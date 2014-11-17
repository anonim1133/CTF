package me.anonim1133.ctf;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity{

	WifiManager mainWifi;
	WifiReceiver receiverWifi;

    private GoogleMap mMap;

	//private GpsHelper mGps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	    Log.d("MAP", "onCreate");

	    //mGps = new GpsHelper(this);

	    setUpMapIfNeeded();
    }

	private  void setUpWifi(){
		mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

		if(mainWifi.isWifiEnabled()==false){
			mainWifi.setWifiEnabled(true);
		}

		mainWifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		receiverWifi = new WifiReceiver();
		registerReceiver(receiverWifi, new IntentFilter(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

	}

	private void scanWifi(){
		mainWifi.startScan();
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
		Log.d("MAP", "onScan");

		setUpWifi();
		scanWifi();
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

	class WifiReceiver extends BroadcastReceiver {
		public void onReceive(Context c, Intent intent) {

			ArrayList<String> connections = new ArrayList<String>();
			ArrayList<Float> Signal_Strenth = new ArrayList<Float>();

			List<ScanResult> wifiList;
			wifiList = mainWifi.getScanResults();
			for (int i = 0; i < wifiList.size(); i++) {
				connections.add(wifiList.get(i).SSID);
				Log.d("MAP", "SSID: " + wifiList.get(i).SSID);
			}


		}
	}
}
