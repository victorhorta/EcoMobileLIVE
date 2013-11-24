package com.ecomaplive.ecomobilelive;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import au.com.bytecode.opencsv.CSVReader;

/**
 * This class corresponds to a parsed CSV file.
 * 
 * @author Victor
 * 
 */
public class EcoMonParsedCSV {
    public final static String TAG = "EcoMonParsedCSV";
    
    static String NO_GPS = new String("!!!NO-GPS!!!"); 
    
    List<EcoMonSingleData> listOfPoints;
    String providerName;
    private CSVReader reader;
    boolean hasO3Sensor = false;
    
    EcoMonParsedCSV(String pathOfCSVFileToBeParsed, String providerName) {
        this.providerName = providerName;
        listOfPoints = new ArrayList<EcoMonSingleData>();
        
        try {
            reader = new CSVReader(new FileReader(pathOfCSVFileToBeParsed));
            // nextLine[] is an array of values from the line
            String [] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                EcoMonSingleData nextCSVData;
                // The Ecomini CSV file is supposed to have exactly 17 fields per line! [0 to 16]
                // Order of fields:
                // 0. Time,
                // 1. Latitude,
                // 2. Longitude,
                // 3. BatteryV,
                // 4. AccelX,
                // 5. AccelY,
                // 6. AccelZ,
                // 7. Temperature,
                // 8. Humidity,
                // 9. PhotocellIR,
                // 10 PhotocellBlue,
                // 11 IAQPred,
                // 12 IAQRes,
                // 13 SO2We,
                // 14 SO2Aux,
                // 15 O3We,
                // 16 O3Aux
                
                //TODO: How do we know if a Ecomonitor has this sensor? Only by counting csv fields?
                // This way is pretty bizarre...
                if(nextLine.length <=15)
                     hasO3Sensor = false;
                
                if(hasO3Sensor) {
                nextCSVData = new EcoMonSingleData(providerName, 
                        Long.parseLong(nextLine[0]),    //Time,
                        nextLine[1],//Latitude, 
                        nextLine[2],//Longitude, 
                        Float.parseFloat(nextLine[3]),  //BatteryV, 
                        Integer.parseInt(nextLine[4]),  //AccelX, 
                        Integer.parseInt(nextLine[5]),  //AccelY, 
                        Integer.parseInt(nextLine[6]),  //AccelZ, 
                        Float.parseFloat(nextLine[7]),  //Temperature, 
                        Float.parseFloat(nextLine[8]),  //Humidity, 
                        Integer.parseInt(nextLine[9]),  //PhotocellIR, 
                        Integer.parseInt(nextLine[10]), //PhotocellBlue, 
                        Integer.parseInt(nextLine[11]), //IAQPred, 
                        Integer.parseInt(nextLine[12]), //IAQRes, 
                        Integer.parseInt(nextLine[13]), //SO2We,
                        Integer.parseInt(nextLine[14]), //SO2Aux,    
                        Integer.parseInt(nextLine[15]), //O3We, 
                        Integer.parseInt(nextLine[16])  //O3Aux
                        );
                } else {
                    nextCSVData = new EcoMonSingleData(providerName, 
                            Long.parseLong(nextLine[0]),    //Time,
                            nextLine[1],//Latitude, 
                            nextLine[2],//Longitude, 
                            Float.parseFloat(nextLine[3]),  //BatteryV, 
                            Integer.parseInt(nextLine[4]),  //AccelX, 
                            Integer.parseInt(nextLine[5]),  //AccelY, 
                            Integer.parseInt(nextLine[6]),  //AccelZ, 
                            Float.parseFloat(nextLine[7]),  //Temperature, 
                            Float.parseFloat(nextLine[8]),  //Humidity, 
                            Integer.parseInt(nextLine[9]),  //PhotocellIR, 
                            Integer.parseInt(nextLine[10]), //PhotocellBlue, 
                            Integer.parseInt(nextLine[11]), //IAQPred, 
                            Integer.parseInt(nextLine[12]), //IAQRes, 
                            Integer.parseInt(nextLine[13]), //SO2We,
                            Integer.parseInt(nextLine[14]), //SO2Aux,    
                            -1, //O3We, 
                            -1  //O3Aux
                            );
                }
                listOfPoints.add(nextCSVData);
                Log.d(TAG, "Added data!");
            }
            
        } catch (RuntimeException e) {
            Log.d(TAG, "RuntimeException!");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "FileNotFound!");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d(TAG, "IOException!");
        }
    }

}
