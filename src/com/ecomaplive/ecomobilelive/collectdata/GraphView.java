package com.ecomaplive.ecomobilelive.collectdata;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * GraphView creates a scaled line or bar graph with x and y axis labels.
 * 
 * @author
 * 
 */
public class GraphView extends View
{
    private static final String TAG = "GraphView";

    int maxY;
    private Canvas canvas;
    private Paint paint;
    private int value;
    private String[] verlabels;
    private String title;

    private List<Float> yCoordinates = new ArrayList<Float>();
    private List<Float> accelYCoordinates = new ArrayList<Float>();
    private List<Float> accelZCoordinates = new ArrayList<Float>();

    private static final int DEFAULT_X_INCREMENT = 10;
    
    private float width = 10.0f;
    private float graphheight;
    private float border;

    private int lastX;
    private int lastY;
    private boolean firstPoint;
    private boolean xAccelerationPlotOn = false;
    private boolean yAccelerationPlotOn = false;
    private boolean zAccelerationPlotOn = false;
    private boolean defaultPlotOn = false;

    private boolean disableAccelConversion;
    
    private int startX, incrementX;

    //set after data to be plotted is determined
    private int maximumPlottableValue = 0;

    private int DATA_TO_PLOT = 99;

    public GraphView(Context context)
    {
        super(context);
        init();
    }

    public GraphView(Context context, AttributeSet attributes)
    {
        super(context, attributes);
    }

    @Override
    public void onFinishInflate()
    {
        init();
    }

    int bufferSize;

    public void init()
    {
        this.lastX = 0;
        this.lastY = value;
        this.title= setTitle();
        this.maxY = 0;
        this.startX = 45;
        this.incrementX = DEFAULT_X_INCREMENT;
        this.bufferSize = -1;
        this.disableAccelConversion = false;
        //this.verlabels = new String[] { "60000", "50000", "40000", "30000", "20000", "10000", "0" };
        //this.verlabels = new String[] { "0", "1000", "2000", "3000", "4000", "5000" };    
        this.verlabels = new String[]{};
        paint = new Paint();
        paint.setStrokeWidth(5);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        //Log.w(TAG, "JUST started onDraw");
        super.onDraw(canvas);
        this.canvas = canvas;       

        border = 20;
        int i, j;
        float horstart = border * 2;
        float height = getHeight() - 20;

        if(bufferSize == -1) setGraphWidth(getWidth());
        /*
        graphheight = height - (2 * border);

        paint.setTextAlign(Align.LEFT);
        int vers = verlabels.length - 1;
        //draw the labels for eda, temp, battery and heart rate

        for (i=0;i<verlabels.length;i++) {
            paint.setColor(Color.DKGRAY);
            float y = ((graphheight / vers) * i) + border;
            this.canvas.drawLine(horstart, y, 1000*width, y, paint);
            if (i < vers) {
                this.canvas.drawLine(horstart, y, horstart, (graphheight / vers)
         * (i + 1) + border, paint);
            }
            paint.setColor(Color.WHITE);
            this.canvas.drawText(verlabels[i], 0, y, paint);
        }*/

        graphheight = height;// - (2 * border);

        paint.setTextAlign(Align.LEFT);
        int vers = verlabels.length - 1;
        //draw the labels for eda, temp, battery and heart rate

        for (i=0;i<verlabels.length;i++) {
            paint.setColor(Color.DKGRAY);
            float y = ((graphheight / vers) * i)+10;// + border;
            this.canvas.drawLine(horstart, y, 1000*width, y, paint);
            if (i < vers) {
                /*
                this.canvas.drawLine(horstart, y, horstart, (graphheight / vers)
                        * (i + 1) + border, paint);
                */
                this.canvas.drawLine(horstart, y, horstart, (graphheight / vers) * (i + 1)+10, paint);
            }
            paint.setColor(Color.WHITE);
            this.canvas.drawText(verlabels[i], 0, y, paint);
        }


        // trim the list of points
        paint.setColor(Color.RED);
        float y, prevY = 0; 
        //Double y, prevY =0;

        //this is the general routine for plotting eda, temp, accel, battery and heart rate.
        //graph for acceleration which has three plots have 3 separate routines for plotting below

        if(defaultPlotOn){
            for(i=0,j=startX;i<yCoordinates.size();i++,j+=incrementX){

                y = (yCoordinates.get(i));
                /*
            if (this.DATA_TO_PLOT == 2){ //2 is the value if EDA is being plotted
                //want to plot Log values for eda

                y = (float)Math.log10(y + 1);  //1 is an offset to prevent log(y) from being infinity when y =0

            }
                 */


                y = y * graphheight / maximumPlottableValue;
                //y = graphheight - y + border; 
                y = graphheight - y + 10;
                
                this.canvas.drawPoint(j, y, paint);
                if(i != 0){
                    this.canvas.drawLine(j-incrementX, prevY, j, y, paint);
                }
                prevY = y;
            }
        }

        //plot for x-axis acceleration. the x accel points are stored in yCoordinates
        if(xAccelerationPlotOn){
            paint.setColor(Color.RED);
            y =0; prevY = 0; 
            float acc = 0f;
            for(i=0,j=startX;i<yCoordinates.size();i++,j+=incrementX){

                y =(yCoordinates.get(i));

                //acc = (y/250f) - (32768/250); //this is acceleration in units of g -- y/250 should be approx 131
                acc = y;

                if(!disableAccelConversion){
                    //  we are arbitrarily setting the acceleration range from -4 to +4 which is total of 8 units
                    // so max range is 8
                    // note when acc is zero the plot should be a line at half of ymax
                    acc = (acc * graphheight) /8 + (graphheight/2);
                }
                
                //y = graphheight - y + border; 
                Log.w(TAG,"Value of acc :"+String.valueOf(acc));
                //y = y - 40;

                this.canvas.drawPoint(j, acc, paint);
                if(i != 0){
                    this.canvas.drawLine(j-incrementX, prevY, j, acc, paint);
                }
                prevY = acc;
            }
            xAccelerationPlotOn = false;

        }


        //plot for y-axis acceleration
        if(yAccelerationPlotOn){
            paint.setColor(Color.BLUE);
            y =0; prevY = 0; 
            float acc = 0f;
            for(i=0,j=startX;i<accelYCoordinates.size();i++,j+=incrementX){

                y =(accelYCoordinates.get(i));

                //acc = (y/250f) - (32768/250); //this is acceleration in units of g -- y/250 should be approx 131
                acc = y;

                if(!disableAccelConversion){
                    // we are arbitrarily setting the acceleration range from -4 to +4 which is total of 8 units
                    // so max range is 8
                    // note when acc is zero the plot should be a line at half of ymax
                    acc = (acc * graphheight) /8 + (graphheight/2);
    
                    //y = graphheight - y + border; 
                    Log.w(TAG,"Value of acc :"+String.valueOf(acc));
                    //y = y - 40;
                }
                
                this.canvas.drawPoint(j, acc, paint);
                if(i != 0){
                    this.canvas.drawLine(j-incrementX, prevY, j, acc, paint);
                }
                prevY = acc;
            }
            yAccelerationPlotOn = false;

        }

        //plot for y-axis acceleration
        if(zAccelerationPlotOn){
            paint.setColor(Color.GREEN);
            y =0; prevY = 0; 
            float acc = 0f;
            for(i=0,j=startX;i<accelZCoordinates.size();i++,j+=incrementX){

                y =(accelZCoordinates.get(i));

                //acc = (y/250f) - (32768/250); //this is acceleration in units of g -- y/250 should be approx 131
                acc = y;

                if(!disableAccelConversion){
                    // we are arbitrarily setting the acceleration range from -4 to +4 which is total of 8 units
                    // so max range is 8
                    // note when acc is zero the plot should be a line at half of ymax
                    acc = (acc * graphheight) /8 + (graphheight/2);

                    //y = graphheight - y + border; 
                    Log.w(TAG,"Value of acc :"+String.valueOf(acc));
                    //  y = y - 40;
                }

                this.canvas.drawPoint(j, acc, paint);
                if(i != 0){
                    this.canvas.drawLine(j-incrementX, prevY, j, acc, paint);
                }
                prevY = acc;
            }
            zAccelerationPlotOn = false;

        }

        lastX = lastX + (int)width;
        lastY = value;

        paint.setColor(Color.WHITE);
        paint.setTextAlign(Align.CENTER);
        this.canvas.drawText(title, horstart * 2, border - 4, paint);
        this.canvas.drawText("Time(sec) ", horstart * 3, height, paint);
        this.canvas.drawText("-30", horstart*9, height, paint);
    }

    public void addDataPoint(float rawY)
    {
        defaultPlotOn = true;

        xAccelerationPlotOn = false;
        zAccelerationPlotOn = false;
        yAccelerationPlotOn = false;


        if(yCoordinates.size() == bufferSize) yCoordinates.remove(0);

        float plottableY = 0;
        plottableY = convertRawToPlottableData(rawY);
        yCoordinates.add(plottableY);

        //yCoordinates.add(new Integer((int)rawY));
        //if(maxY < rawY) maxY = rawY;
        //Log.w(TAG,"at end of addDataPoint" );
        invalidate();
    }

    public void addXDataPoint(float rawY)
    {
        defaultPlotOn = false;
        xAccelerationPlotOn = true;
        //yAccelerationPlotOn = false;
        //Log.w(TAG,"at BEGINNING addDataPoint"+String.valueOf(rawY) );
        //Log.w(TAG,"X-ACCELERATION   :"+String.valueOf(rawY) );

        if(yCoordinates.size() == bufferSize) yCoordinates.remove(0);

        float plottableY = 0;
        plottableY = convertRawToPlottableData(rawY);
        yCoordinates.add(plottableY);

        //yCoordinates.add(new Integer((int)rawY));
        //if(maxY < rawY) maxY = rawY;
        //Log.w(TAG,"at end of addDataPoint" );
        invalidate();
    }



    public void addYDataPoint(float rawY)
    {   
        defaultPlotOn = false;
        //xAccelerationPlotOn = false;
        yAccelerationPlotOn = true;
        //Log.w(TAG,"Y-ACCELERATION"+String.valueOf(rawY) );
        if(accelYCoordinates.size() == bufferSize) accelYCoordinates.remove(0);

        float plottableY = 0;
        plottableY = convertRawToPlottableData(rawY);
        accelYCoordinates.add(plottableY);

        //yCoordinates.add(new Integer((int)rawY));
        //if(maxY < rawY) maxY = rawY;
        //Log.w(TAG,"at end of addDataPoint" );
        invalidate();
    }
    public void addZDataPoint(float rawY)
    {


        defaultPlotOn = false;
        zAccelerationPlotOn = true;
        //Log.w(TAG,"at BEGINNING addDataPoint"+String.valueOf(rawY) );
        if(accelZCoordinates.size() == bufferSize) accelZCoordinates.remove(0);

        float plottableY = 0;
        plottableY = convertRawToPlottableData(rawY);
        accelZCoordinates.add(plottableY);

        //yCoordinates.add(new Integer((int)rawY));
        //if(maxY < rawY) maxY = rawY;
        //Log.w(TAG,"at end of addDataPoint" );
        invalidate();
    }



    /**
     * Called by Explorer to change data to plot
     * @param dataToPlot
     */
    public void setDataToPlot(int dataToPlot)
    {
        this.DATA_TO_PLOT = dataToPlot;
        this.title = setTitle();
        this.verlabels = setGraphVerticalLabels();
        String s = verlabels[0];

        this.maximumPlottableValue = Integer.parseInt(s);
        if(dataToPlot == Explorer.PLOT_HR_RAW){
            this.incrementX = 2;
            paint.setStrokeWidth(2);
        }else{
            this.incrementX = DEFAULT_X_INCREMENT;
            paint.setStrokeWidth(5);
        }
    }

    public void setupPlot(int dataToPlot, String title, String[] verticalLabels){

        this.DATA_TO_PLOT = dataToPlot;
        this.title = title;
        this.verlabels = verticalLabels;
        String s = verlabels[0];
        this.maximumPlottableValue = Integer.parseInt(s);
        if(dataToPlot == Explorer.PLOT_HR_RAW){
            this.incrementX = 2;
            paint.setStrokeWidth(2);
        }else{
            this.incrementX = DEFAULT_X_INCREMENT;
            paint.setStrokeWidth(5);
        }
    }
    
    /**
     * Called by Explorer to clear graph before plotting different data
     */
    public void clear()
    {
        yCoordinates.clear();
        accelZCoordinates.clear();
        accelYCoordinates.clear();
        invalidate();
    }

    void setGraphWidth(int width)
    {
        bufferSize = ((width - startX) / incrementX) + 1;
        while(yCoordinates.size() > bufferSize) yCoordinates.remove(0);
    }

    private String setTitle()
    {
        //
        // TODO: add a title for each PLOT_(*) variable
        // 
        switch(DATA_TO_PLOT){
        case Explorer.PLOT_EDA_BIAS:
            return "Eda_Bias";
        case Explorer.PLOT_EDA_P:
            return "EDA P";
        case Explorer.PLOT_EDA:
            return "EDA";
        case Explorer.PLOT_TEMPERATURE:
            return "Temperature (C)";
        case Explorer.PLOT_MOTION_X:
            return "Motion_X";
        case Explorer.PLOT_MOTION_Y:
            return "Motion_Y";
        case Explorer.PLOT_MOTION_Z:
            return "Motion_Z";
        case Explorer.PLOT_BATTERY:
            return "Battery Level";
        case Explorer.PLOT_HEARTRATE:
            return "Heart Rate (bpm)";
        case Explorer.PLOT_HEARTRATE_AVERAGE:
            return "Avg. Heart Rate (bpm)";
        case Explorer.PLOT_HRV:
            return "HR Variability";
        case Explorer.PLOT_ACCELERATION:
            return "Acceleration (g)";
        case Explorer.PLOT_AMBIENT_TEMP:
            return "Temp (C)";
        case Explorer.PLOT_AMBIENT_HUMIDITY:
            return "RH (%)";
        case Explorer.PLOT_AMBIENT_SOUND:
            return "Sound (raw)";
        case Explorer.PLOT_TOTAL_VISIBLE_LIGHT:
            return "Light Level";
        case Explorer.PLOT_TOTAL_INFRARED_LIGHT:
            return "Light Level";
        case Explorer.PLOT_RED_LIGHT_LEVEL:
            return "Light Level";
        case Explorer.PLOT_BLUE_LIGHT_LEVEL:
            return "Light Level";
        case Explorer.PLOT_GREEN_LIGHT_LEVEL:
            return "Light Level";           
        default:
            return "Data to plot not selected"; 
        }
    }
    
    private String[] setGraphVerticalLabels(){
        //TODO: change the String arrays returned to real values
        //
        // TODO: this sets the vertical scale used.
        // The numbers are the numeric values on the horizontal lines
        // There will be 1 line for every value in the array
        // The arrays are top of the display (first entry in the array)
        // to the bottom (last entry in the array).
        //
        switch(DATA_TO_PLOT){
        case Explorer.PLOT_EDA_BIAS:
            return new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_EDA_P:
            return new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_EDA:
            //return new String[] { "2", "1.6", "1.2", "0.8", "0.4", "0" };
            //return new String[] { "20", "16", "12", "8", "4", "0" };
            return new String[] { "5", "4", "3", "2", "1", "0" };
        case Explorer.PLOT_TEMPERATURE:
            return new String[] { "40", "30", "20", "10", "0"};
        case Explorer.PLOT_AMBIENT_TEMP:
            return new String[] { "50", "40", "30", "20", "10", "0"};
        case Explorer.PLOT_AMBIENT_HUMIDITY:
            return new String[] { "100", "80", "60", "40", "20", "0"};
        case Explorer.PLOT_MOTION_X:
            return new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_MOTION_Y:
            return new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_MOTION_Z:
            return new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_BATTERY:
            return new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_ACCELERATION:
            //return  new String[] { "4", "3","2","1", "0", "-1","-2","-3","-4" };
            return  new String[] { "3","2","1", "0", "-1","-2","-3"};
        case Explorer.PLOT_HEARTRATE:
            return new String[] { "250", "200", "150", "100", "50", "0"};
        case Explorer.PLOT_HEARTRATE_AVERAGE:
            return new String[] { "250", "200", "150", "100", "50", "0"};
        case Explorer.PLOT_HRV:
            return  new String[] { "250", "200","150","100", "50", "0" };
        case Explorer.PLOT_HR_RAW:
            return  new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_AMBIENT_SOUND:
            return  new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_TOTAL_VISIBLE_LIGHT:
            return  new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_TOTAL_INFRARED_LIGHT:
            return  new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_RED_LIGHT_LEVEL:
            return  new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_BLUE_LIGHT_LEVEL:
            return  new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_GREEN_LIGHT_LEVEL:
            return  new String[] { "5000", "4000","3000","2000", "1000", "0" };         
        default:
            return new String[]{ "5000", "4000","3000","2000", "1000", "0" };
        }

    }
    
    private  float convertRawToPlottableData(float value)
    {
        //TODO: compute the correct integers to be returned
        switch(DATA_TO_PLOT){
        case Explorer.PLOT_HR_RAW:
            return value;
        case Explorer.PLOT_EDA_BIAS:
            return 0;
        case Explorer.PLOT_EDA_P:
            return 0;
        case Explorer.PLOT_EDA:
            //normal value is between 5-10. when nervous, value goes to 20. values of over 50 seen in people with autism
            //decision: plot values between 0 and 25
            return value;
        case Explorer.PLOT_TEMPERATURE:
            return value;
        case Explorer.PLOT_MOTION_X:
            return 0;
        case Explorer.PLOT_MOTION_Y:
            return 0;
        case Explorer.PLOT_MOTION_Z:
            return 0;
        case Explorer.PLOT_BATTERY:
            return 0;
        case Explorer.PLOT_HEARTRATE:
            return value;
        case Explorer.PLOT_HEARTRATE_AVERAGE:
            return value;
        case Explorer.PLOT_ACCELERATION:
            return value;
        case Explorer.PLOT_HRV:
            return value;
        case Explorer.PLOT_AMBIENT_TEMP:
            return value;
        case Explorer.PLOT_AMBIENT_HUMIDITY:
            return value;
        case Explorer.PLOT_AMBIENT_SOUND:
            return value;
        case Explorer.PLOT_TOTAL_VISIBLE_LIGHT:
            return value;
        case Explorer.PLOT_TOTAL_INFRARED_LIGHT:
            return value;
        case Explorer.PLOT_RED_LIGHT_LEVEL:
            return value;
        case Explorer.PLOT_BLUE_LIGHT_LEVEL:
            return value;
        case Explorer.PLOT_GREEN_LIGHT_LEVEL:
            return value;           
        default:
            return 0;   
        }
    }
    
    public String[] getGraphVerticalLabels(int selection)
    {
        switch(selection){
        case Explorer.PLOT_EDA_BIAS:
            return new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_EDA_P:
            return new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_EDA:
            //return new String[] { "2", "1.6", "1.2", "0.8", "0.4", "0" };
            //return new String[] { "20", "16", "12", "8", "4", "0" };
            return new String[] { "5", "4", "3", "2", "1", "0" };
        case Explorer.PLOT_TEMPERATURE:
            return new String[] { "50", "40", "30", "20", "10", "0"};
        case Explorer.PLOT_MOTION_X:
            return new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_MOTION_Y:
            return new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_MOTION_Z:
            return new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_BATTERY:
            return new String[] { "5000", "4000","3000","2000", "1000", "0" };
        case Explorer.PLOT_ACCELERATION:
            //return  new String[] { "4", "3","2","1", "0", "-1","-2","-3","-4" };
            return  new String[] { "4", "3","2","1", "0", "-1","-2","-3","-4" };
        case Explorer.PLOT_HEARTRATE:
            return new String[] { "250", "200", "150", "100", "50", "0"};
        case Explorer.PLOT_HEARTRATE_AVERAGE:
            return new String[] { "250", "200", "150", "100", "50", "0"};
        case Explorer.PLOT_HRV:
            //return  new String[] { "4", "3","2","1", "0", "-1","-2","-3","-4" };
            return  new String[] { "250", "200","150","100", "50", "0" };
        case Explorer.PLOT_HR_RAW:
            return  new String[] { "5000", "4000","3000","2000", "1000", "0" };
        default:
            return new String[]{ "5000", "4000","3000","2000", "1000", "0" };
        }
    }
    
    void setDisableAccelConversion(boolean disableAccelConversion)
    {
        this.disableAccelConversion = disableAccelConversion;
    }
}

