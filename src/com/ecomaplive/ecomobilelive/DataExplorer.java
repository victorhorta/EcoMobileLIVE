package com.ecomaplive.ecomobilelive;

import java.io.File;

import android.os.Build;

public class DataExplorer {
    public static final String STORAGE_DIR = "EcoMapLIVE";
    
    public static final String STORAGE_DIR_SCREENSHOTS = File.separator + "EcoMapLIVE"
                                                       + File.separator + "Screenshots";
    
	public static final String STORAGE_DIR_CSVSESSIONS = File.separator + "EcoMapLIVE" 
													   + File.separator + "Sessions";
	
	public static String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	private static String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}
}
