package com.ecomaplive.ecomobilelive.config;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class sets all values for a CSV configuration pattern.  
 * 
 * @author Victor
 *
 */
public class CSVConfig implements VersionConfig {
    final private int versionId;
    final private String sensorName;
    final private List<String> orderedFields;
    
    /**
     * @param versionId
     * @param sensorName
     * @param orderedFields requires all names to be unique.
     */
    public CSVConfig(int versionId, String sensorName, List<String> orderedFields){
        this.versionId = versionId;
        this.sensorName = sensorName;
        this.orderedFields = orderedFields;         
    }
    
    @Override
    public int getVersionId() {
        return versionId;
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
