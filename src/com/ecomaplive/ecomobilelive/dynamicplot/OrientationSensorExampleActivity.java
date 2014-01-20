package com.ecomaplive.ecomobilelive.dynamicplot;

import java.text.DecimalFormat;
import java.util.Arrays;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.androidplot.Plot;
import com.androidplot.util.Redrawer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import com.ecomaplive.ecomobilelive.R;
import com.ecomaplive.ecomobilelive.btmanager.BTService;

public class OrientationSensorExampleActivity extends Activity {
    //Ideas:
    //-update the plot when loading the application
    public static final String EXTRA_DYNAMIC_UPDATE = "intent_dynamic_update";
    public static final String ACTIVATED_DYNAMIC_MONITOR = "activated_dynamic_monitor";
    public static final String ACTIVATED_DYNAMIC_YMIN = "activated_dynamic_ymin";
    public static final String ACTIVATED_DYNAMIC_YMAX = "activated_dynamic_ymax";
    
    int activatedMonitorIndex;
    float plotYMin;
    float plotYMax;
    
    private static final int HISTORY_SIZE = 10;       // number of points to plot in history
//    private SensorManager sensorMgr = null;
//    private Sensor orSensor = null;

    private XYPlot aprHistoryPlot = null;

//    private CheckBox hwAcceleratedCb;
//    private CheckBox showFpsCb;
    private SimpleXYSeries valuesHistorySeries = null;

    private Redrawer redrawer;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.orientation_sensor_example);
        
        // Starting the required activatedMonitorIndex
        activatedMonitorIndex = getIntent().getIntExtra(ACTIVATED_DYNAMIC_MONITOR, 0);
        plotYMax = getIntent().getFloatExtra(ACTIVATED_DYNAMIC_YMAX,  100);
        plotYMin = getIntent().getFloatExtra(ACTIVATED_DYNAMIC_YMIN, -100);

        // setup the APR History plot:
        aprHistoryPlot = (XYPlot) findViewById(R.id.aprHistoryPlot);

        valuesHistorySeries = new SimpleXYSeries("Values");
        valuesHistorySeries.useImplicitXVals();
        

        aprHistoryPlot.setRangeBoundaries(plotYMin, plotYMax, BoundaryMode.FIXED);
        aprHistoryPlot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
        aprHistoryPlot.addSeries(valuesHistorySeries,
                new LineAndPointFormatter(
                        Color.rgb(200, 100, 100), null, null, null));
        
        aprHistoryPlot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
        aprHistoryPlot.setDomainStepValue(HISTORY_SIZE/10);
        aprHistoryPlot.setTicksPerRangeLabel(3);
        aprHistoryPlot.setDomainLabel("Time");
        aprHistoryPlot.getDomainLabelWidget().pack();
        aprHistoryPlot.setRangeLabel("Measured val");
        aprHistoryPlot.getRangeLabelWidget().pack();

        aprHistoryPlot.setRangeValueFormat(new DecimalFormat("#"));
        aprHistoryPlot.setDomainValueFormat(new DecimalFormat("#"));

        // setup checkboxes:
//        hwAcceleratedCb = (CheckBox) findViewById(R.id.hwAccelerationCb);
//        final PlotStatistics histStats = new PlotStatistics(1000, false);
//
//        aprHistoryPlot.addListener(histStats);
//        hwAcceleratedCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(b) {
//                    aprHistoryPlot.setLayerType(View.LAYER_TYPE_NONE, null);
//                } else {
//                    aprHistoryPlot.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//                }
//            }
//        });

//        showFpsCb = (CheckBox) findViewById(R.id.showFpsCb);
//        showFpsCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                histStats.setAnnotatePlotEnabled(b);
//            }
//        });

        // register for orientation sensor events:
//        sensorMgr = (SensorManager) getApplicationContext().getSystemService(Context.SENSOR_SERVICE);
//        for (Sensor sensor : sensorMgr.getSensorList(Sensor.TYPE_ORIENTATION)) {
//            if (sensor.getType() == Sensor.TYPE_ORIENTATION) {
//                orSensor = sensor;
//            }
//        }
//
//        // if we can't access the orientation sensor then exit:
//        if (orSensor == null) {
//            System.out.println("Failed to attach to orSensor.");
//            cleanup();
//        }
//
//        sensorMgr.registerListener(this, orSensor, SensorManager.SENSOR_DELAY_UI);

        redrawer = new Redrawer(
                Arrays.asList(new Plot[]{aprHistoryPlot}),
                100, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mDataArrived,
                new IntentFilter(BTService.INTENT_DATA_ARRIVED_FROM_SENSOR));
        redrawer.start();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDataArrived);
        redrawer.pause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        redrawer.finish();
        super.onDestroy();
    }

    private BroadcastReceiver mDataArrived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //update content from Collect!!
             String csvLine = (String) intent.getExtras().get(EXTRA_DYNAMIC_UPDATE);
             float value = Float.parseFloat(csvLine.split(",")[activatedMonitorIndex]);
             updateHistorySeries(value);
        }
    };
    
//    private void cleanup() {
//        // aunregister with the orientation sensor before exiting:
////        sensorMgr.unregisterListener(this);
//        finish();
//    }


    // Updates the history series
    public void updateHistorySeries(float value) {
        // get rid the oldest sample in history:
        if (valuesHistorySeries.size() > HISTORY_SIZE) {
            valuesHistorySeries.removeFirst();
        }

        // add the latest history sample:
        valuesHistorySeries.addLast(null, value);
    }
    
    // Called whenever a new orSensor reading is taken.
//    @Override
//    public synchronized void onSensorChanged(SensorEvent sensorEvent) {
//        
//        // get rid the oldest sample in history:
//        if (valuesHistorySeries.size() > HISTORY_SIZE) {
//            valuesHistorySeries.removeFirst();
//        }
//
//        // add the latest history sample:
//        valuesHistorySeries.addLast(null, sensorEvent.values[2]);
//    }


//    @Override
//    public void onAccuracyChanged(Sensor sensor, int i) {
//        // Not interested in this event
//    }
}