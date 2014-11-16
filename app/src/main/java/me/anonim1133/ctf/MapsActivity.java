package me.anonim1133.ctf;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationListener;


import android.content.IntentSender;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;


public class MapsActivity extends FragmentActivity implements LocationListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
	private LocationClient mLocationClient;

	// A request to connect to Location Services
	private LocationRequest mLocationRequest;

	/*
	 * Note if updates have been turned on. Starts out as "false"; is set to "true" in the
	 * method handleRequestSuccess of LocationUpdateReceiver.
	 *
	 */
	boolean mUpdatesRequested = true;

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

	    Log.d("MAP", "onCreate");

	    mLocationRequest = LocationRequest.create();
	    mLocationRequest.setInterval(LocationUtils.UPDATE_INTERVAL_IN_MILLISECONDS);
	    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	    mLocationRequest.setFastestInterval(LocationUtils.FAST_INTERVAL_CEILING_IN_MILLISECONDS);

	    mLocationClient = new LocationClient(this, this, this);

	    setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call  once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
	    Log.d("MAP", "Start periodic updates");

        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    /*
     * Called when the Activity becomes visible.
     */
    @Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
	    Log.d("MAP", "onStart");
	    mLocationClient.connect();
    }

    /*
     * Called when the Activity is no longer visible.
     */

	@Override
	public void onStop() {
		Log.d("MAP", "onStop");
		// If the client is connected
		if (mLocationClient.isConnected()) {
			stopPeriodicUpdates();
		}

		// After disconnect() is called, the client is considered "dead".
		mLocationClient.disconnect();

		super.onStop();
	}

	@Override
	public void onPause() {
		Log.d("MAP", "onPause");
		super.onPause();
	}

	/*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle bundle) {
		Log.d("MAP", "onConnected");
	    if (mUpdatesRequested) {
		    startPeriodicUpdates();
	    }
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
	    Log.d("MAP", "onDisconnected");
        // Display the connection status
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
	    Log.d("MAP", "onConnectionFailed");
    /*
     * Google Play services can resolve some errors it detects.
     * If the error has a resolution, try sending an Intent to
     * start a Google Play services activity that can resolve
     * error.
     */
	    if (connectionResult.hasResolution()) {
	        try {
	            // Start an Activity that tries to resolve the error
	            connectionResult.startResolutionForResult(
	                    this,
	                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

	        } catch (IntentSender.SendIntentException e) {
	            // Log the error
	            e.printStackTrace();
	        }
	    } else {
	        /*
	         * If no resolution is available, display a dialog to the
	         * user with the error.
			 */
	        showErrorDialog(connectionResult.getErrorCode());
	    }
    }

	private void startPeriodicUpdates() {
		Log.d("MAP", "startPeriodicUpdates");
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	private void stopPeriodicUpdates() {
		Log.d("MAP", "stopPeriodicUpdates");
		mLocationClient.removeLocationUpdates(this);
	}

	private void showErrorDialog(int errorCode) {

		// Get the error dialog from Google Play services
		Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
				errorCode,
				this,
				LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST);

		// If Google Play services can provide an error dialog
		if (errorDialog != null) {

			// Create a new DialogFragment in which to show the error dialog
			ErrorDialogFragment errorFragment = new ErrorDialogFragment();

			// Set the dialog in the DialogFragment
			errorFragment.setDialog(errorDialog);

			// Show the error dialog in the DialogFragment
			errorFragment.show(getFragmentManager(), LocationUtils.APPTAG);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

		// Choose what to do based on the request code
		switch (requestCode) {

			// If the request code matches the code sent in onConnectionFailed
			case LocationUtils.CONNECTION_FAILURE_RESOLUTION_REQUEST :

				switch (resultCode) {
					// If Google Play services resolved the problem
					case Activity.RESULT_OK:

						// Log the result
						Log.d(LocationUtils.APPTAG, getString(R.string.resolved));

						// Display the result

						break;

					// If any other result was returned by Google Play services
					default:
						// Log the result
						Log.d(LocationUtils.APPTAG, getString(R.string.no_resolution));

						// Display the result

						break;
				}

				// If any other request code was received
			default:
				// Report that this Activity received an unknown requestCode
				Log.d(LocationUtils.APPTAG,
						getString(R.string.unknown_activity_request_code, requestCode));

				break;
		}
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d("MAP", "onLocationChanged");
		mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Marker"));
	}
}
