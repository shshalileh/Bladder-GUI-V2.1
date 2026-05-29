package com.ti.nfcdemo.activities.iso15693;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.MifareUltralight;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.os.Bundle;
import android.os.Handler;
import androidx.core.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.util.PixelUtils;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.util.PlotStatistics;
import com.androidplot.xy.SimpleXYSeries;

//import com.androidplot.series.XYSeries;
import com.androidplot.xy.*;

import java.nio.ByteBuffer;
import java.text.Format;
import java.util.Arrays;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYSeriesRenderer;
import com.flomio.ndef.helper.utils.FlomioNdefHelper;
import com.flomio.ndef.helper.utils.ToastMaker;
import com.flomio.ndef.helper.utils.iso15693.Iso15693ConstructAndSendCommands;
import com.flomio.ndef.helper.utils.iso15693.Iso15693Utils;
import com.flomio.ndef.helper.utils.iso15693.Iso15693WriteMultipleBlocks;
import com.flomio.ndef.helper.utils.iso15693.Iso15693WriteSingleBlock;
import com.flomio.ndef.helper.utils.iso15693.OnCommandExecutedCallBack;
import com.ti.nfcdemo.R;
import com.ti.nfcdemo.activities.nfc.NfcWriteTagBaseActivity;
import com.ti.nfcdemo.data.ReadingStore;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


//public class Iso15693WriteTagActivity extends NfcWriteTagBaseActivity implements OnCommandExecutedCallBack, SensorEventListener {
public class Iso15693WriteTagActivity extends NfcWriteTagBaseActivity implements OnCommandExecutedCallBack {
    private static final int REQUEST_EXPORT_CSV = 1001;
    public static final String PREFS_NAME = "bladder_volume_preferences";
    public static final String KEY_INITIAL_RESISTANCE = "initial_resistance";
    public static final String KEY_SLOPE = "slope";
    public static final String KEY_CALIBRATION_CONSTANT = "calibration_constant";
    public static final double DEFAULT_INITIAL_RESISTANCE = 3.75;
    public static final double DEFAULT_SLOPE = 263.0;
    public static final double DEFAULT_CALIBRATION_CONSTANT = 0.0;

    private static final long DATA_GAP_THRESHOLD_MS = 2000L;
    private static final int DATA_GAP_THRESHOLD_TICKS = 20;
    private static final int TIMER_INTERVAL_MS = 100;
    private static final int HISTORY_WINDOW_TICKS = 100;
    private static final int AXIS_BACKGROUND_COLOR = Color.rgb(64, 64, 64);
    private static final int AXIS_TEXT_COLOR = Color.WHITE;
    private static final int GRID_LINE_COLOR = Color.rgb(210, 210, 210);
    private static final int ORIGIN_LINE_COLOR = Color.rgb(80, 80, 80);

    private static final int RECORDING_NOT_STARTED = 0;
    private static final int RECORDING_ACTIVE = 1;
    private static final int RECORDING_PAUSED = 2;
    private static final int RECORDING_STOPPED = 3;

    private static Iso15693WriteTagActivity activeActivity;

  	byte Command0[] = new byte[] {
  			(byte) 0x01, //-General Control Register -> Start bit is set
  			(byte) 0x00, //-Status Register
  			(byte) 0x03, //-Sensor Control Register -> ADC1,2 being active
  			(byte) 0x03, //-Frequency Register
  			(byte) 0x01, //-Number of Passes Register
  			(byte) 0x01, //-Extra Sampling Register
  			(byte) 0x00,  //-Power Modes
  			(byte) 0x40 
  	};
  	byte Command1[] = new byte[] {
  			(byte) 0x00, //-ADC1 Sensor Skip
  			(byte) 0x00, //-ADC2 Sensor Skip
  			(byte) 0x00, //-ADC0 Sensor Skip
  			(byte) 0x00, //-Internal Sensor Skip
  			(byte) 0x00, //-...
  			(byte) 0x00, //-...
  			(byte) 0x00,  //-...
  			(byte) 0x00 
  	};
  	byte Command2[] = new byte[] {
  			(byte) 0x21, //-ADC1 (Reference) Config reg
  			(byte) 0x23, //-ADC2 (Thermistor) Config reg
  			(byte) 0x18, //-Sensor Control Register
  			(byte) 0x00, //-Frequency Register
  			(byte) 0x00, //-Number of Passes Register
  			(byte) 0x00, //-Extra Sampling Register
  			(byte) 0x00,  //-Power Modes
  			(byte) 0x00 
  	};
  	byte Command3[] = new byte[] {
  			(byte) 0x00, //-General Control Register 
  			(byte) 0x00, //-Status Register
  			(byte) 0x00, //-Sensor Control Register
  			(byte) 0x00, //-Frequency Register
  			(byte) 0x00, //-Number of Passes Register
  			(byte) 0x00, //-Extra Sampling Register
  			(byte) 0x00,  //-Power Modes
  			(byte) 0x00 
  	};
  	byte Command4[] = new byte[] {
  			(byte) 0x00, //-General Control Register 
  			(byte) 0x00, //-Status Register
  			(byte) 0x00, //-Sensor Control Register
  			(byte) 0x00, //-Frequency Register
  			(byte) 0x00, //-Number of Passes Register
  			(byte) 0x00, //-Extra Sampling Register
  			(byte) 0x00,  //-Power Modes
  			(byte) 0x00 
  	};
  	byte Command5[] = new byte[] {
  			(byte) 0x00, //-General Control Register 
  			(byte) 0x00, //-Status Register
  			(byte) 0x00, //-Sensor Control Register
  			(byte) 0x00, //-Frequency Register
  			(byte) 0x00, //-Number of Passes Register
  			(byte) 0x00, //-Extra Sampling Register
  			(byte) 0x00,  //-Power Modes
  			(byte) 0x00 
  	};
  	byte Command6[] = new byte[] {
  			(byte) 0x00, //-General Control Register 
  			(byte) 0x00, //-Status Register
  			(byte) 0x00, //-Sensor Control Register
  			(byte) 0x00, //-Frequency Register
  			(byte) 0x00, //-Number of Passes Register
  			(byte) 0x00, //-Extra Sampling Register
  			(byte) 0x00,  //-Power Modes
  			(byte) 0x00 
  	};
  	byte Command7[] = new byte[] {
  			(byte) 0x00, //-General Control Register 
  			(byte) 0x00, //-Status Register
  			(byte) 0x00, //-Sensor Control Register
  			(byte) 0x00, //-Frequency Register
  			(byte) 0x00, //-Number of Passes Register
  			(byte) 0x00, //-Extra Sampling Register
  			(byte) 0x00,  //-Power Modes
  			(byte) 0x00 
  	};

    

    private static final String LOG_TAG = Iso15693WriteTagActivity.class.getSimpleName();

    private OnCommandExecutedCallBack mOnCommandExecutedCallBack;

    //private EditText mEditText;
    //private Spinner mSpinner;
    private static ProgressDialog mProgressDialog;
    //private EditText mEditTextBlockNumber;
    
 //   private XYPlot plot;  
    
    private static final int HISTORY_SIZE = 10;            // number of points to plot in history
//    private SensorManager sensorMgr = null;
//    private Sensor orSensor = null;

    private XYPlot gsrHistoryPlot = null;
    private static XYPlot aprHistoryPlot = null;

    private CheckBox hwAcceleratedCb;
    private CheckBox showFpsCb;
    private SimpleXYSeries aprLevelsSeries = null;
    
    private static SimpleXYSeries referenceSensorSeries = null;
    private static SimpleXYSeries thermistorSensorSeries = null;
    private static ArrayList<SimpleXYSeries> thermistorPlotSegments = new ArrayList<SimpleXYSeries>();
    private static SimpleXYSeries ADC0SensorSeries = null;
    private static SimpleXYSeries internalSensorSeries = null;
    private static SimpleXYSeries gsrSeries = null;
    private TextView sessionStatusTextView;
    private Button startRecordingButton;
    private Button pauseRecordingButton;
    private Button resumeRecordingButton;
    private Button stopRecordingButton;
    private ReadingStore readingStore;
    private long currentSessionId = -1;
    private int currentSessionSampleCount = 0;
    private int recordingState = RECORDING_NOT_STARTED;
    private static double initialResistanceValue = DEFAULT_INITIAL_RESISTANCE;
    private static double slopeValue = DEFAULT_SLOPE;
    private static double calibrationConstantValue = DEFAULT_CALIBRATION_CONSTANT;

    public static boolean done = true;


    public static int domainCounter = 0;
    private static int lastDataDomainCounter = -1;
    private static long lastDataTimestampMillis = 0L;
    private static int tagGoneCounter = 0;
    private static boolean gapNeeded = false;
    public static  Intent myintent;
    public static Intent myintentOld;
    
    
    private static boolean unit = true; //true is degree F
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    
        setContentView(R.layout.xy_plot_layout);
        activeActivity = this;
        readingStore = new ReadingStore(getApplicationContext());
        currentSessionId = -1;
        currentSessionSampleCount = 0;
        sessionStatusTextView = (TextView) findViewById(R.id.textView1);
        loadCalibrationSettings();
        setupRecordingButtons();
        
        
 //       gsrHistoryPlot = (XYPlot) findViewById(R.id.aprLevelsPlot);
        
        ADC0SensorSeries = new SimpleXYSeries("ADC0 Sensor");
        
        gsrSeries = new SimpleXYSeries("GSR");
 
//        gsrHistoryPlot.setRangeBoundaries(-1000, 13000, BoundaryMode.FIXED);
//        gsrHistoryPlot.setDomainBoundaries(0, 10, BoundaryMode.FIXED);
//        gsrHistoryPlot.addSeries(ADC0SensorSeries, new LineAndPointFormatter(Color.rgb(200, 0, 0), Color.BLACK, null, null));
//        gsrHistoryPlot.setDomainStepValue(5);
//        gsrHistoryPlot.setTicksPerRangeLabel(3);
//        gsrHistoryPlot.setDomainLabel("Sample Index");
//        gsrHistoryPlot.getDomainLabelWidget().pack();
//        gsrHistoryPlot.setRangeLabel("GSR ADC value");
//        gsrHistoryPlot.getRangeLabelWidget().pack();
        domainCounter = 0;
        lastDataDomainCounter = -1;
        lastDataTimestampMillis = 0L;
        tagGoneCounter = 0;
        gapNeeded = false;

        // setup the APR History plot:
        aprHistoryPlot = (XYPlot) findViewById(R.id.aprHistoryPlot);
        aprHistoryPlot.getBackgroundPaint().setColor(AXIS_BACKGROUND_COLOR);
        aprHistoryPlot.getBorderPaint().setColor(AXIS_BACKGROUND_COLOR);
        aprHistoryPlot.getRangeLabelWidget().getLabelPaint().setTextSize(PixelUtils.dpToPix(20));
        aprHistoryPlot.getRangeLabelWidget().getLabelPaint().setColor(AXIS_TEXT_COLOR);
        aprHistoryPlot.getDomainLabelWidget().getLabelPaint().setTextSize(PixelUtils.dpToPix(13));
        aprHistoryPlot.getDomainLabelWidget().getLabelPaint().setColor(AXIS_TEXT_COLOR);
        aprHistoryPlot.getTitleWidget().getLabelPaint().setTextSize(PixelUtils.dpToPix(20));
        aprHistoryPlot.getTitleWidget().getLabelPaint().setColor(AXIS_TEXT_COLOR);

        aprHistoryPlot.getGraphWidget().setMarginTop(PixelUtils.dpToPix(20));
        aprHistoryPlot.getGraphWidget().setMarginLeft(PixelUtils.dpToPix(35));
        aprHistoryPlot.getGraphWidget().setMarginBottom(PixelUtils.dpToPix(25));
        aprHistoryPlot.getGraphWidget().setMarginRight(PixelUtils.dpToPix(20));

        aprHistoryPlot.getGraphWidget().getRangeLabelPaint().setTextSize(PixelUtils.dpToPix(10));
        aprHistoryPlot.getGraphWidget().getRangeOriginLabelPaint().setTextSize(PixelUtils.dpToPix(10));
        aprHistoryPlot.getGraphWidget().getDomainLabelPaint().setTextSize(PixelUtils.dpToPix(10));
        aprHistoryPlot.getGraphWidget().getDomainOriginLabelPaint().setTextSize(PixelUtils.dpToPix(10));
        aprHistoryPlot.getGraphWidget().getRangeLabelPaint().setColor(AXIS_TEXT_COLOR);
        aprHistoryPlot.getGraphWidget().getRangeOriginLabelPaint().setColor(AXIS_TEXT_COLOR);
        aprHistoryPlot.getGraphWidget().getDomainLabelPaint().setColor(AXIS_TEXT_COLOR);
        aprHistoryPlot.getGraphWidget().getDomainOriginLabelPaint().setColor(AXIS_TEXT_COLOR);
        aprHistoryPlot.getGraphWidget().getDomainGridLinePaint().setColor(GRID_LINE_COLOR);
        aprHistoryPlot.getGraphWidget().getRangeGridLinePaint().setColor(GRID_LINE_COLOR);
        aprHistoryPlot.getGraphWidget().getDomainSubGridLinePaint().setColor(GRID_LINE_COLOR);
        aprHistoryPlot.getGraphWidget().getRangeSubGridLinePaint().setColor(GRID_LINE_COLOR);
        aprHistoryPlot.getGraphWidget().getDomainOriginLinePaint().setColor(ORIGIN_LINE_COLOR);
        aprHistoryPlot.getGraphWidget().getRangeOriginLinePaint().setColor(ORIGIN_LINE_COLOR);

        referenceSensorSeries = new SimpleXYSeries("Reference Sensor");
        thermistorSensorSeries = null;
        thermistorPlotSegments.clear();
       
        internalSensorSeries = new SimpleXYSeries("Internal Sensor");
 
        //aprHistoryPlot.setRangeBoundaries(-1500, 120000, BoundaryMode.FIXED);
        //aprHistoryPlot.setRangeBoundaries(62500, 69000, BoundaryMode.FIXED);   
        aprHistoryPlot.setRangeBoundaries(0, 100, BoundaryMode.FIXED);
        aprHistoryPlot.setDomainBoundaries(0, HISTORY_WINDOW_TICKS, BoundaryMode.FIXED);
   //     aprHistoryPlot.addSeries(referenceSensorSeries,  new LineAndPointFormatter(Color.rgb(0, 0, 200), Color.BLACK, null, null));
    //    aprHistoryPlot.addSeries(thermistorSensorSeries, new LineAndPointFormatter(Color.rgb(0, 200, 0), Color.BLACK, null, null));
   //     aprHistoryPlot.addSeries(internalSensorSeries,   new LineAndPointFormatter(Color.rgb(200, 200, 200), Color.BLACK, null, null));
        aprHistoryPlot.setDomainStepValue(11);
        aprHistoryPlot.setTicksPerRangeLabel(1);
   //     aprHistoryPlot.setDomainLabel("Sample Index");
        aprHistoryPlot.getDomainLabelWidget().pack();
        String degreeSymbol = ( ""+(char) 0x00B0 );
        aprHistoryPlot.setRangeLabel("Resistance (Kohm)");
        aprHistoryPlot.setDomainLabel("Time (sec)");
        aprHistoryPlot.getDomainLabelWidget().position(0, XLayoutStyle.ABSOLUTE_FROM_CENTER, 0, YLayoutStyle.RELATIVE_TO_BOTTOM, AnchorPosition.BOTTOM_MIDDLE);
        
        aprHistoryPlot.getDomainLabelWidget().getLabelPaint().setTextSize(PixelUtils.dpToPix(20));
        aprHistoryPlot.getDomainLabelWidget().getLabelPaint().setColor(AXIS_TEXT_COLOR);
        aprHistoryPlot.getDomainLabelWidget().pack();
        // aprHistoryPlot.getGraphWidget().getRangeLabelPaint().setTextSize(PixelUtils.dpToPix(18));
        // aprHistoryPlot.getGraphWidget().getDomainLabelPaint().setTextSize(PixelUtils.dpToPix(10));
        
      
        startNewPlotSegment();
        
//        LineAndPointFormatter s2Format = new LineAndPointFormatter();
//        s2Format.setPointLabelFormatter(new PointLabelFormatter());
//        s2Format.configure(getApplicationContext(),
//                R.xml.lpf2);
//        aprHistoryPlot.addSeries(gsrSeries, s2Format);
        
        aprHistoryPlot.getGraphWidget().getBackgroundPaint().setColor(AXIS_BACKGROUND_COLOR);
        aprHistoryPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
        aprHistoryPlot.getTitleWidget().getLabelPaint().setColor(Color.WHITE);
        aprHistoryPlot.getGraphWidget().setDomainValueFormat(new Format() {
            @Override
            public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                int val = ((Number) obj).intValue();
                return toAppendTo.append(val / 10);
            }

            @Override
            public Object parseObject(String source, ParsePosition pos) {
                return null;
            }
        });
        aprHistoryPlot.getGraphWidget().setRangeValueFormat(new DecimalFormat("0.##"));
        aprHistoryPlot.getLegendWidget().setVisible(false);
        
        // setup checkboxes:
       // hwAcceleratedCb = (CheckBox) findViewById(R.id.hwAccelerationCb);
        final PlotStatistics gsrStats = new PlotStatistics(1000, false);
        final PlotStatistics histStats = new PlotStatistics(1000, false);
        
        //aprLevelsPlot.addListener(levelStats);
    //    gsrHistoryPlot.addListener(gsrStats);
        aprHistoryPlot.addListener(histStats);
//        hwAcceleratedCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(b) {                    
//          //      	gsrHistoryPlot.setLayerType(View.LAYER_TYPE_NONE, null);
//                	aprHistoryPlot.setLayerType(View.LAYER_TYPE_NONE, null);
//                } else {                    
//           //     	gsrHistoryPlot.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//                    aprHistoryPlot.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//                }
//            }
//        });
        
//        showFpsCb = (CheckBox) findViewById(R.id.showFpsCb);
//        showFpsCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                //levelStats.setAnnotatePlotEnabled(b);
//            	gsrStats.setAnnotatePlotEnabled(b);
//                histStats.setAnnotatePlotEnabled(b);
//            }
//        });

        getActionBar().setDisplayHomeAsUpEnabled(true);
        initializeNfcAdapterAndIntentFilters();
        mOnCommandExecutedCallBack = this;
        mProgressDialog = new ProgressDialog(mContext);
        
      startTime = System.currentTimeMillis();
      updateSessionStatus();
      // timerHandler.postDelayed(timerRunnable, 0); // Moved to onResume
    }

//    private void cleanup() {
//        finish();		
//	}

    
  long startTime = 0;

  // runs without a timer by reposting this handler at the end of the runnable
  Handler timerHandler = new Handler();
  Runnable timerRunnable = new Runnable() {

      @Override
      public void run() {
          long millis = System.currentTimeMillis() - startTime;
          int seconds = (int) (millis / 1000);
          int minutes = seconds / 60;
          seconds = seconds % 60;
       
          
          
	  enableForegroundDispatch(mNfcPendingIntent, mWriteTagFilters, null);
	   myintent = getIntent();
	   
	   String action = myintent.getAction();
	   
	   if((myintent != myintentOld) && (action != null))
	   {
		   done = true;
		   myintentOld = myintent;
	   }
	  
	  
	  if (myintent != null) {
		  
	      mTag = myintent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	  }

//          Log.d("DAMIAN", "Seconds is: "+ seconds);
          mProcessNewTags = true;
         
          domainCounter++;
          if (domainCounter > HISTORY_WINDOW_TICKS) {
              aprHistoryPlot.setDomainBoundaries(domainCounter - HISTORY_WINDOW_TICKS, domainCounter, BoundaryMode.FIXED);
          } else {
              aprHistoryPlot.setDomainBoundaries(-1, HISTORY_WINDOW_TICKS, BoundaryMode.FIXED);
          }
          aprHistoryPlot.redraw();
          
          if (mTag != null) {
              tagGoneCounter = 0;
              if (mProcessNewTags) {
              	              
              if (done == true)
              {
              	if((Iso15693Utils.getManufacturer(mTag.getId()) == Iso15693Utils.ISO_15693_TEXAS_INSTRUMENTS )){                            
              		
              		try{
                    Iso15693WriteSingleBlock iso15693WriteSingleBlock = new Iso15693WriteSingleBlock(mContext, mTag, 1, 1);             
                    iso15693WriteSingleBlock.WriteSingleBlock((byte) 1, Command1, mOnCommandExecutedCallBack);
                    iso15693WriteSingleBlock.WriteSingleBlock((byte) 2, Command2, mOnCommandExecutedCallBack);
                    iso15693WriteSingleBlock.WriteSingleBlock((byte) 3, Command3, mOnCommandExecutedCallBack);
                    iso15693WriteSingleBlock.WriteSingleBlock((byte) 4, Command4, mOnCommandExecutedCallBack);
                    iso15693WriteSingleBlock.WriteSingleBlock((byte) 5, Command5, mOnCommandExecutedCallBack);
                    iso15693WriteSingleBlock.WriteSingleBlock((byte) 6, Command6, mOnCommandExecutedCallBack);
                    iso15693WriteSingleBlock.WriteSingleBlock((byte) 7, Command7, mOnCommandExecutedCallBack);
                    iso15693WriteSingleBlock.WriteSingleBlock((byte) 0, Command0, mOnCommandExecutedCallBack);
              		done = false;
              		}catch(Exception e){
              			ToastMaker.makeToastShort(mContext,"Failed to interact with Tag.",ToastMaker.STYLE_INFO);
              		}
              		
              	}
              	
             
              	else{
              		//ToastMaker.makeToastShort(mContext,"Operation not supported for this tag manufacturer",ToastMaker.STYLE_INFO);
              	}
              }
              }
              disableScanningAndDismissScanDialog();
          } else {
              tagGoneCounter++;
              if (tagGoneCounter > DATA_GAP_THRESHOLD_TICKS) {
                  gapNeeded = true;
              }
          }

          mTag = null;
       //   myintent.setAction(null);
          action = null;
          timerHandler.postDelayed(timerRunnable, TIMER_INTERVAL_MS);

      }
  };    
    
	   protected void enableForegroundDispatch(PendingIntent intent,
                IntentFilter[] filters, String[][] techLists) {
try {
String[][] techListsArray = new String[][]{new String[]{MifareUltralight.class.getName(),
                                           Ndef.class.getName(),
                                           NfcA.class.getName()},
                              new String[]{MifareClassic.class.getName(),
                                           Ndef.class.getName(),
                                           NfcA.class.getName()}};
mNfcAdapter.enableForegroundDispatch(this, intent, filters, techListsArray);
} catch (IllegalStateException e) {
//       NfcDebuglog.e(LOG_TAG, "feature not supported or activity not in foreground");
}
}
	   
	   
//	    @Override
//	    public boolean onCreateOptionsMenu(Menu menu) {
//	        MenuInflater inflater = getMenuInflater();
//	        inflater.inflate(R.menu.nfc_read_operation_action_bar_menu, menu);
//	        return true;
//	    }
//	   
//	    @Override
//	    public boolean onOptionsItemSelected(MenuItem item) {
//	        switch (item.getItemId()) {
//	            case R.id.write_tag:
//
//	                return true;
//	            default:
//	                return super.onOptionsItemSelected(item);
//	        }
//	    }
	   
	   @Override
	   public boolean onCreateOptionsMenu(Menu menu) {
	       MenuInflater inflater = getMenuInflater();
	       inflater.inflate(R.menu.nfc_read_operation_action_bar_menu, menu);
	       return true;
	   } 
	   
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	    	int id = item.getItemId();
	        if (id == R.id.Resistance) {
	            item.setChecked(true);
	             aprHistoryPlot.setRangeLabel("Resistance (Kohm)");
	            unit = true;
	            clearSeries();
	            updatePlotBoundaries();
                updateSessionStatus();
	            return true;
	        } else if (id == R.id.Volume) {
	            item.setChecked(true);
	             aprHistoryPlot.setRangeLabel("Volume (%)");
	            unit = false;
	            clearSeries();
	            updatePlotBoundaries();
                updateSessionStatus();
	            return true;
	        } else if (id == R.id.ExportCsv) {
	            startCsvExport();
	            return true;
	        } else {
	            return super.onOptionsItemSelected(item);
	        }
	    }
	   
	   
	    
	public void onResume() {
        super.onResume();
        timerHandler.removeCallbacks(timerRunnable);
        timerHandler.postDelayed(timerRunnable, 0);
//        mProcessNewTags = true;
//        if (mTag != null) {
//            if (mProcessNewTags) {
//            	              
//            	byte Command[] = new byte[] {
//            			(byte) 0x01, //-General Control Register 
//            			(byte) 0x02, //-Status Register
//            			(byte) 0xB2, //-Sensor Control Register
//            			(byte) 0x02, //-Frequency Register
//            			(byte) 0x01, //-Number of Passes Register
//            			(byte) 0x01, //-Extra Sampling Register
//            			(byte) 0x00  //-Power Modes
//            	};
//            	
//            	if((Iso15693Utils.getManufacturer(mTag.getId()) == Iso15693Utils.ISO_15693_TEXAS_INSTRUMENTS )){                            
//            		Iso15693WriteMultipleBlocks iso15693WriteMultipleBlocks = new Iso15693WriteMultipleBlocks(mContext, mTag);
//            		iso15693WriteMultipleBlocks.WriteMultipleBlock((byte) 8, Command, mOnCommandExecutedCallBack);
//            		showProgressDialog();
//            	}else{
//            		ToastMaker.makeToastShort(mContext,"Operation not supported for this tag manufacturer",ToastMaker.STYLE_INFO);
//            	}
//            }
//            disableScanningAndDismissScanDialog();
//        }
    }

    @Override
    public void onPause() {
        super.onPause();
        timerHandler.removeCallbacks(timerRunnable);
    }

    @Override
    protected void onDestroy() {
        if (activeActivity == this) {
            activeActivity = null;
        }
        if (readingStore != null) {
            readingStore.close();
        }
        super.onDestroy();
    }

    public void showProgressDialog() {
        mProgressDialog.setTitle("Please do not remove device! Reading Patch values. ");
        mProgressDialog.show();
    }

    public void dismissProgressDialog() {
        mProgressDialog.dismiss();
    }

    @Override
    public void onCommandExecuted(byte[] data) {
        onWriteCommandExecuted(data);
    }
  

    @Override
    public void onWriteCommandExecuted(byte[] result) {
    	
        dismissProgressDialog();
        if (result != null) {
            String displayText = null;
            switch (result[0]) {
                case 0x00:
                    displayText = "Failed to write tag";
                    mProgressDialog.dismiss();
                    ToastMaker.makeToastShort(mContext, displayText, ToastMaker.STYLE_FAILURE);
                    mTag = null;
                    break;
                case 0x01:
                    displayText = "GSR & temperature successfully read";
                    mProgressDialog.dismiss();
                    ToastMaker.makeToastShort(mContext, displayText, ToastMaker.STYLE_SUCCESS);
                    mTag = null;
                    String dataFromTag = FlomioNdefHelper.mBytesToHexString(result);
                    Log.d(LOG_TAG, "Raw tag hex: " + dataFromTag);

                    String tempString = null;
                    //mEditTextBlockNumber.setText(Integer.parseInt(dataFromTag.substring(6)));

                    
                    //long thermValue = Long.parseLong(dataFromTag.substring(12,14).concat(dataFromTag.substring(10,12)),16);
                    //long refValue = Long.parseLong(dataFromTag.substring(8,10).concat(dataFromTag.substring(6,8)),16);

                    long refValue   = Long.parseLong(dataFromTag.substring(6,8).concat(dataFromTag.substring(4,6)), 16);
                    long thermValue = Long.parseLong(dataFromTag.substring(10,12).concat(dataFromTag.substring(8,10)), 16);
                    //long adc0Value  = Long.parseLong(dataFromTag.substring(14,16).concat(dataFromTag.substring(12,14)), 16);


//                    double resistanceOhms = ((double) thermValue * 100000.0) / ((double) refValue * 4.0);
//                    double volumeMl = (resistanceOhms / 100.0) + 2.5;
//                    double dataToPlot = unit ? resistanceOhms : volumeMl;
                    double resistanceOhms = ((double) thermValue * 100.0) / ((double) refValue * 4.0);
                    double volumeMl = ((resistanceOhms-initialResistanceValue) / initialResistanceValue) * slopeValue + calibrationConstantValue;
                    double dataToPlot = unit ? resistanceOhms : volumeMl;
                    saveReading(dataFromTag, refValue, thermValue, resistanceOhms, volumeMl);

                    Log.d(LOG_TAG, "Calculated resistance: " + resistanceOhms + " ohms, Volume: " + volumeMl + "%");
                    
                    Log.d(LOG_TAG, "Reference Sensor "+dataFromTag.substring(8,10).concat(dataFromTag.substring(6,8)));                       
                    Log.d(LOG_TAG, "Thermistor Sensor "+dataFromTag.substring(12,14).concat(dataFromTag.substring(10,12)));                                       
                    Log.d(LOG_TAG, "ADC0 Sensor "+dataFromTag.substring(16,18).concat(dataFromTag.substring(14,16)));                    
                    Log.d(LOG_TAG, "Internal Sensor "+dataFromTag.substring(20,22).concat(dataFromTag.substring(18,20)));
                          
                    addDataPointToPlot(dataToPlot, Long.parseLong(dataFromTag.substring(16,18).concat(dataFromTag.substring(14,16)),16));

                    // redraw the Plots:
                    updatePlotBoundaries();

           //         gsrHistoryPlot.redraw();
                    aprHistoryPlot.redraw();                                                            
                    break;
                case 0x02:
                    displayText = "Text too long";
                    mProgressDialog.dismiss();
                    ToastMaker.makeToastShort(mContext, displayText, ToastMaker.STYLE_INFO);
                    break;
                case 0x03:
                    displayText = "Failed to talk to tag, try again";
                    mProgressDialog.dismiss();
                    ToastMaker.makeToastShort(mContext, displayText, ToastMaker.STYLE_FAILURE);
                    mTag = null;
                    break;
                case 0x04:
                    displayText = "Tag technology not supported";
                    mProgressDialog.dismiss();
                    ToastMaker.makeToastShort(mContext, displayText, ToastMaker.STYLE_INFO);
                    mTag = null;
                    break;
            }
        }
    }
    
 
	
	static int Arrindex = 0;
	static long histArr[] = new long[10];
	static long histArrGSR[] = new long[10];
	static long max = -100;
	static long min = 500;
	public static void processData(byte[] result) {
        if (activeActivity != null) {
            activeActivity.processDataFromTag(result);
            return;
        }
        processDataForPlot(result);
	}

    private void processDataFromTag(byte[] result) {
        processDataForPlot(result);
        String dataFromTag = FlomioNdefHelper.mBytesToHexString(result);
        long refValue = Long.parseLong(dataFromTag.substring(6,8).concat(dataFromTag.substring(4,6)),16);
        long thermValue  = Long.parseLong(dataFromTag.substring(10,12).concat(dataFromTag.substring(8,10)),16);
        double resistanceOhms = ((double) thermValue * 100.0) / ((double) refValue * 4.0);
        double volumeMl = ((resistanceOhms-initialResistanceValue) / initialResistanceValue) * slopeValue + calibrationConstantValue;
        saveReading(dataFromTag, refValue, thermValue, resistanceOhms, volumeMl);
    }

	private static void processDataForPlot(byte[] result) {
//	 	 displayText = "GSR & temperature successfully read";
        mProgressDialog.dismiss();
   //     ToastMaker.makeToastShort(mContext, displayText, ToastMaker.STYLE_SUCCESS);
       // mTag = null;
        String dataFromTag = FlomioNdefHelper.mBytesToHexString(result);
        Log.d(LOG_TAG, "Raw tag hex (processData): " + dataFromTag);
        String tempString = null;
        //mEditTextBlockNumber.setText(Integer.parseInt(dataFromTag.substring(6)));

        double B_Value = 4330.0;     
        double R0_Value = 100000.0; 
        double T0_Value = 298.15;
        double K0_Temp = 273.15;
        
        long refValue = Long.parseLong(dataFromTag.substring(6,8).concat(dataFromTag.substring(4,6)),16);
        long thermValue  = Long.parseLong(dataFromTag.substring(10,12).concat(dataFromTag.substring(8,10)),16);
      
        long gsrValue = Long.parseLong(dataFromTag.substring(14,16).concat(dataFromTag.substring(12,14)),16); 
        
        gsrValue = (long)(gsrValue*100/16384.0);
        double resistanceOhms = ((double) thermValue * 100.0) / ((double) refValue * 4.0);
        //double resistanceOhms = ((double) thermValue * 100000.0) / (double) refValue;
        double volumeMl = ((resistanceOhms-initialResistanceValue) / initialResistanceValue) * slopeValue + calibrationConstantValue;
        double dataToPlot = unit ? resistanceOhms : volumeMl;

        Log.d(LOG_TAG, "Calculated resistance: " + resistanceOhms + " ohms, Volume: " + volumeMl + " %");

        addDataPointToPlot(dataToPlot, gsrValue);
        
        histArrGSR[Arrindex] = gsrValue; 
        histArr[Arrindex++] = (long) dataToPlot;
        
        if(Arrindex == 10)
        {
        	Arrindex = 0;
        }
//        
        
        if(domainCounter < 10)
        {
        	for(int i=0;i<Arrindex;i++)
        	{
        		if(histArr[i]<histArrGSR[i])
        		{
        			if(histArr[i]<=min)
        			{
        				min = histArr[i];
        			}
        		}
        		else
        		{
        			if(histArrGSR[i]<=min)
        			{
        				min = histArrGSR[i];
        			}	
        		}
        		
        		if(histArr[i]>histArrGSR[i])
        		{
        			if(histArr[i] > max)
        			{
        				max = histArr[i];        		
        			}
        		}
        		else
        		{
           			if(histArrGSR[i] > max)
        			{
        				max = histArrGSR[i];        		
        			}	
        			
        		}
        		
        		
        		
        	}
        }
        else
        {
        	for(int i=0;i<10;i++)
        	{
        		if(i == 0)
        		{
        			min = 500;
        			max = -100;
        		}
        		if(histArr[i]<histArrGSR[i])
        		{
        			if(histArr[i]<=min)
        			{
        				min = histArr[i];
        			}
        		}
        		else
        		{
        			if(histArrGSR[i]<=min)
        			{
        				min = histArrGSR[i];
        			}	
        		}
        		if(histArr[i]>histArrGSR[i])
        		{
        			if(histArr[i] > max)
        			{
        				max = histArr[i];        		
        			}
        		}
        		else
        		{
           			if(histArrGSR[i] > max)
        			{
        				max = histArrGSR[i];        		
        			}	
        			
        		}
        	}
        }
        
        updatePlotBoundaries();
        aprHistoryPlot.redraw();
        done = true;
	}

    private static LineAndPointFormatter createThermistorFormatter() {
        LineAndPointFormatter formatter = new LineAndPointFormatter(Color.BLACK, Color.BLACK, null, new PointLabelFormatter(Color.BLACK));
        formatter.getPointLabelFormatter().getTextPaint().setTextSize(PixelUtils.dpToPix(12));
        formatter.getPointLabelFormatter().getTextPaint().setColor(Color.BLACK);
        formatter.setPointLabeler(new PointLabeler() {
            @Override
            public String getLabel(XYSeries series, int index) {
                Number value = series.getY(index);
                if (value == null) {
                    return "";
                }
                return new DecimalFormat("0.##").format(value);
            }
        });
        return formatter;
    }

    private static void addDataPointToPlot(double dataToPlot, long gsrValue) {
        long now = System.currentTimeMillis();
        boolean dataGapExceeded = lastDataTimestampMillis > 0
                && now - lastDataTimestampMillis > DATA_GAP_THRESHOLD_MS;

        if ((gapNeeded || dataGapExceeded) && lastDataDomainCounter != -1) {
            startNewPlotSegment();
        }
        gapNeeded = false;

        if (thermistorSensorSeries == null) {
            startNewPlotSegment();
        }
        thermistorSensorSeries.addLast(domainCounter, dataToPlot);
        lastDataDomainCounter = domainCounter;
        lastDataTimestampMillis = now;
        gsrSeries.addLast(domainCounter, gsrValue);
        prunePlotSegments();
        while (gsrSeries.size() > HISTORY_WINDOW_TICKS) {
            gsrSeries.removeFirst();
        }
    }

    private static void startNewPlotSegment() {
        if (aprHistoryPlot == null) {
            return;
        }
        thermistorSensorSeries = new SimpleXYSeries("");
        thermistorPlotSegments.add(thermistorSensorSeries);
        aprHistoryPlot.addSeries(thermistorSensorSeries, createThermistorFormatter());
    }

    private static void prunePlotSegments() {
        int firstVisibleDomain = domainCounter - HISTORY_WINDOW_TICKS;
        for (int segmentIndex = 0; segmentIndex < thermistorPlotSegments.size(); segmentIndex++) {
            SimpleXYSeries segment = thermistorPlotSegments.get(segmentIndex);
            while (segment.size() > 0) {
                Number xValue = segment.getX(0);
                if (xValue == null || xValue.intValue() >= firstVisibleDomain) {
                    break;
                }
                segment.removeFirst();
            }
            if (segment.size() == 0 && thermistorPlotSegments.size() > 1) {
                aprHistoryPlot.removeSeries(segment);
                thermistorPlotSegments.remove(segmentIndex);
                segmentIndex--;
            }
        }
    }
    private void loadCalibrationSettings() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        initialResistanceValue = Double.longBitsToDouble(preferences.getLong(
                KEY_INITIAL_RESISTANCE,
                Double.doubleToLongBits(DEFAULT_INITIAL_RESISTANCE)));
        slopeValue = Double.longBitsToDouble(preferences.getLong(
                KEY_SLOPE,
                Double.doubleToLongBits(DEFAULT_SLOPE)));
        if (initialResistanceValue <= 0) {
            initialResistanceValue = DEFAULT_INITIAL_RESISTANCE;
        }
    }

    private void setupRecordingButtons() {
        startRecordingButton = (Button) findViewById(R.id.button_start_recording);
        pauseRecordingButton = (Button) findViewById(R.id.button_pause_recording);
        resumeRecordingButton = (Button) findViewById(R.id.button_resume_recording);
        stopRecordingButton = (Button) findViewById(R.id.button_stop_recording);

        if (startRecordingButton != null) {
            startRecordingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startRecording();
                }
            });
        }
        if (pauseRecordingButton != null) {
            pauseRecordingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pauseRecording();
                }
            });
        }
        if (resumeRecordingButton != null) {
            resumeRecordingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resumeRecording();
                }
            });
        }
        if (stopRecordingButton != null) {
            stopRecordingButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    stopRecording();
                }
            });
        }
    }

    private void saveReading(String rawHex, long referenceAdc, long thermistorAdc,
                             double resistanceKohm, double volumePercent) {
        if (readingStore == null || currentSessionId < 0 || recordingState != RECORDING_ACTIVE) {
            return;
        }
        ReadingStore.Reading reading = new ReadingStore.Reading();
        reading.sessionId = currentSessionId;
        reading.timestampMillis = System.currentTimeMillis();
        reading.sampleIndex = currentSessionSampleCount + 1;
        reading.rawHex = rawHex;
        reading.referenceAdc = referenceAdc;
        reading.thermistorAdc = thermistorAdc;
        reading.resistanceKohm = resistanceKohm;
        reading.volumePercent = volumePercent;
        reading.displayMode = unit ? "Resistance" : "Volume";
        long insertedId = readingStore.insertReading(reading);
        if (insertedId >= 0) {
            currentSessionSampleCount++;
            updateSessionStatus();
        }
    }

    private void startRecording() {
        currentSessionId = readingStore.createSession();
        currentSessionSampleCount = 0;
        recordingState = RECORDING_ACTIVE;
        updateSessionStatus();
        ToastMaker.makeToastShort(mContext, "Recording started", ToastMaker.STYLE_SUCCESS);
    }

    private void pauseRecording() {
        if (recordingState == RECORDING_ACTIVE) {
            recordingState = RECORDING_PAUSED;
            updateSessionStatus();
            ToastMaker.makeToastShort(mContext, "Recording paused", ToastMaker.STYLE_INFO);
        }
    }

    private void resumeRecording() {
        if (recordingState == RECORDING_PAUSED) {
            recordingState = RECORDING_ACTIVE;
            updateSessionStatus();
            ToastMaker.makeToastShort(mContext, "Recording resumed", ToastMaker.STYLE_SUCCESS);
        }
    }

    private void stopRecording() {
        if (recordingState == RECORDING_ACTIVE || recordingState == RECORDING_PAUSED) {
            recordingState = RECORDING_STOPPED;
            updateSessionStatus();
            ToastMaker.makeToastShort(mContext, "Recording stopped", ToastMaker.STYLE_INFO);
        }
    }

    private void startCsvExport() {
        if (readingStore == null || currentSessionId < 0 || currentSessionSampleCount == 0) {
            ToastMaker.makeToastShort(mContext, "No readings to export", ToastMaker.STYLE_INFO);
            return;
        }
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        intent.putExtra(Intent.EXTRA_TITLE, "bladder_volume_" + timestamp + ".csv");
        startActivityForResult(intent, REQUEST_EXPORT_CSV);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EXPORT_CSV && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                exportCurrentSessionToUri(uri);
            }
        }
    }

    private void exportCurrentSessionToUri(Uri uri) {
        List<ReadingStore.Reading> readings = readingStore.getReadingsForSession(currentSessionId);
        if (readings.size() == 0) {
            ToastMaker.makeToastShort(mContext, "No readings to export", ToastMaker.STYLE_INFO);
            return;
        }

        OutputStream outputStream = null;
        OutputStreamWriter writer = null;
        try {
            outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream == null) {
                throw new IOException("Could not open export destination");
            }
            writer = new OutputStreamWriter(outputStream, "UTF-8");
            writer.write("timestamp_iso,sample_index,reference_adc,thermistor_adc,resistance_kohm,volume_percent,display_mode,raw_hex\n");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);
            for (ReadingStore.Reading reading : readings) {
                writer.write(csv(dateFormat.format(new Date(reading.timestampMillis))));
                writer.write(",");
                writer.write(String.valueOf(reading.sampleIndex));
                writer.write(",");
                writer.write(String.valueOf(reading.referenceAdc));
                writer.write(",");
                writer.write(String.valueOf(reading.thermistorAdc));
                writer.write(",");
                writer.write(String.valueOf(reading.resistanceKohm));
                writer.write(",");
                writer.write(String.valueOf(reading.volumePercent));
                writer.write(",");
                writer.write(csv(reading.displayMode));
                writer.write(",");
                writer.write(csv(reading.rawHex));
                writer.write("\n");
            }
            writer.flush();
            ToastMaker.makeToastShort(mContext, "CSV exported", ToastMaker.STYLE_SUCCESS);
        } catch (IOException e) {
            Log.e(LOG_TAG, "CSV export failed", e);
            ToastMaker.makeToastShort(mContext, "CSV export failed", ToastMaker.STYLE_FAILURE);
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignored) {
                }
            } else if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    private String csv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private void updateSessionStatus() {
        if (sessionStatusTextView != null) {
            sessionStatusTextView.setText(getRecordingStateLabel() + " | Saved samples: " + currentSessionSampleCount);
        }
        if (startRecordingButton != null) {
            startRecordingButton.setEnabled(recordingState == RECORDING_NOT_STARTED || recordingState == RECORDING_STOPPED);
        }
        if (pauseRecordingButton != null) {
            pauseRecordingButton.setEnabled(recordingState == RECORDING_ACTIVE);
        }
        if (resumeRecordingButton != null) {
            resumeRecordingButton.setEnabled(recordingState == RECORDING_PAUSED);
        }
        if (stopRecordingButton != null) {
            stopRecordingButton.setEnabled(recordingState == RECORDING_ACTIVE || recordingState == RECORDING_PAUSED);
        }
    }

    private String getRecordingStateLabel() {
        if (recordingState == RECORDING_ACTIVE) {
            return "Recording";
        }
        if (recordingState == RECORDING_PAUSED) {
            return "Paused";
        }
        if (recordingState == RECORDING_STOPPED) {
            return "Stopped";
        }
        return "Not recording";
    }
	private static void updatePlotBoundaries() {
        double rangeMax = -Double.MAX_VALUE;
        double rangeMin = Double.MAX_VALUE;

        if (thermistorPlotSegments == null || thermistorPlotSegments.size() == 0) {
            aprHistoryPlot.setRangeBoundaries(0, unit ? 100 : 50, BoundaryMode.FIXED);
            aprHistoryPlot.redraw();
            return;
        }

        for (int segmentIndex = 0; segmentIndex < thermistorPlotSegments.size(); segmentIndex++) {
            SimpleXYSeries segment = thermistorPlotSegments.get(segmentIndex);
            for (int i = 0; i < segment.size(); i++) {
                Number val = segment.getY(i);
                if (val != null) {
                    double dVal = val.doubleValue();
                    if (dVal > rangeMax) rangeMax = dVal;
                    if (dVal < rangeMin) rangeMin = dVal;
                }
            }
        }

        if (rangeMin == Double.MAX_VALUE) {
            rangeMin = 0;
            rangeMax = unit ? 100 : 50;
        }

        double delta = rangeMax - rangeMin;
        double buffer = delta * 0.2;
        if (delta == 0) buffer = 2.5;

        double lower = rangeMin - buffer;
        double upper = rangeMax + buffer;

        if (upper - lower < 5) {
            double center = (upper + lower) / 2.0;
            lower = center - 2.5;
            upper = center + 2.5;
        }

        aprHistoryPlot.setRangeBoundaries(lower, upper, BoundaryMode.FIXED);
        aprHistoryPlot.redraw();
    }

    private static void clearSeries() {
        if (thermistorPlotSegments != null) {
            for (int segmentIndex = 0; segmentIndex < thermistorPlotSegments.size(); segmentIndex++) {
                SimpleXYSeries segment = thermistorPlotSegments.get(segmentIndex);
                while (segment.size() > 0) {
                    segment.removeFirst();
                }
            }
            thermistorPlotSegments.clear();
            startNewPlotSegment();
        }
        if (gsrSeries != null) {
            while (gsrSeries.size() > 0) {
                gsrSeries.removeFirst();
            }
        }
        domainCounter = 0;
        lastDataDomainCounter = -1;
        lastDataTimestampMillis = 0L;
        tagGoneCounter = 0;
        gapNeeded = false;
    }
}

