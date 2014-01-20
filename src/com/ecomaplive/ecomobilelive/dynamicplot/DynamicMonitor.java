package com.ecomaplive.ecomobilelive.dynamicplot;
//package dynamicplot;
//
//import java.text.DecimalFormat;
//import java.util.Observable;
//import java.util.Observer;
//
//import android.app.Activity;
//import android.graphics.Color;
//import android.os.Bundle;
//
//import com.androidplot.Plot;
//import com.androidplot.xy.BoundaryMode;
//import com.androidplot.xy.LineAndPointFormatter;
//import com.androidplot.xy.PointLabelFormatter;
//import com.androidplot.xy.XYPlot;
//import com.androidplot.xy.XYSeries;
//import com.androidplot.xy.XYStepMode;
//import com.ecomaplive.ecomobilelive.R;
//
//public class DynamicMonitor extends Activity {
//    //Ideas:
//    //-we need to update the plot when loading the application
//    
//    String activatedMonitor = "";
//    
//    // Redraws a plot whenever an update is received:
//    private class MyPlotUpdater implements Observer {
//        Plot plot;
//
//        public MyPlotUpdater(Plot plot) {
//            this.plot = plot;
//        }
//
//        @Override
//        public void update(Observable o, Object arg) {
//            plot.redraw();
//        }
//    }
//    
//    
//    private XYPlot dynamicPlot;
//    private XYPlot staticPlot;
//    private MyPlotUpdater plotUpdater;
//    SampleDynamicXYDatasource data;
//    private Thread myThread;
//    
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//
//        // android boilerplate stuff
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.dynamicxyplot_example);
//
//        // get handles to our View defined in layout.xml:
//        dynamicPlot = (XYPlot) findViewById(R.id.dynamicXYPlot);
//
//        plotUpdater = new MyPlotUpdater(dynamicPlot);
//
//        // only display whole numbers in domain labels
//        dynamicPlot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));
//
//        // getInstance and position datasets:
//        data = new SampleDynamicXYDatasource();
//        SampleDynamicSeries sine1Series = new SampleDynamicSeries(data, 0, "Sine 1");
//        SampleDynamicSeries sine2Series = new SampleDynamicSeries(data, 1, "Sine 2");
//
//        dynamicPlot.addSeries(sine1Series, new LineAndPointFormatter(Color.rgb(0, 0, 0), null, Color.rgb(0, 80, 0)));
//
//        // create a series using a formatter with some transparency applied:
//        LineAndPointFormatter f1 =
//                new LineAndPointFormatter(Color.rgb(0, 0, 200), null, Color.rgb(0, 0, 80), (PointLabelFormatter) null);
//
//        f1.getFillPaint().setAlpha(220);
//        dynamicPlot.addSeries(sine2Series, f1);
//
//        // hook up the plotUpdater to the data model:
//        data.addObserver(plotUpdater);
//
//        dynamicPlot.setDomainStepMode(XYStepMode.SUBDIVIDE);
//        dynamicPlot.setDomainStepValue(sine1Series.size());
//
//        // thin out domain/range tick labels so they dont overlap each other:
//        dynamicPlot.setTicksPerDomainLabel(5);
//        dynamicPlot.setTicksPerRangeLabel(3);
//
//        // uncomment this line to freeze the range boundaries:
//        dynamicPlot.setRangeBoundaries(-100, 100, BoundaryMode.FIXED);
//    }
//    
//    @Override
//    public void onResume() {
//        // kick off the data generating thread:
//        myThread = new Thread(data);
//        myThread.start();
//        super.onResume();
//    }
//
//    @Override
//    public void onPause() {
//        data.stopThread();
//        super.onPause();
//    }
//
//    class SampleDynamicXYDatasource implements Runnable {
//
//        // encapsulates management of the observers watching this datasource for update events:
//        class MyObservable extends Observable {
//            @Override
//            public void notifyObservers() {
//                setChanged();
//                super.notifyObservers();
//            }
//        }
//
//        private static final int MAX_AMP_SEED = 100;
//        private static final int MIN_AMP_SEED = 10;
//        private static final int AMP_STEP = 5;
//        public static final int SINE1 = 0;
//        public static final int SINE2 = 1;
//        private static final int SAMPLE_SIZE = 30;
//        private int phase = 0;
//        private int sinAmp = 20;
//        private MyObservable notifier;
//        private boolean keepRunning = false;
//
//        {
//            notifier = new MyObservable();
//        }
//
//        public void stopThread() {
//            keepRunning = false;
//        }
//
//        //@Override
//        public void run() {
//            try {
//                keepRunning = true;
//                boolean isRising = true;
//                while (keepRunning) {
//
//                    Thread.sleep(50); // decrease or remove to speed up the refresh rate.
//                    phase++;
//                    if (sinAmp >= MAX_AMP_SEED) {
//                        isRising = false;
//                    } else if (sinAmp <= MIN_AMP_SEED) {
//                        isRising = true;
//                    }
//
//                    if (isRising) {
//                        sinAmp += AMP_STEP;
//                    } else {
//                        sinAmp -= AMP_STEP;
//                    }
//                    notifier.notifyObservers();
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        public int getItemCount(int series) {
//            return SAMPLE_SIZE;
//        }
//
//        public Number getX(int series, int index) {
//            if (index >= SAMPLE_SIZE) {
//                throw new IllegalArgumentException();
//            }
//            return index;
//        }
//
//        public Number getY(int series, int index) {
//            if (index >= SAMPLE_SIZE) {
//                throw new IllegalArgumentException();
//            }
//            double amp = sinAmp * Math.sin(index + phase + 4);
//            switch (series) {
//                case SINE1:
//                    return amp;
//                case SINE2:
//                    return -amp;
//                default:
//                    throw new IllegalArgumentException();
//            }
//        }
//
//        public void addObserver(Observer observer) {
//            notifier.addObserver(observer);
//        }
//
//        public void removeObserver(Observer observer) {
//            notifier.deleteObserver(observer);
//        }
//
//    }
//
//    class SampleDynamicSeries implements XYSeries {
//        private SampleDynamicXYDatasource datasource;
//        private int seriesIndex;
//        private String title;
//
//        public SampleDynamicSeries(SampleDynamicXYDatasource datasource, int seriesIndex, String title) {
//            this.datasource = datasource;
//            this.seriesIndex = seriesIndex;
//            this.title = title;
//        }
//
//        @Override
//        public String getTitle() {
//            return title;
//        }
//
//        @Override
//        public int size() {
//            return datasource.getItemCount(seriesIndex);
//        }
//
//        @Override
//        public Number getX(int index) {
//            return datasource.getX(seriesIndex, index);
//        }
//
//        @Override
//        public Number getY(int index) {
//            return datasource.getY(seriesIndex, index);
//        }
//    }
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    
//    /**
//     * @param parameter string of the field. Comes from the intent, as one of its extras.
//     */
//    private void updatePlot(String parameter) {
//        if(parameter.equals(activatedMonitor)) {
//            // this is just an update of the same data before
//            
//        } else {
//            // another type of monitored data has been picked!
//        }
//    }
//    
//    private void resetPlot() {
//        activatedMonitor = "";
//    }
//
//}
