package com.ecomaplive.ecomobilelive;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

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

import com.ecomaplive.ecomobilelive.csvconfig.EcoCSVObject;

public class DataPlotSelect extends Activity implements OnItemClickListener {
    public final static String TAG = "DataPlotGraph";
    
    private File fileToBePlotted;
    TextView showPathOnScreen;
    ListView listView;
    List<TextView> showInfo;
    Intent intentToBePassedToDataPlotClass;
    
    EcoCSVObject fileAlreadyParsed;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_plot_select);
            
            // Creating the list of TextViews
            showInfo = new ArrayList<TextView>();
            
            // Retrieving file to be plotted
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                String filePath = extras.getString("file_path");
                fileToBePlotted = new File(filePath);
                
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
                showInfo.get(2).setText(Long.toString(fileToBePlotted.length())+" B");
                showInfo.get(3).setText(fileAlreadyParsed.getSensorName());
    
                if (fileAlreadyParsed != null)
                    showInfo.get(4).setText(fileAlreadyParsed.getFirstTime().toString());
                
                //showPathOnScreen = (TextView) findViewById(R.id.p_info_filename);
                //showPathOnScreen.setText(fileToBePlotted.getAbsolutePath());
                
            }
    
    
            // ListView of plot options
            listView = (ListView) findViewById(R.id.plotoptions_list);
            
            //String[] plotOptions = fileAlreadyParsed.getHeaderData();
            String[] plotOptions = new String[fileAlreadyParsed.getHeaderPlotLabels().size()];
            plotOptions = (String[]) fileAlreadyParsed.getHeaderPlotLabels().toArray(new String[fileAlreadyParsed.getHeaderPlotLabels().size()]);
            //String[] plotOptions = getResources().getStringArray(R.array.plotoptions_array);
            
            // Row layout defined by Android: android.R.layout.simple_list_item_1
            listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
                    plotOptions));
            listView.setOnItemClickListener(this);
            //intentToBePassedToDataPlotClass.putExtra("fileAlreadyParsed", fileAlreadyParsed);
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), "Invalid file format.", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }
    
    @Override
    public void onItemClick(AdapterView<?> listAdapter, View arg1, int arg2, long arg3) {
        //l, v, position, id
        Log.d(TAG, "listAdapter: " + listAdapter.toString());
        Log.d(TAG, "arg1: " + arg1.toString());
        Log.d(TAG, "arg2: " + arg2);
        Log.d(TAG, "arg3: " + arg3);
        // number of the label -> fileAlreadyParsed.headerLabels.get(numberofthelabel) ->
        intentToBePassedToDataPlotClass = new Intent(getBaseContext(), DataPlot.class);
        
        intentToBePassedToDataPlotClass.putExtra("graph_data", fileAlreadyParsed.getXYandLabelsfromData(arg2));
        
        //intentToBePassedToDataPlotClass.
        startActivity(intentToBePassedToDataPlotClass);
        
    }
    
    
}
