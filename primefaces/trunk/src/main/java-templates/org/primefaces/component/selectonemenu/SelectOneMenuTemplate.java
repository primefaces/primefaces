
    public final static String STYLE_CLASS = "ui-selectonemenu ui-widget ui-state-default ui-corner-all ui-helper-clearfix";
    public final static String LABEL_CLASS = "ui-selectonemenu-label ui-corner-all";
    public final static String TRIGGER_CLASS = "ui-selectonemenu-trigger ui-state-default ui-corner-right";
    public final static String PANEL_CLASS = "ui-selectonemenu-panel ui-helper-hidden-accessible";
    public final static String LIST_CLASS = "ui-selectonemenu-list ui-widget-content ui-widget ui-corner-all ui-helper-reset";

    private String selectedLabel = "";

    public void setSelectedLabel(String label) {
        this.selectedLabel = label;
    }

    public String getSelectedLabel() {
        return this.selectedLabel;
    }

    