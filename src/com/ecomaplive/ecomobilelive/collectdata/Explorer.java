package com.ecomaplive.ecomobilelive.collectdata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ecomaplive.ecomobilelive.R;

public class Explorer extends Activity implements OnClickListener, AdapterView.OnItemSelectedListener
{
    private static final boolean DEBUG = false;
    private boolean savesensors;

    public static final String STORAGE_DIR = "EcoMapLIVE";
    
    static final String TAG = "Explorer";

    /*
     * The EDA multiplication factor.
     */
    private static final float FACTOR_EDA = 1.05f;

    private static final int SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int DISPLAY_GRAPH = 3;

    private static final String COMMAND_ACK = "ACKNOWLEDGED";
    private static final String SENSOR_AVAILABLE = "AVAILABLE";
    private static final String DATA = "D";
    private static final String DATA_V1 = "D1";
    private static final String DATA_V2 = "D2";
    private static final String DATA_V3 = "D3";
    private static final String DATA_V5 = "D5";
    private static final String DATA_V7 = "D7";
    private static final String DATA_V8 = "D8";
    private static final String CLOSED = "C";
    private static final String N = "TERMINAL";

    private int sensorType = 0;
    
    private int DATA_TO_PLOT = 99;
    public static final int PLOT_EDA_BIAS = 0;
    public static final int PLOT_EDA_P = 1;
    public static final int PLOT_EDA = 2;
    public static final int PLOT_TEMPERATURE = 3;
    public static final int PLOT_MOTION_X= 4;
    public static final int PLOT_MOTION_Y = 5;
    public static final int PLOT_MOTION_Z = 6;
    public static final int PLOT_BATTERY = 7;
    public static final int PLOT_ACCELERATION = 8;
    public static final int PLOT_HEARTRATE = 9;
    public static final int PLOT_HEARTRATE_AVERAGE = 10;
    public static final int PLOT_HRV = 11;
    public static final int PLOT_HR_RAW = 12;
    public static final int PLOT_AMBIENT_TEMP = 13;
    public static final int PLOT_AMBIENT_HUMIDITY = 14;
    public static final int PLOT_AMBIENT_SOUND = 15;
    public static final int PLOT_TOTAL_VISIBLE_LIGHT = 16;
    public static final int PLOT_TOTAL_INFRARED_LIGHT = 17;
    public static final int PLOT_RED_LIGHT_LEVEL = 18;
    public static final int PLOT_BLUE_LIGHT_LEVEL = 19;
    public static final int PLOT_GREEN_LIGHT_LEVEL = 20;

    private boolean dataBeingPlottedChanged = false;
    private boolean ISRECORDING = false;
    private boolean viewsUpdated;

    private static final int MENU_VIEW_GRAPH = Menu.FIRST;
    private static final int MENU_SET_TIME = Menu.FIRST + 1;
    private static final int MENU_GET_TIME = Menu.FIRST + 2;
    private static final int MENU_GET_BATTERY = Menu.FIRST + 3;
    private static final int MENU_SET_ID = Menu.FIRST + 4;
    private static final int MENU_SET_MODE = Menu.FIRST + 5;
    private static final int MENU_CONFIG_ID = Menu.FIRST + 6;
    private static final int MENU_SESSION_ID = Menu.FIRST + 7;

    public static final String DEVICE_NAME = "name";
    public static final String DEVICE_ADDRESS = "address";

    private ScrollView scroller;
    private ShapeDrawable drawable;

    private GraphView graphView;
    private View mainView, currentView;

    private Button connect, select, enable;
    private ToggleButton togglebutton;
    private Spinner selected;
    private boolean sensorAvailable;
    
    private TextView label1, label2, label9, label3, label4;
    private TextView label5, label6, label7, label8, label10;
    private ArrayList<TextView> textViews = new ArrayList<TextView>();


    private ArrayList<bluetoothCommSession> commSessions;

    private BluetoothAdapter mBluetoothAdapter;

    FileOutputStream fos;
    FileOutputStream fosList;

    private BufferedWriter bufferredWriter = null;
    private boolean FILE_IS_OPEN = false;
    private int fileNumberCounter = 1;

    private SimpleDateFormat format, format2, formatFilename;
    
    private boolean MARKER_ON = false;

    private ArrayAdapter<bluetoothCommSession> adapter;

    private JSONArray devices;

    private String configId, sessionId;

    private DecimalFormat df, df2;

    private String[] modes;

    private SubMenu infomenu, settingsmenu;

    private boolean idset;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mainView = findViewById(R.id.layoutroot);
        currentView = mainView;
        scroller = (ScrollView)findViewById(R.id.scroller);

        connect = (Button)findViewById(R.id.connect);
        connect.setOnClickListener(this);

        select = (Button)findViewById(R.id.selectdevice);
        select.setOnClickListener(this);

        enable = (Button)findViewById(R.id.enablebluetooth);
        enable.setOnClickListener(this);

        togglebutton = (ToggleButton) findViewById(R.id.togglebutton);
        togglebutton.setOnClickListener(this);

        label1 = (TextView)findViewById(R.id.eda);
        label1.setOnClickListener(this);

        label2 = (TextView)findViewById(R.id.temp);
        label2.setOnClickListener(this);

        label3 = (TextView)findViewById(R.id.acceleration);
        label3.setOnClickListener(this);

        label4 = (TextView)findViewById(R.id.edaBias);
        label4.setOnClickListener(this);
        label4.setVisibility(View.GONE);

        label5 = (TextView)findViewById(R.id.edaP);
        label5.setOnClickListener(this);
        label5.setVisibility(View.GONE);

        label6 = (TextView)findViewById(R.id.heartRate);
        label6.setOnClickListener(this);

        label7 = (TextView)findViewById(R.id.heartRateAverage);
        label7.setOnClickListener(this);

        label8 = (TextView)findViewById(R.id.heartRateVariability);
        label8.setOnClickListener(this);

        label9 = (TextView)findViewById(R.id.battery);
        label9.setOnClickListener(this);
        
        label10 = (TextView)findViewById(R.id.time);

        textViews.add(label1);
        textViews.add(label2);
        textViews.add(label3);
        textViews.add(label9);
        textViews.add(label4);
        textViews.add(label5);
        textViews.add(label6);
        textViews.add(label7);
        textViews.add(label8);
        textViews.add(label10);

        df = new DecimalFormat("#"); 
        df2 = new DecimalFormat("#.###");
        
        //used to change color of clicked text field for user feedback
        drawable = new ShapeDrawable();
        float[] outerR = new float[] { 18, 18, 18, 18, 18, 18, 18, 18 };
        drawable = new ShapeDrawable(new RoundRectShape(outerR, null, null));
        drawable.setBounds(0, 50, 50, 0);
        drawable.getPaint().setColor(0xff448844);

        selected = (Spinner)findViewById(R.id.selected);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        commSessions = new ArrayList<bluetoothCommSession>(3);
        adapter = new ArrayAdapter<bluetoothCommSession>(this, android.R.layout.simple_spinner_item, commSessions){
            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent)
            {
                bluetoothCommSession s = commSessions.get(position);
                View v = super.getDropDownView(position, convertView, parent);
                if(s.isConnected()){
                    TextView v1 = (TextView)v.findViewById(android.R.id.text1); 
                    v1.setText(Html.fromHtml("<big><b>"+s.toString()+"</b></strong>"));
                    
                }
                return v;
            }
        };
        
        adapter.setDropDownViewResource(R.layout.dropdown_item);
        selected.setAdapter(adapter);
        selected.setOnItemSelectedListener(this);
        graphView = null;
        format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz");
        format2 = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        formatFilename = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss.");
        selected.setEnabled(false);
        selected.setEmptyView(findViewById(R.id.empty));
        sensorAvailable = false;
        clearDisplay();
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        savesensors = prefs.getBoolean(getString(R.string.pref_savesensors), true);
        devices = new JSONArray();
        if(savesensors){
            // load 'em from the pref
            if(prefs.contains(getString(R.string.pref_sensors))){
                String s = prefs.getString(getString(R.string.pref_sensors), null);
                
                // its json: parse it
                devices = (JSONArray)JSONValue.parse(s);
                JSONObject obj;
                for(int i=0;i<devices.size();i++){
                    obj = (JSONObject)devices.get(i);
                    commSessions.add(new bluetoothCommSession(this, mBluetoothAdapter, obj.get("name").toString(), obj.get("address").toString(), msgHandler));                 
                }
                adapter.notifyDataSetChanged();
                selected.setSelection(0);
                selected.setEnabled(true);
                connect.setEnabled(true);
            }
        }else{
            if(prefs.contains(getString(R.string.pref_sensors))){
                SharedPreferences.Editor e = prefs.edit();
                e.remove(getString(R.string.pref_sensors));
                e.commit();
            }
        }
        configId = "000";
        sessionId = "1";
        
        modes = new String[]{
                "v0: EDA, T, Accel", 
                "v1: IBI (Group 8)",
                "v2: IBI, IBI average",
                "v3: ECG Waveform",
                "v4: HR-PPG, EDA, T, Accel",
                "v5: HR-ECG, EDA, T, Accel",
                "v6: PPG Waveform",
                "v7: EDA, Ta, Ts, Hum, Accel",
                "v8: RGB Color, T, Hum, Accel"
        };
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            Toast.makeText(this, "Device does not support Bluetooth", Toast.LENGTH_LONG);
        }else{
            if (!mBluetoothAdapter.isEnabled()) {
                enableBluetooth();
            }else{
                enable.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onConfigurationChanged(Configuration _newConfig){
        super.onConfigurationChanged(_newConfig);
        int height = getWindowManager().getDefaultDisplay().getHeight();
        int width = getWindowManager().getDefaultDisplay().getWidth();
        if(width > height){
            showGraphView();
            //graphView.clear();
        }else{
            showDataView();
        }
        if(graphView != null) graphView.setGraphWidth(width);
    }


    private Handler msgHandler = new Handler() {
        /* (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case BLUETOOTH_ERROR:
                Toast.makeText(Explorer.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                break;
            case BLUETOOTH_MESSAGE:
                Object[] bits = (Object[])msg.obj;
                updateProgress((bluetoothCommSession)bits[0],
                        (Object[])bits[1]);
                break;
            case SCROLL_DOWN:
                //scroller.fullScroll(ScrollView.FOCUS_DOWN);
                break;
            }
            super.handleMessage(msg);
        }
    };

    private static final int BLUETOOTH_MESSAGE = 1;
    private static final int SCROLL_DOWN = 2;
    private static final int BLUETOOTH_ERROR = 3;

    void enableBluetooth()
    {
        connect.setVisibility(View.GONE);
        select.setVisibility(View.GONE);
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);  
    }

    void connectOrDisconnect()
    {
        if(!mBluetoothAdapter.isEnabled()){
            Toast.makeText(this, "Bluetooth is currently not enabled. Please try to enable Bluetooth later.", Toast.LENGTH_LONG);
            return;
        }

        bluetoothCommSession session = (bluetoothCommSession)selected.getSelectedItem();
        if(session.isConnected() || session.isConnecting()){
            session.stop();
            connect.setText("Connect");
            togglebutton.setEnabled(false);
        }else{
            session.go();
            viewsUpdated = false;
            connect.setText("Disconnect");
        }
    }

    void selectDevice()
    {
        Intent it = new Intent(Intent.ACTION_PICK);
        it.setClass(getBaseContext(), DevicePicker.class);
        startActivityForResult(it, SELECT_DEVICE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        // process it if necessary
        if(resultCode == RESULT_OK){
            switch(requestCode){
            case SELECT_DEVICE:
            {
                int i, j = commSessions.size();
                String address = data.getStringExtra(DEVICE_ADDRESS);
                for(i=0;i<j;i++){
                    if(commSessions.get(i).getAddress().equals(address))
                        return;
                }

                // fine, its brand new: add it
                String name= data.getStringExtra(DEVICE_NAME);
                JSONObject obj = new JSONObject();
                obj.put("name", name);
                obj.put("address", address);
                devices.add(obj);
                
                bluetoothCommSession sess = new bluetoothCommSession(this, mBluetoothAdapter, name, address, msgHandler); 
                adapter.add(sess);
                adapter.notifyDataSetChanged();
                selected.setEnabled(true);
                selected.setSelection(adapter.getPosition(sess));
                
                // save it
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                SharedPreferences.Editor e = prefs.edit();
                e.putString(getString(R.string.pref_sensors), devices.toJSONString());
                e.commit();
                
                // enable the connect button
                connect.setEnabled(true);
            }
            break;
            case REQUEST_ENABLE_BT:
                enable.setVisibility(View.GONE);
                select.setVisibility(View.VISIBLE);
                break;
            }
        }else if(resultCode == RESULT_CANCELED){
            switch(requestCode){
            case REQUEST_ENABLE_BT:
                Toast.makeText(this, "Bluetooth was not successfully enabled. Please try to enable Bluetooth later.", Toast.LENGTH_LONG);
                break;
            }
        }
    }

    /*
     * This is magic.
     */
    private static final UUID SPP_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private class command 
    {
        String command;
        boolean waitForResponse;
    }
    
    /*
     * AsyncTask can't be used for a class for which there will be multiple instances.
     */
    private class bluetoothCommSession implements Runnable
    {
        private Explorer parent;
        private BluetoothAdapter mBluetoothAdapter;
        private BluetoothSocket mmSocket;
        private BluetoothServerSocket mmServerSocket;
        private BluetoothDevice mmDevice;
        private String name, address;
        private boolean connected, connecting, stopped, expectingOK;
        private Handler handler;
        private Thread thread;
        private Vector<command> commands;
        private String lastResponse;
        private command lastCommand;
        private long lastCommandTime;
        private int version;
        
        /* 
         * The sensor band id.
         */
        public String id;
        private int linecount;
        private boolean idset;
        
        bluetoothCommSession(Explorer parent, BluetoothAdapter mBluetoothAdapter, String name, String address, Handler handler)
        {
            this.handler = handler;
            this.parent = parent;
            this.mBluetoothAdapter = mBluetoothAdapter;
            mmSocket = null;
            mmServerSocket = null;
            mmDevice = null;
            this.name = name;
            this.address = address;
            this.connected = false;
            this.connecting = false;
            this.thread = null;
            this.stopped = false;
            this.commands = new Vector<command>(5);
            this.expectingOK = false;
            this.lastResponse = null;
            this.lastCommand = null;
            this.lastCommandTime = -1;
            this.id = "";
            this.linecount = 4;
            this.idset = false;
            Explorer.this.setIdAvailable(this.idset);
            this.version = 0;
        }

        public boolean isConnecting(){ return connecting; }
        public boolean isConnected(){ return connected; }
        public String getAddress(){ return address; }

        public void run()
        {
            byte[] command;
            InputStream is = null;
            OutputStream os = null;
            byte[] b = new byte[1024];
            int cnt = 0, i;
            long sleep, now;
            try{
                if(mBluetoothAdapter.isDiscovering()){
                    mBluetoothAdapter.cancelDiscovery();
                }

                /*
                 * Initiate a connection
                 */
                // fetch the device and the accompanying socket
                mmDevice = mBluetoothAdapter.getRemoteDevice(address);
                mmSocket = OpenBluetoothPort.newInstance().open(mmDevice, SPP_UUID);
                //mmSocket = mmDevice.createRfcommSocketToServiceRecord(SPP_UUID);

                connecting = true;
                /* 
                 * Wait for a connection.
                 */
                /*
                mmServerSocket = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(N, MY_UUID);
                while(!isCancelled()){
                    try {
                        mmSocket = mmServerSocket.accept();
                        break;
                    } catch (Exception e){
                        break;
                    }
                }
                 */
                if(mmSocket != null){
                    if(mmServerSocket != null){
                        mmServerSocket.close();
                        mmServerSocket = null;
                    }

                    // open the bluetooth connection
                    mmSocket.connect();

                    connected = true;
                    is = mmSocket.getInputStream();
                    os = mmSocket.getOutputStream();
                    publishProgress(SENSOR_AVAILABLE);
                    try{ Thread.sleep(250); }catch(Exception e3){}
                    //addCommand("\r");
                    getID(this);
                    getBattery(this);
                    while(!stopped){
                        now = System.currentTimeMillis();
                        if(is.available() > 0){
                            // read the data
                            if(cnt == 1024) cnt = 0;
                            cnt += is.read(b, cnt, 1024-cnt);
                            android.util.Log.v(TAG,"---------->Read ["+cnt+"] bytes");
                            cnt = process(b, cnt);
                            sleep = 50;
                        }else{
                            sleep = 250;
                        }
                        if(commands.size() > 0){
                            if(this.lastCommand == null || (now - this.lastCommandTime) > 10000){
                                this.lastCommand = commands.remove(0);
                                this.lastCommandTime = now;
                                this.expectingOK = this.lastCommand.waitForResponse;
                                os.write(lastCommand.command.getBytes());
                                os.flush();
                            }
                        }
                        try{ Thread.sleep(sleep); }catch(Exception e3){}
                        if(!idset){
                            if(linecount > 0){
                                if(--linecount == 0 && "".equals(this.id)){
                                    getID(this);
                                    this.linecount = 4;
                                }
                            }
                        }
                    }
                }
            }catch(Exception e1){
                android.util.Log.v(TAG, "Session request failed: "+e1.getMessage(), e1);
            }finally{
                // close it
                if(is != null) try{ is.close(); }catch(Exception e2){}
                if(os != null) try{ os.close(); }catch(Exception e2){}
                if(mmServerSocket != null)try{  mmServerSocket.close();  }catch(Exception e2){}
                if(mmSocket != null) try{ mmSocket.close(); }catch(Exception e2){}
            }
            connected = false;
            publishProgress(CLOSED);
        }

        protected void onProgressUpdate(Object... progress)
        {
            parent.updateProgress(this, progress);
        }

        void stop()
        {
            stopped = true;
            
            // kill the connection
            if(connecting && !connected){
                if(mmServerSocket != null)try{  mmServerSocket.close();  }catch(Exception e2){}
                if(mmSocket != null) try{ mmSocket.close(); }catch(Exception e2){}
            }
            connecting = false;
        }

        void go()
        {
            this.stopped = false;
            this.thread = new Thread(this);
            idset = false;
            Explorer.this.setIdAvailable(this.idset);
            linecount = 4;
            this.thread.start();
        }

        int intVal(byte topbyte, byte thirdbyte, byte hibyte, byte lobyte)
        {
            int x = intVal(topbyte, thirdbyte);
            x = x << 8;
            return x | intVal(hibyte, lobyte);
        }

        int intVal(byte hi, byte lo)
        {
            /*
            int x, y;
            char h = (char)hi, l = (char)lo;
            if(!Character.isDigit(h)){
                h = Character.toUpperCase(h);
                x = h - 'A' + 10;  
            }else{
                x = h - '0';
            }
            x = x << 4;
            if(!Character.isDigit(l)){
                l = Character.toUpperCase(l);
                y = l - 'A' + 10;
            }else{
                y = l - '0';
            }
            return (x | y);
             */
            StringBuilder sb = new StringBuilder(2);
            sb.append((char)hi).append((char)lo);
            return Integer.parseInt(sb.toString(), 16);

        }

        String hexpad(int data){ return hexpad(data,2);}
        
        String hexpad(int data, int padding)
        {
            StringBuilder sb = new StringBuilder(Integer.toHexString(data).toUpperCase());
            while(sb.length() < padding){ sb.insert(0, '0'); }
            return sb.toString();
        }

        int process(byte[] b, int span)
        {
            int len = 0, lastbreak = 0, offset;
            int tempInt, edaBiasInt, edaInt, motionXInt, motionYInt, motionZInt, batteryInt, heartRateInt;
            int po2Int, csum;
            float ambientTemp, aTempRaw, ambientHum, ambientSound;
            int totalVisibleLight, infraredLight, redLightLevel, blueLightLevel, greenLightLevel;
            float volts, tempCelsius, motionX, motionY, motionZ;
            float totalEda, heartRate;
            long timestamp;
            StringBuilder db = new StringBuilder(100);
            StringBuilder sb = new StringBuilder(100);
            String line;
            int i, j;
            for(i=0;i<span;i++){
                // look for the line break
                if((char)b[i] == '\r' || (char)b[i] == '\n'){
                    if(DEBUG){
                        if(db.length() > 0){
                            android.util.Log.v(TAG, db.toString());
                            db.delete(0, db.length());
                        }
                    }
                    ++linecount;
                    if(len >= 62){
                        this.version = 0;
                        if(sb.length() > 0) sb.delete(0, sb.length());
                        for(j=0;j<len;j++){
                            sb.append((char)b[lastbreak+j]);
                        }
                        
                        /*
                         * 
                        tempInt = intVal(b[lastbreak+42], b[lastbreak+43]);
                        volts = 2.8 * tempInt / 1023.0;
                        tempCelsius = (volts - 0.424) / .00625;
                        roundedTempCelsius = Double.valueOf(twoDForm.format(tempCelsius));

                        edaBiasInt = intVal(b[lastbreak+50], b[lastbreak+51]);
                        edaInt = intVal(b[lastbreak+46], b[lastbreak+47]);
                        totalEda = 10 * Math.abs((double) (edaBiasInt - edaInt)) / (4096 - edaBiasInt);
                        roundedTotalEda = Double.valueOf(twoDForm.format(totalEda));

                        motionXInt = intVal(b[lastbreak+30], b[lastbreak+31], b[lastbreak+32], b[lastbreak+33]);
                        motionYInt = intVal(b[lastbreak+34], b[lastbreak+35], b[lastbreak+36], b[lastbreak+37]);
                        motionZInt = intVal(b[lastbreak+38], b[lastbreak+39], b[lastbreak+40], b[lastbreak+41]);                
                        motionWInt = intVal(b[lastbreak+42], b[lastbreak+43], b[lastbreak+44], b[lastbreak+45]);
                        batteryInt = intVal(b[lastbreak+22], b[lastbreak+23]);
                         */

                        // find the start
                        line = sb.toString().toUpperCase();
                        offset = line.indexOf("AA19040007");
                        //android.util.Log.v("Packet", "["+offset+"]["+line+"]");
                        if(offset != -1){
                            // RF comment - this section of code seems to be assuming a type 0 sensor?
                            tempInt = Integer.parseInt(line.substring(offset+42,offset+46), 16);
                            volts =(float) (2.8 * tempInt / 1023.0);
                            tempCelsius = (float) ((volts - 0.424) / .0625);
                            //roundedTempCelsius = Double.valueOf(twoDForm.format(tempCelsius));

                            edaInt = Integer.parseInt(line.substring(offset+46,offset+50), 16);
                            edaBiasInt = Integer.parseInt(line.substring(offset+50,offset+54), 16);
                            heartRateInt = Integer.parseInt(line.substring(offset+54, offset+58), 16);
                            heartRate =(float) (heartRateInt / 10000.0);    //convert to milliseconds
                            heartRate = (1 / heartRate) * 60; //convert to beats per minute

                            //totalEda = 10 * Math.abs((double) (edaBiasInt - edaInt)) / (4096 - edaBiasInt);
                            // 2010.12.12: OAO
                            //totalEda = 10 * Math.abs((float) (edaBiasInt - edaInt)) / (4096 - edaBiasInt);
                            totalEda = FACTOR_EDA * Math.abs((float) (edaBiasInt - edaInt)) / (4096 - edaBiasInt);
                            //roundedTotalEda = Double.valueOf(twoDForm.format(totalEda));

                            motionXInt = Integer.parseInt(line.substring(offset+30,offset+34), 16);             
                            motionYInt = Integer.parseInt(line.substring(offset+34,offset+38), 16);
                            motionZInt = Integer.parseInt(line.substring(offset+38,offset+42), 16);
                            batteryInt = Integer.parseInt(line.substring(offset+22,offset+24), 16);

                            /*
                            float motionX = Float.parseFloat(String.valueOf(motionXInt));
                            float motionY = Float.parseFloat(String.valueOf(motionYInt));
                            float motionZ = Float.parseFloat(String.valueOf(motionZInt));
                             */
                            motionX = (Float.parseFloat(String.valueOf(motionXInt)) - 32768)/250;
                            motionY = (Float.parseFloat(String.valueOf(motionYInt)) - 32768)/250;
                            motionZ = (Float.parseFloat(String.valueOf(motionZInt)) - 32768)/250;
                            
                            //android.util.Log.v("Motion","["+hexpad(motionXInt)+"]["+hexpad(motionYInt)+"]["+hexpad(motionZInt)+"]");                          
                            //publishProgress(DATA, edaBiasInt, edaInt, roundedTotalEda, motionXInt, motionYInt, motionZInt, tempInt, batteryInt);
                            publishProgress(DATA, edaBiasInt, edaInt, totalEda, motionX, motionY, motionZ, tempCelsius, batteryInt, heartRate, line);
                        }
                    }else if(len == 57){
                        if(sb.length() > 0) sb.delete(0, sb.length());
                        for(j=0;j<len;j++){
                            sb.append((char)b[lastbreak+j]);
                        }

                        // new data format: needs to start with 0000
                        line = sb.toString().toUpperCase().trim();
                        if(line.startsWith("*0000")){
                            // RF comment - I thought that if it begins with 0000 it is a type 0
                            // why are we defining this as type 1 ?
                            this.version = 1;
                            line = line.substring(1);
                            timestamp = Long.parseLong(line.substring(6, 22), 16);
                            
                            motionXInt = Integer.parseInt(line.substring(22,26), 16);               
                            motionYInt = Integer.parseInt(line.substring(26,30), 16);
                            motionZInt = Integer.parseInt(line.substring(30,34), 16);
                            
                            tempInt = Integer.parseInt(line.substring(34,38), 16);
                            volts =(float) (2.7 * tempInt / 4096);
                            tempCelsius = (float) ((volts - 0.424) / .00625);
                            
                            edaInt = Integer.parseInt(line.substring(38,42), 16);
                            edaBiasInt = Integer.parseInt(line.substring(42,46), 16);
                            heartRateInt = Integer.parseInt(line.substring(46, 50), 16);
                            po2Int = Integer.parseInt(line.substring(50, 54), 16);
                            csum =  Integer.parseInt(line.substring(54, 56), 16);
                            
                            heartRate =(float) (heartRateInt / 10000.0);    //convert to milliseconds
                            heartRate = (1 / heartRate) * 60; //convert to beats per minute
                            
                            totalEda = FACTOR_EDA * Math.abs((float) (edaBiasInt - edaInt)) / (4096 - edaBiasInt);
                            
                            motionX = (Float.parseFloat(String.valueOf(motionXInt)) - 32768)/250;
                            motionY = (Float.parseFloat(String.valueOf(motionYInt)) - 32768)/250;
                            motionZ = (Float.parseFloat(String.valueOf(motionZInt)) - 32768)/250;

                            publishProgress(DATA_V1, timestamp, edaBiasInt, edaInt, totalEda, motionX, motionY, motionZ, tempCelsius, heartRate, po2Int, line);
                        }else if(line.startsWith("*0002")){
                            this.version = 2;
                            line = line.substring(1);
                            timestamp = Long.parseLong(line.substring(6, 22), 16);
                            
                            motionXInt = Integer.parseInt(line.substring(22,26), 16);               
                            motionYInt = Integer.parseInt(line.substring(26,30), 16);
                            motionZInt = Integer.parseInt(line.substring(30,34), 16);
                            
                            tempInt = Integer.parseInt(line.substring(34,38), 16);
                            volts =(float) (2.7 * tempInt / 4096);
                            tempCelsius = (float) ((volts - 0.424) / .00625);
                            
                            edaInt = Integer.parseInt(line.substring(38,42), 16);
                            edaBiasInt = Integer.parseInt(line.substring(42,46), 16);
                            csum =  Integer.parseInt(line.substring(54, 56), 16);
                            
                            totalEda = FACTOR_EDA * Math.abs((float) (edaBiasInt - edaInt)) / (4096 - edaBiasInt);
                            
                            motionX = (Float.parseFloat(String.valueOf(motionXInt)) - 32768)/250;
                            motionY = (Float.parseFloat(String.valueOf(motionYInt)) - 32768)/250;
                            motionZ = (Float.parseFloat(String.valueOf(motionZInt)) - 32768)/250;
                            
                            int ibiInstant = Integer.parseInt(line.substring(46, 50), 16);
                            int ibiAverage = Integer.parseInt(line.substring(50, 54), 16);

                            publishProgress(DATA_V2, timestamp, edaBiasInt, edaInt, totalEda, motionX, motionY, motionZ, tempCelsius, ibiInstant, ibiAverage, line);
                        }else if(line.startsWith("*0005")){
                            this.version = 5;
                            line = line.substring(1);
                            timestamp = Long.parseLong(line.substring(6, 22), 16);
                            
                            motionXInt = Integer.parseInt(line.substring(22,26), 16);               
                            motionYInt = Integer.parseInt(line.substring(26,30), 16);
                            motionZInt = Integer.parseInt(line.substring(30,34), 16);
                            
                            tempInt = Integer.parseInt(line.substring(34,38), 16);
                            volts =(float) (2.7 * tempInt / 4096);
                            tempCelsius = (float) ((volts - 0.424) / .00625);
                            
                            edaInt = Integer.parseInt(line.substring(38,42), 16);
                            edaBiasInt = Integer.parseInt(line.substring(42,46), 16);
                            csum =  Integer.parseInt(line.substring(54, 56), 16);
                            
                            totalEda = FACTOR_EDA * Math.abs((float) (edaBiasInt - edaInt)) / (4096 - edaBiasInt);
                            
                            motionX = (Float.parseFloat(String.valueOf(motionXInt)) - 32768)/250;
                            motionY = (Float.parseFloat(String.valueOf(motionYInt)) - 32768)/250;
                            motionZ = (Float.parseFloat(String.valueOf(motionZInt)) - 32768)/250;
                            
                            int ibiInstant = Integer.parseInt(line.substring(46, 50), 16);
                            int ibiAverage = Integer.parseInt(line.substring(50, 54), 16);

                            publishProgress(DATA_V5, timestamp, edaBiasInt, edaInt, totalEda, motionX, motionY, motionZ, tempCelsius, ibiInstant, ibiAverage, line);
                        }else if(line.startsWith("*0003")){
                            this.version = 3;
                            line = line.substring(1);
                            timestamp = Long.parseLong(line.substring(6, 22), 16);
                            int values[] = new int[8];

                            for(j=0;j<8;j++){
                                values[j] = Integer.parseInt(line.substring(22+j*4,26+j*4), 16);
                            }
                            publishProgress(DATA_V3, timestamp, values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7], line); 
                        }else if(line.startsWith("*0007")){
                            this.version = 1;
                            line = line.substring(1);
                            timestamp = Long.parseLong(line.substring(6, 22), 16);
                            
                            motionXInt = Integer.parseInt(line.substring(22,26), 16);               
                            motionYInt = Integer.parseInt(line.substring(26,30), 16);
                            motionZInt = Integer.parseInt(line.substring(30,34), 16);
                            
                            //
                            // TODO: Update the skin temperature calculation for V7
                            // RICH
                            //
                            tempInt = Integer.parseInt(line.substring(34,38), 16);
                            
                            // tempCelsius is the skin temperature
                            // this was the code used for the old LM 60 temp sensor
                            // volts =(float) (2.7 * tempInt / 4096);
                            // tempCelsius = (float) ((volts - 0.424) / .00625);
                            
                            // this best fit polynomial is based on Jeff's data table
                            // this formula assumes a 10-bit ADC which only goes up to 1023
                            // since our Lifeband uses a 12-bit ADC, we need to divide the ADC value by 4
                            // before applying the formula.  This also helps keep the running values small
                            // since the formula computes the square.
                            //
                            aTempRaw= (float)(tempInt);
                            // this is temperature in farenheight
                            //tempCelsius = (float) (77.898+0.08026*aTempRaw-(aTempRaw*aTempRaw*0.00017707));
                            
                            // this is temperature in celsius
                            //tempCelsius = (float)(70.66432-0.006*aTempRaw);
                            
                            // use the following line for debugging calibration
                            tempCelsius = (float)(tempInt);
                            
                            edaInt = Integer.parseInt(line.substring(38,42), 16);
                            edaBiasInt = Integer.parseInt(line.substring(42,46), 16);
                            
                            ambientTemp = ((float)Integer.parseInt(line.substring(46, 50), 16))/32 - 50;
                            
                            ambientHum = ((float)Integer.parseInt(line.substring(50, 54), 16))/16 - 24;
                            
                            csum =  Integer.parseInt(line.substring(54, 56), 16);
                            
                            totalEda = FACTOR_EDA * Math.abs((float) (edaBiasInt - edaInt)) / (4096 - edaBiasInt);
                            motionX = (Float.parseFloat(String.valueOf(motionXInt)) - 32768)/1024;
                            motionY = (Float.parseFloat(String.valueOf(motionYInt)) - 32768)/1024;
                            motionZ = (Float.parseFloat(String.valueOf(motionZInt)) - 32768)/1024;

                            publishProgress(DATA_V7, timestamp, edaBiasInt, edaInt, totalEda, motionX, motionY, motionZ, tempCelsius, ambientTemp, ambientHum, line);
                        }
                    }else if(len == 65){
                        if(sb.length() > 0) sb.delete(0, sb.length());
                        for(j=0;j<len;j++){
                            sb.append((char)b[lastbreak+j]);
                        }

                        // new data format: needs to start with 0000?
                        line = sb.toString().toUpperCase().trim();

                        if(line.startsWith("*0008")){
                            this.version = 1;
                            line = line.substring(1);
                            timestamp = Long.parseLong(line.substring(6, 22), 16);
                            
                            motionXInt = Integer.parseInt(line.substring(22,26), 16);               
                            motionYInt = Integer.parseInt(line.substring(26,30), 16);
                            motionZInt = Integer.parseInt(line.substring(30,34), 16);
                            
                            totalVisibleLight = Integer.parseInt(line.substring(34,38), 16);
                            infraredLight = Integer.parseInt(line.substring(38,42), 16);
                            redLightLevel = Integer.parseInt(line.substring(42,46), 16);
                            blueLightLevel = Integer.parseInt(line.substring(46,50), 16);
                            greenLightLevel = Integer.parseInt(line.substring(50,54), 16);
                            ambientTemp = Integer.parseInt(line.substring(54, 58), 16);
                            ambientSound = Integer.parseInt(line.substring(58, 62), 16);
                            csum =  Integer.parseInt(line.substring(62, 64), 16);

                            motionX = (Float.parseFloat(String.valueOf(motionXInt)) - 32768)/250;
                            motionY = (Float.parseFloat(String.valueOf(motionYInt)) - 32768)/250;
                            motionZ = (Float.parseFloat(String.valueOf(motionZInt)) - 32768)/250;

                            publishProgress(DATA_V8, timestamp, totalVisibleLight, infraredLight, redLightLevel, blueLightLevel, greenLightLevel, motionX, motionY, motionZ, ambientTemp, ambientSound, line);
                        }
                    }else if(len > 1){
                        if(sb.length() > 0) sb.delete(0, sb.length());
                        for(j=0;j<len;j++){
                            sb.append((char)b[lastbreak+j]);
                        }

                        line = sb.toString().toUpperCase().trim();
                        if(line.equals("OK")){
                            if(lastCommand != null && !"".equals(lastCommand.command.trim())){
                                publishProgress(COMMAND_ACK, lastCommand.command, lastResponse);
                            }
                            lastResponse = null;
                            lastCommand = null;
                        }else{
                            if(lastResponse != null){
                                Message m = Message.obtain();
                                m.what = BLUETOOTH_ERROR;
                                m.obj = "An error occured: "+lastResponse;
                                handler.sendMessageDelayed(m, 11);
                            }
                            if(!line.startsWith("ERR ")) lastResponse = line;
                        }
                        if(DEBUG) android.util.Log.v(TAG, "Other Response ["+line+"]");
                        android.util.Log.v(TAG, "Other Response ["+line+"]");
                    }
                    if(this.lastCommand != null && !this.expectingOK){
                        this.lastCommand = null;
                    }
                    lastbreak = i+1;
                    len = 0;
                }else{
                    ++len;
                    if(DEBUG) db.append((char)b[i]);
                }
            }
            if(lastbreak > 0 && lastbreak < span){
                // update the buffer
                System.arraycopy(b, lastbreak, b, 0, span-lastbreak);
            }
            return span - lastbreak;
        }

        public String toString()
        {
            return this.name;
        }
        
        void publishProgress(Object... pieces)
        {
            Message m = Message.obtain();
            m.what = BLUETOOTH_MESSAGE;
            m.obj = new Object[]{ this, pieces };
            handler.sendMessageDelayed(m, 10);
        }
        
        void addCommand(String command){ this.addCommand(command, true); }
        
        void addCommand(String command, boolean waitForResponse){
            command c = new command();
            c.command = command;
            c.waitForResponse = waitForResponse;
            this.commands.add(c);
        }
    }

    void updateProgress(bluetoothCommSession session, Object[] progress)
    {
        if(progress[0] == COMMAND_ACK){
            //
            // It is assumed that all commands return an OK response
            // Commands that return data in addition to the OK are processed below 
            // These are typically only the GET commands.
            // There is a 10 second timeout where we wait for the response for all command
            //
            if(progress[1].toString().startsWith("SET 1 ")){
                Toast.makeText(this, "The time was successfully set.", Toast.LENGTH_SHORT).show();
            }else if(progress[1].toString().startsWith("SET 5 ")){
                    Toast.makeText(this, "The device ID was successfully set.", Toast.LENGTH_SHORT).show();
            }else if(progress[1].toString().startsWith("GET 1")){
                if(progress[2] != null){
                    long time = Long.parseLong(progress[2].toString(), 16) * 1000;;
                    Toast.makeText(this, "Sensor Time: "+format2.format(new Date(time)), Toast.LENGTH_SHORT).show();
                }
            } else if (progress[1].toString().startsWith("GET 5")) {
                if (progress[2] != null){
                    session.id = progress[2].toString();
                    session.idset = true;
                    setIdAvailable(session.idset);
                }
            }else if(progress[1].toString().startsWith("GET 6")){
                // BATTERY
                if(progress[2] != null){
                    String batteryVal = progress[2].toString();
                    //float battery = (Integer.parseInt(batteryVal, 16)*1.0f)/275 - 1.68f;
                    
                    //float battery = 0.1f + (float)Integer.parseInt(batteryVal, 16) / 796f;
                    // 2011.06.05: from Joyce
                    
                    //parseInt returns the decimal value base 16
                    // I don't know where this battery formula came from but
                    // the correct formula is:
                    // (ADC/4096)*3.3V/0.1988 = Vbattery
                    // roughly ADC*.00405 = Vbattery
                    // the 0.1988 comes from the resistor voltage divider
                    // 806K and 200K resistors
                    // 0.1988=200K/(806K+200K)
                    //
                    // but this doesn't match the ADC values we're getting, which are approx 0x0880 - 0x0900
                    
                    try{
                        //float battery = 0.0016431f * (float)Integer.parseInt(batteryVal, 16) - 1.0912f;
                        // if battery is less than 3.5 then low-bat indicator comes on
                        float battery = 0.002818f * (float)Integer.parseInt(batteryVal, 16) - 2.632f;
                        if(battery < 0) battery = 0;
                        View v = findViewById(R.id.lowbattery);
                        if(v != null){
                            if(battery < 3.5) findViewById(R.id.lowbattery).setVisibility(View.VISIBLE);
                            else findViewById(R.id.lowbattery).setVisibility(View.GONE);
                        }
                        Toast.makeText(this, "Sensor battery: ["+batteryVal+"]["+battery+"]", Toast.LENGTH_SHORT).show();
                    }catch(Exception e1111){}
                }
            }else{
                //did this line change?
                Toast.makeText(this, "The command ["+progress[1]+"] was successfully sent.", Toast.LENGTH_SHORT).show();
            }
        }else if(progress[0] == SENSOR_AVAILABLE){
            // the sensor is now available
            sensorAvailable = true;
            this.DATA_TO_PLOT = PLOT_EDA;
            dataBeingPlottedChanged = true;
            label1.setBackgroundDrawable(drawable);
            resetAllButOneView(label1);
        }else if(progress[0] == DATA_V3){
            // we're here: plot automatically
            if(session == selected.getSelectedItem()){
                this.DATA_TO_PLOT = PLOT_HR_RAW;
                if(graphView == null || currentView != graphView){
                    dataBeingPlottedChanged = true;
                    showGraphView();
                }

                if(dataBeingPlottedChanged){
                    //graphView.setDataToPlot(DATA_TO_PLOT);
                    graphView.clear();
                    dataBeingPlottedChanged = false;
                }

                graphView.addDataPoint(((Integer)progress[2]));
                graphView.addDataPoint(((Integer)progress[3]));
                graphView.addDataPoint(((Integer)progress[4]));
                graphView.addDataPoint(((Integer)progress[5]));
                graphView.addDataPoint(((Integer)progress[6]));
                graphView.addDataPoint(((Integer)progress[7]));
                graphView.addDataPoint(((Integer)progress[8]));
                graphView.addDataPoint(((Integer)progress[9]));
            }
        }else if(progress[0] == DATA_V1){
            if(!viewsUpdated){
//              hideAllTextViewsExcept(edaBiasView, edaPView, edaView, accelerationView, tempView, heartRateView, timeView);
                hideAllTextViewsExcept(label1, label3, label2, label10);
                viewsUpdated = true;
            }
            sensorType = 1;
            /*
             publishProgress(DATA_V1, timestamp, edaBiasInt, edaInt, totalEda, motionX, 
                motionY, motionZ, tempCelsius, heartRate, po2Int, line);
             */
            long time = (Long)progress[1];
            Date d = new Date(time);
            String now = String.valueOf(time), nowPretty = format.format(d);
            if(currentView == mainView){
                if(session == selected.getSelectedItem()){
//                  edaBiasView.setText("EdaBias (units)  :"+progress[2].toString());
//                  edaPView.setText("EdaP (units)  :"+progress[3].toString());
                    label1.setText("EDA (mho)  :"+progress[4].toString());
                    label3.setText("Accel: "+df2.format(progress[5])+","+df2.format(progress[6])+","+df2.format(progress[7])); //Not correct: This is only the x acceleration values
                    label2.setText("Temp (C)  :"+progress[8].toString());
//                  heartRateView.setText("Heart Rate (bpm)  :"+progress[9].toString());
                    label10.setText("Sensor Time: "+format2.format(d));
                }

                if(idset && ISRECORDING){
                    try{
                        
                        if(MARKER_ON){ //if marker is on, it means we only record a marker in the text file
                            Toast.makeText(this, "Marker was successfully set.", Toast.LENGTH_SHORT).show();
                            bufferredWriter.write(now);
                            bufferredWriter.write(",");
                            bufferredWriter.write(nowPretty);
                            bufferredWriter.write(",MARKER,AA2104FF01FFFF7F30C410D588FFFF00000000000000000000000000000000800000FF\r\n"); //marker value
                            this.MARKER_ON = false;
                        }

                        bufferredWriter.write(now);
                        bufferredWriter.write(",");
                        bufferredWriter.write(nowPretty);
                        bufferredWriter.write(",REV0,");
                        bufferredWriter.write(progress[11].toString());   //raw byte data
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[2].toString());    //eda bias
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[3].toString());    //edaP
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[4].toString());    //eda
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[5].toString());    //x-accel   //mugisha:the x, y and z might have been interchanged
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[6].toString());    //y-axel
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[7].toString());    //z-accel
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[8].toString());    //temperature
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[9].toString());    //heart rate
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[10].toString());     // po2
                        bufferredWriter.write(",");
                        bufferredWriter.write(session.id);
                        bufferredWriter.write(",");
                        bufferredWriter.write(session.getAddress());
                        bufferredWriter.write("\r\n"); 
                    }catch(IOException e){
                        Log.w(TAG,"IOException when trying to write to file");
                    }
                }

                Message msg = new Message();
                msg.what = SCROLL_DOWN;
                msgHandler.sendMessageDelayed(msg, 100);
            }

            if(session == selected.getSelectedItem() && graphView != null){
                if(dataBeingPlottedChanged){
                    //graphView.setDataToPlot(DATA_TO_PLOT);
                    graphView.clear();
                    dataBeingPlottedChanged = false;
                }

                switch(DATA_TO_PLOT){
                case PLOT_EDA_BIAS:
                    graphView.addDataPoint(((Integer)progress[2]).intValue());
                    break;
                case PLOT_EDA_P:
                    graphView.addDataPoint((Integer)progress[3]);
                    break;
                case PLOT_EDA:
                    graphView.addDataPoint((Float)progress[4]);
                    break;
                case PLOT_ACCELERATION:
                    graphView.addXDataPoint((Float)progress[5]);
                    graphView.addYDataPoint((Float)progress[6]);
                    graphView.addZDataPoint((Float)progress[7]);
                    break;

                case PLOT_MOTION_X:
                    graphView.addDataPoint(((Integer)progress[5]).intValue() - 32768);
                    break;
                case PLOT_MOTION_Y:
                    graphView.addDataPoint(((Integer)progress[6]).intValue() - 32768);
                    break;
                case PLOT_MOTION_Z:
                    graphView.addDataPoint(((Integer)progress[7]).intValue() - 32768);
                    break;
                case PLOT_TEMPERATURE:
                    graphView.addDataPoint((Float)progress[8]);
                    break;
                case PLOT_HEARTRATE:
                    graphView.addDataPoint((Float)progress[9]);
                    break;
                default:
                    break;
                }
            }
        }else if(progress[0] == DATA_V2){
            if(!viewsUpdated){
                hideAllTextViewsExcept(label6, label7, label10);
                viewsUpdated = true;
            }
            // DATA_V2, timestamp, edaBiasInt, edaInt, totalEda, 
            // motionX, motionY, motionZ, tempCelsius, ibiInstant, ibiAverage, line
            sensorType = 2;
            long time = (Long)progress[1];
            Date d = new Date(time);
            String now = String.valueOf(time), nowPretty = format.format(d);
            double bpm = 60000 / Double.valueOf((Integer)progress[9]);
            double bpmAvg = 60000 / Double.valueOf((Integer)progress[10]);
            if(currentView == mainView){
                if(session == selected.getSelectedItem()){
                    /*
                    edaView.setText("Instant HR (bpm): "+df.format(bpm));
                    tempView.setText("Average HR (bpm): "+df.format(bpmAvg));
                    */
                    
                    label6.setText(Html.fromHtml("<font color='red'>Instant</font> HR (bpm): "+df.format(bpm)));
                    label7.setText(Html.fromHtml("<font color='blue'>Average</font> HR (bpm): "+df.format(bpmAvg)));
                    label10.setText("Sensor Time: "+format2.format(d));
                }

                if(ISRECORDING){
                    try{
                        
                        if(MARKER_ON){ //if marker is on, it means we only record a marker in the text file
                            Toast.makeText(this, "Marker was successfully set.", Toast.LENGTH_SHORT).show();
                            bufferredWriter.write(now);
                            bufferredWriter.write(",");
                            bufferredWriter.write(nowPretty);
                            bufferredWriter.write(",MARKER,AA2104FF01FFFF7F30C410D588FFFF00000000000000000000000000000000800000FF\r\n"); //marker value
                            this.MARKER_ON = false;
                        }

                        bufferredWriter.write(now);
                        bufferredWriter.write(",");
                        bufferredWriter.write(nowPretty);
                        bufferredWriter.write(",REV2,");
                        bufferredWriter.write(progress[11].toString());   //raw byte data
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[9].toString());    //ibi Instant
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[10].toString());     // ibi Average
                        bufferredWriter.write(",");
                        bufferredWriter.write(df.format(bpm));      // heartrate in beats per minute
                        bufferredWriter.write(",");
                        bufferredWriter.write(df.format(bpmAvg));       // heartrate in beats per minute
                        bufferredWriter.write(",");
                        bufferredWriter.write(session.id);
                        bufferredWriter.write(",");
                        bufferredWriter.write(session.getAddress());
                        bufferredWriter.write("\r\n"); 
                    }catch(IOException e){
                        Log.w(TAG,"IOException when trying to write to file");
                    }
                }

                Message msg = new Message();
                msg.what = SCROLL_DOWN;
                msgHandler.sendMessageDelayed(msg, 100);
            }

            if(session == selected.getSelectedItem() && graphView != null){
                if(dataBeingPlottedChanged){
                    graphView.setDataToPlot(PLOT_HEARTRATE);
                    graphView.clear();
                    dataBeingPlottedChanged = false;
                }

                graphView.addXDataPoint(new Float(bpm));
                graphView.addYDataPoint(new Float(bpmAvg));
            }
        }else if(progress[0] == DATA_V5){
            if(!viewsUpdated){
//              hideAllTextViewsExcept(edaBiasView, edaPView, edaView, accelerationView, tempView, heartRateView, heartRateAverageView, hrv, timeView);
                hideAllTextViewsExcept(label1, label3, label2, label6, label7, label8, label10);
                viewsUpdated = true;
            }
            // DATA_V5, timestamp, edaBiasInt, edaInt, totalEda, 
            // motionX, motionY, motionZ, tempCelsius, ibiInstant, ibiAverage, line
            sensorType = 5;
            long time = (Long)progress[1];
            Date d = new Date(time);
            String now = String.valueOf(time), nowPretty = format.format(d);
            double bpm = 60000 / Double.valueOf((Integer)progress[9]);
            double bpmAvg = 60000 / Double.valueOf((Integer)progress[10]);
            double hrvV = Math.sqrt(Math.abs(bpm*bpm - bpmAvg*bpmAvg)); 
            String bpmVal = df.format(bpm), bpmAvgVal = df.format(bpmAvg);
            String hrvVal = df2.format(hrvV);
            
            if(currentView == mainView){
                if(session == selected.getSelectedItem()){
                    //edaBiasView.setText("EdaBias (units)  :"+progress[2].toString());
                    //edaPView.setText("EdaP (units)  :"+progress[3].toString());
                    label1.setText("EDA (mho)  :"+progress[4].toString());
                    label3.setText("Accel: "+df2.format(progress[5])+","+df2.format(progress[6])+","+df2.format(progress[7])); //Not correct: This is only the x acceleration values
                    label2.setText("Temp (C)  :"+progress[8].toString());
                    label6.setText("Heart Rate (bpm)  :"+bpmVal);
                    label7.setText("Avg. Heart Rate (bpm)  :"+bpmAvgVal);
                    label8.setText("HR Variability: "+hrvVal);
                    label10.setText("Sensor Time: "+format2.format(d));
                }

                if(ISRECORDING){
                    try{
                        
                        if(MARKER_ON){ //if marker is on, it means we only record a marker in the text file
                            Toast.makeText(this, "Marker was successfully set.", Toast.LENGTH_LONG).show();
                            bufferredWriter.write(now);
                            bufferredWriter.write(",");
                            bufferredWriter.write(nowPretty);
                            bufferredWriter.write(",MARKER,AA2104FF01FFFF7F30C410D588FFFF00000000000000000000000000000000800000FF\r\n"); //marker value
                            this.MARKER_ON = false;
                        }

                        bufferredWriter.write(now);
                        bufferredWriter.write(",");
                        bufferredWriter.write(nowPretty);
                        bufferredWriter.write(",REV5,");
                        bufferredWriter.write(progress[11].toString());   //raw byte data
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[2].toString());    //eda bias
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[3].toString());    //edaP
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[4].toString());    //eda
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[5].toString());    //x-accel   //mugisha:the x, y and z might have been interchanged
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[6].toString());    //y-axel
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[7].toString());    //z-accel
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[8].toString());    //temperature
                        bufferredWriter.write(",");
                        bufferredWriter.write(bpmVal);      // heartrate in beats per minute
                        bufferredWriter.write(",");
                        bufferredWriter.write(bpmAvgVal);       // heartrate in beats per minute
                        bufferredWriter.write(",");
                        bufferredWriter.write(hrvVal);      // HRV value
                        bufferredWriter.write(",");
                        bufferredWriter.write(session.id);
                        bufferredWriter.write(",");
                        bufferredWriter.write(session.getAddress());
                        bufferredWriter.write("\r\n");
                    }catch(IOException e){
                        Log.w(TAG,"IOException when trying to write to file");
                    }
                }

                Message msg = new Message();
                msg.what = SCROLL_DOWN;
                msgHandler.sendMessageDelayed(msg, 100);
            }

            if(session == selected.getSelectedItem() && graphView != null){
                if(dataBeingPlottedChanged){
                    //graphView.setDataToPlot(DATA_TO_PLOT);
                    graphView.clear();
                    dataBeingPlottedChanged = false;
                }

                switch(DATA_TO_PLOT){
                case PLOT_EDA_BIAS:
                    graphView.addDataPoint(((Integer)progress[2]).intValue());
                    break;
                case PLOT_EDA_P:
                    graphView.addDataPoint((Integer)progress[3]);
                    break;
                case PLOT_EDA:
                    graphView.addDataPoint((Float)progress[4]);
                    break;
                case PLOT_ACCELERATION:
                    graphView.addXDataPoint((Float)progress[5]);
                    graphView.addYDataPoint((Float)progress[6]);
                    graphView.addZDataPoint((Float)progress[7]);
                    break;

                case PLOT_MOTION_X:
                    graphView.addDataPoint(((Integer)progress[5]).intValue() - 32768);
                    break;
                case PLOT_MOTION_Y:
                    graphView.addDataPoint(((Integer)progress[6]).intValue() - 32768);
                    break;
                case PLOT_MOTION_Z:
                    graphView.addDataPoint(((Integer)progress[7]).intValue() - 32768);
                    break;
                case PLOT_TEMPERATURE:
                    graphView.addDataPoint((Float)progress[8]);
                    break;
                case PLOT_HEARTRATE:
                    graphView.addDataPoint(new Float(bpm));
                    break;
                case PLOT_HEARTRATE_AVERAGE:
                    graphView.addDataPoint(new Float(bpmAvg));
                    break;
                case PLOT_HRV:
                    graphView.addDataPoint(new Float(hrvVal));
                    break;
                default:
                    break;
                }
            }
        }else if(progress[0] == DATA){
            if(!viewsUpdated){
                hideAllTextViewsExcept(label4, label5, label1, label3, label2, label6);
                viewsUpdated = true;
            }

            // Fletcher edit commented out EDAP-EDAbias-and-heartrate
            sensorType = 0;
            if(currentView == mainView){
                //String current = progress[1]+"\n"+ progress[2]+progress[3]+progress[4]+progress[5]+progress[6]+progress[7]+progress[8];           
                if(session == selected.getSelectedItem()){
                    //edaBiasView.setText("EdaBias (units)  :"+progress[1].toString());
                    //edaPView.setText("EdaP (units)  :"+progress[2].toString());
                    label1.setText("EDA (mho)v0  :"+progress[3].toString());
                    label3.setText("Accel: "+df2.format(progress[4])+","+df2.format(progress[5])+","+df2.format(progress[6])); //Not correct: This is only the x acceleration values
                    label2.setText("Temp (C)  :"+progress[7].toString());
                    label9.setText("Battery (Volts)  :"+progress[8].toString());
                    //heartRateView.setText("Heart Rate (BPM)  :"+progress[9].toString());
                }

                if(ISRECORDING){
                    try{
                        //Log.w(TAG, "WE ARE WRITING TO FILE");
                        /*
                        fos.write(progress[1].toString().getBytes());
                        fos.write("\n".getBytes());     //add a new line. this might have issues

                        fos.write(progress[2].toString().getBytes());
                        fos.write("\n".getBytes());     //add a new line. this might have issues

                        fos.write(progress[3].toString().getBytes());
                        fos.write("\n".getBytes());     //add a new line. this might have issues

                        fos.write(progress[4].toString().getBytes());
                        fos.write("\n".getBytes());     //add a new line. this might have issues

                        fos.write(progress[5].toString().getBytes());
                        fos.write("\n".getBytes());     //add a new line. this might have issues

                        fos.write(progress[6].toString().getBytes());
                        fos.write("\n".getBytes());     //add a new line. this might have issues

                        fos.write(progress[7].toString().getBytes());
                        fos.write("\n".getBytes());     //add a new line. this might have issues

                        fos.write(progress[8].toString().getBytes());
                        fos.write("\n".getBytes());     //add a new line. this might have issues

                        fos.write(progress[9].toString().getBytes());
                        fos.write("\n".getBytes());     //add a new line. this might have issues*/
                        Date d = new Date();
                        Long timeNow = new Long(d.getTime());

                        if(MARKER_ON){ //if marker is on, it means we only record a marker in the text file
                            Toast.makeText(this, "Marker was successfully set.", Toast.LENGTH_SHORT).show();
                            bufferredWriter.write(timeNow.toString());
                            bufferredWriter.write(",");
                            bufferredWriter.write(format.format(d));
                            bufferredWriter.write(",MARKER,AA2104FF01FFFF7F30C410D588FFFF00000000000000000000000000000000800000FF\r\n"); //marker value
                            this.MARKER_ON = false;
                        }

                        bufferredWriter.write(timeNow.toString());
                        bufferredWriter.write(",");
                        bufferredWriter.write(format.format(d));
                        bufferredWriter.write(",DATA,");
                        bufferredWriter.write(progress[10].toString());   //raw byte data
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[3].toString());    //eda
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[4].toString());    //x-accel   //mugisha:the x, y and z might have been interchanged
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[5].toString());    //y-axel
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[6].toString());    //z-accel
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[7].toString());    //temperature
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[8].toString());     //battery voltage
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[9].toString());    //heart rate
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[1].toString());    //eda bias
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[2].toString());
                        bufferredWriter.write("\r\n");    //edaP
                        //Log.w(TAG, "WRITTEN DATA TO FILE");
                    }catch(IOException e){
                        Log.w(TAG,"IOException when trying to write to file");
                    }
                }

                Message msg = new Message();
                msg.what = SCROLL_DOWN;
                msgHandler.sendMessageDelayed(msg, 100);
            }

            if(session == selected.getSelectedItem() && graphView != null){
                if(dataBeingPlottedChanged){
                    //graphView.setDataToPlot(DATA_TO_PLOT);
                    graphView.clear();
                    dataBeingPlottedChanged = false;
                }

                switch(DATA_TO_PLOT){
                case PLOT_EDA_BIAS:
                    graphView.addDataPoint(((Integer)progress[1]).intValue());
                    break;
                case PLOT_EDA_P:
                    graphView.addDataPoint((Integer)progress[2]);
                    break;
                case PLOT_EDA:
                    /*
                    Log.w(TAG,"we are in case EDA");
                    Log.d(TAG, "The y value is "+String.valueOf(progress[3]));
                     */
                    //Log.w(TAG, "The y value with conversion is :"+String.valueOf((Float)progress[3]));
                    graphView.addDataPoint((Float)progress[3]);
                    break;

                case PLOT_ACCELERATION:
                    //Log.w(TAG, "Value of accel (x) :"+String.valueOf(progress[4]));
                    graphView.addXDataPoint((Float)progress[4]);
                    graphView.addYDataPoint((Float)progress[5]);
                    //Log.w(TAG, "Value of accel (y) :"+String.valueOf(progress[5]));
                    graphView.addZDataPoint((Float)progress[6]);
                    break;

                case PLOT_MOTION_X:
                    graphView.addDataPoint(((Integer)progress[4]).intValue() - 32768);
                    break;
                case PLOT_MOTION_Y:
                    graphView.addDataPoint(((Integer)progress[5]).intValue() - 32768);
                    break;
                case PLOT_MOTION_Z:
                    graphView.addDataPoint(((Integer)progress[6]).intValue() - 32768);
                    break;
                case PLOT_TEMPERATURE:
                    //Log.w(TAG,"the temperature value is : "+String.valueOf((Float)progress[7]));
                    graphView.addDataPoint((Float)progress[7]);
                    break;
                case PLOT_BATTERY:
                    graphView.addDataPoint((Integer)progress[8]);
                    break;
                case PLOT_HEARTRATE:
                    //Log.w(TAG, "just before adding heartrate point");
                    graphView.addDataPoint((Float)progress[9]);
                    break;
                default:
                    //Log.w(TAG, "Not a valid data to plot value");
                    break;
                }
            }
            /*
            float rand = (float) Math.random()*400;
            graphView.addDataPoint(rand);
             */
        }else if(progress[0] == DATA_V7){
            if(!viewsUpdated){
//              hideAllTextViewsExcept(edaBiasView, edaPView, edaView, accelerationView, tempView, heartRateView, timeView);
                hideAllTextViewsExcept(label1, label3, label2, label10, label4, label5);
                viewsUpdated = true;
            }
            sensorType = 7;
            /*
             publishProgress(DATA_V7, timestamp, edaBiasInt, edaInt, totalEda, motionX, 
             motionY, motionZ, tempCelsius, ambientTemp, ambientHum, line);
             */
            long time = (Long)progress[1];
            Date d = new Date(time);
            String now = String.valueOf(time), nowPretty = format.format(d);
            if(currentView == mainView){
                if(session == selected.getSelectedItem()){
                    /*
                     * The label names below are just label names and do not correspond
                     * to the actual sensor value displayed. We refactored the code because the 
                     * label names used to have other names such as, edaPView, which was
                     * really confusing. The position of these labels is in the layout.
                     */
                    label1.setText("EDA (uS)  :"+progress[4].toString());
                    label2.setText("Skin Temp (C)  :"+progress[8].toString());
                    label3.setText("Ambient Temp (C)  :"+progress[9].toString());
                    label4.setText("Ambient Humidity (RH%) :"+progress[10].toString());
                    label5.setText("Accel (g): "+df2.format(progress[5])+","+df2.format(progress[6])+","+df2.format(progress[7])); //Not correct: This is only the x acceleration values
                    label10.setText("Sensor Time: "+format2.format(d));
                }

                if(idset && ISRECORDING){
                    try{
                        
                        if(MARKER_ON){ //if marker is on, it means we only record a marker in the text file
                            Toast.makeText(this, "Marker was successfully set.", Toast.LENGTH_SHORT).show();
                            bufferredWriter.write(now);
                            bufferredWriter.write(",");
                            bufferredWriter.write(nowPretty);
                            bufferredWriter.write(",MARKER,AA2104FF01FFFF7F30C410D588FFFF00000000000000000000000000000000800000FF\r\n"); //marker value
                            this.MARKER_ON = false;
                        }

                        bufferredWriter.write(now);
                        bufferredWriter.write(",");
                        bufferredWriter.write(nowPretty);
                        bufferredWriter.write(",REV7,");
                        bufferredWriter.write(progress[11].toString());   //raw byte data
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[2].toString());    //eda bias
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[3].toString());    //edaP
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[4].toString());    //eda
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[5].toString());    //x-accel   //mugisha:the x, y and z might have been interchanged
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[6].toString());    //y-axel
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[7].toString());    //z-accel
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[8].toString());    //temperature
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[9].toString());    //ambient temperature
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[10].toString());     // ambient humidity
                        bufferredWriter.write(",");
                        bufferredWriter.write(session.id);
                        bufferredWriter.write(",");
                        bufferredWriter.write(session.getAddress());
                        bufferredWriter.write("\r\n"); 
                    }catch(IOException e){
                        Log.w(TAG,"IOException when trying to write to file");
                    }
                }

                Message msg = new Message();
                msg.what = SCROLL_DOWN;
                msgHandler.sendMessageDelayed(msg, 100);
            }

            if(session == selected.getSelectedItem() && graphView != null){
                if(dataBeingPlottedChanged){
                    //graphView.setDataToPlot(DATA_TO_PLOT);
                    graphView.clear();
                    dataBeingPlottedChanged = false;
                }

                switch(DATA_TO_PLOT){
                case PLOT_EDA_BIAS:
                    graphView.addDataPoint(((Integer)progress[2]).intValue());
                    break;
                case PLOT_EDA_P:
                    graphView.addDataPoint((Integer)progress[3]);
                    break;
                case PLOT_EDA:
                    graphView.addDataPoint((Float)progress[4]);
                    break;
                case PLOT_ACCELERATION:
                    graphView.addXDataPoint((Float)progress[5]);
                    graphView.addYDataPoint((Float)progress[6]);
                    graphView.addZDataPoint((Float)progress[7]);
                    break;

                case PLOT_MOTION_X:
                    graphView.addDataPoint(((Integer)progress[5]).intValue() - 32768);
                    break;
                case PLOT_MOTION_Y:
                    graphView.addDataPoint(((Integer)progress[6]).intValue() - 32768);
                    break;
                case PLOT_MOTION_Z:
                    graphView.addDataPoint(((Integer)progress[7]).intValue() - 32768);
                    break;
                case PLOT_TEMPERATURE:
                    graphView.addDataPoint((Float)progress[8]);
                    break;
                case PLOT_AMBIENT_TEMP:
                    graphView.addDataPoint((Float)progress[9]);
                    break;
                case PLOT_AMBIENT_HUMIDITY:
                    graphView.addDataPoint((Float)progress[10]);
                    break;
                default:
                    break;
                }
                /*
                 publishProgress(DATA_V7, timestamp, edaBiasInt, edaInt, totalEda, motionX, 
                 motionY, motionZ, tempCelsius, ambientTemp, ambientHum, line);
                 */
                
            }
        }else if(progress[0] == DATA_V8){
            if(!viewsUpdated){
//              hideAllTextViewsExcept(edaBiasView, edaPView, edaView, accelerationView, tempView, heartRateView, timeView);
                hideAllTextViewsExcept(label1, label3, label2, label10, label4, label5, label6);
                viewsUpdated = true;
            }
            sensorType = 8;
            /*
            publishProgress(DATA_V8, timestamp, totalVisibleLight, infraredLight, redLightLevel, 
            blueLightLevel, greenLightLevel, motionX, motionY, motionZ, ambientTemp, ambientSound, line);
             */
            long time = (Long)progress[1];
            Date d = new Date(time);
            String now = String.valueOf(time), nowPretty = format.format(d);
            if(currentView == mainView){
                if(session == selected.getSelectedItem()){
                    //
                    // TODO: turn on the appropriate labels and then set their values
                    //
//                  edaBiasView.setText("EdaBias (units)  :"+progress[2].toString());
//                  edaPView.setText("EdaP (units)  :"+progress[3].toString());
                    label1.setText("EDA (uS)  :"+progress[4].toString());
                    label3.setText("Accel: "+df2.format(progress[5])+","+df2.format(progress[6])+","+df2.format(progress[7])); //Not correct: This is only the x acceleration values
                    label2.setText("Temp (C)  :"+progress[8].toString());
//                  heartRateView.setText("Heart Rate (bpm)  :"+progress[9].toString());
                    label10.setText("Sensor Time: "+format2.format(d));
                }

                if(idset && ISRECORDING){
                    try{
                        
                        if(MARKER_ON){ //if marker is on, it means we only record a marker in the text file
                            Toast.makeText(this, "Marker was successfully set.", Toast.LENGTH_SHORT).show();
                            bufferredWriter.write(now);
                            bufferredWriter.write(",");
                            bufferredWriter.write(nowPretty);
                            bufferredWriter.write(",MARKER,AA2104FF01FFFF7F30C410D588FFFF00000000000000000000000000000000800000FF\r\n"); //marker value
                            this.MARKER_ON = false;
                        }

                        bufferredWriter.write(now);
                        bufferredWriter.write(",");
                        bufferredWriter.write(nowPretty);
                        bufferredWriter.write(",REV8,");
                        bufferredWriter.write(progress[12].toString());   //raw byte data
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[2].toString());    //total light
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[3].toString());    //infrared
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[4].toString());    //red
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[5].toString());    //green
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[6].toString());    //blue
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[7].toString());    //x-accel   //mugisha:the x, y and z might have been interchanged
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[8].toString());    //y-axel
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[9].toString());    //z-accel
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[10].toString());    //ambient temperature
                        bufferredWriter.write(",");
                        bufferredWriter.write(progress[11].toString());     // ambient sound
                        bufferredWriter.write(",");
                        bufferredWriter.write(session.id);
                        bufferredWriter.write(",");
                        bufferredWriter.write(session.getAddress());
                        bufferredWriter.write("\r\n"); 
                    }catch(IOException e){
                        Log.w(TAG,"IOException when trying to write to file");
                    }
                }

                Message msg = new Message();
                msg.what = SCROLL_DOWN;
                msgHandler.sendMessageDelayed(msg, 100);
            }

            if(session == selected.getSelectedItem() && graphView != null){
                if(dataBeingPlottedChanged){
                    //graphView.setDataToPlot(DATA_TO_PLOT);
                    graphView.clear();
                    dataBeingPlottedChanged = false;
                }

                switch(DATA_TO_PLOT){
                case PLOT_TOTAL_VISIBLE_LIGHT:
                    graphView.addDataPoint((Integer)progress[2]);
                    break;
                case PLOT_TOTAL_INFRARED_LIGHT:
                    graphView.addDataPoint((Integer)progress[3]);
                    break;
                case PLOT_RED_LIGHT_LEVEL:
                    graphView.addDataPoint((Integer)progress[4]);
                    break;
                case PLOT_BLUE_LIGHT_LEVEL:
                    graphView.addDataPoint((Integer)progress[5]);
                    break;
                case PLOT_GREEN_LIGHT_LEVEL:
                    graphView.addDataPoint((Integer)progress[6]);
                    break;
                
                case PLOT_ACCELERATION:
                    graphView.addXDataPoint((Float)progress[7]);
                    graphView.addYDataPoint((Float)progress[8]);
                    graphView.addZDataPoint((Float)progress[9]);
                    break;
                case PLOT_MOTION_X:
                    graphView.addDataPoint(((Integer)progress[7]).intValue() - 32768);
                    break;
                case PLOT_MOTION_Y:
                    graphView.addDataPoint(((Integer)progress[8]).intValue() - 32768);
                    break;
                case PLOT_MOTION_Z:
                    graphView.addDataPoint(((Integer)progress[9]).intValue() - 32768);
                    break;
                case PLOT_AMBIENT_TEMP:
                    graphView.addDataPoint((Float)progress[10]);
                    break;
                case PLOT_AMBIENT_SOUND:
                    graphView.addDataPoint((Float)progress[11]);
                    break;
                default:
                    break;
                }
                /*
                 publishProgress(DATA_V7, timestamp, edaBiasInt, edaInt, totalEda, motionX, 
                 motionY, motionZ, tempCelsius, ambientTemp, ambientHum, line);
                 */
            }
        }else if(progress[0] == CLOSED){
            connectionClosed(session);
            sensorAvailable = false;
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        if(menu.size() == 0){
            menu.add(0, MENU_VIEW_GRAPH, Menu.NONE, "Set Marker");
            infomenu = menu.addSubMenu("Information");
            infomenu.add(0, MENU_GET_TIME, Menu.NONE, "Get Sensor Time");
            infomenu.add(0, MENU_GET_BATTERY, Menu.NONE, "Get Battery Status");
            
            settingsmenu = menu.addSubMenu("Settings");
            settingsmenu.add(0, MENU_SET_TIME, Menu.NONE, "Set Sensor Time"); 
            settingsmenu.add(0, MENU_SET_ID, Menu.NONE, "View/Set Device ID");
            settingsmenu.add(0, MENU_SET_MODE, Menu.NONE, "Set Sensor Mode");
            menu.add(0, MENU_CONFIG_ID, Menu.NONE, "View/Set Participant ID");
            menu.add(0, MENU_SESSION_ID, Menu.NONE, "View/Set Session ID");
        }
        
        menu.findItem(MENU_VIEW_GRAPH).setTitle("Set Marker");
        if(sensorAvailable){
            infomenu.findItem(MENU_GET_TIME).setVisible(true);
            infomenu.findItem(MENU_GET_BATTERY).setVisible(true);
            settingsmenu.findItem(MENU_SET_TIME).setVisible(true);
            settingsmenu.findItem(MENU_SET_ID).setVisible(true);
            if(ISRECORDING){
                menu.findItem(MENU_CONFIG_ID).setEnabled(false);
                menu.findItem(MENU_SESSION_ID).setEnabled(false);
            }else{
                menu.findItem(MENU_CONFIG_ID).setEnabled(true);
                menu.findItem(MENU_SESSION_ID).setEnabled(true);
            }
        }else{
            infomenu.findItem(MENU_GET_TIME).setVisible(false);
            infomenu.findItem(MENU_GET_BATTERY).setVisible(false);
            settingsmenu.findItem(MENU_SET_TIME).setVisible(false);
            settingsmenu.findItem(MENU_SET_ID).setVisible(false);
            menu.findItem(MENU_CONFIG_ID).setEnabled(false);
            menu.findItem(MENU_SESSION_ID).setEnabled(false);
        }
        return true;
    }

    void showDataView()
    {
        setContentView(mainView);
        currentView = mainView; 
    }

    void showGraphView()
    {
        if(currentView == mainView){
            if(graphView == null){
                setContentView(R.layout.activity_graphview);
                graphView = (GraphView)findViewById(R.id.graphview);
            }
            setContentView(graphView);
            currentView = graphView;
            if(selected.getSelectedItem() != null){
                bluetoothCommSession sess = (bluetoothCommSession)selected.getSelectedItem();
                if(sess.version == 2){
                    graphView.setDisableAccelConversion(true);
                    if(DATA_TO_PLOT == PLOT_EDA){
                        graphView.setupPlot(PLOT_EDA, "EDA", graphView.getGraphVerticalLabels(PLOT_EDA));
                    }else if(DATA_TO_PLOT == PLOT_TEMPERATURE){
                        graphView.setupPlot(PLOT_TEMPERATURE, "Temperature", graphView.getGraphVerticalLabels(PLOT_TEMPERATURE));
                    }else if(DATA_TO_PLOT == PLOT_ACCELERATION){
                        graphView.setupPlot(PLOT_ACCELERATION, "Acceleration", graphView.getGraphVerticalLabels(PLOT_ACCELERATION));
                    }else if(DATA_TO_PLOT == PLOT_HEARTRATE){
                        graphView.setupPlot(PLOT_HEARTRATE, "Heart Rate", graphView.getGraphVerticalLabels(PLOT_HEARTRATE));
                    }else if(DATA_TO_PLOT == PLOT_HEARTRATE_AVERAGE){
                        graphView.setupPlot(PLOT_HEARTRATE_AVERAGE, "Avg. Heart Rate", graphView.getGraphVerticalLabels(PLOT_HEARTRATE_AVERAGE));
                    }else if(DATA_TO_PLOT == PLOT_HRV){
                        graphView.setupPlot(PLOT_HRV, "HR Variability", graphView.getGraphVerticalLabels(PLOT_HRV));
                    }
                }else{
                    graphView.setDisableAccelConversion(false);
                    graphView.setDataToPlot(DATA_TO_PLOT);
                }
            }
            graphView.clear();
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
        case MENU_VIEW_GRAPH:
            this.MARKER_ON = true;    //so that a marker is recorded in file
            return true;
        case MENU_SET_TIME:
            // need to send a time set command
            setTime();
            return true;
        case MENU_SET_MODE:
            // need to send a time set command
            setMode();
            return true;
        case MENU_GET_TIME:
            // need to send a time set command
            getTime();
            return true;
        case MENU_GET_BATTERY:
            // need to send a time set command
            getBattery();
            return true;
        case MENU_SET_ID:
            setId();
            return true;
        case MENU_CONFIG_ID:
            showConfigDialog(null);
            return true;
        case MENU_SESSION_ID:
            showSessionDialog(null);
            return true;
        default:
            return false;
        }
    }
    
    void connectionClosed(bluetoothCommSession source)
    {
        bluetoothCommSession s;
        if((s = (bluetoothCommSession)selected.getSelectedItem()) != null){
            if(s.getAddress().equals(source.getAddress())){
                connect.setText("Connect");
                
                // stop the recording if this is the only one recording
                if(FILE_IS_OPEN){
                    int i, j = commSessions.size();
                    boolean none = true;
                    for(i=0;i<j;i++){
                        if(commSessions.get(i).isConnected()){
                            none = false;
                            break;
                        }
                    }
                    if(none){
                        // stop this recording session
                        this.closeFile(this.bufferredWriter);
                        ISRECORDING = false;
                    }
                }
            }
        }
    }

    void resetAllButOneView(TextView v){

        Iterator <TextView> it = textViews.iterator();
        while(it.hasNext()){
            TextView currentView = it.next();
            if (! currentView.equals(v)){
                //Log.w(TAG,"inside the if");

                currentView.setBackgroundColor(0);
                //this.mainView.invalidate();
            }
        }
    }

    public void onClick(View view)
    {
        if(view == connect){
            connectOrDisconnect();
        }else if(view == select){
            selectDevice();
        }else if(view == enable){
            enableBluetooth();
        }else if(view == togglebutton){
            //make sure phone is connected to device
            if (togglebutton.isChecked()) {
                //start recording stuff
                this.bufferredWriter = openFile();

                ISRECORDING = true;
            }else{
                //stop this recording session
                this.closeFile(this.bufferredWriter);
                ISRECORDING = false;
            }
        }else if(view == label4){
            if(sensorType == 7){
                this.DATA_TO_PLOT = PLOT_AMBIENT_HUMIDITY;
            }else{
                this.DATA_TO_PLOT = PLOT_EDA_BIAS;
            }
            dataBeingPlottedChanged = true;
            label4.setBackgroundDrawable(drawable);
            resetAllButOneView((TextView)view);

        }else if(view == label5){
            if(sensorType == 7){
                this.DATA_TO_PLOT = PLOT_ACCELERATION;
            }else{
                this.DATA_TO_PLOT = PLOT_EDA_P;
            }
            dataBeingPlottedChanged = true;
            label5.setBackgroundDrawable(drawable);
            resetAllButOneView((TextView)view);
        }else if(view == label1){
            if(sensorType == 7){
                this.DATA_TO_PLOT = PLOT_EDA;
            }else{
                this.DATA_TO_PLOT = PLOT_EDA;
            }
            dataBeingPlottedChanged = true;
            //edaView.setBackgroundResource(R.drawable.border);
            //edaView.setTextColor(19);
            label1.setBackgroundDrawable(drawable);
            resetAllButOneView((TextView)view);
        }else if(view == label2){
            if(sensorType == 7){
                this.DATA_TO_PLOT = PLOT_TEMPERATURE;
            }else{
                this.DATA_TO_PLOT = PLOT_TEMPERATURE;
            }
            dataBeingPlottedChanged = true;
            label2.setBackgroundDrawable(drawable);
            resetAllButOneView((TextView)view);
        }else if(view == label3){
            if(sensorType == 7){
                this.DATA_TO_PLOT = PLOT_AMBIENT_TEMP;
            }else{
                this.DATA_TO_PLOT = PLOT_ACCELERATION;
            }
            dataBeingPlottedChanged = true;
            label3.setBackgroundDrawable(drawable);
            resetAllButOneView((TextView)view);
        }else if(view == label9){
            if(sensorType == 0){
                this.DATA_TO_PLOT = PLOT_BATTERY;
                dataBeingPlottedChanged = true;
                label9.setBackgroundDrawable(drawable);
                resetAllButOneView((TextView)view);
            }
        }else if(view == label6){
            //Log.w(TAG, "heart rate view clicked");
            this.DATA_TO_PLOT=PLOT_HEARTRATE;
            dataBeingPlottedChanged = true;
            label6.setBackgroundDrawable(drawable);
            resetAllButOneView((TextView)view);
        }else if(view == label7){
            //Log.w(TAG, "heart rate view clicked");
            this.DATA_TO_PLOT=PLOT_HEARTRATE_AVERAGE;
            dataBeingPlottedChanged = true;
            label7.setBackgroundDrawable(drawable);
            resetAllButOneView((TextView)view);
        }else if(view == label8){
            //Log.w(TAG, "heart rate view clicked");
            this.DATA_TO_PLOT = PLOT_HRV;
            dataBeingPlottedChanged = true;
            label8.setBackgroundDrawable(drawable);
            resetAllButOneView((TextView)view);
        }
    }

    //----------Code for recording data to files-------
    /**
     * opens the file where data currently being received will be stored.
     * returns a bufferedWriter that is used to write to the file.
     */
    public BufferedWriter openFile(){
        //the file name will be equal to the current date
        java.util.Date today = new java.util.Date();

        //String FILENAME = t.toString();
        String FILENAME = formatFilename.format(new Date())+Integer.toString(this.fileNumberCounter);
        fileNumberCounter += 1;

        //first check that sd card exists and is ready
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        if (mExternalStorageAvailable){
            File parentDirectory = new File(Environment.getExternalStorageDirectory(), STORAGE_DIR);
            if(!parentDirectory.exists()) parentDirectory.mkdir();

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss");
            File f = new File(parentDirectory, sdf.format(new Date())+"-"+configId+"-"+sessionId+"." + "ashaview.csv");
            try {
                FileOutputStream fileIS = new FileOutputStream(f);
                bufferredWriter = new BufferedWriter(new OutputStreamWriter(fileIS));
                FILE_IS_OPEN = true;
                MARKER_ON = false;
            } catch (Exception e) {
                Log.w(TAG, "Couldn't open file ["+FILENAME+"]", e);
            }
        }   
        return bufferredWriter;
    }

    /**
     * close the file currently being written to.
     * Saves the size of the file to disk
     */
    public void closeFile(BufferedWriter buf)
    {
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but all we need
            //  to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }

        if (mExternalStorageAvailable){
            try {
                //first close the bufferedWriter
                buf.close();
                FILE_IS_OPEN = false;

                // toggle the button
                if (togglebutton.isChecked()) {
                    togglebutton.setChecked(false);
                }
            }catch(Exception e) {
                Log.w(TAG, "Couldn't close file:["+e.getMessage()+"]", e);
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long id)
    {
        bluetoothCommSession s = commSessions.get(position);
        if(s.isConnected()){
            connect.setText("Disconnect");
        }else if(s.isConnecting()){
            connect.setText("Connecting...");
        }else{
            connect.setText("Connect");
        }
        
        // wipe the display
        clearDisplay();
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0)
    {

    }
    
    void clearDisplay()
    {
        label4.setText("EdaBias (units)  :");
        label5.setText("EdaP (units)  :");
        label1.setText("EDA (mho)  :");
        label3.setText("Accel: ");
        label2.setText("Temp (C)  :");
        label9.setText("Battery (Volts)  :");
        label6.setText("Heart Rate (BPM)  :");  
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
        for(int i=0;i<commSessions.size();i++){
            commSessions.get(i).stop();
        }
    }
    
    void getBattery()
    {
        bluetoothCommSession session = (bluetoothCommSession)selected.getSelectedItem();
        if(!session.connected){
            Toast.makeText(this, "Please select a connected device", Toast.LENGTH_LONG).show();
            return;
        }
        getBattery(session);
    }
    
    void getBattery(bluetoothCommSession session)
    {
        StringBuilder sb = new StringBuilder(17);
        sb.append("GET 6\r");
        session.addCommand(sb.toString());
    }
    
    void getID(bluetoothCommSession session){
        if(session == null) session = (bluetoothCommSession)selected.getSelectedItem();
        if(!session.connected){
            Toast.makeText(this, "Please select a connected device", Toast.LENGTH_LONG).show();
            return;
        }
        StringBuilder sb = new StringBuilder(17);
        sb.append("GET 5\r");
        session.addCommand(sb.toString());
    }
    
    void setTime()
    {
        bluetoothCommSession session = (bluetoothCommSession)selected.getSelectedItem();
        if(!session.connected){
            Toast.makeText(this, "Please select a connected device", Toast.LENGTH_LONG).show();
            return;
        }
        StringBuilder sb = new StringBuilder(17);
        sb.append("SET 1 ");
        String v = Long.toHexString(System.currentTimeMillis() / 1000).toUpperCase();
        int i, j= 8-v.length();
        for(i=0;i<j;i++) sb.append('0');
        sb.append(v).append('\r');
        session.addCommand(sb.toString());
    }
    
    void getTime()
    {
        bluetoothCommSession session = (bluetoothCommSession)selected.getSelectedItem();
        if(!session.connected){
            Toast.makeText(this, "Please select a connected device", Toast.LENGTH_LONG).show();
            return;
        }
        session.addCommand("GET 1\r");
    }
    
    void showIdDialog(final bluetoothCommSession session, String source)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Set ID:");
        alert.setMessage("Change the serial number of the band (exactly 16 characters, 0-9 and A-F permitted):");

        // Set an EditText view to get user input 
        final EditText input = new EditText(this);
        input.setText(source);
        alert.setView(input);
        
        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();
                  
                // make sure it is a 16 digit hex string
                if(value.length() != 16){
                    Toast.makeText(Explorer.this, "Please enter a valid serial number: it must be exactly 16 characters and only use 0-9 and A-F", Toast.LENGTH_SHORT).show();
                    showIdDialog(session, value);
                    return;
                }
                
                String accepted = "0123456789ABCDEFabcdef";
                for(int i=0;i<16;i++){
                    if(accepted.indexOf(value.charAt(i)) == -1){
                        Toast.makeText(Explorer.this, "Please enter a valid serial number: it must be exactly 16 characters and only use 0-9 and A-F", Toast.LENGTH_SHORT).show();
                        showIdDialog(session, value);
                        return;
                    }
                }
                
                // actually send it
                StringBuilder sb = new StringBuilder(17);
                sb.append("SET 5 ").append(value.substring(0, 8)).append('\r');
                session.addCommand(sb.toString(), false);
                sb.delete(0, sb.length());
                sb.append("SET 5 ").append(value.substring(8)).append('\r');
                session.addCommand(sb.toString(), false);
                session.id = value;
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            // Canceled.
          }
        });

        alert.show();       
    }
    
    void setId()
    {
        final bluetoothCommSession session = (bluetoothCommSession)selected.getSelectedItem();
        if(!session.connected){
            Toast.makeText(this, "Please select a connected device", Toast.LENGTH_SHORT).show();
            return;
        }
                
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Set Device ID:");
        alert.setMessage(Html.fromHtml("Current Device ID: "+session.id + "<br>Enter new ID for sensor device. ID must be 8 hex bytes (exactly 16 characters, 0-9 and A-F are permitted):"));

        // Set an EditText view to get user input 
        final EditText input = new EditText(this);
        alert.setView(input);
                
        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton){
                String value = input.getText().toString();
                  
                // make sure it is a 16 digit hex string
                if(value.length() != 16){
                    Toast.makeText(Explorer.this, "ID must be 8 hex bytes (exactly 16 characters, 0 to 9 and A-F are permitted)", Toast.LENGTH_SHORT).show();
                    showIdDialog(session, value);
                    return;
                }
                
                String accepted = "0123456789ABCDEFabcdef";
                char chr;
                for(int i=0;i<value.length();i++){
                    chr = value.charAt(i);
                    if(accepted.indexOf(chr) == -1){
                        Toast.makeText(Explorer.this, "Please enter a valid serial number: it must be exactly 16 characters and only use 0-9 and A-F", Toast.LENGTH_SHORT).show();
                        showIdDialog(session, value);
                        return;
                    }
                }
                
                // actually send it
                StringBuilder sb = new StringBuilder(17);
                sb.append("SET 5 ").append(value.substring(0, 8)).append('\r');
                session.addCommand(sb.toString());
                sb.delete(0, sb.length());
                sb.append("SET 5 ").append(value.substring(8)).append('\r');
                session.addCommand(sb.toString());
                session.id = value;
            }
        });
        
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            // Canceled.
          }
        });

        alert.show();
    }
    
    void showConfigDialog(String id)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Set Participant ID:");
        alert.setMessage(Html.fromHtml("Current Participant ID: " + configId + 
                "<br>Enter the participant ID (at most 8 characters). " +
                "ID string can be numbers or letters:"));

        // Set an EditText view to get user input 
        final EditText input = new EditText(this);
        if(id != null) input.setText(id);
        alert.setView(input);
        //input.setKeyListener(DigitsKeyListener.getInstance("0123456789ABCDEFabcdef"));
                
        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton){
                String value = input.getText().toString().trim();
                  
                // make sure it is less than or equal to 8
                if(value.length() > 8){
                    Toast.makeText(Explorer.this, "Participant ID must be at most 8 numbers, 0 to 9 permitted)", Toast.LENGTH_SHORT).show();
                    showConfigDialog(value);
                    return;
                }
                
                /*
                String accepted = "0123456789";
                char chr;
                for(int i=0;i<value.length();i++){
                    chr = value.charAt(i);
                    if(accepted.indexOf(chr) == -1){
                        Toast.makeText(Explorer.this, "Participant ID must be at most 8 numbers, 0 to 9 permitted)", Toast.LENGTH_SHORT).show();
                        showConfigDialog(value);
                        return;
                    }
                }
                */
                
                configId = value;               
            }
        });
        
        alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
              // Canceled.
              //showSessionDialog(null);
          }
        });

        alert.show();
    }
    
    void showSessionDialog(String id)
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Set Session ID:");
        alert.setMessage(Html.fromHtml("Current Session ID: "+sessionId+"<br>Enter the session ID for this recording (at most 3 numbers, 0-9 permitted):"));

        // Set an EditText view to get user input 
        final EditText input = new EditText(this);
        if(id != null) input.setText(id);
        input.setKeyListener(DigitsKeyListener.getInstance("0123456789ABCDEFabcdef"));
        alert.setView(input);
                
        alert.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton){
                String value = input.getText().toString().trim();
                  
                // make sure it is a 16 digit hex string
                if(value.length() > 3){
                    Toast.makeText(Explorer.this, "Session ID must be at most 3 numbers, 0 to 9 permitted", Toast.LENGTH_SHORT).show();
                    showSessionDialog(value);
                    return;
                }
                
                String accepted = "0123456789";
                char chr;
                for(int i=0;i<value.length();i++){
                    chr = value.charAt(i);
                    if(accepted.indexOf(chr) == -1){
                        Toast.makeText(Explorer.this, "Session ID must be at most 3 numbers, 0 to 9 permitted", Toast.LENGTH_SHORT).show();
                        showSessionDialog(value);
                        return;
                    }
                }
                
                sessionId = value;          
            }
        });
        
        alert.setNegativeButton("Exit", new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int whichButton) {
            // Canceled.
          }
        });

        alert.show();
    }
    
    void showAllTextViewsExcept(TextView... v)
    {
        Iterator<TextView> it = textViews.iterator();
        int i, l = 0;
        if(v != null) l = v.length;
        TextView currentView;
        mainloop:
        while (it.hasNext()) {
            currentView = it.next();
            if(l != 0){
                for(i=0;i<l;i++){
                    if(currentView.equals(v[i])) continue mainloop;
                }
            }
            currentView.setVisibility(View.VISIBLE);
        }
    }
    
    void hideAllTextViewsExcept(TextView... v)
    {
        Iterator<TextView> it = textViews.iterator();
        int i, l = 0;
        if(v != null) l = v.length;
        TextView currentView;
        mainloop:
        while (it.hasNext()) {
            currentView = it.next();
            if(l != 0){
                for(i=0;i<l;i++){
                    if(currentView.equals(v[i])){
                        currentView.setVisibility(View.VISIBLE);
                        continue mainloop;
                    }
                }
            }
            currentView.setVisibility(View.GONE);
        }
    }
    
    void setMode()
    {
        bluetoothCommSession session = (bluetoothCommSession)selected.getSelectedItem();
        if(!session.connected){
            Toast.makeText(this, "Please select a connected device", Toast.LENGTH_SHORT).show();
            return;
        }
        showDialog(DIALOG_USE_MODE);
    }
    
    void selectMode(int option)
    {
        bluetoothCommSession session = (bluetoothCommSession)selected.getSelectedItem();
        if(!session.connected){
            Toast.makeText(this, "Please select a connected device", Toast.LENGTH_SHORT).show();
            return;
        }
        
        /*
        if(option == 0){
            session.addCommand("SET 8 2\r");
        }else if(option == 1){
            session.addCommand("SET 8 3\r");
        }else if(option == 2){
            session.addCommand("SET 8 5\r");
        }
        */
        session.addCommand("SET 8 "+String.valueOf(option)+"\r");
    }
    private static final int DIALOG_USE_MODE = 1;
    
    @Override
    protected Dialog onCreateDialog(int id)
    {
        switch(id)
        {
        case DIALOG_USE_MODE:
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            return builder.setTitle("Choose Data Format:").
            setCancelable(true).setItems(modes, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int item){
                    dialog.dismiss();
                    selectMode(item);
                }
            }).create();
        }
        return null;
    }
    
    void setIdAvailable(boolean idAvailable)
    {
        idset = idAvailable;
        togglebutton.setEnabled(idset);
    }
}

