package com.ecomaplive.ecomobilelive.btmanager;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GeoService extends Service implements LocationListener {
    private final static String TAG = "GeoService";
    private final static boolean DEBUG = true;
    
    private LocationManager locationManager;
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    
    
    // Do not call this method directly!
    // Performs one-time setup procedures (before it calls either onStartCommand() or onBind())
    @Override
    public void onCreate() {
     // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (DEBUG) Log.d(TAG, "onStartCommand");

        
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }
    
  //Do not call this method directly!
    // Called by the system to notify a Service that it is no longer used and is
    // being removed. The service should clean up any resources it holds
    // (threads, registered receivers, etc) at this point. Upon return, there
    // will be no more calls in to this Service object and it is effectively
    // dead.
    
    /**
     * If a component starts the service by calling startService() (which
     * results in a call to onStartCommand()), then the service remains running
     * until it stops itself with stopSelf() or another component stops it by
     * calling stopService().
     * 
     * @see android.app.Service#onDestroy()
     */
    @Override
    public void onDestroy() {
        locationManager.removeUpdates(this);
    }


    @Override
    public void onLocationChanged(Location arg0) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub
        
    }
    
    

}
