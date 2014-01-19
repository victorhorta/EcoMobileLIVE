package com.ecomaplive.ecomobilelive.btmanager;

import java.io.File;
import java.io.IOException;

import com.ecomaplive.ecomobilelive.fragments.StatData;

/**
 * This interface must be implemented by all devices that are capable of receiving
 * bluetooth data from devices to the app.
 * 
 * @author Victor
 * 
 */
public interface IDeviceStreamHandler {
    /**
     * What is the device supposed to do with the incoming data? Is it valid? Is
     * it a STAT info? Is it a CSV line of data? This method is responsible for
     * verifying it and deciding what to do with the data.
     * 
     * @param data
     *            String to be handled
     */
    public void parseAndHandleStream(String data);
    
    /**
     * To be used on the config screen. Updates the available config data and
     * values according to the incoming data from the Bluetooth.
     * 
     * @return StatData[] of latest config info received
     */
    public StatData[] getStatDataArray();
    
    public boolean saveCsvFiles(String path, String sessionName);
    
    /**
     * @return the number of csv lines on last save as a String
     */
    public String getAmountOfData();
    
    /**
     * @return filepath on last save
     */
    public String getSavedFilePath();
    
    /**
	 * This method should be called by the writeToCsv()
	 * method. Writes initial data to all created csv files. These info should
	 * include the app version and info about the hardware as well (Android
	 * version, Hardware version, Kernel version, etc.). This method must be
	 * called only when the file has already been created, but still empty.
	 */
    public String getCsvHeader();
    
    
    /**
     * Writes any given String to the csv file, appending automatically a CR and
	 * a LF to the end of the desired string, and creating a file if needed.
     * @param content
     * @throws IOException 
     */
    public void writeToCsv(String content) throws IOException;
    
    /**
	 * Sets the recording state of the Stream handler. If true, for every new
	 * valid incoming line, the csv file will be written. If false, any lines
	 * will be written.
	 * 
	 * @param state
	 *            the new Recording state
	 */
    public void setRecordingState(boolean state);
    
    /**
     * Sets the SETMARKER as true for the next iteration.
     */
    public void setNextMarker();
    
    /**
     * Sets the project name (folder and initial filenames) according to the given parameter.
     * @param name the desired project name.
     */
    public void setProjectName(String name);
    
    /**
     * Returns the names of the monitorable data fields from this model. This
     * method is intended to be used when filling the spinner from the Collect
     * fragment.
     * 
     * @return an array containing all monitorable data names
     */
    public String[] getMonitorableDataNames();
    
    //public ??? getRecentHistory();
    //public resetRecentHistory();     
            
}
