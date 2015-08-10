
    public final static String STYLE_CLASS = "ui-checkbox ui-widget";
    public final static String CHECKBOX_BOX_CLASS = "ui-checkbox-box ui-widget ui-corner-all ui-state-default";
    public final static String CHECKBOX_INPUT_WRAPPER_CLASS = "ui-helper-hidden";
    public final static String CHECKBOX_ICON_CLASS = "ui-checkbox-icon";
    public final static String CHECKBOX_CHECKED_ICON_CLASS = "ui-icon ui-icon-check";
    public final static String LABEL_CLASS = "ui-checkbox-label";
    
    public final static String MOBILE_STYLE_CLASS = "ui-checkbox";
    public final static String MOBILE_LABEL_OFF_CLASS = "ui-btn ui-btn-inherit ui-btn-icon-left ui-corner-all ui-checkbox-off";
    public final static String MOBILE_LABEL_ON_CLASS = "ui-btn ui-btn-inherit ui-btn-icon-left ui-corner-all ui-checkbox-on";

    public String getInputClientId() {
        return this.getClientId(getFacesContext()) + "_input";
    }

    public String getValidatableInputClientId() {
        return this.getInputClientId();
    }

    public void setLabelledBy(String labelledBy) {
        getStateHelper().put("labelledby", labelledBy);
    }
    public String getLabelledBy() {
        return (String) getStateHelper().get("labelledby");
    }