package com.ecomaplive.ecomobilelive.config;

import java.util.List;

/**
 * This class sets all values for a CSV configuration pattern. This class is immutable.
 * 
 * @author Victor
 *
 */
public class CSVConfig implements VersionConfig {
    final private int versionId;
    final private String versionNumberAsText;
    final private String sensorName;
    final private List<String> orderedFields;
    
    /**
     * @param versionId
     * @param sensorName
     * @param orderedFields requires all names to be unique.
     */
    public CSVConfig(int versionId, String versionNumberAsText, String sensorName, List<String> orderedFields){
        this.versionId = versionId;
        this.versionNumberAsText = versionNumberAsText;
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
        return orderedFields.get(fieldIndex);
    }
    
    @Override
    public int getFieldIndex(String fieldLabel) {
        return orderedFields.indexOf(fieldLabel);
    }

    
}
