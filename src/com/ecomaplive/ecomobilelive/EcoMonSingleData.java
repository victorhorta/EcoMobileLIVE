package com.ecomaplive.ecomobilelive;

import java.sql.Timestamp;

import android.location.Location;

/** This class comprehends a single line of a parsed file.
 * @author Victor
 *
 */
public class EcoMonSingleData {
    //Timestamp
    private Timestamp time;
    private boolean hasGPS;
    //Latitude and Longitude are stored here -- if not available, lat and long are 0
    Location location;
    // Other values
    private int accelX, accelY, accelZ, photocellIR, photocellBlue, iAQPred, iAQRes, sO2We,sO2Aux, o3We, o3Aux;
    
    private float batteryV, temperature, humidity;
    
    public EcoMonSingleData(String providerName, long theTime, String latitude, String longitude, float batteryV, int accelX,
            int accelY, int accelZ, float temperature, float humidity, int photocellIR,
            int photocellBlue, int iAQPred, int iAQRes, int sO2We, int sO2Aux, int o3We, int o3Aux) {
        
        this.time = new Timestamp(theTime);
        
        this.location = new Location(providerName);
        this.hasGPS = !(latitude.equals(EcoMonParsedCSV.NO_GPS) || longitude.equals(EcoMonParsedCSV.NO_GPS));
        
        if(hasGPS){
            this.location.setLatitude(Double.parseDouble(latitude));
            this.location.setLongitude(Double.parseDouble(longitude));            
        }
        
        this.batteryV = batteryV;
        this.accelX = accelX;
        this.accelY = accelY;
        this.accelZ = accelZ;
        this.temperature = temperature;
        this.humidity = humidity;
        this.photocellIR = photocellIR;
        this.photocellBlue = photocellBlue;
        this.iAQPred = iAQPred;
        this.iAQRes = iAQRes;
        this.sO2We = sO2We;
        this.sO2Aux = sO2Aux;
        this.o3We = o3We;
        this.o3Aux = o3Aux;
    }

    public Timestamp getTime() {
        return time;
    }

    public float getBatteryV() {
        return batteryV;
    }

    public int getAccelX() {
        return accelX;
    }

    public Location getLocation() {
        return location;
    }

    public int getAccelY() {
        return accelY;
    }

    public int getAccelZ() {
        return accelZ;
    }

    public float getTemperature() {
        return temperature;
    }

    public float getHumidity() {
        return humidity;
    }

    public int getPhotocellIR() {
        return photocellIR;
    }

    public int getPhotocellBlue() {
        return photocellBlue;
    }

    public int getiAQPred() {
        return iAQPred;
    }

    public int getiAQRes() {
        return iAQRes;
    }

    public int getsO2We() {
        return sO2We;
    }

    public int getsO2Aux() {
        return sO2Aux;
    }

    public int getO3We() {
        return o3We;
    }

    public int getO3Aux() {
        return o3Aux;
    }

}
