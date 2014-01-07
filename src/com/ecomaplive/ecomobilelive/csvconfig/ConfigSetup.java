package com.ecomaplive.ecomobilelive.csvconfig;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In this class, we manage/add all new CSV configurations that should exist.
 * 
 * @author Victor
 *
 */
public class ConfigSetup {
    final private static Map<Integer, CSVConfig> availableConfigs;
    
    //TODO: Modular names for variables instead of hard-coded ones!
    //TODO: Change hard-coded references all over csvconfig package...
    // final private static String COLUMN_VOC
    // final private static String HEADER_VOC...
    
    
    static {
        Map<Integer, CSVConfig> allConfigs = new HashMap<Integer, CSVConfig>();
        //List<CSVConfig> tempConfig = new ArrayList<CSVConfig>();
        List<CSVConfig> tempConfig = Arrays.asList(
        /**
         * To add new CSV configs, just create a new CSVConfig instance by
         * following the examples:
         */
            // ECOMINI: 02
            new CSVConfig(02,
                    "ECOMINI",
                    true,
                    Arrays.asList(
                            new CSVField("SensorVersion", "1.3"),
                            new CSVField("Time", "s"),
                            new CSVField("Latitude", "Lat"),
                            new CSVField("Longitude", "Long"),
                            new CSVField("BatteryV", "V"),
                            new CSVField("AccelX", "m/s2"),
                            new CSVField("AccelY", "m/s2"),
                            new CSVField("AccelZ", "m/s2"),
                            new CSVField("Temperature", "C"),
                            new CSVField("Humidity", "RH%"),
                            new CSVField("PhotocellIR", "IR"),
                            new CSVField("PhotocellR", "R"),
                            new CSVField("PhotocellG", "G"),
                            new CSVField("PhotocellB", "B"),
                            new CSVField("Sound", ""),
                            new CSVField("VOCPred", "%"),
                            new CSVField("VOCRes", "%"),
                            new CSVField("Gas1PPB","%"),
                            new CSVField("Gas1We", "%"),
                            new CSVField("Gas1Aux", "%"),
                            new CSVField("Gas2PPB", "%"),
                            new CSVField("Gas2We", "%"),
                            new CSVField("Gas2Aux", "%"),
                            new CSVField("ButtonStatus", "")
                    )
            ),
                              
            // ECOMONITOR: 0001
            new CSVConfig(0001,
                    "ECOMONITOR",
                    true,
                    Arrays.asList(
                            new CSVField("SensorVersion", "1.1"),
                            new CSVField("Time", "s"),
                            new CSVField("Latitude", "Lat"),
                            new CSVField("Longitude", "Long"),
                            new CSVField("BatteryV", "V"),
                            new CSVField("AccelX", "m/s"),
                            new CSVField("AccelY", "m/s"),
                            new CSVField("AccelZ", "m/s"),
                            new CSVField("Temperature", "C"),
                            new CSVField("Humidity", "ppm"),
                            new CSVField("PhotocellIR", ""),
                            new CSVField("PhotocellR", "R"),
                            new CSVField("PhotocellG", "G"),
                            new CSVField("PhotocellB", "B"),
                            new CSVField("VOCPred", "%"),
                            new CSVField("VOCRes", "%"),
                            new CSVField("Gas1PPB","%"),
                            new CSVField("Gas1We", "%"),
                            new CSVField("Gas1Aux", "%"),
                            new CSVField("Gas2PPB", "%"),
                            new CSVField("Gas2We", "%"),
                            new CSVField("Gas2Aux", "%"),
                            new CSVField("ButtonStatus", "")
                    )
            )
                
        );
        
        
        /** END OF ADDING NEW CSV CONFIGS ---------------------------------- */
        for(CSVConfig c : tempConfig)
            allConfigs.put(new Integer(c.getVersionId()), c);
        availableConfigs = Collections.unmodifiableMap(allConfigs);
    }
    
    /**
     * @param versionId the required version ID to be retrieved.
     * @return the value of the CSVConfig corresponding to the specified
     *         versionId, or null if no mapping for the specified versionId is
     *         found.
     */
    public static CSVConfig getConfigFromId(int versionId) {
        return availableConfigs.get(new Integer(versionId));
    }
    
    

}
