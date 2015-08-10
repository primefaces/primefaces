import javax.faces.component.UINamingContainer;

    public static final String CONTAINER_CLASS = "ui-selectmanymenu ui-inputfield ui-widget ui-widget-content ui-corner-all";
    public static final String LIST_CONTAINER_CLASS = "ui-selectlistbox-listcontainer";
    public static final String LIST_CLASS = "ui-selectlistbox-list";
    public static final String ITEM_CLASS = "ui-selectlistbox-item ui-corner-all";
    public final static String FILTER_CONTAINER_CLASS = "ui-selectlistbox-filter-container";
    public final static String FILTER_CLASS = "ui-selectlistbox-filter ui-inputfield ui-widget ui-state-default ui-corner-all";
    public final static String FILTER_ICON_CLASS = "ui-icon ui-icon-search";

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

    public static String[] DOM_EVENTS = {
		"onchange",
		"onclick",
        "ondblclick"
	};