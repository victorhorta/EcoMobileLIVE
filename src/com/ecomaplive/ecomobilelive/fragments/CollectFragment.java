package com.ecomaplive.ecomobilelive.fragments;

import java.util.ArrayList;
import java.util.Iterator;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.ecomaplive.ecomobilelive.R;
import com.ecomaplive.ecomobilelive.btmanager.BTService;

public class CollectFragment extends Fragment {
    public static final String REQUEST_FILEPATH = "request_filepath";
    
    
    private static String lastTextString;
    private static String amountTextString;
    private static String sessionNameString = "NewProject";
    
    
    // Receives status from BTConnect and reflects its results on screen.
    public static boolean isConnected;
    public static boolean isRecording = false;
    public static ArrayList<String> fieldLabels = new ArrayList<String>();
    public static ArrayList<String> fieldValues = new ArrayList<String>();
    
    private MainFragments mainFragmentsContext;
    
    Button addMarkerButton;
    ToggleButton recordToggleButton;
    TextView  sessionName;
    
    //TextView amountTextView;
    TableLayout fieldsToBeUpdated;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_collect, container, false);
    }
    
    @Override
    public void onStart() {
        super.onStart();

        addMarkerButton = (Button) getActivity().findViewById(R.id.frag_collect_add_marker);
        recordToggleButton = (ToggleButton) getActivity().findViewById(R.id.toggle_button_record);
        recordToggleButton.setChecked(isRecording);
        sessionName = (TextView) getActivity().findViewById(R.id.collect_session_name);
        sessionName.setText(sessionNameString);
//        if(sessionName != null)
//        	sessionName.setId(3001);
        
//        lastTextView = (TextView) getActivity().findViewById(R.id.frag_last_capture_time);
//        amountTextView = (TextView) getActivity().findViewById(R.id.collect_amount_samples);
        
        fieldsToBeUpdated = (TableLayout) getActivity().findViewById(R.id.frag_table_to_be_updated);
        
        
		/////////////////////////////////////////////
//		RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
//				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

//		relativeParams.addRule(RelativeLayout.BELOW,
//				R.id.frag_lastdatareceived);
		
		LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

		// fieldsToBeUpdated.setLayoutParams(new
		// RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
		// RelativeLayout.LayoutParams.WRAP_CONTENT));
		fieldsToBeUpdated.setLayoutParams(linearParams);
		///////////////////////////////////////////
        
        
		Thread fieldLabelsUpdater = new Thread() {

			@Override
			public void run() {
				try {
					while (!isInterrupted()) {
						Thread.sleep(1000);
						updateLatestLabelsAndValues();
					}
				} catch (InterruptedException e) {
				}
			}
		};

		fieldLabelsUpdater.start();
        
        
        
        
        
        if(lastTextString == null) lastTextString = "N/A";
        if(amountTextString == null) amountTextString = "N/A";
        
        
        
        // Project name picker
        sessionName.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builder = new AlertDialog.Builder(mainFragmentsContext);
				builder.setTitle("Set the project name");

				// Set up the input
				final EditText input = new EditText(mainFragmentsContext);
				// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
				input.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
				builder.setView(input);

				// Set up the buttons
				builder.setPositiveButton("OK", new DialogInterface.OnClickListener() { 
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				    	String tempSessionNameString = input.getText().toString().replaceAll("[\\/:\"*?<>|%\0\\s*]+", "");
				    	if(!tempSessionNameString.isEmpty()){
				    		if(!tempSessionNameString.equals(input.getText().toString())) {
				    			Toast.makeText(getActivity(), "Invalid chars replaced. New name set to '" + tempSessionNameString + "'", Toast.LENGTH_SHORT).show();
				    		} else {
				    			Toast.makeText(getActivity(), "New name set to '" + tempSessionNameString + "'", Toast.LENGTH_SHORT).show();
				    		}
				    		sessionNameString = tempSessionNameString;
				    		sessionName.setText(sessionNameString);
				    	} else {
				    		Toast.makeText(getActivity(), "Invalid name", Toast.LENGTH_SHORT).show();
				    	}
				    }
				});
				builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				    @Override
				    public void onClick(DialogInterface dialog, int which) {
				        dialog.cancel();
				    }
				});

				builder.show();
			}
        });
            
            

        // addMarker button
        addMarkerButton.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                // SEND MESSAGE TO BTClient, send filename and ask it to save data
                Intent intentReply = new Intent(BTService.INTENT_SAVE_REQUEST);
                intentReply.putExtra(BTService.EXTRA_SAVE_REQUEST_FILENAME, sessionNameString);
//                intentReply.putExtra(BTService.EXTRA_SAVE_REQUEST_FILEPATH, command);
                intentReply.putExtra(BTService.EXTRA_SAVE_REQUEST_NEXTMARKER, true);
                intentReply.putExtra(BTService.EXTRA_SAVE_REQUEST_START_OR_STOP, recordToggleButton.isChecked());
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intentReply);
                
                // TODO: listen to intent confirming save and update values on the screen

                // Avoids flooding the screen with a lot of marker commands ------
                addMarkerButton.setEnabled(false);
                //sessionName.setEnabled(false);
                addMarkerButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        addMarkerButton.setEnabled(true);
                        //sessionName.setEnabled(true);
                    }
                }, 2000);
                // End of flood avoidance -----------------------------------------
            }
        });
        
     // addMarker button
        recordToggleButton.setOnClickListener(new View.OnClickListener() {
            
            public void onClick(View v) {
                // SEND MESSAGE TO BTClient, send filename and ask it to save data
                Intent intentReply = new Intent(BTService.INTENT_SAVE_REQUEST);
                intentReply.putExtra(BTService.EXTRA_SAVE_REQUEST_FILENAME, sessionNameString);
//                intentReply.putExtra(BTService.EXTRA_SAVE_REQUEST_FILEPATH, command);
                intentReply.putExtra(BTService.EXTRA_SAVE_REQUEST_NEXTMARKER, false);
                intentReply.putExtra(BTService.EXTRA_SAVE_REQUEST_START_OR_STOP, recordToggleButton.isChecked());
                isRecording = recordToggleButton.isChecked();
                
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intentReply);
                
                // Avoids flooding the screen with a lot of record commands ------
                recordToggleButton.setEnabled(false);
                recordToggleButton.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    	recordToggleButton.setEnabled(true);
                    }
                }, 2000);
                // End of flood avoidance -----------------------------------------
            }
        });
        
        
    }
    
    public void onResume() {
    	super.onResume();
    	updateLatestLabelsAndValues();
    }
    
//    public void updateAmountAndTimestamp(String amount, String timestamp) {
//        lastTextString = timestamp;
//        amountTextString = amount;
//        lastTextView.setText(lastTextString);
//        amountTextView.setText(amountTextString);
//    }
    
	public void updateLatestLabelsAndValues() {
		mainFragmentsContext = (MainFragments) getActivity();
		
		// we need to check if it is null, as getActivity() can return null if
		// it is called before onAttach of the respective fragment. 
		if (mainFragmentsContext == null)
			return;

		mainFragmentsContext.runOnUiThread(new Runnable() {
			@Override
			public void run() {

				// Removing previously inserted views (old rows...)
				fieldsToBeUpdated.removeAllViews();
				
/*
				/////////////////////////////////////////////
				RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
						LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
				// relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
				relativeParams.addRule(RelativeLayout.BELOW,
						R.id.frag_text_filename);

				// fieldsToBeUpdated.setLayoutParams(new
				// RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
				// RelativeLayout.LayoutParams.WRAP_CONTENT));
				fieldsToBeUpdated.setLayoutParams(relativeParams);
				///////////////////////////////////////////
				*/

				Iterator<String> fieldLabelsIterator = fieldLabels.iterator();
				Iterator<String> fieldValuesIterator = fieldValues.iterator();

				while (fieldLabelsIterator.hasNext()
						&& fieldValuesIterator.hasNext()) {
					String label = fieldLabelsIterator.next();
					String value = fieldValuesIterator.next();

					/* Create a new row to be added. */
					TableRow tr = new TableRow(mainFragmentsContext);
					tr.setLayoutParams(new TableLayout.LayoutParams(
							android.widget.TableLayout.LayoutParams.MATCH_PARENT,
							TableLayout.LayoutParams.WRAP_CONTENT));

					/* Create textview to be the row-content. */
					TextView textLabel = new TextView(mainFragmentsContext);
					textLabel.setText(label);
					textLabel.setLayoutParams(new TableRow.LayoutParams(
							TableRow.LayoutParams.MATCH_PARENT,
							TableRow.LayoutParams.WRAP_CONTENT));
					textLabel.setPadding(20, 3, 20, 3);
					
					textLabel.setTextAppearance(mainFragmentsContext,
							R.style.boldtext);
					tr.addView(textLabel);

					TextView textValue = new TextView(mainFragmentsContext);
					textValue.setText(value);
					textValue.setLayoutParams(new TableRow.LayoutParams(
							TableRow.LayoutParams.MATCH_PARENT,
							TableRow.LayoutParams.WRAP_CONTENT));
					textValue.setPadding(10, 3, 10, 3);
					// textValue.setTextAppearance(this,
					// R.style.detaileventTime);
					tr.addView(textValue);

					/* Add row to TableLayout. */
					fieldsToBeUpdated.addView(tr, new TableLayout.LayoutParams(
							TableLayout.LayoutParams.MATCH_PARENT,
							TableLayout.LayoutParams.WRAP_CONTENT));
				}
				// addView(fieldsToBeUpdated);

			}
		});
	}
}

