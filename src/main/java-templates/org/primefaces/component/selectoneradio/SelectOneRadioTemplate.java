import javax.faces.component.UINamingContainer;

    public final static String STYLE_CLASS = "ui-selectoneradio ui-widget";
    public final static String RADIO_BUTTON_CLASS = "ui-radiobutton ui-widget";
    public final static String RADIO_BOX_CLASS = "ui-radiobutton-box ui-widget ui-corner-all ui-radiobutton-relative ui-state-default";
    public final static String RADIO_ICON_CLASS = "ui-radiobutton-icon";
    public final static String RADIO_CHECKED_ICON_CLASS = "ui-icon ui-icon-bullet";
    
    private int index = -1;

    public String getContainerClientId(FacesContext context) {
        index++;

        return this.getClientId(context) + UINamingContainer.getSeparatorChar(context) + index;
    }
