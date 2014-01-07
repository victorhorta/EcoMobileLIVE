package com.ecomaplive.ecomobilelive.fragments;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class StatFragment extends ListFragment {
	static StatAdapter mAdapter;
	static StatData[] lastData = new StatData[]{
        new StatData("99", "No device info available", "Refresh STAT to update")
                }; 
	
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
      mAdapter = new StatAdapter(getActivity(), lastData);
      setListAdapter(mAdapter);
    }
    
    /**
     * Callback for updating data being shown on the UI. Useful for STAT messages.
     * 
     * @param data
     */
    public static void updateStatFragments(StatData[] data){
    	lastData = data;
    	mAdapter.updateData(data);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
      // do something with the data

    }
  } 