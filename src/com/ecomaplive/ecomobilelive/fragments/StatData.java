package com.ecomaplive.ecomobilelive.fragments;

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
    
    public StatData(String register, String header) {
        this.mRegister = register;
        this.mHeader = header;
        mDetails = NONE;
    }
    
    StatData(String register, String header, String details) {
        this.mRegister = register;
        this.mHeader = header;
        this.mDetails = details;
    }

    public String getRegister() {
        return mRegister;
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

	@Override
	public int compareTo(StatData another) {
        int last = this.mRegister.compareTo(another.mRegister);
		return last;
	}
}
