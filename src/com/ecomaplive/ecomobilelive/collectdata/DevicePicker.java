package com.ecomaplive.ecomobilelive.collectdata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ecomaplive.ecomobilelive.R;

public class DevicePicker extends ListActivity {
	private DeviceAdapter adapter;
	private List<CustomListRow> devices;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean shut, registered;
	private Set<BluetoothDevice> pairedDevices;
	private Set<String> listedDevices;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listrow_deviceselector);

		// initialization
		adapter = null;

		getListView().setFocusable(true);
		getListView().setFocusableInTouchMode(true);

		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		shut = false;

		listedDevices = new HashSet<String>(10);
		pairedDevices = mBluetoothAdapter.getBondedDevices();
		registered = false;
	}

	public void onListItemClick(ListView l, View v, int position, long id) {
		Intent data = new Intent();
		data.putExtra(Explorer.DEVICE_NAME, devices.get((int) id).get("name"));
		data.putExtra(Explorer.DEVICE_ADDRESS, devices.get((int) id).get("address"));
		setResult(RESULT_OK, data);
		shut = true;
		cancelDiscovery();
		finish();
	}
	
	void cancelDiscovery() {
		if (discovering) {
			discovering = false;
			mBluetoothAdapter.cancelDiscovery();

			// remove the receiver
			if(registered){
				unregisterReceiver(mReceiver);
				registered = false;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelDiscovery();
		if(registered){
			unregisterReceiver(mReceiver);
			registered = false;
		}
	}
	
	private boolean discovering;

	@Override
	protected void onStart() {
		super.onStart();
		fillData();
		discovering = true;
		mBluetoothAdapter.startDiscovery();
		Message msg = new Message();
		msg.what = CANCEL_DISCOVERY;
		msgHandler.sendMessageDelayed(msg, 15000);
	}

	private Handler msgHandler = new Handler() {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.Handler#handleMessage(android.os.Message)
		 */
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case CANCEL_DISCOVERY:
				cancelDiscovery();
				break;
			case UPDATE_LIST:
				adapter.notifyDataSetChanged();
				break;
			}
			super.handleMessage(msg);
		}
	};

	private static final int CANCEL_DISCOVERY = 1;
	private static final int UPDATE_LIST = 2;

	private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

				if (pairedDevices.contains(device) && !listedDevices.contains(device.getAddress())){
					HashMap<String, String> h = new HashMap<String, String>(5);
					String name = device.getName();
					if (name == null)
						name = "<NONE>";
					h.put("name", name + " (" + device.getAddress() + ")");
					h.put("address", device.getAddress());
					
					// add it to the listed devices set, so we don't add it multiple times
					listedDevices.add(device.getAddress());
					
					// Add the name and address to an array adapter to show in a
					// ListView
					if (!shut) {
						devices.add(new CustomListRow(h));

						// update the list
						Message msg = new Message();
						msg.what = UPDATE_LIST;
						msgHandler.sendMessageDelayed(msg, 100);
					}
				}
			}
		}
	};

	void fillData() {
		// create the adapter
		this.devices = new ArrayList<CustomListRow>(5);
		adapter = new DeviceAdapter(this, R.layout.listrow_devicerow, this.devices);
		setListAdapter(adapter);
		getListView().setTextFilterEnabled(true);
		getListView().requestFocus();

		// Register the BroadcastReceiver
		IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
		registerReceiver(mReceiver, filter); // Don't forget to unregister
												// during onDestroy
		registered = true;
	}

	/*
	 * public void loadDevices(List<CustomListRow> results) { this.devices =
	 * results; if(this.devices == null){ TextView tt =
	 * (TextView)findViewById(android.R.id.empty); if (tt != null)
	 * tt.setText("No bluetooth devices found."); }else{ adapter = new
	 * DeviceAdapter(this, R.layout.drow, this.devices);
	 * setListAdapter(adapter); getListView().setTextFilterEnabled(true);
	 * getListView().requestFocus(); } }
	 */

	private class CustomListRow {
		private HashMap<String, String> source;
		private String s;

		public CustomListRow(HashMap<String, String> source) {
			this.source = source;
			// s = this.source.get("name").toLowerCase();
			s = this.source.get("name");
		}

		@Override
		public String toString() {
			return s;
		}

		public String get(String key) {
			return source.get(key);
		}
	}

	private class DeviceAdapter extends ArrayAdapter<CustomListRow> {
		public DeviceAdapter(Context context, int textViewResourceId,
				List<CustomListRow> items) {
			super(context, textViewResourceId, items);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.listrow_devicerow, null);
			}
			CustomListRow r = getItem(position);
			TextView tt = (TextView) v.findViewById(R.id.name);
			if (tt != null)
				tt.setText(r.get("name"));
			return v;
		}
	}
}
