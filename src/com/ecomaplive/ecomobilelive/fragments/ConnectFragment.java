package com.ecomaplive.ecomobilelive.fragments;

import java.util.ArrayList;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ecomaplive.ecomobilelive.R;
import com.ecomaplive.ecomobilelive.btconnect.DeviceListActivity;
import com.ecomaplive.ecomobilelive.btconnect.MyService;

public class ConnectFragment extends Fragment implements OnClickListener{
    private static final boolean DEBUG = true;
    private static final String TAG = "Main Activity";
    private static final int ENABLE_BLUETOOTH = 1;
   
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    
    BluetoothAdapter mBluetoothAdapter;
    
    Button button1;
    Button button2;
    Button button3;
    TextView deviceLabel;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_connect, container, false);
    }

    
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        private void initControls(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        
        explicitStart();
        bindToService();
        
        button1 = (Button)getView().findViewById(R.id.frag_connect_select);
        button2 = (Button)getView().findViewById(R.id.frag_updatesession);
        button3 = (Button)getView().findViewById(R.id.frag_open);
        deviceLabel = (TextView)getView().findViewById(R.id.frag_last_capture);
        
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
    
    @Override
    public void onStart() {
        super.onStart();
        if(DEBUG) Log.e(TAG, "++ ON START ++");
        
        initControls();

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        // Otherwise, setup the chat session
        } else {
            if (serviceRef == null) setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if(DEBUG) Log.e(TAG, "+ ON RESUME +");

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (serviceRef != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (serviceRef.getState() == MyService.STATE_NONE) {
              // Start the Bluetooth chat services
                serviceRef.start();
            }
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

//        // Initialize the array adapter for the conversation thread
//        mConversationArrayAdapter = new ArrayAdapter<String>(this, R.layout.message);
//        mConversationView = (ListView) findViewById(R.id.in);
//        mConversationView.setAdapter(mConversationArrayAdapter);
//
//        // Initialize the compose field with a listener for the return key
//        mOutEditText = (EditText) findViewById(R.id.edit_text_out);
//        mOutEditText.setOnEditorActionListener(mWriteListener);
//
//        // Initialize the send button with a listener that for click events
//        mSendButton = (Button) findViewById(R.id.button_send);
//        mSendButton.setOnClickListener(new OnClickListener() {
//            public void onClick(View v) {
//                // Send a message using content of the edit text widget
//                TextView view = (TextView) findViewById(R.id.edit_text_out);
//                String message = view.getText().toString();
//                sendMessage(message);
//            }
//        });

        // Initialize the BluetoothChatService to perform bluetooth connections
//        mChatService = new BluetoothChatService(this, mHandler);
        bindToService();
        

        // Initialize the buffer for outgoing messages
//        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public synchronized void onPause() {
        super.onPause();
        if(DEBUG) Log.e(TAG, "- ON PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if(DEBUG) Log.e(TAG, "-- ON STOP --");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (serviceRef != null) serviceRef.stop();
        if(DEBUG) Log.e(TAG, "--- ON DESTROY ---");
        getActivity().unbindService(mConnection);
    }
    
    
    
    
    /**
     * Starting the service.<br>
     * Prevents the service from shutting down after activities are not bounded
     * anymore.
     */
    private void explicitStart() {
        // Explicitly start My Service
        Intent intent = new Intent(getActivity(), MyService.class);
        // TODO Add extras if required.
        getActivity().startService(intent);
    }
    
    /**
     * Creating a Service Connection for Service binding.<br>
     * 
     * To bind a Service to another application component, you need to implement
     * a new ServiceConnection, overriding the onServiceConnected and
     * onServiceDisconnected methods to get a reference to the Service instance
     * after a connection has been established.
     */
    // Reference to the service
    private MyService serviceRef;
    // Handles the connection between the service and activity
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // Called when the connection is made.
            serviceRef = ((MyService.MyBinder) service).getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            // Received when the service unexpectedly disconnects.
            serviceRef = null;
        }
    };
    
    
    
    
    /**
     * Binding to the service.<br>
     * When the Service has been bound, all its public methods and properties
     * are available through the "serviceBinder" object obtained from the
     * "onServiceConnected" handler.
     */
    private void bindToService() {
        // Bind to the service
        Intent bindIntent = new Intent(getActivity(), MyService.class);
        getActivity().bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
    }
    
    /** Bluetooth-related methods --------------------------*/
    
//    private void initBluetooth() {
//        if (!mBluetoothAdapter.isEnabled()) {
//            // Bluetooth isn’t enabled, prompt the user to turn it on.
//            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//            startActivityForResult(intent, ENABLE_BLUETOOTH);
//        } else {
//            // Bluetooth is enabled, initialize the UI.
//             initBluetoothUI();
//        }
//    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == ENABLE_BLUETOOTH)
//            if (resultCode == RESULT_OK) {
//                // Bluetooth has been enabled, initialize the UI.
//                 initBluetoothUI();
//            }
//    }
//        
//    void initBluetoothUI() {
//        Toast.makeText(getApplicationContext(), "initBluetoothUI", Toast.LENGTH_SHORT).show();
//    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(DEBUG) Log.d(TAG, "onActivityResult " + resultCode);
        
        
        switch (requestCode) {
        case REQUEST_CONNECT_DEVICE_SECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                String name = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_NAME);
                deviceLabel.setText(name + "\n" + address);
                serviceRef.setAddressToConnect(address);
                serviceRef.setBluetoothDevice(mBluetoothAdapter.getRemoteDevice(address));
                serviceRef.connect(true);
            }
            break;
        case REQUEST_CONNECT_DEVICE_INSECURE:
            // When DeviceListActivity returns with a device to connect
            if (resultCode == Activity.RESULT_OK) {
                String address = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                String name = data.getExtras().getString(DeviceListActivity.EXTRA_DEVICE_NAME);
                deviceLabel.setText(name + "\n" + address);
                serviceRef.setAddressToConnect(address);
                serviceRef.setBluetoothDevice(mBluetoothAdapter.getRemoteDevice(address));
                serviceRef.connect(false);
            }
            break;
        case REQUEST_ENABLE_BT:
            // When the request to enable Bluetooth returns
            if (resultCode == Activity.RESULT_OK) {
                // Bluetooth is now enabled, so set up a chat session
                setupChat();
            } else {
                // User did not enable Bluetooth or an error occurred
                Log.d(TAG, "BT not enabled");
                Toast.makeText(getActivity(), "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
        }
    }
    
    
    private ArrayList<BluetoothDevice> deviceList = new ArrayList<BluetoothDevice>();

    private void startDiscovery() {
        getActivity().registerReceiver(discoveryResult, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        if (mBluetoothAdapter.isEnabled() && !mBluetoothAdapter.isDiscovering())
            deviceList.clear();
        mBluetoothAdapter.startDiscovery();
    }

    BroadcastReceiver discoveryResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String remoteDeviceName = intent.getStringExtra(BluetoothDevice.EXTRA_NAME);
            BluetoothDevice remoteDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            deviceList.add(remoteDevice);
            Log.d(TAG, "Discovered " + remoteDeviceName);
        }
    };

    @Override
    public void onClick(View v) {
        Intent serverIntent = null;
        switch (v.getId()) {
        case R.id.frag_connect_select:
            serviceRef.testServiceMethod("ae");
            break;
        case R.id.frag_updatesession:
            serverIntent = new Intent(getActivity(), DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
            break;
        case R.id.frag_open:
            serverIntent = new Intent(getActivity(), DeviceListActivity.class);
            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_INSECURE);
            break;
        }
    }
}



