package com.ecomaplive.ecomobilelive.csvconfig;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.util.Log;
import au.com.bytecode.opencsv.CSVReader;

// Serializable interface allows us to pass an instance of an EcoCSVObject to another Activity
@SuppressWarnings("serial")
public class EcoCSVObjectOLD implements Serializable {
    public final static String TAG = "EcoCSVObject";

    static String NO_GPS = new String("!!!NO-GPS!!!");

    String filePath;
    String sensorFirmwareVersion;
    // headerData are columns headers from the csv (e.g.: Latitude,Longitude)
    // headerLabels are the final header names to be shown to the user (e.g.: Positioning)
    public String headerData[];
    public ArrayList<String> headerLabels;
    public Map<String, Integer> headerLabelsMap;
    private int sensorType;
    String sensorName;
    // These booleans indicate what kind of data our csv file has.
    //    They are initialized on the constructor, and aren't changed later.
    //    They will be used to build the headerDataLabels
    private boolean hasGPS, hasBattery, hasAccel, hasTemp, hasHumidity, hasIAQ, hasGas1, hasGas2;
    private Timestamp firstTime;
    // private CSVReader reader;

    public Map<String, Integer> headerMap;

    EcoCSVObjectOLD(String pathOfCSVFileToBeParsed) {
        this.filePath = pathOfCSVFileToBeParsed;

        try {
            CSVReader reader = new CSVReader(new FileReader(pathOfCSVFileToBeParsed));

            // nextLine[] is an array of values from the line
            String[] nextLine;

            // 1st line: sensor
            nextLine = reader.readNext();
            //Log.d(TAG, nextLine[0].substring(0, nextLine[0].indexOf(' ')) + "<<<");
            sensorName = new String(nextLine[0].substring(0, nextLine[0].indexOf(' ')));
            //sensorType = aSensorTypes.get(sensorName);
            sensorFirmwareVersion = new String(nextLine[0].substring(nextLine[0].lastIndexOf(' ')+1));
            
            // 2nd line: header data
            nextLine = reader.readNext();
            headerData = new String[nextLine.length];
            for (int i = 0; i < nextLine.length; i++) {
                // this handles the space that comes on the header
                headerData[i] = new String(nextLine[i].charAt(0) == ' '? nextLine[i].substring(1) : nextLine[i]);
            }
            //System.arraycopy(nextLine, 0, headerData, 0, nextLine.length);
            
            headerMap = new HashMap<String, Integer>();
            for (int i = 0; i < headerData.length; ++i) {
                //Integer valueOf = Integer.valueOf(i);
                headerMap.put(headerData[i].toString(), i);
                //headerMap.put(new String(headerData[i].replaceAll("\\s+", "")), valueOf);
                //System.out.println(headerMap.get(new String(headerData[i])));
            }
            
            Iterator<String> keySetIterator = headerMap.keySet().iterator();

            while(keySetIterator.hasNext()){
              String key = keySetIterator.next();
              System.out.println("key: '" + key + "' value: '" + headerMap.get(key)+ "'");
            }
            
            // 3rd line: general information
            nextLine = reader.readNext();
            //boolean isempty = headerMap.isEmpty();
            
            // checking if there's GPS data available            
            //String gpsLat = new String(nextLine[headerMap.get(new String("Latitude"))]);
            //String gpsLong = new String(nextLine[headerMap.get(new String("Longitude"))]);
            

            hasAccel = headerMap.containsKey("AccelX") && headerMap.containsKey("AccelY")
                    && headerMap.containsKey("AccelZ");
            hasBattery = headerMap.containsKey("BatteryV");
            hasGas1 = headerMap.containsKey("Gas1We") && headerMap.containsKey("Gas1Aux");
            hasGas2 = headerMap.containsKey("Gas2We") && headerMap.containsKey("Gas2Aux");
            hasHumidity = headerMap.containsKey("Humidity");
            hasIAQ = headerMap.containsKey("IAQPred") && headerMap.containsKey("IAQRes");
            hasTemp = headerMap.containsKey("Temperature");
            
            // getting the first Timestamp available
            firstTime = new Timestamp(Long.parseLong(nextLine[headerMap.get("Time")]));
            
            
            // Creating the readable field labels to be shown on the ListView
            headerLabels = new ArrayList<String>();
            headerLabelsMap = new HashMap<String, Integer>();
            if (hasAccel) {
                headerLabels.add("Accelerometer");
                headerLabelsMap.put("Accelerometer", 0);
            }
            if (hasBattery) {
                headerLabels.add("Battery levels");
                headerLabelsMap.put("Battery levels", 1);
            }
            if (hasGas1) {
                headerLabels.add("Gas #1 levels");
                headerLabelsMap.put("Gas #1 levels", 2);
            }
            if (hasGas2) {
                headerLabels.add("Gas #2 levels");
                headerLabelsMap.put("Gas #2 levels", 3);
            }
            if (hasHumidity) {
                headerLabels.add("Humidity");
                headerLabelsMap.put("Humidity", 4);
            }
            if (hasIAQ) {
                headerLabels.add("IAQ");
                headerLabelsMap.put("IAQ", 5);
            }
            if (hasTemp) {
                headerLabels.add("Temperature");
                headerLabelsMap.put("Temperature", 6);
            }
           
            
            hasGPS = false;
            // We will check here if the data has at least one line containing GPS data.
            while ((nextLine = reader.readNext()) != null) {
                hasGPS = !(nextLine[headerMap.get("Latitude")].equals(NO_GPS) || nextLine[headerMap.get("Longitude")].equals(NO_GPS));
                
                if(hasGPS)
                    break;
            }
            
            // Closing the reader
            reader.close();
        } catch (RuntimeException e) {
            Log.d(TAG, "RuntimeException!");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "FileNotFound!");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "IOException!");
        }

    }

    ArrayList<String> getParticularDataListAsStrings(String dataName, boolean onlyDataWithGPS) {
        ArrayList<String> dataRetrievedFromCSV = new ArrayList<String>();
        if (headerMap.containsKey(dataName)) {
            try {
                CSVReader reader = new CSVReader(new FileReader(filePath));

                // nextLine[] is an array of values from the line
                String[] nextLine;
                
                //TODO: ADAPT FOR THE NEWER HEADER (THE FIRST LINE WILL INDICATE THE NUMBER OF HEADER LINES)
                // Skip 1st and 2nd lines
                reader.readNext();
                reader.readNext();
                
                String headerLat = "Latitude";
                String headerLon = "Longitude";
                
                
                while ((nextLine = reader.readNext()) != null) {
                    if(onlyDataWithGPS) 
                        if(nextLine[headerMap.get(headerLat)].equals(NO_GPS) || nextLine[headerMap.get(headerLon)].equals(NO_GPS))
                            continue;
                    dataRetrievedFromCSV.add(new String(nextLine[headerMap.get(dataName)]));                        
                }

                // Closing the reader
                reader.close();
                
            } catch (RuntimeException e) {
                Log.d(TAG, "RuntimeException!");
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d(TAG, "FileNotFound!");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.d(TAG, "IOException!");
            }
        }
        return dataRetrievedFromCSV;
    }
    
    
    // numberOfTheHeaderLabelIndex --> String that matters --> switch case treating each case
    public ArrayList<ArrayList<String>> getXYandLabelsfromData(int numberOfTheHeaderLabelIndex) { 
        // position zero of the array: x values
        // position one of the array: y values
        // position two of the array: labels (title, x_axis_label, y_axis_label)
        ArrayList<ArrayList<String>> overallList = new ArrayList<ArrayList<String>>();
        ArrayList<String> xValues;
        ArrayList<String> yValues = new ArrayList<String>();;
        ArrayList<String> labels = new ArrayList<String>();

        String parameterWanted = headerLabels.get(numberOfTheHeaderLabelIndex);

        // Adding the title
        labels.add(parameterWanted);

        // Adding the x_axis
        labels.add("Time");

        // Adding time values! they will be our x axis!
        xValues = getParticularDataListAsStrings("Time", false);
        
        // There are specific operations which need to be made in order to properly calculate each value to be plotted!
        switch (headerLabelsMap.get(parameterWanted)) {
        case 0:// case "Displacement".hashCode():
            ArrayList<String> aX = getParticularDataListAsStrings("AccelX", false);
            ArrayList<String> aY = getParticularDataListAsStrings("AccelY", false);
            ArrayList<String> aZ = getParticularDataListAsStrings("AccelZ", false);
            for(int i = 0; i < aX.size(); i++) {
                double aXd = Double.parseDouble(aX.get(i)); 
                double aYd = Double.parseDouble(aY.get(i));
                double aZd = Double.parseDouble(aZ.get(i));
                yValues.add(Double.toString(Math.sqrt(aXd*aXd + aYd*aYd + aZd*aZd)));                
            }
            
            labels.add("m");
            break;
        case 1:// case "Battery levels".hashCode():
            yValues = getParticularDataListAsStrings("BatteryV", false);
            labels.add("V");
            break;
        case 2:// case "Gas #1 levels".hashCode():
            //TODO: fix Gas calculation!
            yValues = getParticularDataListAsStrings("Gas1We", false);
            labels.add("ppb");
            break;
        case 3:// case "Gas #2 levels".hashCode():
            //TODO: fix Gas calculation!
            yValues = getParticularDataListAsStrings("Gas2We", false);
            labels.add("ppb");
            break;
        case 4:// "Humidity".hashCode():
            yValues = getParticularDataListAsStrings("Humidity", false);
            labels.add("V");
            break;
        case 5:// "IAQ".hashCode():
            yValues = getParticularDataListAsStrings("IAQPred", false);
            labels.add("p");
            break;
        case 6:// "Temperature".hashCode():
            yValues = getParticularDataListAsStrings("Temperature", false);
            labels.add("C");
            break;
        }
        overallList.add(xValues);
        overallList.add(yValues);
        overallList.add(labels);
        
        return overallList;
    }

    //TODO: method getInfoPointsToBeMapped(numberOfTheHeaderLabelIndex){
    // numberOfTheHeaderLabelIndex --> String that matters --> switch case treating each case
    public ArrayList<ArrayList<String>> getInfoPointsToBeMapped(int numberOfTheHeaderLabelIndex) { 
        // position 0 of the array: Lat values
        // position 1 of the array: Long values
        // position 2 of the array: data value
        // position 3 of the array: timestamp
        // position 4 of the array: labels (parameterWanted, unit)
        
        ArrayList<ArrayList<String>> overallList = new ArrayList<ArrayList<String>>();
        ArrayList<String> latValues;
        ArrayList<String> lonValues;
        ArrayList<String> dataValues;
        ArrayList<String> timeValues;
        ArrayList<String> labels = new ArrayList<String>();

        String parameterWanted = headerLabels.get(numberOfTheHeaderLabelIndex);

        // Adding the title
        labels.add(parameterWanted);

        // Adding time values! they will be our x axis!
        latValues = getParticularDataListAsStrings("Latitude", true);
        lonValues = getParticularDataListAsStrings("Longitude", true);
        dataValues = getParticularDataListAsStrings(parameterWanted, true);
        timeValues = getParticularDataListAsStrings("Time", true);
        
        // Here, we are getting the unit of the data measured.
        switch (headerLabelsMap.get(parameterWanted)) {
        case 0:// case "Displacement".hashCode():
            dataValues.clear();
            ArrayList<String> aX = getParticularDataListAsStrings("AccelX", false);
            ArrayList<String> aY = getParticularDataListAsStrings("AccelY", false);
            ArrayList<String> aZ = getParticularDataListAsStrings("AccelZ", false);
            for(int i = 0; i < aX.size(); i++) {
                double aXd = Double.parseDouble(aX.get(i)); 
                double aYd = Double.parseDouble(aY.get(i));
                double aZd = Double.parseDouble(aZ.get(i));
                dataValues.add(Double.toString(Math.sqrt(aXd*aXd + aYd*aYd + aZd*aZd)));
            }
            labels.add("m");
            break;
        case 1:// case "Battery levels".hashCode():
            //yValues = getParticularDataListAsStrings("BatteryV");
            labels.add("V");
            break;
        case 2:// case "Gas #1 levels".hashCode():
            labels.add("ppm");
            break;
        case 3:// case "Gas #2 levels".hashCode():
            labels.add("ppm");
            break;
        case 4:// "Humidity".hashCode():
            //yValues = getParticularDataListAsStrings("Humidity");
            labels.add("%");
            break;
        case 5:// "IAQ".hashCode():
            labels.add("p");
            break;
        case 6:// "Temperature".hashCode():
            //yValues = getParticularDataListAsStrings("Temperature");
            labels.add("C");
            break;
        }
        overallList.add(latValues);
        overallList.add(lonValues);
        overallList.add(dataValues);
        overallList.add(timeValues);

        overallList.add(labels);
        
        return overallList;
    }


    
    public int getSensorType() {
        return sensorType;
    }
    
    public String getSensorName() {
        return sensorName;
    }

    public boolean hasGPS() {
        return hasGPS;
    }

    public Timestamp getFirstTime() {
        return firstTime;
    }

    public String[] getHeaderData() {
        return headerData;
    }

    public boolean hasBattery() {
        return hasBattery;
    }

    public boolean hasAccel() {
        return hasAccel;
    }

    public boolean hasTemp() {
        return hasTemp;
    }

    public boolean hasHumidity() {
        return hasHumidity;
    }

    public boolean hasIAQ() {
        return hasIAQ;
    }

    public boolean hasGas1() {
        return hasGas1;
    }

    public boolean hasGas2() {
        return hasGas2;
    }
}
