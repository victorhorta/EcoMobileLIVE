package com.ecomaplive.ecomobilelive.csvconfig;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import android.util.Log;
import au.com.bytecode.opencsv.CSVReader;

/**
 * This class corresponds to a parsed CSV file.
 * 
 * @author Victor
 *
 */
// Serializable interface allows us to pass an instance of an EcoCSVObject to another Activity
@SuppressWarnings("serial")
public class EcoCSVObject  implements Serializable {
    final static private String TAG = "EcoCSVObject";
    static String NO_GPS = new String("!!!NO-GPS!!!");
    
    private boolean hasGPS;
    
    private String filePath;
    private CSVConfig fileConfig;
    private Timestamp firstTime;
    
    private ArrayList<String> headerPlotLabels;
    private ArrayList<String> headerMapLabels;
    private ArrayList<String> headerColumnLabels;
    
    public EcoCSVObject(String pathOfCSVFileToBeParsed) {
        this.filePath = pathOfCSVFileToBeParsed;

        try {
            CSVReader reader = new CSVReader(new FileReader(pathOfCSVFileToBeParsed));

            // nextLine[] is an array of values from the line
            String[] nextLine;
            nextLine = reader.readNext();
            
            // 1st Field: sensor
            fileConfig = ConfigSetup.getConfigFromId(Integer.parseInt(nextLine[0]));
                       
            // getting the first Timestamp available
            firstTime = new Timestamp(Long.parseLong(nextLine[fileConfig.getFieldIndex("Time")]));
            
            // getting the available Plot Labels, Map Labels and Column Labels
            headerPlotLabels = fileConfig.getHeaderPlotLabels();
            headerMapLabels = fileConfig.getHeaderMapLabels();
            headerColumnLabels = fileConfig.getHeaderColumnsLabels();
            
            // We will check here if the data has at least one line containing GPS data.
            if (fileConfig.getFieldIndex("Latitude") != -1
                    && fileConfig.getFieldIndex("Longitude") != -1) {
                while ((nextLine = reader.readNext()) != null) {
                    hasGPS = !(nextLine[fileConfig.getFieldIndex("Latitude")].equals(NO_GPS) || nextLine[fileConfig
                            .getFieldIndex("Longitude")].equals(NO_GPS));
                    
                    if (hasGPS)
                        break;
                }
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
    
    
    /**
     * @param dataNameOnCsv
     *            The respective CSV column label.
     * @param onlyDataWithGPS
     *            boolean that indicates to retrieve only data containing valid
     *            GPS information (those containing 'NO_GPS' are discarded).
     * @return a list of Strings with the desired RAW data.
     */
    ArrayList<String> getRawData(String dataNameOnCsv, boolean onlyDataWithGPS) {
        ArrayList<String> dataRetrievedFromCSV = new ArrayList<String>();
        if (headerColumnLabels.contains(dataNameOnCsv)) {
            try {
                CSVReader reader = new CSVReader(new FileReader(filePath));

                // nextLine[] is an array of values from the line
                String[] nextLine;
                
                String headerLat = "Latitude";
                String headerLon = "Longitude";
                
                while ((nextLine = reader.readNext()) != null) {
                    if(onlyDataWithGPS) 
                        if(nextLine[fileConfig.getFieldIndex(headerLat)].equals(NO_GPS) || nextLine[fileConfig.getFieldIndex(headerLon)].equals(NO_GPS))
                            continue;
                    
                    //TODO: CALL HELPER METHODS HERE TO GET THE DATA ACCORDING TO THE CORRESPONDING FUNCTION!
                    // if dataName equals "Gas1..." add ...
                    // else if dataName equals "VOC..." add ...
                    dataRetrievedFromCSV.add(new String(nextLine[fileConfig.getFieldIndex(dataNameOnCsv)]));                        
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
    
    
    /**
     * @param dataNameOnPlotHeader
     *            The respective CSV Plot label.
     * @param onlyDataWithGPS
     *            boolean that indicates to retrieve only data containing valid
     *            GPS information (those containing 'NO_GPS' are discarded).
     * @return a list of Strings with the desired RAW data.
     */
    ArrayList<String> getProcessedData(String dataNameOnPlotHeader, boolean onlyDataWithGPS) {
        ArrayList<String> dataRetrievedFromCSV = new ArrayList<String>();
        if (headerPlotLabels.contains(dataNameOnPlotHeader)) {
            try {
                CSVReader reader = new CSVReader(new FileReader(filePath));

                // nextLine[] is an array of values from the line
                String[] nextLine;
                
                String headerLat = "Latitude";
                String headerLon = "Longitude";
                
                while ((nextLine = reader.readNext()) != null) {
                    if(onlyDataWithGPS) 
                        if(nextLine[fileConfig.getFieldIndex(headerLat)].equals(NO_GPS) || nextLine[fileConfig.getFieldIndex(headerLon)].equals(NO_GPS))
                            continue;
                    
                    dataRetrievedFromCSV.add(getSingleProcessedData(dataNameOnPlotHeader, nextLine));                        
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
    
    
    /**
     * Prepares data to be plotted.
     * 
     * @param headerPlotLabelIndex
     *            the index of the selected header plot.
     * @return an array of arrays containing all data that is needed.
     */
    public ArrayList<ArrayList<String>> getXYandLabelsfromData(int headerPlotLabelIndex) { 
        // position zero of the array: x values
        // position one of the array: y values
        // position two of the array: labels (title, x_axis_label, y_axis_label)
        ArrayList<ArrayList<String>> overallList = new ArrayList<ArrayList<String>>();
        ArrayList<String> xValues;
        ArrayList<String> yValues = new ArrayList<String>();
        ArrayList<String> labels = new ArrayList<String>();

        ////String parameterWanted = headerLabels.get(numberOfTheHeaderLabelIndex);
        String parameterWanted = headerPlotLabels.get(headerPlotLabelIndex);

        // Adding the title
        labels.add(parameterWanted);

        // Adding the x_axis
        labels.add("Time");

        // Adding time values! they will be our x axis!
        xValues = getRawData("Time", false);
        
        yValues = getProcessedData(parameterWanted, false);
        labels.add(getUnitFromHeaderLabel(parameterWanted));

        overallList.add(xValues);
        overallList.add(yValues);
        overallList.add(labels);
        
        return overallList;
    }
    
    
    /**
     * Prepares data to be mapped.
     * 
     * @param numberOfTheHeaderLabelIndex
     * @return
     */
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

        String parameterWanted = headerMapLabels.get(numberOfTheHeaderLabelIndex);

        // Adding the title
        labels.add(parameterWanted);

        // Adding time values! they will be our x axis!
        latValues = getRawData("Latitude", true);
        lonValues = getRawData("Longitude", true);
        timeValues = getRawData("Time", true);
        dataValues = getProcessedData(parameterWanted, true);
        
        labels.add(getUnitFromHeaderLabel(parameterWanted));

        overallList.add(latValues);
        overallList.add(lonValues);
        overallList.add(dataValues);
        overallList.add(timeValues);

        overallList.add(labels);
        
        return overallList;
    }
    
    
    /**
     * Given a header label (not the column label, but the meaningful plot
     * name), returns the desired data by using the helper methods described in
     * this class.
     * 
     * @param headerLabel
     *            the correspondent header label
     * @return the desired data as a String, or the String "0" if the
     *         headerLabel does not matches.
     */
    public String getSingleProcessedData(String headerLabel, String[] nextline) {
        // First, we need to check if the headerLabel exists for this model.
        if(headerPlotLabels.contains(headerLabel)){
            // Then, we need to check if it needs a helper method.
            // Otherwise, just return the correspondent data.
            
            // Acceleration:
            if(headerLabel.equals("Acceleration"))
                return calculateAcceleration(nextline[fileConfig.getFieldIndex("AccelX")],
                        nextline[fileConfig.getFieldIndex("AccelY")],
                        nextline[fileConfig.getFieldIndex("AccelZ")]);
            
            // Battery Levels:
            if(headerLabel.equals("Battery Levels"))
                return nextline[fileConfig.getFieldIndex("BatteryV")];
            
            // Temperature:
            if(headerLabel.equals("Temperature"))
                return nextline[fileConfig.getFieldIndex("Temperature")];
                        
            // Humidity:
            if(headerLabel.equals("Humidity"))
                return nextline[fileConfig.getFieldIndex("Humidity")];
            
            // Infra-red:
            if(headerLabel.equals("Infra-red Levels"))
                return nextline[fileConfig.getFieldIndex("PhotocellIR")];
            
            // Red:
            if(headerLabel.equals("Red Levels"))
                return nextline[fileConfig.getFieldIndex("PhotocellR")];

            // Green:
            if(headerLabel.equals("Green Levels"))
                return nextline[fileConfig.getFieldIndex("PhotocellG")];

            // Blue:
            if(headerLabel.equals("Blue Levels"))
                return nextline[fileConfig.getFieldIndex("PhotocellB")];
            
            // Air Quality:
            if (headerLabel.equals("VOC levels"))
                return calculateAirQuality(nextline[fileConfig.getFieldIndex("VOCPred")],
                        nextline[fileConfig.getFieldIndex("VOCRes")]);
            
            // Gas #1:
            if (headerLabel.equals("Gas #1"))
                return nextline[fileConfig.getFieldIndex("Gas1PPB")];
            
            // Gas #1 precise:
            if (headerLabel.equals("Gas #1 precise"))
                return calculateGasLevel(nextline[fileConfig.getFieldIndex("Gas1We")],
                        nextline[fileConfig.getFieldIndex("Gas1Aux")]);
            
            // Gas #2:
            if (headerLabel.equals("Gas #2"))
                return nextline[fileConfig.getFieldIndex("Gas2PPB")];
            
            // Gas #1 precise:
            if (headerLabel.equals("Gas #2 precise"))
                return calculateGasLevel(nextline[fileConfig.getFieldIndex("Gas2We")],
                        nextline[fileConfig.getFieldIndex("Gas2Aux")]);

            Log.d(TAG, "Unrecognized header label on getSingleProcessedData!");
            return "0";
        } else {
            Log.d(TAG, "Unrecognized header label on getSingleProcessedData!");
            return "0";
        }
        
    }
    
    
    // HELPER METHODS ------------------------------------------------------
    
    public String getUnitFromHeaderLabel(String headerLabel) {
        if(headerPlotLabels.contains(headerLabel)){
            // Then, we need to check if it needs a helper method.
            // Otherwise, just return the correspondent data.
            
            // Acceleration:
            if(headerLabel.equals("Acceleration"))
                return fileConfig.getUnitFromFieldLabel("AccelX");
            
            // Battery Levels:
            if(headerLabel.equals("Battery Levels"))
                return fileConfig.getUnitFromFieldLabel("BatteryV");
            
            // Temperature:
            if(headerLabel.equals("Temperature"))
                return fileConfig.getUnitFromFieldLabel("Temperature");
                        
            // Humidity:
            if(headerLabel.equals("Humidity"))
                return fileConfig.getUnitFromFieldLabel("Humidity");
            
            // Infra-red:
            if(headerLabel.equals("Infra-red Levels"))
                return fileConfig.getUnitFromFieldLabel("PhotocellIR");
            
            // Red:
            if(headerLabel.equals("Red Levels"))
                return fileConfig.getUnitFromFieldLabel("PhotocellR");

            // Green:
            if(headerLabel.equals("Green Levels"))
                return fileConfig.getUnitFromFieldLabel("PhotocellG");

            // Blue:
            if(headerLabel.equals("Blue Levels"))
                return fileConfig.getUnitFromFieldLabel("PhotocellB");
            
            // Air Quality:
            if (headerLabel.equals("Air Quality"))
                return fileConfig.getUnitFromFieldLabel("VOCPred");
                        
            // Gas #1:
            if (headerLabel.equals("Gas #1"))
                return fileConfig.getUnitFromFieldLabel("Gas1PPB");
            
            // Gas #1 precise:
            if (headerLabel.equals("Gas #1 precise"))
                return fileConfig.getUnitFromFieldLabel("Gas1We");
            
            // Gas #2:
            if (headerLabel.equals("Gas #2"))
                return fileConfig.getUnitFromFieldLabel("Gas2PPB");
            
            // Gas #1 precise:
            if (headerLabel.equals("Gas #2 precise"))
                return fileConfig.getUnitFromFieldLabel("Gas1We");

            Log.d(TAG, "Unrecognized header label on getUnitFromHeaderLabel!");
            return "units";
        } else {
            Log.d(TAG, "Unrecognized header label on getUnitFromHeaderLabel!");
            return "units";
        }
    }
    
    
    /**
     * Calculates the Euclidean norm of the acceleration vector.
     * 
     * @param aX
     *            String representing x-axis acceleration
     * @param aY
     *            String representing y-axis acceleration
     * @param aZ
     *            String representing z-axis acceleration
     * @return the norm of the acceleration vector
     */
    public String calculateAcceleration(String aX, String aY, String aZ) {
        double aXd = Double.parseDouble(aX); 
        double aYd = Double.parseDouble(aY);
        double aZd = Double.parseDouble(aZ);
        
        return Double.toString(Math.sqrt(aXd*aXd + aYd*aYd + aZd*aZd));
    }
    
    /**
     * Calculates the air quality using a more precise algorithm.
     * 
     * @param VOCPred
     * @param VOCRes
     * @return
     */
    public String calculateAirQuality(String VOCPred, String VOCRes) {
        //TODO: Air Quality calc!!
        return VOCPred;
    }
    
    /**
     * Calculates the gas levels.
     * 
     * @param GasWe
     * @param GasAux
     * @return
     */
    public String calculateGasLevel(String GasWe, String GasAux) {
        //TODO: Gas levels calc!!
        return GasWe;
    }
    
    public boolean hasGPS() {
        return hasGPS;
    }
    
    public String getSensorName() {
        return fileConfig.getSensorName();
    }
    
    public Timestamp getFirstTime() {
        return firstTime;
    }
    
    public ArrayList<String> getHeaderPlotLabels() {
        return headerPlotLabels;
    }
    
    public ArrayList<String> getHeaderMapLabels() {
        return headerMapLabels;
    }
    
    public ArrayList<String> getHeaderColumnLabels() {
        return headerColumnLabels;
    }
    
}
