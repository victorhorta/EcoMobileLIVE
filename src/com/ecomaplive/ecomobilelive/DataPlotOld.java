package com.ecomaplive.ecomobilelive;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.LineAndPointRenderer;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;

public class DataPlotOld extends Activity implements OnTouchListener {
    private XYPlot mySimpleXYPlot;
    private SimpleXYSeries mySeries;
    private PointF minXY;
    private PointF maxXY;
    ArrayList<ArrayList<String>> overallList;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // getting the overallList...
        //Deneme dene = (Deneme)i.getSerializableExtra("sampleObject");
        Intent i = getIntent();
        overallList = (ArrayList<ArrayList<String>>)i.getSerializableExtra("graph_data");
        
        
        setContentView(R.layout.activity_plot_data); 
        mySimpleXYPlot = (XYPlot) findViewById(R.id.dataXYPlot);
        mySimpleXYPlot.setOnTouchListener(this);

        //Plot layout configurations
        mySimpleXYPlot.getGraphWidget().setTicksPerRangeLabel(1);
        mySimpleXYPlot.getGraphWidget().setTicksPerDomainLabel(1);
        mySimpleXYPlot.getGraphWidget().setRangeValueFormat(
                new DecimalFormat("#####.##"));
        mySimpleXYPlot.getGraphWidget().setDomainValueFormat(
                new DecimalFormat("#####.##"));
        mySimpleXYPlot.getGraphWidget().setRangeLabelWidth(25);
        mySimpleXYPlot.setRangeLabel("");
        mySimpleXYPlot.setDomainLabel("");
        //////mySimpleXYPlot.disableAllMarkup();

        //Creation of the series
        Vector<Double> vector=new Vector<Double>();
//        for (double x=0.0;x<Math.PI*5;x+=Math.PI/20){
//            vector.add(x);
//            vector.add(Math.sin(x));
//        }
        List<Double> xVals = new ArrayList<Double>();
        List<Double> yVals = new ArrayList<Double>();
        for(int pos = 0; pos< overallList.get(0).size(); pos++){
            xVals.add(Double.parseDouble(overallList.get(0).get(pos)));
            yVals.add(Double.parseDouble(overallList.get(1).get(pos)));
        }
        
        
        mySeries = new SimpleXYSeries(xVals, yVals, overallList.get(0).get(0));

//        mySimpleXYPlot.addSeries(mySeries, LineAndPointRenderer.class,
//                new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.rgb(200,
//                        0, 0)));
//////        mySimpleXYPlot.addSeries(mySeries, new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.rgb(200, 0, 0)));
        mySimpleXYPlot.addSeries(mySeries, new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.rgb(200, 0, 0), 0, null));
        

        mySimpleXYPlot.redraw();

        //Set of internal variables for keeping track of the boundaries
        mySimpleXYPlot.calculateMinMaxVals();
        minXY=new PointF(mySimpleXYPlot.getCalculatedMinX().floatValue(),mySimpleXYPlot.getCalculatedMinY().floatValue());
        maxXY=new PointF(mySimpleXYPlot.getCalculatedMaxX().floatValue(),mySimpleXYPlot.getCalculatedMaxY().floatValue());

    }

    // Definition of the touch states
    static final int NONE = 0;
    static final int ONE_FINGER_DRAG = 1;
    static final int TWO_FINGERS_DRAG = 2;
    int mode = NONE;

    PointF firstFinger;
    float lastScrolling;
    float distBetweenFingers;
    float lastZooming;

    @Override
    public boolean onTouch(View arg0, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
        case MotionEvent.ACTION_DOWN: // Start gesture
            firstFinger = new PointF(event.getX(), event.getY());
            mode = ONE_FINGER_DRAG;
            break;
        case MotionEvent.ACTION_UP: 
        case MotionEvent.ACTION_POINTER_UP:
            //When the gesture ends, a thread is created to give inertia to the scrolling and zoom 
            Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        while(Math.abs(lastScrolling)>1f || Math.abs(lastZooming-1)<1.01){ 
                        lastScrolling*=.8;
                        scroll(lastScrolling);
                        lastZooming+=(1-lastZooming)*.2;
                        zoom(lastZooming);
                        mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
                        //try {
                            //((XYPlot)mySimpleXYPlot).postRedraw();
                            mySimpleXYPlot.redraw();
                        //} catch (InterruptedException e) {
                         //   e.printStackTrace();
                        //}
                        // the thread lives until the scrolling and zooming are imperceptible
                    }
                    }
                }, 0);

        case MotionEvent.ACTION_POINTER_DOWN: // second finger
            // controlling the number of events!!! avoids problems on spacing method!
            if(event.getPointerCount()>=2){
                distBetweenFingers = spacing(event);
                // the distance check is done to avoid false alarms
                if (distBetweenFingers > 5f) {
                    mode = TWO_FINGERS_DRAG;
                }                
            }
            break;
        case MotionEvent.ACTION_MOVE:
            if (mode == ONE_FINGER_DRAG) {
                PointF oldFirstFinger=firstFinger;
                firstFinger=new PointF(event.getX(), event.getY());
                lastScrolling=oldFirstFinger.x-firstFinger.x;
                scroll(lastScrolling);
                lastZooming=(firstFinger.y-oldFirstFinger.y)/mySimpleXYPlot.getHeight();
                if (lastZooming<0)
                    lastZooming=1/(1-lastZooming);
                else
                    lastZooming+=1;
                zoom(lastZooming);
                mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
                mySimpleXYPlot.redraw();

            } else if (mode == TWO_FINGERS_DRAG) {
                // controlling the number of events!!! avoids problems on spacing method!
                if(event.getPointerCount()>=2){
                    float oldDist = distBetweenFingers;
                    distBetweenFingers = spacing(event);
                    lastZooming = oldDist / distBetweenFingers;
                    zoom(lastZooming);
                    mySimpleXYPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
                    mySimpleXYPlot.redraw();
                }
            }
            break;
        }
        return true;
    }

    private void zoom(float scale) {
        float domainSpan = maxXY.x  - minXY.x;
        float domainMidPoint = maxXY.x      - domainSpan / 2.0f;
        float offset = domainSpan * scale / 2.0f;
        minXY.x=domainMidPoint- offset;
        maxXY.x=domainMidPoint+offset;
    }

    private void scroll(float pan) {
        float domainSpan = maxXY.x  - minXY.x;
        float step = domainSpan / mySimpleXYPlot.getWidth();
        float offset = pan * step;
        minXY.x+= offset;
        maxXY.x+= offset;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }
}