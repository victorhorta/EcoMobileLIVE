package com.ecomaplive.ecomobilelive.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ecomaplive.ecomobilelive.R;
import com.ecomaplive.ecomobilelive.btmanager.BTService;
import com.ecomaplive.ecomobilelive.btmanager.DeviceListActivity;

public class DeviceFragment extends Fragment {
    static final String TAG = "DeviceFragment";
    BluetoothAdapter mBluetoothAdapter;
    
    String deviceAddressSelected;
    String deviceTypeSelected;
    String deviceNameSelected;
    
    private static boolean sConnected = false;
    
    // Return Intent extra
    public static String EXTRA_DEVICE_TYPE_SELECTED = "device_type_selected";
    
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE= 2;
    private static final int REQUEST_ENABLE_BT = 3;
    
    Button openButton;
    Button cancelButton;
    TextView textviewDeviceName;
    RadioGroup radioGroup;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }
    
    @Override
    public void onStart() {
        super.onStart();

        openButton = (Button) getActivity().findViewById(R.id.frag_open);
        cancelButton = (Button) getActivity().findViewById(R.id.frag_button_cancel);
        textviewDeviceName = (TextView) getActivity().findViewById(R.id.frag_last_capture);
        radioGroup = (RadioGroup) getActivity().findViewById(R.id.frag_radiogroup);
        
        updateDeviceLabel();

        // Open Button
        openButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Launch the DeviceListActivity to see devices and do scan
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    textviewDeviceName.setText("No bluetooth adapter available");
                }

                if (!mBluetoothAdapter.isEnabled()) {
                    Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    //startActivityForResult(enableBluetooth, REQUEST_ENABLE_BT);
                    startActivityForResult(enableBluetooth, 0);
                    
                }

                Intent serverIntent = null;
                serverIntent = new Intent(v.getContext(),
                        com.ecomaplive.ecomobilelive.btmanager.DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                // findBT();
                // openBT();
            }
        });
        
        // Cancel Button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Launch the DeviceListActivity to see devices and do scan
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (mBluetoothAdapter == null) {
                    textviewDeviceName.setText("No bluetooth adapter available");
                    return;
                }

                if (sConnected) {
                    // use this to start and trigger a service
                    Intent i = new Intent(getActivity(), BTService.class);
                    // add data to the intent
                    getActivity().stopService(i);
                } else {
                    Log.d(TAG, "Can't cancel service when not connected!");
                }
            }
        });
    }
    
    private void updateDeviceLabel() {
        String label;
        if(sConnected) {
            label = deviceNameSelected;
        } else {
            label = "None selected";
        }
        textviewDeviceName.setText(label);
    }
    
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "REQUEST_CONNECT_DEVICE --- success!!!");
                deviceAddressSelected = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                deviceNameSelected = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_NAME);
                deviceTypeSelected = ((RadioButton) getActivity().findViewById(radioGroup.getCheckedRadioButtonId())).getText().toString();
                
                
                
                // use this to start and trigger a service
                Intent i = new Intent(getActivity(), BTService.class);
                // add data to the intent
                i.putExtra(DeviceListActivity.EXTRA_DEVICE_ADDRESS, deviceAddressSelected);
                i.putExtra(DeviceFragment.EXTRA_DEVICE_TYPE_SELECTED, deviceTypeSelected);
                getActivity().startService(i);
                
                sConnected = true;
                updateDeviceLabel();
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
//                setupChat();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
//                Toast.makeText(this, "Bluetooth not enable, leaving...", Toast.LENGTH_SHORT).show();
//                finish();
            }
        }
    }
}
