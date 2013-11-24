package com.ecomaplive.ecomobilelive;

import android.app.Activity;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.ExpandableListView;

public class DataManagerMain extends Activity {

    // more efficient than HashMap for mapping integers to objects
    SparseArray<Group> groups = new SparseArray<Group>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_expandable_main);
      createData();
      ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
      DataManagerExpandableListAdapter adapter = new DataManagerExpandableListAdapter(this, groups);
      listView.setAdapter(adapter);
    }

    public void createData() {
      for (int j = 0; j < 5; j++) {
        Group group = new Group("Test " + j);
        for (int i = 0; i < 5; i++) {
          group.children.add("Sub Item" + i);
        }
        groups.append(j, group);
      }
    }

  }
