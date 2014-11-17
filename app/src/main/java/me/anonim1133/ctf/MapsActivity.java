package me.anonim1133.ctf;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;


public class MapsActivity extends FragmentActivity{

    private GoogleMap mMap;
	private GpsHelper mGps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	    Log.d("MAP", "onCreate");

	    mGps = new GpsHelper(this);

	    setUpMapIfNeeded();
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
