package com.ecomaplive.ecomobilelive.fragments;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.ecomaplive.ecomobilelive.R;
import com.ecomaplive.ecomobilelive.btmanager.BTService;

public class MainFragments extends FragmentActivity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    /* (non-Javadoc)
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i))
                    .setTabListener(this));
        }
           
    }
    
    
    
    @Override
    public void onResume() {
        super.onResume();
        /** Broadcast Managers ------------------------------------------------ */
        LocalBroadcastManager.getInstance(this).registerReceiver(mSaveError,
                new IntentFilter(BTService.INTENT_SAVE_ERROR));

        LocalBroadcastManager.getInstance(this).registerReceiver(mSaveSuccess,
                new IntentFilter(BTService.INTENT_SAVE_SUCCESS));

        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectError,
                new IntentFilter(BTService.INTENT_CONNECT_ERROR));

        LocalBroadcastManager.getInstance(this).registerReceiver(mConnectSuccess,
                new IntentFilter(BTService.INTENT_CONNECT_SUCCESS));

        LocalBroadcastManager.getInstance(this).registerReceiver(mCommandError,
                new IntentFilter(BTService.INTENT_COMMAND_ERROR));

        LocalBroadcastManager.getInstance(this).registerReceiver(mCommandSuccess,
                new IntentFilter(BTService.INTENT_COMMAND_SUCCESS));

        LocalBroadcastManager.getInstance(this).registerReceiver(mStatUpdated,
                new IntentFilter(BTService.INTENT_STAT_UPDATED));
        
        LocalBroadcastManager.getInstance(this).registerReceiver(mDataArrived,
                new IntentFilter(BTService.INTENT_DATA_ARRIVED_FROM_SENSOR));

    }
    
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCommandError);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mCommandSuccess);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mConnectError);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mConnectSuccess);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSaveError);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mSaveSuccess);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mStatUpdated);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDataArrived);
    }
    

    private BroadcastReceiver mSaveError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Error trying to save file", Toast.LENGTH_SHORT).show();
        }
    };
    
    private BroadcastReceiver mSaveSuccess = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String amountData = intent.getStringExtra(BTService.EXTRA_SAVE_SUCCESS_AMOUNTDATA);
            String successFilePath = intent.getStringExtra(BTService.EXTRA_SAVE_SUCCESS_FILEPATH);
            String successTimestamp = intent.getStringExtra(BTService.EXTRA_SAVE_SUCCESS_TIMESTAMP);

            //TODO: FIX-ME!!!!! AMOUNT UPDATER FROM COLLECTFRAGMENT
//            ((CollectFragment)mSectionsPagerAdapter.getItem(1)).updateAmountAndTimestamp(amountData, successTimestamp);
            
            Toast.makeText(getApplicationContext(), "File saved at: " + successFilePath, Toast.LENGTH_SHORT).show();
        }
    };
    
    private BroadcastReceiver mConnectError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Connection terminated", Toast.LENGTH_SHORT).show();
        }
    };
    
    private BroadcastReceiver mConnectSuccess = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Connected to device", Toast.LENGTH_SHORT).show();
            //Updating values to be shown on the screen...
            CollectFragment.isConnected = true;
        }
    };
    
    private BroadcastReceiver mCommandError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Error trying to send command to device", Toast.LENGTH_SHORT).show();
        }
    };
    
    private BroadcastReceiver mCommandSuccess = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(getApplicationContext(), "Command sent to device - wait for reply", Toast.LENGTH_SHORT).show();
        }
    };
    
    private BroadcastReceiver mStatUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO update content from StatFragment!
        	StatData[] data = (StatData[]) intent.getExtras().get(BTService.EXTRA_STAT_UPDATED_DATA); 
            StatFragment.updateStatFragments(data);
            Toast.makeText(getApplicationContext(), "Device status updated", Toast.LENGTH_SHORT).show();
        }
    };
    
    private BroadcastReceiver mDataArrived = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO update content from Collect!!
        	CollectFragment.fieldLabels = (ArrayList<String>) intent.getExtras().get(BTService.EXTRA_DATA_ARRIVED_LABELS);
        	CollectFragment.fieldValues = (ArrayList<String>) intent.getExtras().get(BTService.EXTRA_DATA_ARRIVED_VALUES); 
        }
    };
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_frags, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      int itemId = item.getItemId();
    if (itemId == R.id.menu_frags_stat) {
        Intent commandRequestStat = new Intent(BTService.INTENT_COMMAND_REQUEST);
        commandRequestStat.putExtra(BTService.EXTRA_COMMAND_REQUEST_COMMAND, "STAT");
        LocalBroadcastManager.getInstance(this).sendBroadcast(commandRequestStat);
    } else {
    }

      return true;
    } 
    
    

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a DummySectionFragment (defined as a static inner class
            // below) with the page number as its lone argument.

            Bundle args = new Bundle();
            Fragment fragment = null;

            switch (position) {
            case 0: // Device fragment
                fragment = new DeviceFragment();
                //args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
                //fragment.setArguments(args);
                break;
            case 1: // Collect fragment
                fragment = new CollectFragment();
                break;
            case 2: // Stat fragment
                fragment = new StatFragment();
                break;
            case 3: // Stat fragment
                fragment = new StatFragment();
                break;
            }
            // Bundle args = new Bundle();
            // args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position +
            // 1);
            // fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
            case 0:
                return "Device";
            case 1:
                return "Collect";
            case 2:
                return "Settings";
            }
            return null;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        public static final String ARG_SECTION_NUMBER = "section_number";

        public DummySectionFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main_dummy, container, false);
            TextView dummyTextView = (TextView) rootView.findViewById(R.id.section_label);
            dummyTextView.setText(Integer.toString(getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }

}
