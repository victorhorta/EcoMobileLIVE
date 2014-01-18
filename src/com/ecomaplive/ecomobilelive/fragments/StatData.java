package com.ecomaplive.ecomobilelive.fragments;

import java.util.Locale;

/**
 * Defines the content of each Stat row. Its main role is to be used on the
 * StatAdapter class, in order to display updated info from the device.
 * 
 * @author Victor
 * 
 */ 
public class StatData implements Comparable<StatData>{
    public static final String NONE = "N/A";

    private String mRegister;
    private String mHeader;
    private String mDetails;
    private StatDataType mStatDatatype;
    
    public StatData(String register, String header, StatDataType pStatDataType) {
        this.mRegister = register;
        this.mHeader = header;
        this.mStatDatatype = pStatDataType;
        mDetails = NONE;
    }
    
    StatData(String register, String header, String details, StatDataType pStatDataType) {
        this.mRegister = register;
        this.mHeader = header;
        this.mDetails = details;
        this.mStatDatatype = pStatDataType;
    }

    public String getRegister() {
        return mRegister;
    }
    
    /**
     * This method returns a String representing the corresponding register, but
     * is meant to be used on GET and SET commands, as these commands use
     * positions 00~0f as 0~F.
     * 
     * @return the register String
     */
    public String getRegisterForDeviceCommands() {
        String mRegisterForDeviceCommands = mRegister.toUpperCase(Locale.US);
        
        if(mRegister.startsWith("0")) {
            return mRegisterForDeviceCommands.substring(1);
        }
        return mRegisterForDeviceCommands;
    }
    

    public void setRegister(String register) {
        this.mRegister = register;
    }
    
    public String getHeader() {
        return mHeader;
    }

    public void setHeader(String header) {
        this.mHeader = header;
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        this.mDetails = details;
    }
    
    public StatDataType getStatDataType() {
        return mStatDatatype;
    }

	@Override
	public int compareTo(StatData another) {
        int last = this.mRegister.compareTo(another.mRegister);
		return last;
	}
}
