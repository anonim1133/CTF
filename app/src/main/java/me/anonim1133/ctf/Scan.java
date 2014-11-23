package me.anonim1133.ctf;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.SimpleAdapter;

import java.util.List;

public class Scan extends Fragment {

	SimpleAdapter adapter;
	WifiManager wifi;
	private Context c;
	String wifis[];

	@Override
	public void onStart() {
		super.onStart();
		Log.d("Scan", "onStart");

	}

	public void setContext(Context context) {
		this.c = context;
	}

	public void startScan(){
		wifi=(WifiManager) c.getSystemService(Context.WIFI_SERVICE);

		if (wifi.isWifiEnabled() == false){
			wifi.setWifiEnabled(true);
		}


	}


	public void onReceive(Context context, Intent intent) {
		List<ScanResult> wifiScanList = wifi.getScanResults();
		wifis = new String[wifiScanList.size()];
		for (int i = 0; i < wifiScanList.size(); i++) {
			wifis[i] = ((wifiScanList.get(i)).toString());
		}
	}
}
