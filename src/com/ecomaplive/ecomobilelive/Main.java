package com.ecomaplive.ecomobilelive;

import java.util.ArrayList;

import com.ecomaplive.ecomobilelive.collectdata.Explorer;
import com.ecomaplive.ecomobilelive.fragments.MainFragments;

import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class Main extends Activity implements OnClickListener {
    public final static String TAG = "Main";

    public static final String PREFS_NAME = "UserPreferences";

    ArrayList<ImageButton> buttons = new ArrayList<ImageButton>();
    private CheckBox checkbox;

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
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        boolean rememberLastDevices = settings.getBoolean("rememberLastDevicesMode", false);
        Log.d(TAG, "Loading checked option as: " + Boolean.toString(rememberLastDevices));
        try {
            menu.findItem(R.id.main_menu_settings).setChecked(rememberLastDevices);
            Log.d(TAG, "Setting checked option as: " + Boolean.toString(rememberLastDevices));
        } catch (Exception e) {

        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.main_menu_settings:
            if (item.isChecked()) {
                item.setChecked(false);
            } else {
                item.setChecked(true);
            }
            // We need an Editor object to make preference changes.
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            Log.d(TAG, "Changing checked option to: " + Boolean.toString(item.isChecked()));
            editor.putBoolean("rememberLastDevicesMode", item.isChecked());

            // Commit the edits!
            editor.commit();
            return true;

        case R.id.main_menu_about:
            // TODO: About screen
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.imageButton_up_left:
            Log.d(TAG, "Starting new intent from: " + v.getTag().toString());
            Intent i1 = new Intent(Main.this, MainFragments.class);
            Main.this.startActivity(i1);
            break;
        case R.id.imageButton_up_right:
            Log.d(TAG, "Starting new intent from: " + v.getTag().toString());
            Intent i2 = new Intent(Main.this, DataManager.class);
            Main.this.startActivity(i2);
            break;
        case R.id.imageButton_down_left:
            Log.d(TAG, "Starting new intent from: " + v.getTag().toString());
            Intent i3 = new Intent(Main.this, DataManagerMain.class);
            Main.this.startActivity(i3);
            break;
        case R.id.imageButton_down_right:
            Log.d(TAG, "Starting new intent from: " + v.getTag().toString());
            
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.ecomaplive.com"));
            startActivity(browserIntent);
            
            break;
        default:
            Log.d(TAG, "onClicked: unexpected!");

        }
    }
}
