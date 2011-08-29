import javax.faces.component.UINamingContainer;

    public final static String STYLE_CLASS = "ui-selectoneradio ui-widget";

    private int index = -1;

    public String getContainerClientId(FacesContext context) {
        index++;

        return this.getClientId(context) + UINamingContainer.getSeparatorChar(context) + index;
    }
