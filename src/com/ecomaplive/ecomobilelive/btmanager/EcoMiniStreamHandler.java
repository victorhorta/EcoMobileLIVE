package com.ecomaplive.ecomobilelive.btmanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap.CompressFormat;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.ecomaplive.ecomobilelive.DataExplorer;
import com.ecomaplive.ecomobilelive.csvconfig.CSVConfig;
import com.ecomaplive.ecomobilelive.csvconfig.ConfigSetup;
import com.ecomaplive.ecomobilelive.fragments.StatData;
import com.ecomaplive.ecomobilelive.fragments.StatDataType;

public class EcoMiniStreamHandler implements IDeviceStreamHandler{
    private final static String TAG = "EcoMiniStreamHandler";
    private final static int MAX_LINES_PER_CSV = 1000;
    
    private final static CSVConfig csvConfig = ConfigSetup.getConfigFromId(2);
    
    private final static int EXPECTED_NUMBER_OF_CSV_FIELDS = csvConfig.getNumberOfFields();

    private int linesCounter;
    private int csvCounter;
    private int csvCounterLastSave;
    private String filePathLastSave;
    
    
    // File-writing related variables:
    private Object csvFileAccessLock = new Object();
    private boolean RECORDING = false;
    private boolean SETMARKER = false;
    private String fileHome = DataExplorer.STORAGE_DIR_CSVSESSIONS;
    private String fileFolder = File.separator + "Anonymous";
    private String fileName = File.separator + "Anonymous";
    private String fileExtension = ".csv";
    		
    
    
    
    // Used only to get the context and broadcast messages!
    private BTService btService;
    
    List<StringBuffer> csvData;

    // ADD NEW VALUES HERE!!!!
    StatData[] data = new StatData[]{
            new StatData("00", "Firmware Version", StatDataType.UNCHANGEABLE),
            new StatData("01", "Time Register", StatDataType.DATEANDTIMEPICKER),
            new StatData("02", "Wireless data-streaming state", StatDataType.RADIOBUTTON_ENABLE_DISABLE),
            new StatData("03", "Data logging state", StatDataType.RADIOBUTTON_ENABLE_DISABLE),
            new StatData("04", "Data sampling interval", StatDataType.SLIDER_0_1000),
            new StatData("05", "Device ID", StatDataType.UNCHANGEABLE),
            new StatData("06", "Battery level", StatDataType.UNCHANGEABLE),
            new StatData("07", "Sensor sampling state", StatDataType.RADIOBUTTON_ENABLE_DISABLE),
            new StatData("08", "Gas #1 Amplifier Gain, working electrode", StatDataType.UNCHANGEABLE),
            new StatData("09", "Gas #1 Amplifier Gain, aux electrode", StatDataType.UNCHANGEABLE),
            new StatData("0a", "Gas #1 Offset Current, working electrode", StatDataType.UNCHANGEABLE),
            new StatData("0b", "Gas #1 Offset Current, aux electrode", StatDataType.UNCHANGEABLE),
            new StatData("0c", "Gas #1 Calibration factor", StatDataType.UNCHANGEABLE),
            new StatData("0d", "Gas #1 Sensor sensitivity", StatDataType.TEXTFIELD_DECIMAL),
            new StatData("0e", "Gas #1 Temperature coefficient", StatDataType.TEXTFIELD_DECIMAL),
            new StatData("0f", "Bluetooth transmission", StatDataType.RADIOBUTTON_ON_OFF),
            new StatData("10", "GPS status", StatDataType.RADIOBUTTON_ON_OFF),
            new StatData("11", "5V power duty cycle status", StatDataType.RADIOBUTTON_ENABLE_DISABLE),
            new StatData("12", "5V power duty cycle sampling period", StatDataType.SLIDER_0_1000),
            new StatData("15", "Gas #2 Amplifier Gain, working electrode", StatDataType.UNCHANGEABLE),
            new StatData("16", "Gas #2 Amplifier Gain, aux electrode", StatDataType.UNCHANGEABLE),
            new StatData("17", "Gas #2 Offset Current, working electrode", StatDataType.UNCHANGEABLE),
            new StatData("18", "Gas #2 Offset Current, aux electrode", StatDataType.UNCHANGEABLE),
            new StatData("19", "Gas #2 Calibration factor", StatDataType.UNCHANGEABLE),
            new StatData("1a", "Gas #2 Sensor sensitivity", StatDataType.TEXTFIELD_DECIMAL),
            new StatData("1b", "Gas #2 Temperature coefficient", StatDataType.TEXTFIELD_DECIMAL),
            new StatData("1c", "Gas #1 Transconductance working polarity", StatDataType.RADIOBUTTON_POS_NEG),
            new StatData("1d", "Gas #1 Transconductance aux polarity", StatDataType.RADIOBUTTON_POS_NEG),
            new StatData("1e", "Gas #1 Offset working polarity", StatDataType.RADIOBUTTON_POS_NEG),
            new StatData("1f", "Gas #1 Offset aux polarity", StatDataType.RADIOBUTTON_POS_NEG),
            new StatData("20", "Gas #1 K polarity", StatDataType.RADIOBUTTON_POS_NEG),
            new StatData("21", "Gas #1 sensitivity polarity", StatDataType.RADIOBUTTON_POS_NEG),
            new StatData("22", "Gas #1 Temp coefficient polarity", StatDataType.RADIOBUTTON_POS_NEG),
            new StatData("23", "Gas #2 Transconductance working polarity", StatDataType.RADIOBUTTON_POS_NEG),
            new StatData("24", "Gas #2 Transconductance aux polarity", StatDataType.RADIOBUTTON_POS_NEG),
            new StatData("25", "Gas #2 Offset working polarity", StatDataType.RADIOBUTTON_POS_NEG),
            new StatData("26", "Gas #2 Offset aux polarity", StatDataType.RADIOBUTTON_POS_NEG),
            new StatData("27", "Gas #2 K polarity", StatDataType.RADIOBUTTON_POS_NEG),
            new StatData("28", "Gas #2 sensitivity polarity", StatDataType.RADIOBUTTON_POS_NEG),
            new StatData("29", "Gas #2 Temp coefficient polarity", StatDataType.RADIOBUTTON_POS_NEG),
    };
    
    Map<String, StatData> hexToStatData;
    
    public EcoMiniStreamHandler(BTService btService) {
    	this.btService = btService;
    	
        hexToStatData = new HashMap<String, StatData>();
        for(StatData sData : data){
            hexToStatData.put(sData.getRegister(), sData);            
        }
        
        csvData = new ArrayList<StringBuffer>();
        csvData.add(new StringBuffer());
    }
    
    
    @Override
    public void parseAndHandleStream(String text) {
        try {
            if (text.contains(":")) {
                // Config parameters
                String hexIndex = text.substring(0, 2);
                String details = text.substring(text.indexOf(" ") + 1, text.length() - 1);
                
                if(hexToStatData.get(hexIndex) != null)
                    hexToStatData.get(hexIndex).setDetails(details);

            } else if (text.contains("OK")) {
                Intent intentReply = new Intent(BTService.INTENT_STAT_UPDATED);
                //TODO: send an array of StatDatas!
                ArrayList<StatData> updatedHexToStatData = new ArrayList<StatData>(hexToStatData.values());
                Collections.sort(updatedHexToStatData);
                
                StatData[] data = updatedHexToStatData.toArray(new StatData[0]);
                //StatData[] data = (hexToStatData.values()).toArray(new StatData[0]);
                intentReply.putExtra(BTService.EXTRA_STAT_UPDATED_DATA, data);
                LocalBroadcastManager.getInstance(btService.getApplicationContext()).sendBroadcast(intentReply);
            	
            	
            } else if (text.split(",").length == EXPECTED_NUMBER_OF_CSV_FIELDS) {
                Log.d(TAG, "Valid data has been captured!");
                if (linesCounter == MAX_LINES_PER_CSV - 1) {
                    linesCounter = 0;
                    csvCounter++;
                    csvData.add(new StringBuffer());
                }
                
                if(SETMARKER) {
                	SETMARKER = false;
                	text = text.substring(0, text.length() - 2) + "1\r";
                }
                
                if(RECORDING) {
                	try {
						writeToCsv(text);
					} catch (IOException e) {
						Log.e(TAG, "Error writing to csv file!");
						e.printStackTrace();
					}
                }
                
                csvData.get(csvCounter).append(text);
                
                // Sending the broadcast to be captured by the Collect Activity
                // (just to update screen values...)
                Intent intentReply = new Intent(BTService.INTENT_DATA_ARRIVED_FROM_SENSOR);
                intentReply.putExtra(BTService.EXTRA_DATA_ARRIVED_LABELS, csvConfig.getHeaderColumnsLabels());
                intentReply.putExtra(BTService.EXTRA_DATA_ARRIVED_VALUES, new ArrayList<String>(Arrays.asList(text.split(","))));
                LocalBroadcastManager.getInstance(btService.getApplicationContext()).sendBroadcast(intentReply);
            } else if(text.startsWith("ERR")) {
                // ERROR DATA....
                Log.d(TAG, "ERR message: " + text);
            } else {
                // INVALID DATA....
                Log.d(TAG, "Invalid data: " + text);
            }
        } catch (NumberFormatException e) {
        }

    }

    @Override
    public StatData[] getStatDataArray() {
        return (StatData[]) hexToStatData.values().toArray();
        
    }

    @Override
    public boolean saveCsvFiles(String path, String sessionName) {
        // TODO we need to order csv lines according to their timestamps here!!!!
        // we also need to set csvCounterLastSave and filePathLastSave!!!
    	
        return false;
    }


    @Override
    public String getAmountOfData() {
        return Integer.toString(csvCounterLastSave);
    }


    @Override
    public String getSavedFilePath() {
        // TODO Auto-generated method stub
        return filePathLastSave;
    }


	@Override
	public String getCsvHeader() {
		PackageInfo pInfo;
		String appVersion = "package not found";
		
		try {
			pInfo = btService.getPackageManager().getPackageInfo(btService.getPackageName(), 0);
			appVersion = pInfo.versionName;
		} catch (NameNotFoundException e) {}
		
		StringBuffer sb = new StringBuffer();
		sb.append("# " + "      Device info: " + DataExplorer.getDeviceName() + "\r");
		sb.append("# " + "  Android version: " + Build.VERSION.CODENAME + "\r");
		sb.append("# " + "  Android release: " + Build.VERSION.RELEASE + "\r");
		sb.append("# " + "EcoMobile version: " + appVersion + "\r");
		
		return sb.toString();
	}


	@Override
	public void writeToCsv(String content) throws IOException {
		// 0) checks the SD card state
		boolean mExternalStorageAvailable = false;
        String state = Environment.getExternalStorageState();
        
		if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write media
            mExternalStorageAvailable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need to know is we can neither read nor write
            mExternalStorageAvailable = false;
        }
        
        if (!mExternalStorageAvailable) {
        	Log.e(TAG, "External Storage not available");
        	//TODO: inform the final user about it.
        }
		
		
		// 1) checks if folder exists. If not, creates and then continue. Else, continue.
        String tempDirPath = Environment.getExternalStorageDirectory() + fileHome + fileFolder;
        Log.d(TAG, "tempDirPath:" + tempDirPath);
		File tempDir = new File(tempDirPath);
		boolean dirWasCreated = tempDir.mkdirs();
		boolean dirExists = tempDir.exists();
		if (!(dirWasCreated || dirExists)) {
		    // Directory creation failed!
			throw new IOException("Could not create new folder.");
		}
		
		// 2) checks if file exists. If not, creates a file, calls writeToCsv_Header and then continue. Else, continue.
		final String actualFullFileName = Environment.getExternalStorageDirectory() +
				                    fileHome +  
					                fileFolder +  
					                fileName + "_" + Integer.toString(csvCounter) + fileExtension;
		
		File tempFile = new File(actualFullFileName);		
		boolean theFileAlreadyExistedBefore = tempFile.exists();
		
		FileWriter fileWriter = new FileWriter(tempFile, true);
		BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
	    
		
		if(!theFileAlreadyExistedBefore) {
			Log.d(TAG, "Writing the header to the new file...");
			bufferedWriter.write(getCsvHeader());
		}
		
		// 3) writes the desired line to the file
		bufferedWriter.write(content);
	    bufferedWriter.close();
	    Log.d(TAG, "Line successfully written to the csv file.");
	}

	
	 
	
	
	@Override
	public void setRecordingState(boolean state) {
		RECORDING = state;
	}
	
	public void setProjectName(String name) {
		String fileFolderOld = fileFolder;
		String fileNameOld = fileName;
				
		if(name != null) {
			if (!name.isEmpty()) {
				fileFolder = File.separator + name;
				fileName = File.separator + name;
				//TODO: RENAME EXISTING FILES INSIDE fileFolderOld!!!
			}
		}
	}


	@Override
	public void setNextMarker() {
		SETMARKER = true;
	}
}
