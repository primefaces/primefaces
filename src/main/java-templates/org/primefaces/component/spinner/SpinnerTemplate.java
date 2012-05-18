
    public final static String CONTAINER_CLASS = "ui-spinner ui-widget ui-corner-all";
    public final static String INPUT_CLASS = "ui-spinner-input ui-inputfield ui-state-default ui-corner-all";
    public final static String UP_BUTTON_CLASS = "ui-spinner-button ui-spinner-up ui-corner-tr ui-button ui-widget ui-state-default ui-button-text-only";
    public final static String DOWN_BUTTON_CLASS = "ui-spinner-button ui-spinner-down ui-corner-br ui-button ui-widget ui-state-default ui-button-text-only";
    public final static String UP_ICON_CLASS = "ui-icon ui-icon-triangle-1-n";
    public final static String DOWN_ICON_CLASS = "ui-icon ui-icon-triangle-1-s";

    public String getInputClientId() {
        return this.getClientId(getFacesContext()) + "_input";
    }