package com.ecomaplive.ecomobilelive;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.ecomaplive.ecomobilelive.csvconfig.EcoCSVObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DataMapSelect extends Activity implements OnItemClickListener {
    public final static String TAG = "DataMapSelect";
    
    private File fileToBeMapped;
    TextView showPathOnScreen;
    ListView listView;
    List<TextView> showInfo;
    Intent intentToBePassedToDataMapClass;
    
    EcoCSVObject fileAlreadyParsed;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_select);
        
        // Creating the list of TextViews
        showInfo = new ArrayList<TextView>();
        
        // Retrieving file to be mapped
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String filePath = extras.getString("file_path");
            fileToBeMapped = new File(filePath);
            
            // Extracting the file name and extension
            String fileName = new String(
                                        filePath.substring(filePath.lastIndexOf("/"))
                                        );
            String extension = fileName.substring(fileName.lastIndexOf("."));
            
            fileName = new String(
                    fileName.substring(1, fileName.length() - extension.length())
                    );
            
            
            // Starting parsing procedure
            fileAlreadyParsed = new EcoCSVObject(filePath);
            
            showInfo.add((TextView)findViewById(R.id.p_info_filename));
            showInfo.add((TextView)findViewById(R.id.p_info_fileextension));
            showInfo.add((TextView)findViewById(R.id.p_info_filesize));
            showInfo.add((TextView)findViewById(R.id.p_info_sensortype));
            showInfo.add((TextView)findViewById(R.id.p_info_timestamp));
            

            
            Log.d(TAG, "filename: " + fileName);
            Log.d(TAG, "filepath: " + filePath);
            Log.d(TAG, "extension: " + extension);
            
            showInfo.get(0).setText(fileName);
            showInfo.get(1).setText(extension);
            showInfo.get(2).setText(Long.toString(fileToBeMapped.length())+" B");
            showInfo.get(3).setText(fileAlreadyParsed.getSensorName());

            if (fileAlreadyParsed != null)
                showInfo.get(4).setText(fileAlreadyParsed.getFirstTime().toString());
            
            
        }


        // ListView of map options
        listView = (ListView) findViewById(R.id.mapoptions_list);
        
        String[] mapOptions = new String[fileAlreadyParsed.headerLabels.size()];
        mapOptions = (String[]) fileAlreadyParsed.headerLabels.toArray(new String[fileAlreadyParsed.headerLabels.size()]);
        
        // Row layout defined by Android: android.R.layout.simple_list_item_1
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                mapOptions));
        listView.setOnItemClickListener(this);
        
    }
    @Override
    public void onItemClick(AdapterView<?> listAdapter, View arg1, int arg2, long arg3) {
        //l, v, position, id
        Log.d(TAG, "listAdapter: " + listAdapter.toString());
        Log.d(TAG, "arg1: " + arg1.toString());
        Log.d(TAG, "arg2: " + arg2);
        Log.d(TAG, "arg3: " + arg3);
        // number of the label -> fileAlreadyParsed.headerLabels.get(numberofthelabel) ->
        
        if(fileAlreadyParsed.hasGPS()) {
            intentToBePassedToDataMapClass = new Intent(getBaseContext(), DataMap.class);
            intentToBePassedToDataMapClass.putExtra("graph_data", fileAlreadyParsed.getInfoPointsToBeMapped(arg2));
            startActivity(intentToBePassedToDataMapClass);            
        } else {
            Toast.makeText(getBaseContext(), "GPS data not available.", Toast.LENGTH_SHORT).show();
        }
        
        
    }
    
    
}
