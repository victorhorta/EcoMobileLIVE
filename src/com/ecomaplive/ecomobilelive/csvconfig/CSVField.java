package com.ecomaplive.ecomobilelive.csvconfig;

/**
 * Corresponds to a field of the CSV file -- a column.<br>
 * It represents the expected CSV label, and the unit used to measure.<br>
 * 
 * For example, the field Temperature is measured in Celsius.
 * This class is immutable.
 * 
 * @author Victor
 * 
 */
public class CSVField {
    private String label;
    private String unit;
    
    public CSVField(String label, String unit) {
        this.label = label;
        this.unit = unit;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getUnit() {
        return unit;
    }
}
