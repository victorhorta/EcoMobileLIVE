package com.ecomaplive.ecomobilelive;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ecomaplive.ecomobilelive.filepicker.FileChooser;
import com.ecomaplive.ecomobilelive.fragments.MainFragments;

public class Main extends Activity implements OnClickListener {
    public final static String TAG = "Main";

    public static final String PREFS_NAME = "UserPreferences";

    ArrayList<ImageButton> buttons = new ArrayList<ImageButton>();
    private CheckBox checkbox;
    
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initializing the buttons
        buttons.add((ImageButton) findViewById(R.id.imageButton_up_left));
        buttons.add((ImageButton) findViewById(R.id.imageButton_up_right));
        buttons.add((ImageButton) findViewById(R.id.imageButton_down_left));
        buttons.add((ImageButton) findViewById(R.id.imageButton_down_right));

        for (ImageButton v : buttons) {
            v.setOnClickListener(this);
        }

        // Gets the package name and app version
        try {
            String componentName = getPackageName();
            PackageInfo pi = getPackageManager().getPackageInfo(componentName,
                    PackageManager.GET_META_DATA);
            TextView tv = (TextView) findViewById(R.id.main_text_version_nr);
            if (tv != null) {
                tv.setText(pi.versionName);
            }
        } catch (Exception e) {
        }
        
        
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
        getActionBar().setCustomView(R.layout.actionbar_centered);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        // Restore preferences
//        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//        boolean rememberLastDevices = settings.getBoolean("rememberLastDevicesMode", false);
//        Log.d(TAG, "Loading checked option as: " + Boolean.toString(rememberLastDevices));
//        try {
//            menu.findItem(R.id.main_menu_settings).setChecked(rememberLastDevices);
//            Log.d(TAG, "Setting checked option as: " + Boolean.toString(rememberLastDevices));
//        } catch (Exception e) {
//
//        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
//        if (itemId == R.id.main_menu_settings) {
//            if (item.isChecked()) {
//                item.setChecked(false);
//            } else {
//                item.setChecked(true);
//            }
//            // We need an Editor object to make preference changes.
//            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//            SharedPreferences.Editor editor = settings.edit();
//            Log.d(TAG, "Changing checked option to: " + Boolean.toString(item.isChecked()));
//            editor.putBoolean("rememberLastDevicesMode", item.isChecked());
//            // Commit the edits!
//            editor.commit();
//            return true;
//        } else
            if (itemId == R.id.main_menu_about) {
            // About screen
                alertDialog = new AlertDialog.Builder(Main.this).create();
                alertDialog.setTitle("About");
                
                alertDialog.setMessage("About");
                alertDialog.setCancelable(true);
                alertDialog.setIcon(android.R.drawable.ic_menu_info_details);
                alertDialog.show();
                
                TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
                String aboutMessage = getString(R.string.about_text);
                textView.setText(Html.fromHtml(aboutMessage));
                
                Pattern pattern1 = Pattern.compile("vhorta@mit.edu");   
                Linkify.addLinks(textView, pattern1, "mailto:");
                Pattern pattern2 = Pattern.compile("victorhorta@gmail.com");   
                Linkify.addLinks(textView, pattern2, "mailto:");
                Pattern pattern3 = Pattern.compile("fletcher@media.mit.edu");   
                Linkify.addLinks(textView, pattern3, "mailto:");
                
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imageButton_up_left) {
            Log.d(TAG, "Starting new intent from: " + v.getTag().toString());
            Intent i1 = new Intent(Main.this, MainFragments.class);
            Main.this.startActivity(i1);
        } else if (id == R.id.imageButton_up_right) {
            Log.d(TAG, "Starting new intent from: " + v.getTag().toString());
//            Intent i2 = new Intent(Main.this, DataManager.class);
//            Main.this.startActivity(i2);

            Intent i2 = new Intent(this, FileChooser.class);
            ArrayList<String> extensions = new ArrayList<String>();
            extensions.add(".csv");
            
            i2.putStringArrayListExtra("filterFileExtension", extensions);
            startActivityForResult(i2, 1);
            
            
        } else if (id == R.id.imageButton_down_left) {
            Log.d(TAG, "Starting new intent from: " + v.getTag().toString());
            // Intent i3 = new Intent(Main.this, DataManagerMain.class);
            // Main.this.startActivity(i3);
            Toast.makeText(this, "To be implemented", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.imageButton_down_right) {
            Log.d(TAG, "Starting new intent from: " + v.getTag().toString());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ecomaplive.com"));
            startActivity(browserIntent);
        } else {
            Log.d(TAG, "onClicked: unexpected!");
        }
    }
    
    @Override
    protected void onPause(){
        super.onPause();
        if(alertDialog != null)
            alertDialog.dismiss();
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
           if(resultCode == RESULT_OK){      
               final String fileSelected = data.getStringExtra("fileSelected");
               
               new AlertDialog.Builder(this)
               .setTitle("File '" + fileSelected.substring(fileSelected.lastIndexOf(File.separator) + 1) + "' selected.")
               .setMessage("What do you want to do?")
               .setPositiveButton("Plot data", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) { 
                       Intent i = new Intent(getApplicationContext(), DataPlotSelect.class);
                       i.putExtra("file_path",fileSelected);
                       startActivity(i);
                   }
                })
               .setNeutralButton("Map Data", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) { 
                       Intent i = new Intent(getApplicationContext(), DataMapSelect.class);
                       i.putExtra("file_path",fileSelected);
                       startActivity(i);
                   }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int which) { 
                       // do nothing
                   }
                })
                .setCancelable(true)
                .show();
               
           }
           
           if (resultCode == RESULT_CANCELED) {    
               //if there's no result
           }
        }
      }
}
