package com.ecomaplive.ecomobilelive;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class DataManager extends ListActivity {
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        preparePlotData(position);
    }

    private ArrayAdapter<File> files;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        files = null;
        registerForContextMenu(getListView());
    }

    @Override
    protected void onResume() {
        super.onResume();
        fillData();
    }

    private void fillData() {
        // Use an existing ListAdapter that will map an array
        // of strings to TextViews
        List<File> recordings = readFiles();
        if (recordings != null) {
            files = new ArrayAdapter<File>(this, android.R.layout.simple_list_item_1, recordings) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View v = convertView;
                    if (v == null) {
                        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        v = vi.inflate(android.R.layout.simple_list_item_1, parent, false);
                    }
                    TextView tv = (TextView) v.findViewById(android.R.id.text1);
                    if (tv != null) {
                        File f = (File) getItem(position);
                        tv.setText(f.getName());
                    }
                    return v;
                }
            };
        } else {
            files = null;
        }
        setListAdapter(files);
    }

    private List<File> readFiles() {
        List<File> res = null;
        boolean mExternalStorageAvailable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = true;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = false;
        }

        if (mExternalStorageAvailable) {
            File parentDirectory = new File(Environment.getExternalStorageDirectory(),
                    DataExplorer.STORAGE_DIR);
            if (parentDirectory.exists()) {
                File[] f = parentDirectory.listFiles(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return (name.substring(Math.max(0, name.length() - 4)).toLowerCase(
                                Locale.ENGLISH).equals(".csv"));
                    }
                });
                if (f != null && f.length > 0) {
                    res = new ArrayList<File>(f.length);
                    for (int i = 0, j = f.length; i < j; i++)
                        res.add(f[i]);
                }
            }
        }
        return res;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        // inflate the menu
        getMenuInflater().inflate(R.menu.filemenu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
        case R.id.menu_delete:
            confirmDeleteItem(info.position);
            break;
        case R.id.menu_plot_data:
            preparePlotData(info.position);
            break;
        case R.id.menu_map_data:
            prepareMapData(info.position);
            break;
        }
        return super.onContextItemSelected(item);
    }

    void confirmDeleteItem(final long rowId) {
        final File f = files.getItem((int) rowId);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.label_confirm_delete))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.button_delete),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                f.delete();
                                fillData();
                            }
                        })
                .setNegativeButton(getString(R.string.button_cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    
    void preparePlotData(final long rowId) {
        final File f = files.getItem((int) rowId);
        Intent i = new Intent(getApplicationContext(), DataPlotSelect.class);
        i.putExtra("file_path",f.getAbsolutePath());
        startActivity(i);
    }
    
    void prepareMapData(final long rowId) {
        final File f = files.getItem((int) rowId);
        //Intent i = new Intent(getApplicationContext(), DataPlotGraph.class);
        Intent i = new Intent(getApplicationContext(), DataMapSelect.class);
        i.putExtra("file_path",f.getAbsolutePath());
        startActivity(i);
    }
}
