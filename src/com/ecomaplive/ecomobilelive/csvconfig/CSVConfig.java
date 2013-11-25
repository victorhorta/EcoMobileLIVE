package com.ecomaplive.ecomobilelive.csvconfig;

import java.util.ArrayList;
import java.util.List;

/**
 * This class sets all values for a CSV configuration pattern. This class is immutable.<br>
 * <br>
 * If you want to add different kinds of fields, you should change:
 * 1) The ConfigSetup class, creating a new CSVField
 * 2) The headerPlotData and the headerMapData (if the new data is plottable / mappable)
 * On the EcoCSVObject:
 * 3) You should also create a helper method, if there is any calculation related to that data
 * On the EcoCSVObject:
 * 4) You should add the field to the 'getData' method (and also call your helper method just in case)
 * 
 * @author Victor
 *
 */
public class CSVConfig implements VersionConfig {
    final private int versionId;
    final private String versionNumberAsText;
    final private String sensorName;
    final private List<CSVField> orderedFields;
    
    /**
     * @param versionId
     * @param sensorName
     * @param orderedFields
     *            requires all names to be unique. Also requires the versionId
     *            to be stored at the 0-position of the CSV file.
     */
    public CSVConfig(int versionId, String sensorName, List<CSVField> orderedFields){
        this.versionId = versionId;
        this.versionNumberAsText = orderedFields.get(0).getUnit();
        this.sensorName = sensorName;
        this.orderedFields = orderedFields;         
    }
    
    @Override
    public int getVersionId() {
        return versionId;
    }
    
    @Override
    public String getVersionNumberAsText() {
        return versionNumberAsText;
    }
    
    @Override
    public String getSensorName() {
        return sensorName;
    }
    
    @Override
    public int getNumberOfFields() {
        return orderedFields.size();
    }
    
    @Override
    public String getFieldLabel(int fieldIndex) {
        return orderedFields.get(fieldIndex).getLabel();
    }
    
    @Override
    public String getUnitFromFieldLabel(String fieldLabel) {
        return orderedFields.get(getFieldIndex(fieldLabel)).getUnit();
    }
    
    @Override
    public int getFieldIndex(String fieldLabel) {
        for(CSVField f: orderedFields) {
            if(f.getLabel().equals(fieldLabel))
                return orderedFields.indexOf(f);
        }
        return -1;
    }
    
    @Override
    public ArrayList<String> getHeaderColumnsLabels() {
         ArrayList<String> headerColumnsLabels = new ArrayList<String>();
         for(CSVField f: orderedFields) {
             headerColumnsLabels.add(f.getLabel());
         }
         return headerColumnsLabels;
    }
    
    
    
    /**
     * Returns an ArrayList of Strings consisting of the plottable data of the CSV.<br>
     * Excludes the Sensor and the Time fields; combines Accel coordinates into
     * a single value; combines IAQs into a single value.
     * 
     */
    public ArrayList<String> getHeaderPlotLabels() {
        ArrayList<String> headerLabels = new ArrayList<String>();

        // Acceleration:
        if (getFieldIndex("AccelX") != -1 
                && getFieldIndex("AccelY") != -1
                && getFieldIndex("AccelZ") != -1)
            headerLabels.add("Acceleration");
        
//        // GPS Positions:        
//        if (getFieldIndex("Latitude") != -1 
//                && getFieldIndex("Longitude") != -1)
//            headerLabels.add("GPS Positions");
        
        // Battery Levels:
        if (getFieldIndex("BatteryV") != -1)
            headerLabels.add("Battery Levels");
        
        // Temperature:
        if (getFieldIndex("Temperature") != -1)
            headerLabels.add("Temperature");
        
        // Humidity:
        if (getFieldIndex("Humidity") != -1)
            headerLabels.add("Humidity");
        
        // Infra-red:
        if (getFieldIndex("PhotocellIR") != -1)
            headerLabels.add("Infra-red Levels");
        
        // Red:
        if (getFieldIndex("PhotocellR") != -1)
            headerLabels.add("Red Levels");
        
        // Green:
        if (getFieldIndex("PhotocellG") != -1)
            headerLabels.add("Green Levels");
        
        // Blue:
        if (getFieldIndex("PhotocellB") != -1)
            headerLabels.add("Blue Levels");
        
        // Air Quality:
        if (getFieldIndex("IAQPred") != -1
                && getFieldIndex("IAQRes") != -1)
            headerLabels.add("Air Quality");
        
        // Gas #1 Levels:
        if (getFieldIndex("Gas1PPB") != -1)
            headerLabels.add("Gas #1");
        
        // Gas #1 Levels:
        if (getFieldIndex("Gas1We") != -1
                && getFieldIndex("Gas1Aux") != -1)
            headerLabels.add("Gas #1 precise");
        
        // Gas #2 Levels:
        if (getFieldIndex("Gas2PPB") != -1)
            headerLabels.add("Gas #2");
        
        // Gas #2 Levels:
        if (getFieldIndex("Gas2We") != -1
                && getFieldIndex("Gas2Aux") != -1)
            headerLabels.add("Gas #2 precise");
        
        return headerLabels;
    }
    
    /**
     * Returns an ArrayList of Strings consisting of the mappable data of the CSV.<br>
     * Excludes the Sensor and the Time fields; combines Accel coordinates into
     * a single value; combines IAQs into a single value.
     * 
     */
    public ArrayList<String> getHeaderMapLabels() {
        ArrayList<String> headerLabels = new ArrayList<String>();

        // We can only map if we have at least Lat and Long fields!
        if (getFieldIndex("Latitude") != -1 
                && getFieldIndex("Longitude") != -1) {

            // Acceleration:
            if (getFieldIndex("AccelX") != -1 
                    && getFieldIndex("AccelY") != -1
                    && getFieldIndex("AccelZ") != -1)
                headerLabels.add("Acceleration");            
            
            
            // Battery Levels:
            if (getFieldIndex("BatteryV") != -1)
                headerLabels.add("Battery Levels");
            
            // Temperature:
            if (getFieldIndex("Temperature") != -1)
                headerLabels.add("Temperature");
            
            // Humidity:
            if (getFieldIndex("Humidity") != -1)
                headerLabels.add("Humidity");
            
            // Infra-red:
            if (getFieldIndex("PhotocellIR") != -1)
                headerLabels.add("Infra-red Levels");
            
            // Red:
            if (getFieldIndex("PhotocellR") != -1)
                headerLabels.add("Red Levels");
            
            // Green:
            if (getFieldIndex("PhotocellG") != -1)
                headerLabels.add("Green Levels");
            
            // Blue:
            if (getFieldIndex("PhotocellB") != -1)
                headerLabels.add("Blue Levels");
            
            // Air Quality:
            if (getFieldIndex("IAQPred") != -1
                    && getFieldIndex("IAQRes") != -1)
                headerLabels.add("Air Quality");
            
            // Gas #1 Levels:
            if (getFieldIndex("Gas1PPB") != -1)
                headerLabels.add("Gas #1");
            
            // Gas #1 Levels:
            if (getFieldIndex("Gas1We") != -1
                    && getFieldIndex("Gas1Aux") != -1)
                headerLabels.add("Gas #1 precise");
            
            // Gas #2 Levels:
            if (getFieldIndex("Gas2PPB") != -1)
                headerLabels.add("Gas #2");
            
            // Gas #2 Levels:
            if (getFieldIndex("Gas2We") != -1
                    && getFieldIndex("Gas2Aux") != -1)
                headerLabels.add("Gas #2 precise");
        }
        return headerLabels;
    }    
}
