import java.util.Map; 

    public final static String STYLE_CLASS = "ui-clock ui-widget ui-widget-header ui-corner-all";

    public boolean isSyncRequest() {
        FacesContext context = getFacesContext();
        Map<String,String> params = context.getExternalContext().getRequestParameterMap();
    
        return params.containsKey(this.getClientId(context) + "_sync");
    }


    
