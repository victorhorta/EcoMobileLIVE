package com.ecomaplive.ecomobilelive.config;

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
    
    static {
        Map<Integer, CSVConfig> allConfigs = new HashMap<Integer, CSVConfig>();
        //List<CSVConfig> tempConfig = new ArrayList<CSVConfig>();
        List<CSVConfig> tempConfig = Arrays.asList(
        /**
         * To add new CSV configs, just create a new CSVConfig instance by
         * following the examples:
         */
            // ECOMINI: 3002
            new CSVConfig(3002,
                    "1.3",
                    "ECOMINI",
                    Arrays.asList(
                            "SensorVersion",
                            "Time",
                            "Latitude",
                            "Longitude",
                            "BatteryV",
                            "AccelX",
                            "AccelY",
                            "AccelZ",
                            "Temperature",
                            "Humidity",
                            "PhotocellIR",
                            "PhotocellR",
                            "PhotocellG",
                            "PhotocellB",
                            "IAQPred",
                            "IAQRes",
                            "Gas1PPB",
                            "Gas1We",
                            "Gas1Aux",
                            "Gas2PPB",
                            "Gas2We",
                            "Gas2Aux",
                            "ButtonStatus"
                    )
            ),
                              
            // ECOMONITOR: 3001
            new CSVConfig(3001,
                    "1.1",
                    "ECOMONITOR",
                    Arrays.asList(
                            "SensorVersion",
                            "Time",
                            "Latitude",
                            "Longitude",
                            "BatteryV",
                            "AccelX",
                            "AccelY",
                            "AccelZ",
                            "Temperature",
                            "Humidity",
                            "PhotocellIR",
                            "PhotocellR",
                            "PhotocellG",
                            "PhotocellB",
                            "IAQPred",
                            "IAQRes",
                            "Gas1PPB",
                            "Gas1We",
                            "Gas1Aux",
                            "Gas2PPB",
                            "Gas2We",
                            "Gas2Aux",
                            "ButtonStatus"
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
