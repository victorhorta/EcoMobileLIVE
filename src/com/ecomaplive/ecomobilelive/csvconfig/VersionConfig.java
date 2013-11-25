package com.ecomaplive.ecomobilelive.csvconfig;

import java.util.List;


/**
 * This interface sets the main methods that a CSV Configuration class must
 * implement.<br>
 * <br>
 * A CSV file will have a version ID, which identifies uniquely the CSV version,
 * and all fields that the CSV file will have.
 * 
 * rep invariants: - All fields must have unique names. - Fields will be indexed
 * starting at zero. - Field zero will always be the ID field.
 * 
 * @author Victor
 * 
 */
public interface VersionConfig {
    /**
     * @return an integer corresponding to the ID of the CSV File
     */
    public int getVersionId();
    
    /**
     * @return a String corresponding to the version number as a text (e.g.: "1.2")
     */
    public String getVersionNumberAsText();

    /**
     * @return a String corresponding to the name of the Sensor for the given
     *         version ID.
     */
    public String getSensorName();

    /**
     * @return the number of fields (values on each line) that the csv contains.
     *         Includes the version ID field.
     */
    public int getNumberOfFields();

    /**
     * Retrieves the label of a field given its index.
     * 
     * @param fieldIndex
     *            the index of the required field.
     * @return the String corresponding to the required field index. If the
     *         given field ID does not exist, returns an empty String.
     * @throws IndexOutOfBoundsException
     *             if the fieldIndex is not a valid one.
     */
    public String getFieldLabel(int fieldIndex) throws IndexOutOfBoundsException;

    /**
     * @param fieldLabel
     *            the corresponding String. This method is case-sensitive.
     * @return an integer corresponding to the index of the requested label, or
     *         -1 if the element was not found.
     */
    public int getFieldIndex(String fieldLabel);
    
    /**
     * @param fieldLabel
     *            the String corresponding to the required field index. If the
     *            given field ID does not exist, returns an empty String.
     * @return the corresponding Unit from the CSVField that corresponds to the
     *         given fieldLabel.
     */
    public String getUnitFromFieldLabel(String fieldLabel);
    
    /**
     * @return a List containing all column labels from the csv file.
     */
    public List<String> getHeaderColumnsLabels();
    
    /**
     * @return if the current version is capable of collecting GPS data.
     */
    public boolean hasGPSFields();
}
