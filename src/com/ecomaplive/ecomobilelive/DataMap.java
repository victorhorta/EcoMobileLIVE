package com.ecomaplive.ecomobilelive;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * To generate a point, we need:
 * an (x,y) position (that must be converted into LatLong later)
 * a title (measured label)
 * a snippet (measured data)
 * @author Victor
 *
 */
public class DataMap extends Activity{
    private GoogleMap map;
    ArrayList<ArrayList<String>> overallList;
    
    private final int MAX_POINTS = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // getting the overallList...
        Intent i = getIntent();
        overallList = (ArrayList<ArrayList<String>>)i.getSerializableExtra("graph_data");
        LatLng firstPoint = new LatLng(Double.parseDouble(overallList.get(0).get(0)), 
                                       Double.parseDouble(overallList.get(1).get(0))
                                      );
        
        
        setContentView(R.layout.activity_mapfragment);
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
            .getMap();
      
      // position 0 of the array: Lat values
      // position 1 of the array: Long values
      // position 2 of the array: data value
      // position 3 of the array: timestamp
      // position 4 of the array: labels (parameterWanted, unit)
      Iterator<String> latIterator = overallList.get(0).iterator();
      Iterator<String> lonIterator = overallList.get(1).iterator();
      Iterator<String> dataIterator = overallList.get(2).iterator();
      Iterator<String> timeIterator = overallList.get(3).iterator();
//      while(latIterator.hasNext()) {
//          map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(latIterator.next()), Double.parseDouble(lonIterator.next())))
//                                           .title(dataIterator.next() + " " + overallList.get(4).get(1))
//                                           .snippet((new Timestamp(Long.parseLong(timeIterator.next()))).toString())
//                       );
//      }
      
//      List<Circle> circles = new ArrayList<Circle>();
//      circles.add(
//              map.addCircle(new CircleOptions()
//                                 .center(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)))
//                                 .radius(1)
//                                 .fillColor(color.Aquamarine)
//              )
//            );
      
      List<Marker> markers = new ArrayList<Marker>();
      List<Double> dataDouble = new ArrayList<Double>();
      

      //these will track the lower and higher data values
      double lowerData = Double.MAX_VALUE;
      double higherData = Double.MIN_VALUE;
      
      while(latIterator.hasNext()) {
          
          String lat = latIterator.next();
          String lon = lonIterator.next();
          String data = dataIterator.next();
          double dataTemp = Double.parseDouble(data);
          
          lowerData = (lowerData < dataTemp) ? lowerData : dataTemp;
          higherData = (higherData > dataTemp) ? higherData : dataTemp;
          
          dataDouble.add(dataTemp);

          markers.add(
                        map.addMarker(new MarkerOptions()
                                     .position(new LatLng(Double.parseDouble(lat), Double.parseDouble(lon)))
                                     .title(data + " " + overallList.get(4).get(1))
                                     .snippet((new Timestamp(Long.parseLong(timeIterator.next()))).toString())
                                     .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_circle_map))
                         )
                      );
      }
//       Scaling the colors
      Iterator<Marker> mIterator = markers.iterator();
      Iterator<Double> dataDoubleIterator = dataDouble.iterator();
      
      while(mIterator.hasNext()) {
          Marker m = mIterator.next(); 
          m.setIcon(BitmapDescriptorFactory.fromResource(scaleMarker(dataDoubleIterator.next(), lowerData, higherData)));
          m.setAlpha(0.5f);
      }

      
      
      
      //      Marker hamburg = map.addMarker(new MarkerOptions().position(HAMBURG)
//          .title("Hamburg"));
//      Marker kiel = map.addMarker(new MarkerOptions()
//          .position(KIEL)
//          .title("Kiel")
//          .snippet("Kiel is cool")
//          .icon(BitmapDescriptorFactory
//              .fromResource(R.drawable.ic_launcher)));

      // Move the camera instantly to the first point with a zoom of 15.
      map.moveCamera(CameraUpdateFactory.newLatLngZoom(firstPoint, 15));

      // Zoom in, animating the camera.
      map.animateCamera(CameraUpdateFactory.zoomTo(20), 2000, null);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//      getMenuInflater().inflate(R.menu.activity_main, menu);
//      return true;
//    }
    int scaleMarker(double value, double min, double max) {
        int fraction = (int)(25 - 25*(value - min)/(max - min));
        return(this.getResources().getIdentifier("ic_circle_map"+Integer.toString(fraction), "drawable", this.getPackageName()));
    }
}
