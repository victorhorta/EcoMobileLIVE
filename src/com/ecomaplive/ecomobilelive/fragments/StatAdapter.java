package com.ecomaplive.ecomobilelive.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ecomaplive.ecomobilelive.R;

public class StatAdapter extends BaseAdapter {

    Context context;
    StatData[] data;
    private static LayoutInflater inflater = null;

    public StatAdapter(Context context, StatData[] data) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.stat_row, null);
        
        TextView textHeader = (TextView) vi.findViewById(R.id.stat_header);
        textHeader.setText(data[position].getHeader());
        
        TextView textDetails = (TextView) vi.findViewById(R.id.stat_details);
        textDetails.setText(data[position].getDetails());
        return vi;
    }
    
    /**
     * Method used to refresh data being shown! Useful for STAT commands.
     * @param data array of StatData containing new STAT info
     */
    public void updateData(StatData[] data) {
    	this.data = data;
    	notifyDataSetChanged();
    }

}
