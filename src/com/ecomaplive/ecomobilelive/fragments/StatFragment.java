package com.ecomaplive.ecomobilelive.fragments;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TimePicker;

import com.ecomaplive.ecomobilelive.R;
import com.ecomaplive.ecomobilelive.btmanager.BTService;

public class StatFragment extends ListFragment {
    static StatAdapter mAdapter;

    // The following StatData[] is intended to be used when there is no connection to
    // the device
    final static StatData[] noConnectionData = new StatData[] { 
        new StatData("99", "No device info available", "Refresh STAT to update",StatDataType.NOSTATRECEIVED),
    };
    
    // Latest stat data received from the device
    static StatData[] lastData = noConnectionData;

    private String valueToBeSent;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new StatAdapter(getActivity(), lastData);
        setListAdapter(mAdapter);
    }

    /**
     * Callback for updating data being shown on the UI. Useful for STAT
     * messages.
     * 
     * @param data
     */
    public static void updateStatFragments(StatData[] data) {
        lastData = data;
        mAdapter.updateData(data);
    }

    @Override
    public void onListItemClick(ListView l, View v, final int position, long id) {
        // do something with the data

        // Checking if the position value is a valid position
        // (avoiding a npe)
        if (position >= lastData.length || position < 0)
            return;

        // For each StatDataType, generate the corresponding dialog screen
        final StatDataType selectedDataType = lastData[position]
                .getStatDataType();

        AlertDialog.Builder alertDialogBuilder;
        // LayoutInflater inflater = getLayoutInflater();
        LayoutInflater inflater = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.dialog_statfragment, null);

        // elements from the screen
        final SeekBar dialogSeekBar = (SeekBar) dialogView
                .findViewById(R.id.dialog_seekbar);
        final TextView dialogSeekBarLabel = (TextView) dialogView
                .findViewById(R.id.dialog_seekbar_label);
        final RadioGroup dialogRadioGroup = (RadioGroup) dialogView
                .findViewById(R.id.dialog_radiogroup);
        final RadioButton dialogRadioButton1 = (RadioButton) dialogView
                .findViewById(R.id.dialog_radiogroup_option1);
        final RadioButton dialogRadioButton2 = (RadioButton) dialogView
                .findViewById(R.id.dialog_radiogroup_option2);
        
        final TextView dialogDecimalHexLabel = (TextView) dialogView
                .findViewById(R.id.dialog_decimal_hex_label);
        final EditText dialogDecimalText = (EditText) dialogView
                .findViewById(R.id.dialog_decimal);
        final EditText dialogHexText = (EditText) dialogView
                .findViewById(R.id.dialog_hex);
        final DatePicker dialogDatePicker = (DatePicker) dialogView
                .findViewById(R.id.dialog_datepicker);
        final TimePicker dialogTimePicker = (TimePicker) dialogView
                .findViewById(R.id.dialog_timepicker);

        dialogSeekBar.setVisibility(View.GONE);
        dialogSeekBarLabel.setVisibility(View.GONE);
        dialogRadioButton1.setVisibility(View.GONE);
        dialogRadioButton2.setVisibility(View.GONE);
        dialogDecimalHexLabel.setVisibility(View.GONE);
        dialogDecimalText.setVisibility(View.GONE);
        dialogHexText.setVisibility(View.GONE);
        dialogDatePicker.setVisibility(View.GONE);
        dialogTimePicker.setVisibility(View.GONE);

        boolean isCancelableAndHasNoAction = false;
        String positiveButtonLabel = "Update";
        String negativeButtonLabel = "Cancel";

        // Common features:
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(dialogView);

        switch (selectedDataType) {
            case NOSTATRECEIVED:
                positiveButtonLabel = "Update";
                break;

            case UNCHANGEABLE:
                isCancelableAndHasNoAction = true;
                negativeButtonLabel = "OK";
                break;

            case RADIOBUTTON_ENABLE_DISABLE:
                dialogRadioButton1.setVisibility(View.VISIBLE);
                dialogRadioButton1.setText("Enable");
                dialogRadioButton2.setVisibility(View.VISIBLE);
                dialogRadioButton2.setText("Disable");
                break;

            case RADIOBUTTON_ON_OFF:
                dialogRadioButton1.setVisibility(View.VISIBLE);
                dialogRadioButton1.setText("ON");
                dialogRadioButton2.setVisibility(View.VISIBLE);
                dialogRadioButton2.setText("OFF");
                break;

            case RADIOBUTTON_POS_NEG:
                dialogRadioButton1.setVisibility(View.VISIBLE);
                dialogRadioButton1.setText("Positive");
                dialogRadioButton2.setVisibility(View.VISIBLE);
                dialogRadioButton2.setText("Negative");
                break;

            case SLIDER_0_1000:
                dialogSeekBar.setVisibility(View.VISIBLE);
                dialogSeekBarLabel.setVisibility(View.VISIBLE);

                dialogSeekBar.setMax(1000);
                dialogSeekBar.setProgress(0);
                dialogSeekBar
                        .setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {
                                // TODO Auto-generated method stub
                            }

                            @Override
                            public void onProgressChanged(SeekBar seekBar,
                                    int progress, boolean fromUser) {
                                // TODO Auto-generated method stub
                                dialogSeekBarLabel.setText(String
                                        .valueOf(progress));
                            }
                        });
                break;

            case TEXTFIELD_DECIMAL:
                dialogDecimalHexLabel.setVisibility(View.VISIBLE);
                dialogDecimalText.setVisibility(View.VISIBLE);
                break;

            case TEXTFIELD_HEX:
                dialogDecimalHexLabel.setVisibility(View.VISIBLE);
                dialogHexText.setVisibility(View.VISIBLE);
                break;

            case DATEPICKER:
                dialogDatePicker.setVisibility(View.VISIBLE);
                break;

            case TIMEPICKER:
                dialogTimePicker.setVisibility(View.VISIBLE);
                break;
            
            case DATEANDTIMEPICKER:
                // Use the current date as the default date in the picker
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                int hour=c.get(Calendar.HOUR_OF_DAY);
                int min=c.get(Calendar.MINUTE);
                
                
                dialogDatePicker.updateDate(year, month, day);
                dialogTimePicker.setCurrentHour(hour);
                dialogTimePicker.setCurrentMinute(min);
                
                dialogDatePicker.setVisibility(View.VISIBLE);
                dialogTimePicker.setVisibility(View.VISIBLE);
                break;
        }

        // Common characteristics
        alertDialogBuilder.setTitle("Set parameter")
                        .setMessage(lastData[position].getHeader() + "\n"+ selectedDataType.getDialogMessage());
        alertDialogBuilder.setCancelable(isCancelableAndHasNoAction)
                .setNegativeButton(negativeButtonLabel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // if this button is clicked, close the dialog
                                dialog.cancel();
                            }
                        });

        // Setting PositiveButton behavior according to the field datatype
        if (!isCancelableAndHasNoAction) {

            alertDialogBuilder.setPositiveButton(positiveButtonLabel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            int selectedId;
                            String radioSelection = "";

                            switch (selectedDataType) {
                                case NOSTATRECEIVED:
                                    valueToBeSent = "STAT";
                                    break;

                                case RADIOBUTTON_ENABLE_DISABLE:
                                    selectedId = dialogRadioGroup.getCheckedRadioButtonId();
                                    radioSelection = ""; 
                                    
                                    if(dialogRadioGroup.getCheckedRadioButtonId() != -1) {
                                        // Option1 will send 0,
                                        // Option2 will send 1.
                                        radioSelection = (selectedId == R.id.dialog_radiogroup_option1)? "0" : "1";                                
                                    }
                                    
                                    valueToBeSent = "SET " + lastData[position].getRegisterForDeviceCommands() + " " + radioSelection;
                                    break;

                                case RADIOBUTTON_ON_OFF:
                                    selectedId = dialogRadioGroup.getCheckedRadioButtonId();
                                    radioSelection = ""; 
                                    
                                    if(dialogRadioGroup.getCheckedRadioButtonId() != -1) {
                                        // Option1 will send 0,
                                        // Option2 will send 1.
                                        radioSelection = (selectedId == R.id.dialog_radiogroup_option1)? "0" : "1";                                
                                    }
                                    
                                    valueToBeSent = "SET " + lastData[position].getRegisterForDeviceCommands() + " " + radioSelection;
                                    break;

                                case RADIOBUTTON_POS_NEG:
                                    selectedId = dialogRadioGroup.getCheckedRadioButtonId();
                                    radioSelection = ""; 
                                    
                                    if(dialogRadioGroup.getCheckedRadioButtonId() != -1) {
                                        // Option1 will send 0,
                                        // Option2 will send 1.
                                        radioSelection = (selectedId == R.id.dialog_radiogroup_option1)? "0" : "1";                                
                                    }
                                    
                                    valueToBeSent = "SET " + lastData[position].getRegisterForDeviceCommands() + " " + radioSelection;
                                    break;

                                case SLIDER_0_1000:
                                    valueToBeSent = "SET "
                                            + lastData[position].getRegisterForDeviceCommands()
                                            + " "
                                            + Integer.toHexString(Integer.parseInt(dialogSeekBarLabel.getText().toString()));
                                    break;

                                case TEXTFIELD_DECIMAL:
                                    valueToBeSent = "SET "
                                            + lastData[position].getRegisterForDeviceCommands()
                                            + " "
                                            + Float.toHexString(Float.parseFloat(dialogSeekBarLabel.getText().toString()));
                                            //+ dialogDecimalText.getText()
                                            //        .toString();
                                    break;

                                case TEXTFIELD_HEX:
                                    valueToBeSent = "SET "
                                            + lastData[position].getRegisterForDeviceCommands()
                                            + " "
                                            + dialogHexText.getText()
                                                    .toString();
                                    break;

                                case DATEPICKER:
                                    // TODO:
                                    // dialogDatePicker.setVisibility(View.VISIBLE);
                                    break;

                                case TIMEPICKER:
                                    // TODO:
                                    // dialogTimePicker.setVisibility(View.VISIBLE);
                                    break;
                                
                                case DATEANDTIMEPICKER:
                                    int currentYear = dialogDatePicker.getYear();
                                    int currentMonth = dialogDatePicker.getMonth();
                                    int currentDay = dialogDatePicker.getDayOfMonth();
                                    int currentHour = dialogTimePicker.getCurrentHour();
                                    int currentMin = dialogTimePicker.getCurrentMinute();

//                                    Calendar pickedDate = new GregorianCalendar(currentYear, currentMonth + 1, currentDay, currentHour, currentMin, 0);
                                    Calendar pickedDate = new GregorianCalendar();
                                    pickedDate.set(currentYear, currentMonth, currentDay, currentHour, currentMin, 0);
                                    
                                    String epochPickedHexDate = Long.toHexString(pickedDate.getTime().getTime()/1000L);
                                    
                                    valueToBeSent = "SET "
                                            + lastData[position].getRegisterForDeviceCommands()
                                            + " "
                                            + epochPickedHexDate;
                                    break;
                            }
                            
                            //SENDING HERE THE 'SET ...' COMMAND TO THE SENSOR!!!
                            Intent commandRequestStat = new Intent(BTService.INTENT_COMMAND_REQUEST);
                            commandRequestStat.putExtra(BTService.EXTRA_COMMAND_REQUEST_COMMAND, valueToBeSent);
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(commandRequestStat);
                            
                            //SENDING NOW THE STAT MESSAGE TO UPDATE ALL FIELDS!!
                            Intent updateStat = new Intent(BTService.INTENT_COMMAND_REQUEST);
                            updateStat.putExtra(BTService.EXTRA_COMMAND_REQUEST_COMMAND, "STAT");
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(updateStat);
                        }
                    });
        }

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();

    }
}