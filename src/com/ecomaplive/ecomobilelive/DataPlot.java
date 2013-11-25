package com.ecomaplive.ecomobilelive;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.util.FloatMath;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidplot.Plot;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

public class DataPlot extends Activity implements OnTouchListener {
    private static final String TAG = "DataPlot";
    private static final int SERIES_SIZE = 200;
    private XYPlot mySimpleXYPlot;
    private Button resetButton;
    private SimpleXYSeries[] series = null;
    private PointF minXY;
    private PointF maxXY;
    ArrayList<ArrayList<String>> overallList;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hiding the ActionBar
        getActionBar().hide();
        
        // Retrieving the data to be plotted
        overallList = (ArrayList<ArrayList<String>>)getIntent().getSerializableExtra("graph_data");
  
        //Creation of the series - might be a performance bottleneck!
//        Vector<Double> vector =new Vector<Double>();
        List<Double> xVals = new ArrayList<Double>();
        List<Double> yVals = new ArrayList<Double>();
        for(int pos = 0; pos< overallList.get(0).size(); pos++){
            xVals.add(Double.parseDouble(overallList.get(0).get(pos)));
            yVals.add(Double.parseDouble(overallList.get(1).get(pos)));
        }
        
        setContentView(R.layout.touch_zoom_example);
//        resetButton = (Button) findViewById(R.id.resetButton);
//        // resetButton functionality
//        resetButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                minXY.x = series[0].getX(0).floatValue();
//                maxXY.x = series[0].getX(series[0].size() - 1).floatValue();
//                mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.FIXED);
//                mySimpleXYPlot.redraw();
//            }
//        });
        

        
        mySimpleXYPlot = (XYPlot) findViewById(R.id.mySimpleXYPlot);

        // Setting the title - it is stored on position [2][0] as default
        mySimpleXYPlot.setTitle(overallList.get(2).get(0));
        
        mySimpleXYPlot.setOnTouchListener(this);
        mySimpleXYPlot.getGraphWidget().setTicksPerRangeLabel(2);
        mySimpleXYPlot.getGraphWidget().setTicksPerDomainLabel(2);
        mySimpleXYPlot.getGraphWidget().getBackgroundPaint().setColor(Color.TRANSPARENT);
        mySimpleXYPlot.getGraphWidget().setRangeValueFormat(
                new DecimalFormat("#####"));
        mySimpleXYPlot.getGraphWidget().setDomainValueFormat(
                new DecimalFormat("#####.#"));
        mySimpleXYPlot.getGraphWidget().setRangeLabelWidth(25);
        mySimpleXYPlot.setRangeLabel("");
        mySimpleXYPlot.setDomainLabel("");

        mySimpleXYPlot.setBorderStyle(Plot.BorderStyle.NONE, null, null);
        //mySimpleXYPlot.disableAllMarkup();
        series = new SimpleXYSeries[1];

        for (int i = 0; i < 1; i++) {
            series[i] = new SimpleXYSeries("S" + i);
            populateSeriesFromArrayList(series[i], xVals, yVals);
        }
//        mySimpleXYPlot.addSeries(series[3],
//                new LineAndPointFormatter(Color.rgb(50, 0, 0), null,
//                        Color.rgb(100, 0, 0), null));
//        mySimpleXYPlot.addSeries(series[2],
//                new LineAndPointFormatter(Color.rgb(50, 50, 0), null,
//                        Color.rgb(100, 100, 0), null));
//        mySimpleXYPlot.addSeries(series[1],
//                new LineAndPointFormatter(Color.rgb(0, 50, 0), null,
//                        Color.rgb(0, 100, 0), null));
        
//        LineAndPointFormatter seriesFormat = new LineAndPointFormatter(Color.argb(50, 0, 0), null, Color.rgb(100, 0, 0), null);
//        Paint lineFill = new Paint();
//        lineFill.setAlpha(200);
//        lineFill.set
//        lineFill.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.GREEN, Shader.TileMode.MIRROR));
//        seriesFormat.setFillPaint(lineFill);
//        
//        mySimpleXYPlot.addSeries(series[0], seriesFormat);
        
        mySimpleXYPlot.addSeries(series[0],
                new LineAndPointFormatter(Color.rgb(50, 0, 0), null,
                        Color.argb(100, 100, 0, 0), null));
        
        // Removing the series label...
        mySimpleXYPlot.getLayoutManager().remove(mySimpleXYPlot.getLegendWidget());
        mySimpleXYPlot.getLayoutManager().remove(mySimpleXYPlot.getDomainLabelWidget());
        
        
        mySimpleXYPlot.redraw();
        mySimpleXYPlot.calculateMinMaxVals();
        minXY = new PointF(mySimpleXYPlot.getCalculatedMinX().floatValue(),
                mySimpleXYPlot.getCalculatedMinY().floatValue());
        maxXY = new PointF(mySimpleXYPlot.getCalculatedMaxX().floatValue(),
                mySimpleXYPlot.getCalculatedMaxY().floatValue());
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.plotmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case R.id.plot_menu_reset:
            minXY.x = series[0].getX(0).floatValue();
            maxXY.x = series[0].getX(series[0].size() - 1).floatValue();
            mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.FIXED);
            mySimpleXYPlot.redraw();
            return true;

        case R.id.plot_menu_screenshot:
            //final StringBuilder imageFileName = new StringBuilder();
         // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(this);
            View promptsView = li.inflate(R.layout.prompt_setimagename, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.userImageName);

            // set dialog message
            alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                    // get user input and set it to result
                    // edit text
                    //imageFileName.append(userInput.getText().toString());
                        takeScreenshot(userInput.getText().toString());
                    }
                  })
                .setNegativeButton("Cancel",
                  new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                    dialog.cancel();
                    }
                  });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            
            /*
            // TODO: Take screenshot and save it
            mySimpleXYPlot.setDrawingCacheEnabled(true);
            int width = mySimpleXYPlot.getWidth();
            int height = mySimpleXYPlot.getHeight();
            mySimpleXYPlot.measure(width, height);
            Bitmap bmp = Bitmap.createBitmap(mySimpleXYPlot.getDrawingCache());
            mySimpleXYPlot.setDrawingCacheEnabled(false);
            
//            String fileName = new SimpleDateFormat("yyyyMMddhhmm'.png'").format(new Date());
            //String fileName = new Date().getTime() + ".txt";
            
//            FileOutputStream fos;
//            try {
//                fos = new FileOutputStream(fileName, true);
//                bmp.compress(CompressFormat.PNG, 100, fos);
//                
//                Toast.makeText(getBaseContext(), "Image '" + fileName + "' saved.", Toast.LENGTH_SHORT).show(); 
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
            
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
                    String fileName = new SimpleDateFormat("yyyyMMddhhmmss'.png'").format(new Date());
                    
                    FileOutputStream fos;
                    try {
                        fos = new FileOutputStream(parentDirectory.getAbsolutePath() + File.separator + fileName, true);
                        bmp.compress(CompressFormat.PNG, 100, fos);
                        fos.flush();
                        fos.close();
                        
                        Toast.makeText(getBaseContext(), "Image '" + fileName + "' saved.",
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Image created at: " + parentDirectory.getAbsolutePath() + File.separator + fileName);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(getBaseContext(), "Error creating image.",
                                Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    
                }
            }
            

            */
            
            
            return true;
            
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    
    private void populateSeriesFromArrayList(SimpleXYSeries series, List<Double> xVals, List<Double> yVals) {
        //TODO: Limit the number of points to be plotted!! We could use SERIES_SIZE to increase performance!
        Iterator<Double> xIterator = xVals.iterator();
        Iterator<Double> yIterator = yVals.iterator();
        
        while(xIterator.hasNext() && yIterator.hasNext()) {
            series.addLast(xIterator.next(), yIterator.next());
        }
    }

    // Definition of the touch states
    static final int NONE = 0;
    static final int ONE_FINGER_DRAG = 1;
    static final int TWO_FINGERS_DRAG = 2;
    int mode = NONE;

    PointF firstFinger;
    float distBetweenFingers;
    boolean stopThread = false;

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: // Start gesture
                firstFinger = new PointF(event.getX(), event.getY());
                mode = ONE_FINGER_DRAG;
                stopThread = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mode = NONE;
                break;
            case MotionEvent.ACTION_POINTER_DOWN: // second finger
                distBetweenFingers = spacing(event);
                // the distance check is done to avoid false alarms
                if (distBetweenFingers > 5f) {
                    mode = TWO_FINGERS_DRAG;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ONE_FINGER_DRAG) {
                    PointF oldFirstFinger = firstFinger;
                    firstFinger = new PointF(event.getX(), event.getY());
                    scroll(oldFirstFinger.x - firstFinger.x);
                    mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x,
                            BoundaryMode.FIXED);
                    mySimpleXYPlot.redraw();

                } else if (mode == TWO_FINGERS_DRAG) {
                    float oldDist = distBetweenFingers;
                    distBetweenFingers = spacing(event);
                    zoom(oldDist / distBetweenFingers);
                    mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x,
                            BoundaryMode.FIXED);
                    mySimpleXYPlot.redraw();
                }
                break;
        }
        return true;
    }

    private void zoom(float scale) {
        float domainSpan = maxXY.x - minXY.x;
        float domainMidPoint = maxXY.x - domainSpan / 2.0f;
        float offset = domainSpan * scale / 2.0f;

        minXY.x = domainMidPoint - offset;
        maxXY.x = domainMidPoint + offset;

        minXY.x = Math.min(minXY.x, series[0].getX(series[0].size() - 3)
                .floatValue());
        maxXY.x = Math.max(maxXY.x, series[0].getX(1).floatValue());
        clampToDomainBounds(domainSpan);
    }

    private void scroll(float pan) {
        float domainSpan = maxXY.x - minXY.x;
        float step = domainSpan / mySimpleXYPlot.getWidth();
        float offset = pan * step;
        minXY.x = minXY.x + offset;
        maxXY.x = maxXY.x + offset;
        clampToDomainBounds(domainSpan);
    }

    private void clampToDomainBounds(float domainSpan) {
        float leftBoundary = series[0].getX(0).floatValue();
        float rightBoundary = series[0].getX(series[0].size() - 1).floatValue();
        // enforce left scroll boundary:
        if (minXY.x < leftBoundary) {
            minXY.x = leftBoundary;
            maxXY.x = leftBoundary + domainSpan;
        } else if (maxXY.x > series[0].getX(series[0].size() - 1).floatValue()) {
            maxXY.x = rightBoundary;
            minXY.x = rightBoundary - domainSpan;
        }
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }
    
    
    
    private void takeScreenshot(String fileName){
     // TODO: Take screenshot and save it
        mySimpleXYPlot.setDrawingCacheEnabled(true);
        int width = mySimpleXYPlot.getWidth();
        int height = mySimpleXYPlot.getHeight();
        mySimpleXYPlot.measure(width, height);
        Bitmap bmp = Bitmap.createBitmap(mySimpleXYPlot.getDrawingCache());
        mySimpleXYPlot.setDrawingCacheEnabled(false);
        
//        String fileName = new SimpleDateFormat("yyyyMMddhhmm'.png'").format(new Date());
        //String fileName = new Date().getTime() + ".txt";
        
//        FileOutputStream fos;
//        try {
//            fos = new FileOutputStream(fileName, true);
//            bmp.compress(CompressFormat.PNG, 100, fos);
//            
//            Toast.makeText(getBaseContext(), "Image '" + fileName + "' saved.", Toast.LENGTH_SHORT).show(); 
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
        
        boolean mExternalStorageAvailable = false;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // We can read and write the media
            mExternalStorageAvailable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // We can only read the media
            mExternalStorageAvailable = false;
        } else {
            // Something else is wrong. It may be one of many other states, but
            // all we need to know is we can neither read nor write
            mExternalStorageAvailable = false;
        }
        
        if (mExternalStorageAvailable) {
            File parentDirectory = new File(Environment.getExternalStorageDirectory() +
                    DataExplorer.STORAGE_DIR_SCREENSHOTS);

            if (!parentDirectory.exists()) {
                if (parentDirectory.mkdirs()) {
                    Toast.makeText(getBaseContext(), "Unable to set image folder.",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            Log.d(TAG, "image " + parentDirectory.getAbsolutePath());
            // dealing with the fileName:
            fileName = fileName.replaceAll("[\\/:\"*?<>|%\0]+", "");
            if (fileName.isEmpty())
                fileName = new SimpleDateFormat("yyyyMMddhhmmss'.png'").format(new Date());

            String fileType = ".png";
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(parentDirectory.getAbsolutePath() + File.separator
                        + fileName + fileType, true);
                bmp.compress(CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();

                Toast.makeText(getBaseContext(), "Image '" + fileName + "' saved.",
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Image created at: " + parentDirectory.getAbsolutePath()
                        + File.separator + fileName);
            } catch (FileNotFoundException e) {
                Toast.makeText(getBaseContext(), "Error creating image.", Toast.LENGTH_SHORT)
                        .show();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}