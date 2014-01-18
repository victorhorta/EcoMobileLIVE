package com.ecomaplive.ecomobilelive.fragments;

/**
 * Indicates what kind of user interface will be used when setting config
 * parameters.
 * 
 * @author Victor
 * 
 */
public enum StatDataType {
    //No stat received yet
    NOSTATRECEIVED("No STAT message received from the device yet. Press UPDATE to send a request."),
    
    //Unchangeable
    UNCHANGEABLE("This parameter is unchangeable."),
    
    //Radiobuttons
    RADIOBUTTON_ON_OFF("Set field as On or Off"),
    RADIOBUTTON_ENABLE_DISABLE("Enable or disable selected field"),
    RADIOBUTTON_POS_NEG("Set value as positive or negative"),
    
    //Sliders
    SLIDER_0_1000("Slide to select a value"),
    
    //Textfields
    TEXTFIELD_DECIMAL(""),
    TEXTFIELD_HEX(""),
    
    //Datepicker
    DATEPICKER("Select a date"),
    TIMEPICKER("Select a time"),
    DATEANDTIMEPICKER("Select a date and time");
    
    private String dialogMessage;
    private StatDataType(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }
    
    public String getDialogMessage() {
        return dialogMessage;
    }
    
}
