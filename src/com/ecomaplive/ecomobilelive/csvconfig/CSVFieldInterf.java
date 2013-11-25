package com.ecomaplive.ecomobilelive.csvconfig;

/**
 * Defines a type of data that a CSV field can contain. <br>
 * All new types of data must implement this interface, and then must be added on the ConfigSetup class.
 * 
 * @author Victor
 *
 */
public interface CSVFieldInterf {
    public String getLabel();

}
